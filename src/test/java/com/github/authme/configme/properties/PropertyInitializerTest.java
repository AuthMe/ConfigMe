package com.github.authme.configme.properties;

import com.github.authme.configme.TestUtils;
import com.github.authme.configme.beanmapper.worldgroup.WorldGroupConfig;
import com.github.authme.configme.samples.TestEnum;
import org.junit.Test;

import static com.github.authme.configme.properties.PropertyInitializer.newBeanProperty;
import static com.github.authme.configme.properties.PropertyInitializer.newListProperty;
import static com.github.authme.configme.properties.PropertyInitializer.newLowercaseListProperty;
import static com.github.authme.configme.properties.PropertyInitializer.newProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link PropertyInitializer}.
 */
public class PropertyInitializerTest {

    @Test
    public void shouldInstantiateProperties() {
        assertThat(newProperty("my.path", true), instanceOf(BooleanProperty.class));
        assertThat(newProperty("my.path", 12), instanceOf(IntegerProperty.class));
        assertThat(newProperty("my.path", "default"), instanceOf(StringProperty.class));
        assertThat(newProperty(TestEnum.class, "my.path", TestEnum.FIRST), instanceOf(EnumProperty.class));
        assertThat(newListProperty("path", "default", "entries"), instanceOf(StringListProperty.class));
        assertThat(newLowercaseListProperty("path", "a", "b", "c"), instanceOf(LowercaseStringListProperty.class));
        assertThat(newBeanProperty(WorldGroupConfig.class, "worlds", new WorldGroupConfig()), instanceOf(BeanProperty.class));
    }

    @Test
    public void shouldHaveProtectedConstructor() {
        TestUtils.validateHasOnlyProtectedEmptyConstructor(PropertyInitializer.class);
    }
}
