package ch.jalu.configme.beanmapper.propertydescription;


import ch.jalu.configme.Comment;
import ch.jalu.configme.beanmapper.ConfigMeMapperException;
import ch.jalu.configme.beanmapper.command.ExecutionDetails;
import ch.jalu.configme.samples.beanannotations.AnnotatedEntry;
import ch.jalu.configme.samples.beanannotations.BeanWithEmptyName;
import ch.jalu.configme.samples.beanannotations.BeanWithExportName;
import ch.jalu.configme.samples.beanannotations.BeanWithExportNameExtension;
import ch.jalu.configme.samples.beanannotations.BeanWithNameClash;
import ch.jalu.configme.samples.inheritance.Child;
import ch.jalu.configme.samples.inheritance.Middle;
import ch.jalu.typeresolver.TypeInfo;
import org.junit.jupiter.api.Test;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static ch.jalu.configme.TestUtils.transform;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
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
        Collection<BeanPropertyDescription> descriptions = factory.getAllProperties(SampleBean.class);

        // then
        assertThat(descriptions, hasSize(2));

        BeanPropertyDescription sizeProperty = getDescription("size", descriptions);
        assertThat(sizeProperty.getTypeInformation(), equalTo(new TypeInfo(int.class)));
        assertThat(sizeProperty.getComments().getComments(), contains("Size of this entry (cm)"));

        BeanPropertyDescription nameProperty = getDescription("name", descriptions);
        assertThat(nameProperty.getTypeInformation(), equalTo(new TypeInfo(String.class)));
        assertThat(nameProperty.getComments(), sameInstance(BeanPropertyComments.EMPTY));
    }

    @Test
    void shouldReturnEmptyListForNonBeanClass() {
        // given / when / then
        assertThat(factory.getAllProperties(List.class), empty());
    }

    @Test
    void shouldHandleBooleanMethodsAndMatchWithFields() {
        // given / when
        List<BeanPropertyDescription> properties = new ArrayList<>(factory.getAllProperties(BooleanTestBean.class));

        // then
        assertThat(properties, hasSize(4));
        assertThat(transform(properties, BeanPropertyDescription::getName),
            containsInAnyOrder("active", "isField", "empty", "isNotMatched"));

        // First two elements can be mapped to fields, so check their order. For the two unknown ones, we don't make any guarantees
        assertThat(properties.get(0).getName(), equalTo("active"));
        assertThat(properties.get(1).getName(), equalTo("isField"));
    }

    @Test
    void shouldNotConsiderTransientFields() {
        // given / when
        Collection<BeanPropertyDescription> properties = factory.getAllProperties(BeanWithTransientFields.class);

        // then
        assertThat(properties, hasSize(2));
        assertThat(transform(properties, BeanPropertyDescription::getName), contains("name", "mandatory"));
    }

    @Test
    void shouldBeAwareOfInheritanceAndRespectOrder() {
        // given / when
        Collection<BeanPropertyDescription> properties = factory.getAllProperties(Middle.class);

        // then
        assertThat(properties, hasSize(3));
        assertThat(transform(properties, BeanPropertyDescription::getName), contains("id", "name", "ratio"));
    }

    @Test
    void shouldLetChildFieldsOverrideParentFields() {
        // given / when
        Collection<BeanPropertyDescription> properties = factory.getAllProperties(Child.class);

        // then
        assertThat(properties, hasSize(5));
        assertThat(transform(properties, BeanPropertyDescription::getName),
            contains("id", "temporary", "name", "ratio", "importance"));
    }

    @Test
    void shouldUseExportName() {
        // given / when
        Collection<BeanPropertyDescription> properties = factory.getAllProperties(AnnotatedEntry.class);

        // then
        assertThat(properties, hasSize(2));
        assertThat(transform(properties, BeanPropertyDescription::getName),
            contains("id", "has-id"));
    }

    @Test
    void shouldThrowForMultiplePropertiesWithSameName() {
        // given / when
        ConfigMeMapperException ex = assertThrows(ConfigMeMapperException.class,
            () -> factory.getAllProperties(BeanWithNameClash.class));

        // then
        assertThat(ex.getMessage(),
            equalTo("class ch.jalu.configme.samples.beanannotations.BeanWithNameClash has multiple properties with name 'threshold'"));
    }

    @Test
    void shouldThrowForWhenExportNameIsNullForProperty() {
        // given / when
        ConfigMeMapperException ex = assertThrows(ConfigMeMapperException.class,
            () -> factory.getAllProperties(BeanWithEmptyName.class));

        // then
        assertThat(ex.getMessage(),
            equalTo("Custom name of Bean property '' with getter 'public java.lang.String ch.jalu.configme.samples.beanannotations.BeanWithEmptyName.getAuthor()' may not be empty"));
    }

    @Test
    void shouldReturnCommentsWithUuidIfNotRepeatable() {
        // given / when
        Collection<BeanPropertyDescription> sampleBeanProperties = factory.getAllProperties(SampleBean.class);
        Collection<BeanPropertyDescription> sampleBeanProperties2 = factory.getAllProperties(SampleBean.class);

        // then
        BeanPropertyComments sizeComments = getDescription("size", sampleBeanProperties).getComments();
        assertThat(sizeComments.getComments(), contains("Size of this entry (cm)"));
        assertThat(sizeComments.getUuid(), notNullValue());

        // Actually ensure that we have the same UUID if we fetch properties for the same class again
        // -> there's no point in the UUID otherwise!
        BeanPropertyComments sizeComments2 = getDescription("size", sampleBeanProperties2).getComments();
        assertThat(sizeComments2.getUuid(), equalTo(sizeComments.getUuid()));
    }

    @Test
    void shouldReturnCommentsWithoutUuid() {
        // given / when
        Collection<BeanPropertyDescription> execDetailsProperties = factory.getAllProperties(ExecutionDetails.class);

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
        List<BeanPropertyDescription> properties = new ArrayList<>(factory.getAllProperties(BeanWithExportName.class));

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
        List<BeanPropertyDescription> properties = new ArrayList<>(factory.getAllProperties(BeanWithExportNameExtension.class));

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

    private static BeanPropertyDescription getDescription(String name,
                                                          Collection<BeanPropertyDescription> descriptions) {
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
        private long longField; // static "getter" method
        private UUID uuid = UUID.randomUUID(); // no setter

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public UUID getUuid() {
            return uuid;
        }

        public static long getLongField() {
            // Method with normal getter name is static!
            return 0;
        }

        public void setLongField(long longField) {
            this.longField = longField;
        }
    }

    private static final class BooleanTestBean {
        private boolean isEmpty;
        private Boolean isReference;
        private boolean active;
        private String isString;
        private boolean isField;
        private boolean notMatched;

        public boolean isEmpty() {
            return isEmpty;
        }

        public void setEmpty(boolean empty) {
            isEmpty = empty;
        }

        public Boolean isReference() { // "is" getter only supported for primitive boolean
            return isReference;
        }

        public void setReference(Boolean isReference) {
            this.isReference = isReference;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public String isString() { // "is" only supported for boolean
            return isString;
        }

        public void setString(String isString) {
            this.isString = isString;
        }

        public boolean getIsField() {
            return isField;
        }

        public void setIsField(boolean field) {
            this.isField = field;
        }

        // -----------------
        // notMatched: creates a valid property "isNotMatched" picked up by the introspector,
        // but we should not match this to the field `notMatched`.
        // -----------------
        public boolean getIsNotMatched() {
            return notMatched;
        }

        public void setIsNotMatched(boolean notMatched) {
            this.notMatched = notMatched;
        }
    }

    private static final class BeanWithTransientFields {
        private String name;
        private transient long tempId;
        private transient boolean isSaved;
        private boolean isMandatory;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getTempId() {
            return tempId;
        }

        @Transient
        public void setTempId(long tempId) {
            this.tempId = tempId;
        }

        @Transient
        public boolean isSaved() {
            return isSaved;
        }

        public void setSaved(boolean saved) {
            isSaved = saved;
        }

        public boolean isMandatory() {
            return isMandatory;
        }

        public void setMandatory(boolean mandatory) {
            isMandatory = mandatory;
        }
    }
}
