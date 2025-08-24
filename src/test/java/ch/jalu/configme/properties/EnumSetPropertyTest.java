package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumSet;
import java.util.Set;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Test for {@link EnumSetProperty}.
 */
@ExtendWith(MockitoExtension.class)
class EnumSetPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    void shouldReturnEnumSetDefaultValue() {
        // given
        EnumSet<TestEnum> set = EnumSet.of(TestEnum.ENTRY_A);
        EnumSetProperty<TestEnum> property =
            new EnumSetProperty<>("enum.path", TestEnum.class, set);
        given(reader.getObject(property.getPath()))
            .willReturn(null);

        // when
        PropertyValue<Set<TestEnum>> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(EnumSet.of(TestEnum.ENTRY_A)));
    }

    @Test
    void shouldReturnEnumSetDefaultValueFromArray() {
        // given
        EnumSetProperty<TestEnum> property =
            new EnumSetProperty<>("enum.path", TestEnum.class, new TestEnum[]{TestEnum.ENTRY_B, TestEnum.ENTRY_C});
        given(reader.getObject(property.getPath()))
            .willReturn(null);

        // when
        PropertyValue<Set<TestEnum>> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(EnumSet.of(TestEnum.ENTRY_B, TestEnum.ENTRY_C)));
    }

    private enum TestEnum {

        ENTRY_A,

        ENTRY_B,

        ENTRY_C

    }
}
