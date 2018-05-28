package ch.jalu.configme.neo.properties;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.neo.propertytype.BooleanType;
import ch.jalu.configme.neo.propertytype.EnumType;
import ch.jalu.configme.neo.propertytype.IntegerType;
import ch.jalu.configme.neo.propertytype.LowercaseStringSetType;
import ch.jalu.configme.neo.propertytype.PropertyType;
import ch.jalu.configme.neo.propertytype.StringListType;
import ch.jalu.configme.neo.propertytype.StringType;
import ch.jalu.configme.samples.TestEnum;
import org.hamcrest.Matcher;
import org.junit.Test;

import static ch.jalu.configme.neo.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.neo.properties.PropertyInitializer.newLowercaseStringSetProperty;
import static ch.jalu.configme.neo.properties.PropertyInitializer.newProperty;
import static ch.jalu.configme.neo.properties.PropertyInitializer.optionalBooleanProperty;
import static ch.jalu.configme.neo.properties.PropertyInitializer.optionalEnumProperty;
import static ch.jalu.configme.neo.properties.PropertyInitializer.optionalIntegerProperty;
import static ch.jalu.configme.neo.properties.PropertyInitializer.optionalStringProperty;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link PropertyInitializer}.
 */
public class PropertyInitializerTest {

    @Test
    public void shouldInstantiateProperties() {
        assertThat(newProperty("my.path", true), propertyUsingType(BooleanType.class));
        assertThat(newProperty("my.path", 12), propertyUsingType(IntegerType.class));
        assertThat(newProperty("my.path", "default"), propertyUsingType(StringType.class));
        assertThat(newProperty(TestEnum.class, "my.path", TestEnum.FIRST), propertyUsingType(EnumType.class));
        assertThat(newListProperty("path", "default", "entries"), propertyUsingType(StringListType.class));
        assertThat(newLowercaseStringSetProperty("path", "a", "b", "c"), propertyUsingType(LowercaseStringSetType.class));
//        assertThat(newBeanProperty(WorldGroupConfig.class, "worlds", new WorldGroupConfig()), instanceOf(BeanProperty.class));

        assertThat(optionalBooleanProperty("path"), instanceOf(OptionalProperty.class));
        assertThat(optionalIntegerProperty("path"), instanceOf(OptionalProperty.class));
        assertThat(optionalStringProperty("path"), instanceOf(OptionalProperty.class));
        assertThat(optionalEnumProperty(TestEnum.class, "path"), instanceOf(OptionalProperty.class));
    }

    @Test
    public void shouldHaveProtectedConstructor() {
        TestUtils.validateHasOnlyProtectedEmptyConstructor(PropertyInitializer.class);
    }

    private static Matcher<Property<?>> propertyUsingType(Class<? extends PropertyType> propertyTypeClass) {
        return hasProperty("propertyType", instanceOf(propertyTypeClass));
    }
}
