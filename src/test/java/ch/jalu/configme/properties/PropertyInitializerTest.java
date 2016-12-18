package ch.jalu.configme.properties;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.beanmapper.worldgroup.WorldGroupConfig;
import ch.jalu.configme.samples.TestEnum;
import org.junit.Test;

import static ch.jalu.configme.properties.PropertyInitializer.newBeanProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newLowercaseListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;
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
