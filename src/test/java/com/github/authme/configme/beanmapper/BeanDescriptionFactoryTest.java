package com.github.authme.configme.beanmapper;

import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

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

        private static int staticField; // static
        private final Object finalObject; // final
        private String name;
        private int size;
        private long longField; // static "getter" method
        private UUID uuid = UUID.randomUUID(); // no setter

        public SampleBean() {
            finalObject = null;
        }

        public int getStaticField() {
            return staticField;
        }

        public void setStaticField(int staticField) {
            SampleBean.staticField = staticField;
        }

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

        public Object getFinalObject() {
            return finalObject;
        }

        public void setFinalObject(Object finalObject) {
            // noop
        }

        public static long getLongField() {
            // Method with normal getter name is static!
            return 0;
        }

        public void setLongField(long longField) {
            this.longField = longField;
        }
    }
}
