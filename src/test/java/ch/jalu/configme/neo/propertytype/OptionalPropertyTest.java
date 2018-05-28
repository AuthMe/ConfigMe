package ch.jalu.configme.neo.propertytype;

import ch.jalu.configme.neo.resource.PropertyReader;
import ch.jalu.configme.neo.samples.TestEnum;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static java.util.Optional.of;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link OptionalType}.
 */
public class OptionalPropertyTest {

    private PropertyReader reader;

    @Before
    public void setUpResource() {
        reader = mock(PropertyReader.class);
        when(reader.getBoolean("bool.path.test")).thenReturn(true);
        when(reader.getBoolean("bool.path.wrong")).thenReturn(null);
        when(reader.getInt("int.path.test")).thenReturn(27);
        when(reader.getInt("int.path.wrong")).thenReturn(null);
        when(reader.getString("enum.path.test")).thenReturn(TestEnum.FOURTH.name());
        when(reader.getString("enum.path.wrong")).thenReturn(null);
    }

    @Test
    public void shouldReturnPresentValues() {
        // given
        OptionalType<Boolean> booleanProp = new OptionalType<>(BooleanType.instance());
        OptionalType<Integer> intProp = new OptionalType<>(IntegerType.instance());
        OptionalType<TestEnum> enumProp = new OptionalType<>(new EnumType<>(TestEnum.class));

        // when
        Optional<Boolean> boolType = booleanProp.getFromReader(reader, "bool.path.test");
        Optional<Integer> intType = intProp.getFromReader(reader, "int.path.test");
        Optional<TestEnum> enumType = enumProp.getFromReader(reader, "enum.path.test");

        // then
        assertThat(boolType, equalTo(of(true)));
        assertThat(intType, equalTo(of(27)));
        assertThat(enumType, equalTo(of(TestEnum.FOURTH)));
    }

    @Test
    public void shouldReturnEmptyOptional() {
        // given
        OptionalType<Boolean> booleanType = new OptionalType<>(BooleanType.instance());
        OptionalType<Integer> intType = new OptionalType<>(IntegerType.instance());
        OptionalType<TestEnum> enumType = new OptionalType<>(new EnumType<>(TestEnum.class));

        // when
        Optional<Boolean> boolResult = booleanType.getFromReader(reader, "bool.path.wrong");
        Optional<Integer> intResult = intType.getFromReader(reader, "int.path.wrong");
        Optional<TestEnum> enumResult = enumType.getFromReader(reader, "enum.path.wrong");

        // then
        assertThat(boolResult, equalTo(Optional.empty()));
        assertThat(intResult, equalTo(Optional.empty()));
        assertThat(enumResult, equalTo(Optional.empty()));
    }

    @Test
    public void shouldAlwaysReturnThatIsPresent() {
        // given
        OptionalType<Boolean> booleanProp = new OptionalType<>(BooleanType.instance());

        // when
        boolean isPresent = booleanProp.isPresent(reader, "bool.path.wrong");

        // then
        assertThat(isPresent, equalTo(true));
    }
}
