package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyResource;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link EnumProperty}.
 */
public class EnumPropertyTest {

    @Test
    public void shouldReturnCorrectEnumValue() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "enum.path", TestEnum.ENTRY_C);
        PropertyResource resource = mock(PropertyResource.class);
        given(resource.getObject(property.getPath())).willReturn("Entry_B");

        // when
        TestEnum result = property.getValue(resource);

        // then
        assertThat(result, equalTo(TestEnum.ENTRY_B));
    }

    @Test
    public void shouldFallBackToDefaultForInvalidValue() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "enum.path", TestEnum.ENTRY_C);
        PropertyResource resource = mock(PropertyResource.class);
        given(resource.getObject(property.getPath())).willReturn("Bogus");

        // when
        TestEnum result = property.getValue(resource);

        // then
        assertThat(result, equalTo(TestEnum.ENTRY_C));
    }

    @Test
    public void shouldFallBackToDefaultForNonExistentValue() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "enum.path", TestEnum.ENTRY_C);
        PropertyResource resource = mock(PropertyResource.class);
        given(resource.getObject(property.getPath())).willReturn(null);

        // when
        TestEnum result = property.getValue(resource);

        // then
        assertThat(result, equalTo(TestEnum.ENTRY_C));
    }

    @Test
    public void shouldReturnTrueForContainsCheck() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "my.test.path", TestEnum.ENTRY_C);
        PropertyResource resource = mock(PropertyResource.class);
        given(resource.getObject(property.getPath())).willReturn("ENTRY_B");

        // when
        boolean result = property.isPresent(resource);

        // then
        assertThat(result, equalTo(true));
    }

    @Test
    public void shouldReturnFalseForFileWithoutConfig() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "my.test.path", TestEnum.ENTRY_C);
        PropertyResource resource = mock(PropertyResource.class);
        given(resource.getObject(property.getPath())).willReturn(null);

        // when
        boolean result = property.isPresent(resource);

        // then
        assertThat(result, equalTo(false));
    }

    @Test
    public void shouldReturnFalseForUnknownValue() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "my.test.path", TestEnum.ENTRY_A);
        PropertyResource resource = mock(PropertyResource.class);
        given(resource.getObject(property.getPath())).willReturn("wrong value");

        // when
        boolean result = property.isPresent(resource);

        // then
        assertThat(result, equalTo(false));
    }

    /**
     * The underlying value is typically a String but may be from the enum if set afterwards.
     */
    @Test
    public void shouldReturnEnumForEnumValue() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "my.test.path", TestEnum.ENTRY_A);
        PropertyResource resource = mock(PropertyResource.class);
        given(resource.getObject(property.getPath())).willReturn(TestEnum.ENTRY_C);

        // when
        TestEnum value = property.getFromResource(resource);

        // then
        assertThat(value, equalTo(TestEnum.ENTRY_C));
    }


    private enum TestEnum {

        ENTRY_A,

        ENTRY_B,

        ENTRY_C

    }
}
