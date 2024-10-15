package ch.jalu.configme.beanmapper.propertydescription;


import ch.jalu.configme.Comment;
import ch.jalu.configme.beanmapper.ConfigMeMapperException;
import ch.jalu.configme.beanmapper.command.ExecutionDetails;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.samples.beanannotations.AnnotatedEntry;
import ch.jalu.configme.samples.beanannotations.BeanWithEmptyName;
import ch.jalu.configme.samples.beanannotations.BeanWithExportName;
import ch.jalu.configme.samples.beanannotations.BeanWithExportNameExtension;
import ch.jalu.configme.samples.beanannotations.BeanWithNameClash;
import ch.jalu.configme.samples.inheritance.Child;
import ch.jalu.configme.samples.inheritance.ChildWithFieldOverrides;
import ch.jalu.configme.samples.inheritance.Middle;
import ch.jalu.typeresolver.TypeInfo;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static ch.jalu.configme.TestUtils.transform;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link BeanDescriptionFactoryImpl}.
 */
class BeanDescriptionFactoryImplTest {

    private final BeanDescriptionFactoryImpl factory = new BeanDescriptionFactoryImpl();

    @Test
    void shouldReturnWritableProperties() {
        // given / when
        List<BeanFieldPropertyDescription> descriptions = factory.collectProperties(SampleBean.class);

        // then
        assertThat(descriptions, hasSize(4));

        BeanPropertyDescription nameProperty = descriptions.get(0);
        assertThat(nameProperty.getName(), equalTo("name"));
        assertThat(nameProperty.getTypeInformation(), equalTo(new TypeInfo(String.class)));
        assertThat(nameProperty.getComments(), sameInstance(BeanPropertyComments.EMPTY));

        BeanPropertyDescription sizeProperty = descriptions.get(1);
        assertThat(sizeProperty.getName(), equalTo("size"));
        assertThat(sizeProperty.getTypeInformation(), equalTo(new TypeInfo(int.class)));
        assertThat(sizeProperty.getComments().getComments(), contains("Size of this entry (cm)"));
        assertThat(sizeProperty.getComments().getUuid(), notNullValue());

        BeanPropertyDescription longFieldProperty = descriptions.get(2);
        assertThat(longFieldProperty.getName(), equalTo("longField"));
        assertThat(longFieldProperty.getTypeInformation(), equalTo(new TypeInfo(long.class)));
        assertThat(longFieldProperty.getComments(), sameInstance(BeanPropertyComments.EMPTY));

        BeanPropertyDescription uuidProperty = descriptions.get(3);
        assertThat(uuidProperty.getName(), equalTo("uuid"));
        assertThat(uuidProperty.getTypeInformation(), equalTo(new TypeInfo(UUID.class)));
        assertThat(uuidProperty.getComments(), sameInstance(BeanPropertyComments.EMPTY));
    }

    @Test
    void shouldReturnEmptyListForNonBeanClass() {
        // given / when / then
        assertThat(factory.collectProperties(List.class), empty());
    }

    @Test
    void shouldNotConsiderTransientFields() {
        // given / when
        Collection<BeanFieldPropertyDescription> properties = factory.collectProperties(BeanWithTransientFields.class);

        // then
        assertThat(properties, hasSize(2));
        assertThat(transform(properties, BeanPropertyDescription::getName), contains("name", "isMandatory"));
    }

    @Test
    void shouldBeAwareOfInheritanceAndRespectOrder() {
        // given / when
        Collection<BeanFieldPropertyDescription> properties = factory.collectProperties(Middle.class);

        // then
        assertThat(properties, hasSize(3));
        assertThat(transform(properties, BeanPropertyDescription::getName), contains("id", "name", "ratio"));
    }

    @Test
    void shouldLetChildFieldsOverrideParentFields() {
        // given / when
        Collection<BeanFieldPropertyDescription> properties = factory.collectProperties(Child.class);

        // then
        assertThat(properties, hasSize(5));
        assertThat(transform(properties, BeanPropertyDescription::getName),
            contains("id", "temporary", "name", "ratio", "importance"));
    }

    @Test
    void shouldUseExportName() {
        // given / when
        Collection<BeanFieldPropertyDescription> properties = factory.collectProperties(AnnotatedEntry.class);

        // then
        assertThat(properties, hasSize(2));
        assertThat(transform(properties, BeanPropertyDescription::getName),
            contains("id", "has-id"));
    }

    @Test
    void shouldThrowForMultiplePropertiesWithSameName() {
        // given / when
        ConfigMeMapperException ex = assertThrows(ConfigMeMapperException.class,
            () -> factory.collectProperties(BeanWithNameClash.class));

        // then
        assertThat(ex.getMessage(),
            equalTo("class ch.jalu.configme.samples.beanannotations.BeanWithNameClash has multiple properties with name 'threshold'"));
    }

    @Test
    void shouldThrowForWhenExportNameIsNullForProperty() {
        // given / when
        ConfigMeMapperException ex = assertThrows(ConfigMeMapperException.class,
            () -> factory.collectProperties(BeanWithEmptyName.class));

        // then
        assertThat(ex.getMessage(),
            equalTo("Custom name of FieldProperty '' for field 'BeanWithEmptyName#author' may not be empty"));
    }

    @Test
    void shouldReturnCommentsWithoutUuid() {
        // given / when
        List<BeanFieldPropertyDescription> execDetailsProperties = factory.collectProperties(ExecutionDetails.class);

        // then
        BeanPropertyComments executorComments = getDescription("executor", execDetailsProperties).getComments();
        assertThat(executorComments, sameInstance(BeanPropertyComments.EMPTY));

        BeanPropertyComments importanceComments = getDescription("importance", execDetailsProperties).getComments();
        assertThat(importanceComments.getComments(), contains("The higher the number, the more important"));
        assertThat(importanceComments.getUuid(), nullValue());
    }

    @Test
    void shouldPickUpCustomNameFromField() {
        // given / when
        List<BeanFieldPropertyDescription> properties = factory.collectProperties(BeanWithExportName.class);

        // then
        assertThat(properties, hasSize(3));
        assertThat(properties.get(0).getName(), equalTo("s_name"));
        assertThat(properties.get(0).getComments().getComments(), contains("name_com"));
        assertThat(properties.get(1).getName(), equalTo("b_active"));
        assertThat(properties.get(1).getComments().getComments(), contains("active_com"));
        assertThat(properties.get(2).getName(), equalTo("i_size"));
        assertThat(properties.get(2).getComments().getComments(), contains("size_com"));
    }

    @Test
    void shouldPickUpCustomNameFromFieldsIncludingInheritance() {
        // given / when
        List<BeanFieldPropertyDescription> properties = factory.collectProperties(BeanWithExportNameExtension.class);

        // then
        assertThat(properties, hasSize(4));
        assertThat(properties.get(0).getName(), equalTo("s_name"));
        assertThat(properties.get(0).getComments().getComments(), contains("name_com"));
        assertThat(properties.get(1).getName(), equalTo("b_active"));
        assertThat(properties.get(1).getComments().getComments(), contains("active_com"));
        assertThat(properties.get(2).getName(), equalTo("i_size"));
        assertThat(properties.get(2).getComments().getComments(), contains("size_com"));
        assertThat(properties.get(3).getName(), equalTo("d_weight"));
        assertThat(properties.get(3).getComments().getComments(), contains("weight_com"));
    }

    @Test
    void shouldTakeOverFieldConfigsFromOverridingClass() {
        // given / when
        List<BeanFieldPropertyDescription> properties = factory.collectProperties(ChildWithFieldOverrides.class);

        // then
        assertThat(transform(properties, BeanPropertyDescription::getName),
            contains("id", "o_ratio"));
    }

    @Test
    void shouldThrowForFinalField() {
        // given / when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> factory.collectProperties(BeanWithFinalField.class));

        // then
        assertThat(ex.getMessage(), equalTo(
            "Field 'BeanDescriptionFactoryImplTest$BeanWithFinalField#version' is marked as final but not to be ignored. Final fields cannot be set by the mapper."));
    }

    private static BeanPropertyDescription getDescription(String name,
                                                          Collection<? extends BeanPropertyDescription> descriptions) {
        for (BeanPropertyDescription description : descriptions) {
            if (name.equals(description.getName())) {
                return description;
            }
        }
        throw new IllegalArgumentException(name);
    }

    private static final class SampleBean {

        private String name;
        @Comment("Size of this entry (cm)")
        private int size;
        private long longField;
        private UUID uuid = UUID.randomUUID();

    }

    private static final class BeanWithTransientFields {

        private static final String CONSTANT = "This will be ignored";
        private static int counter = 3; // This will be ignored

        private String name;
        private transient long tempId;
        private transient boolean isSaved;
        private boolean isMandatory;

    }

    private static final class BeanWithFinalField {

        private String name;
        private final int version = 3;
        private boolean isNew;

    }
}
