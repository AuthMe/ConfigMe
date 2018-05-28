package ch.jalu.configme.neo.propertytype;

import ch.jalu.configme.neo.resource.PropertyReader;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link EnumType}.
 */
public class EnumTypeTest {

    @Test
    public void shouldReturnCorrectEnumValue() {
        // given
        PropertyType<TestEnum> type = new EnumType<>(TestEnum.class);
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString("enum.path")).willReturn("Entry_B");

        // when
        TestEnum result = type.getFromReader(reader, "enum.path");

        // then
        assertThat(result, equalTo(TestEnum.ENTRY_B));
    }

    @Test
    public void shouldReturnNullForInvalidValue() {
        // given
        PropertyType<TestEnum> type = new EnumType<>(TestEnum.class);
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString("enum.path")).willReturn("Bogus");

        // when
        TestEnum result = type.getFromReader(reader, "enum.path");

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldReturnNullForNonExistentValue() {
        // given
        PropertyType<TestEnum> type = new EnumType<>(TestEnum.class);
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString("the.path")).willReturn(null);

        // when
        TestEnum result = type.getFromReader(reader, "the.path");

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldReturnTrueForContainsCheck() {
        // given
        PropertyType<TestEnum> type = new EnumType<>(TestEnum.class);
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString("my.test.path")).willReturn("ENTRY_B");

        // when
        boolean result = type.isPresent(reader, "my.test.path");

        // then
        assertThat(result, equalTo(true));
    }

    @Test
    public void shouldReturnFalseForFileWithoutConfig() {
        // given
        PropertyType<TestEnum> type = new EnumType<>(TestEnum.class);
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString("my.test.path")).willReturn(null);

        // when
        boolean result = type.isPresent(reader, "my.test.path");

        // then
        assertThat(result, equalTo(false));
    }

    @Test
    public void shouldReturnFalseForUnknownValue() {
        // given
        PropertyType<TestEnum> type = new EnumType<>(TestEnum.class);
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString("my.test.path")).willReturn("wrong value");

        // when
        boolean result = type.isPresent(reader, "my.test.path");

        // then
        assertThat(result, equalTo(false));
    }

    @Test
    public void shouldExportAsEnumName() {
        // given
        PropertyType<TestEnum> type = new EnumType<>(TestEnum.class);

        // when
        Object exportObject = type.toExportValue(TestEnum.ENTRY_C);

        // then
        assertThat(exportObject, equalTo("ENTRY_C"));
    }

    private enum TestEnum {

        ENTRY_A,

        ENTRY_B,

        ENTRY_C

    }
}
