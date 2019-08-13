package ch.jalu.configme.properties;

import ch.jalu.configme.configurationdata.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.Test;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
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
        given(reader.getObject(property.getPath())).willReturn("Entry_B");

        // when
        PropertyValue<TestEnum> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(TestEnum.ENTRY_B));
    }

    @Test
    public void shouldFallBackToDefaultForInvalidValue() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "enum.path", TestEnum.ENTRY_C);
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getObject(property.getPath())).willReturn("Bogus");

        // when
        PropertyValue<TestEnum> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(TestEnum.ENTRY_C));
    }

    @Test
    public void shouldFallBackToDefaultForNonExistentValue() {
        // given
        Property<TestEnum> property = new EnumProperty<>(TestEnum.class, "enum.path", TestEnum.ENTRY_C);
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getObject(property.getPath())).willReturn(null);

        // when
        PropertyValue<TestEnum> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(TestEnum.ENTRY_C));
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
