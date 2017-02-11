package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.samples.TestEnum;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static java.util.Optional.of;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link OptionalProperty}.
 */
public class OptionalPropertyTest {

    private PropertyResource resource;

    @Before
    public void setUpResource() {
        resource = mock(PropertyResource.class);
        when(resource.getBoolean("bool.path.test")).thenReturn(true);
        when(resource.getBoolean("bool.path.wrong")).thenReturn(null);
        when(resource.getInt("int.path.test")).thenReturn(27);
        when(resource.getInt("int.path.wrong")).thenReturn(null);
        when(resource.getObject("enum.path.test")).thenReturn(TestEnum.FOURTH.name());
        when(resource.getObject("enum.path.wrong")).thenReturn(null);
    }

    @Test
    public void shouldReturnPresentValues() {
        // given
        OptionalProperty<Boolean> booleanProp = new OptionalProperty<>(new BooleanProperty("bool.path.test", false));
        OptionalProperty<Integer> intProp = new OptionalProperty<>(new IntegerProperty("int.path.test", 0));
        OptionalProperty<TestEnum> enumProp = new OptionalProperty<>(new EnumProperty<>(TestEnum.class, "enum.path.test", TestEnum.SECOND));

        // when
        Optional<Boolean> boolResult = booleanProp.getFromResource(resource);
        Optional<Integer> intResult = intProp.getFromResource(resource);
        Optional<TestEnum> enumResult = enumProp.getFromResource(resource);

        // then
        assertThat(boolResult, equalTo(of(true)));
        assertThat(intResult, equalTo(of(27)));
        assertThat(enumResult, equalTo(of(TestEnum.FOURTH)));
    }

    @Test
    public void shouldReturnEmptyOptional() {
        // given
        OptionalProperty<Boolean> booleanProp = new OptionalProperty<>(new BooleanProperty("bool.path.wrong", false));
        OptionalProperty<Integer> intProp = new OptionalProperty<>(new IntegerProperty("int.path.wrong", 0));
        OptionalProperty<TestEnum> enumProp = new OptionalProperty<>(new EnumProperty<>(TestEnum.class, "enum.path.wrong", TestEnum.SECOND));

        // when
        Optional<Boolean> boolResult = booleanProp.getFromResource(resource);
        Optional<Integer> intResult = intProp.getFromResource(resource);
        Optional<TestEnum> enumResult = enumProp.getFromResource(resource);

        // then
        assertThat(boolResult, equalTo(Optional.empty()));
        assertThat(intResult, equalTo(Optional.empty()));
        assertThat(enumResult, equalTo(Optional.empty()));
    }
}
