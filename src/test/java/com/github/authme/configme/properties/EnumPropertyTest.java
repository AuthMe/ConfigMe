package com.github.authme.configme.properties;

import com.github.authme.configme.resource.PropertyResource;
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
        given(resource.getString(property.getPath())).willReturn("Entry_B");

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
        given(resource.getString(property.getPath())).willReturn("Bogus");

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
        given(resource.getString(property.getPath())).willReturn(null);

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
        given(resource.contains(property.getPath())).willReturn(true);
        given(resource.getString(property.getPath())).willReturn("ENTRY_B");

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
        given(resource.contains(property.getPath())).willReturn(false);

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
        given(resource.contains(property.getPath())).willReturn(true);
        given(resource.getString(property.getPath())).willReturn("wrong value");

        // when
        boolean result = property.isPresent(resource);

        // then
        assertThat(result, equalTo(false));
    }


    private enum TestEnum {

        ENTRY_A,

        ENTRY_B,

        ENTRY_C

    }
}
