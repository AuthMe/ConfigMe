package com.github.authme.configme.beanmapper;

import com.github.authme.configme.samples.inheritance.Child;
import com.github.authme.configme.samples.inheritance.Middle;
import org.junit.Test;

import java.beans.Transient;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.github.authme.configme.TestUtils.transform;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link BeanDescriptionFactory}.
 */
public class BeanDescriptionFactoryTest {

    @Test
    public void shouldReturnWritableProperties() {
        // given
        BeanDescriptionFactory factory = new BeanDescriptionFactory();

        // when
        Collection<BeanPropertyDescription> descriptions = factory.collectWritableFields(SampleBean.class);

        // then
        assertThat(descriptions, hasSize(2));
        assertThat(getDescription("size", descriptions).getType(), equalTo(int.class));
        assertThat(getDescription("name", descriptions).getType(), equalTo(String.class));
    }

    @Test
    public void shouldReturnEmptyListForNonBeanClass() {
        // given
        BeanDescriptionFactory factory = new BeanDescriptionFactory();

        // when / then
        assertThat(factory.collectWritableFields(List.class), empty());
    }

    @Test
    public void shouldHandleBooleanMethodsAndMatchWithFields() {
        // given
        BeanDescriptionFactory factory = new BeanDescriptionFactory();

        // when
        Collection<BeanPropertyDescription> properties = factory.collectWritableFields(BooleanTestBean.class);

        // then
        assertThat(properties, hasSize(4));
        assertThat(transform(properties, BeanPropertyDescription::getName),
            containsInAnyOrder("empty", "active", "isField", "isNotMatched"));
    }

    @Test
    public void shouldNotConsiderTransientFields() {
        // given
        BeanDescriptionFactory factory = new BeanDescriptionFactory();

        // when
        Collection<BeanPropertyDescription> properties = factory.collectWritableFields(BeanWithTransientFields.class);

        // then
        assertThat(properties, hasSize(2));
        assertThat(transform(properties, BeanPropertyDescription::getName), containsInAnyOrder("name", "mandatory"));
    }

    @Test
    public void shouldBeAwareOfInheritance() {
        // given
        BeanDescriptionFactory factory = new BeanDescriptionFactory();

        // when
        Collection<BeanPropertyDescription> properties = factory.collectWritableFields(Middle.class);

        // then
        assertThat(properties, hasSize(3));
        assertThat(transform(properties, BeanPropertyDescription::getName), containsInAnyOrder("id", "ratio", "name"));
    }

    @Test
    public void shouldLetChildFieldsOverrideParentFields() {
        // given
        BeanDescriptionFactory factory = new BeanDescriptionFactory();

        // when
        Collection<BeanPropertyDescription> properties = factory.collectWritableFields(Child.class);

        // then
        assertThat(properties, hasSize(5));
        assertThat(transform(properties, BeanPropertyDescription::getName),
            containsInAnyOrder("temporary", "importance", "ratio", "name", "id"));
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
        private boolean isActive; // <-- should not match! First choice for isActive() / getActive() is active
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
