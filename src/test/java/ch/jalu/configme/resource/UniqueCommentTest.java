package ch.jalu.configme.resource;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.TestUtils;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.properties.BeanProperty;
import ch.jalu.configme.properties.ListProperty;
import ch.jalu.configme.properties.MapProperty;
import ch.jalu.configme.properties.OptionalProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.types.BeanPropertyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * Test for {@link YamlFileResource}, ensuring that unique comments only appear once across all properties.
 *
 * @see <a href="https://github.com/AuthMe/ConfigMe/issues/362">Issue #362</a>
 */
class UniqueCommentTest {

    @TempDir
    Path tempFolder;

    @Test
    void shouldExportWithCommentOnlyOnce() throws IOException {
        // given
        Path file = TestUtils.createTemporaryFile(tempFolder);
        YamlFileResource resource = new YamlFileResource(file);
        ConfigurationData configData = ConfigurationDataBuilder.createConfiguration(ServerSettingHolder.class);
        configData.setValue(ServerSettingHolder.WORLDS, ServerSettingHolder.WORLDS.getDefaultValue());
        configData.setValue(ServerSettingHolder.AUTH_GROUPS, ServerSettingHolder.AUTH_GROUPS.getDefaultValue());
        configData.setValue(ServerSettingHolder.ALT, Optional.of(new ServerCollection(false, "secondary")));

        // when
        resource.exportProperties(configData);

        // then
        assertThat(Files.readAllLines(file), contains(
            "worlds:",
            "    foo:",
            "        # List server names here",
            "        servers:",
            "        - base",
            "        - moon",
            "        enabled: false",
            "    bar:",
            "        servers:",
            "        - overworld",
            "        - nether",
            "        enabled: true",
            "auth:",
            "-   servers:",
            "    - reception",
            "    enabled: true",
            "-   servers:",
            "    - lobby",
            "    enabled: false",
            "alternative:",
            "    servers:",
            "    - secondary",
            "    enabled: false"));
    }

    @Test
    void shouldExportWithCommentOnFirstOccurrence() throws IOException {
        // given
        Path file = TestUtils.createTemporaryFile(tempFolder);
        YamlFileResource resource = new YamlFileResource(file);
        ConfigurationData configData = ConfigurationDataBuilder.createConfiguration(ServerSettingHolder.class);
        configData.setValue(ServerSettingHolder.WORLDS, Collections.emptyMap());
        configData.setValue(ServerSettingHolder.AUTH_GROUPS, ServerSettingHolder.AUTH_GROUPS.getDefaultValue());
        configData.setValue(ServerSettingHolder.ALT, Optional.empty());

        // when
        resource.exportProperties(configData);

        // then
        assertThat(Files.readAllLines(file), contains(
            "worlds: {}",
            "auth:",
            "-   # List server names here",
            "    servers:",
            "    - reception",
            "    enabled: true",
            "-   servers:",
            "    - lobby",
            "    enabled: false"));
    }

    @Test
    void shouldExportWithCommentOnFirstOccurrence2() throws IOException {
        // given
        Path file = TestUtils.createTemporaryFile(tempFolder);
        YamlFileResource resource = new YamlFileResource(file);
        ConfigurationData configData = ConfigurationDataBuilder.createConfiguration(ServerSettingHolder.class);
        configData.setValue(ServerSettingHolder.WORLDS, Collections.emptyMap());
        configData.setValue(ServerSettingHolder.AUTH_GROUPS, Collections.emptyList());
        configData.setValue(ServerSettingHolder.ALT, Optional.of(new ServerCollection(true, "backup")));

        // when
        resource.exportProperties(configData);

        // then
        assertThat(Files.readAllLines(file), contains(
            "worlds: {}",
            "auth: []",
            "alternative:",
            "    # List server names here",
            "    servers:",
            "    - backup",
            "    enabled: true"));
    }

    public static final class ServerSettingHolder implements SettingsHolder {

        public static final Property<Map<String, ServerCollection>> WORLDS =
            new MapProperty<>("worlds",
                createDefaultWorldsMap(),
                BeanPropertyType.of(ServerCollection.class));

        public static final Property<List<ServerCollection>> AUTH_GROUPS =
            new ListProperty<>("auth",
                BeanPropertyType.of(ServerCollection.class),
                new ServerCollection(true, "reception"), new ServerCollection(false, "lobby"));

        public static final Property<Optional<ServerCollection>> ALT =
            new OptionalProperty<>(new BeanProperty<>("alternative", ServerCollection.class, new ServerCollection()));

        private ServerSettingHolder() {
        }

        private static Map<String, ServerCollection> createDefaultWorldsMap() {
            Map<String, ServerCollection> serversByKey = new LinkedHashMap<>();
            serversByKey.put("foo", new ServerCollection(false, "base", "moon"));
            serversByKey.put("bar", new ServerCollection(true, "overworld", "nether"));
            return serversByKey;
        }
    }

    public static class ServerCollection {

        @Comment("List server names here")
        private List<String> servers = new ArrayList<>();

        private boolean enabled;

        public ServerCollection() {
        }

        public ServerCollection(boolean enabled, String... servers) {
            this.enabled = enabled;
            this.servers.addAll(Arrays.asList(servers));
        }

        public List<String> getServers() {
            return servers;
        }

        public void setServers(List<String> servers) {
            this.servers = servers;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
