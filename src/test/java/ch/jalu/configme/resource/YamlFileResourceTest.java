package ch.jalu.configme.resource;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.beanmapper.worldgroup.GameMode;
import ch.jalu.configme.beanmapper.worldgroup.Group;
import ch.jalu.configme.beanmapper.worldgroup.WorldGroupConfig;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.samples.TestConfiguration;
import ch.jalu.configme.samples.TestEnum;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ch.jalu.configme.TestUtils.verifyException;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link YamlFileResource} and {@link YamlFileReader}.
 */
public class YamlFileResourceTest {

    private static final String COMPLETE_FILE = "/config-sample.yml";
    private static final String INCOMPLETE_FILE = "/config-incomplete-sample.yml";
    private static final String DIFFICULT_FILE = "/config-difficult-values.yml";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();



    @Test
    public void shouldSetValuesButNotPersist() {
        // given
        File file = copyFileFromResources(INCOMPLETE_FILE);
        YamlFileResource resource = new YamlFileResource(file);

        // when
        assertThat(TestConfiguration.RATIO_ORDER.getValue(resource), equalTo(TestEnum.SECOND)); // default value
        resource.setValue(TestConfiguration.RATIO_ORDER.getPath(), TestEnum.THIRD);
        resource.setValue(TestConfiguration.SKIP_BORING_FEATURES.getPath(), true);

        // then
        assertThat(TestConfiguration.RATIO_ORDER.getValue(resource), equalTo(TestEnum.THIRD));
        assertThat(TestConfiguration.SKIP_BORING_FEATURES.getValue(resource), equalTo(true));

        // when (2) - reload without saving, so will fallback to default again
        resource.reload();

        // then
        assertThat(TestConfiguration.RATIO_ORDER.getValue(resource), equalTo(TestEnum.SECOND));
        assertThat(TestConfiguration.SKIP_BORING_FEATURES.getValue(resource), equalTo(false));
    }

    @Test
    public void shouldSetValueAfterLoadingEmptyFile() {
        // given
        String durationPath = "duration";
        int duration = 13;
        String headerPath = "text.sample.titles.header";
        String header = "Test header";

        File file = copyFileFromResources("/empty_file.yml");
        PropertyResource resource = new YamlFileResource(file);

        // when
        resource.setValue(durationPath, duration);
        resource.setValue(headerPath, header);

        // then
        assertThat(resource.getObject(durationPath), equalTo(duration));
        assertThat(resource.getObject(headerPath), equalTo(header));
    }

    @Test
    public void shouldSetBeanPropertyValueAtRoot() {
        // given
        // Custom WorldGroupConfig
        Group easyGroup = new Group();
        easyGroup.setDefaultGamemode(GameMode.CREATIVE);
        easyGroup.setWorlds(Arrays.asList("easy1", "easy2"));
        Group hardGroup = new Group();
        hardGroup.setDefaultGamemode(GameMode.SURVIVAL);
        hardGroup.setWorlds(Arrays.asList("hard1", "hard2"));

        Map<String, Group> groups = new HashMap<>();
        groups.put("easy", easyGroup);
        groups.put("hard", hardGroup);
        WorldGroupConfig worldGroupConfig = new WorldGroupConfig();
        worldGroupConfig.setGroups(groups);

        // Load resource with empty file
        File file = copyFileFromResources("/beanmapper/worlds.yml");
        PropertyResource resource = new YamlFileResource(file);

        // when
        resource.setValue("", worldGroupConfig);

        // then
        assertThat(resource.getObject(""), equalTo(worldGroupConfig));
    }

    @Test
    public void shouldThrowExceptionWhenSettingSubpathOfRootBean() {
        // given
        PropertyResource resource = new YamlFileResource(copyFileFromResources("/empty_file.yml"));
        resource.setValue("", new WorldGroupConfig());

        // when / then
        verifyException(
            () -> resource.setValue("some.path", 14),
            ConfigMeException.class,
            "The root path is a bean property");
    }

    @Test
    public void shouldReturnNullForUnknownPath() {
        // given
        File file = copyFileFromResources(COMPLETE_FILE);
        YamlFileResource resource = new YamlFileResource(file);

        // when / then
        assertThat(resource.getObject("sample.ratio.wrong.dunno"), nullValue());
        assertThat(resource.getObject(TestConfiguration.RATIO_ORDER.getPath() + ".child"), nullValue());
    }


    @Test
    public void shouldClearIntermediateValuesForNull() {
        // given
        File file = copyFileFromResources("/empty_file.yml");
        YamlFileResource resource = new YamlFileResource(file);
        resource.setValue("abc.def", 25);
        resource.setValue("abc.xyz", "Hi Peter");

        // when
        resource.setValue("abc.def.ghi.jjj", null);

        // then
        assertThat((Map<?, ?>) resource.getObject("abc.def"), anEmptyMap());
    }

    private File copyFileFromResources(String path) {
        return TestUtils.copyFileFromResources(path, temporaryFolder);
    }
}
