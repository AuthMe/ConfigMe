package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;
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
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString(property.getPath())).willReturn("Entry_B");

        // when
        TestEnum result = property.determineValue(reader);

        // then
        assertThat(result, equalTo(TestEnum.ENTRY_B));
    }

    @Test
    public void shouldFallBackToDefaultForInvalidValue() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "enum.path", TestEnum.ENTRY_C);
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString(property.getPath())).willReturn("Bogus");

        // when
        TestEnum result = property.determineValue(reader);

        // then
        assertThat(result, equalTo(TestEnum.ENTRY_C));
    }

    @Test
    public void shouldFallBackToDefaultForNonExistentValue() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "enum.path", TestEnum.ENTRY_C);
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString(property.getPath())).willReturn(null);

        // when
        TestEnum result = property.determineValue(reader);

        // then
        assertThat(result, equalTo(TestEnum.ENTRY_C));
    }

    @Test
    public void shouldReturnTrueForContainsCheck() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "my.test.path", TestEnum.ENTRY_C);
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString(property.getPath())).willReturn("ENTRY_B");

        // when
        boolean result = property.isPresent(reader);

        // then
        assertThat(result, equalTo(true));
    }

    @Test
    public void shouldReturnFalseForFileWithoutConfig() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "my.test.path", TestEnum.ENTRY_C);
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString(property.getPath())).willReturn(null);

        // when
        boolean result = property.isPresent(reader);

        // then
        assertThat(result, equalTo(false));
    }

    @Test
    public void shouldReturnFalseForUnknownValue() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "my.test.path", TestEnum.ENTRY_A);
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString(property.getPath())).willReturn("wrong value");

        // when
        boolean result = property.isPresent(reader);

        // then
        assertThat(result, equalTo(false));
    }

    @Test
    public void shouldExportAsEnumName() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "my.test.path", TestEnum.ENTRY_A);

        // when
        Object exportObject = property.toExportValue(TestEnum.ENTRY_C);

        // then
        assertThat(exportObject, equalTo("ENTRY_C"));
    }

    private enum TestEnum {

        ENTRY_A,

        ENTRY_B,

        ENTRY_C

    }
}
