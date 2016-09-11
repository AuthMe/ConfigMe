package com.github.authme.configme.beanmapper;

import com.github.authme.configme.TestUtils;
import com.github.authme.configme.beanmapper.sample.GameMode;
import com.github.authme.configme.beanmapper.sample.Group;
import com.github.authme.configme.resource.PropertyResource;
import com.github.authme.configme.resource.YamlFileResource;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link Mapper}.
 */
public class MapperTest {

    @Test
    public void shouldCreateMap() {
        // given
        PropertyResource resource = new YamlFileResource(TestUtils.getJarFile("/beanmapper/worlds.yml"));
        Mapper mapper = new Mapper();
        String path = "groups";

        // when
        Map<String, Group> result = mapper.createMap(path, resource, Group.class);

        // then
        assertThat(result.keySet(), containsInAnyOrder("default", "creative"));
        Group survival = result.get("default");
        assertThat(survival.getWorlds(), contains("world", "world_nether", "world_the_end"));
        assertThat(survival.getDefaultGamemode(), equalTo(GameMode.SURVIVAL));
        Group creative = result.get("creative");
        assertThat(creative.getWorlds(), contains("creative"));
        assertThat(creative.getDefaultGamemode(), equalTo(GameMode.CREATIVE));
    }
}
