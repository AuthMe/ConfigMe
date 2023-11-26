package ch.jalu.configme.beanmapper.instantiation;

import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactory;
import ch.jalu.configme.beanmapper.propertydescription.BeanFieldPropertyDescription;
import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyComments;
import ch.jalu.configme.internal.record.RecordComponent;
import ch.jalu.configme.internal.record.RecordInspector;
import ch.jalu.configme.properties.StringProperty;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.BDDMockito.given;

/**
 * Test for {@link BeanInstantiationServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class BeanInstantiationServiceImplTest {

    private BeanInstantiationServiceImpl beanInstantiationService;

    @Mock
    private RecordInspector recordInspector;

    @Mock
    private BeanDescriptionFactory beanDescriptionFactory;

    @BeforeEach
    void createBeanInstantiationService() {
        this.beanInstantiationService = new BeanInstantiationServiceImpl(recordInspector, beanDescriptionFactory);
    }

    @Test
    void shouldProvideInstantiationForRecord() throws NoSuchFieldException {
        // given
        RecordComponent nameComponent = new RecordComponent("name", String.class, String.class);
        RecordComponent shoeSizeComponent = new RecordComponent("shoeSize", int.class, int.class);
        RecordComponent ageComponent = new RecordComponent("age", double.class, double.class);
        RecordComponent[] components = {nameComponent, shoeSizeComponent, ageComponent};
        given(recordInspector.getRecordComponents(FakeRecord.class))
            .willReturn(components);

        List<BeanFieldPropertyDescription> beanProperties = Arrays.asList(
            new BeanFieldPropertyDescription(FakeRecord.class.getDeclaredField("name"), null, BeanPropertyComments.EMPTY),
            new BeanFieldPropertyDescription(FakeRecord.class.getDeclaredField("shoeSize"), null, BeanPropertyComments.EMPTY),
            new BeanFieldPropertyDescription(FakeRecord.class.getDeclaredField("age"), null, BeanPropertyComments.EMPTY));
        given(beanDescriptionFactory.collectPropertiesForRecord(FakeRecord.class, components)).willReturn(beanProperties);

        // when
        Optional<BeanInstantiation> instantiation = beanInstantiationService.findInstantiation(FakeRecord.class);

        // then
        assertThat(instantiation.isPresent(), equalTo(true));
        assertThat(instantiation.get(), instanceOf(BeanRecordInstantiation.class));
        assertThat(instantiation.get().getProperties(), hasSize(3));

        Object bean = instantiation.get().create(Arrays.asList("Test", 39, 40.25), new ConvertErrorRecorder());
        assertThat(bean, notNullValue());
        assertThat(((FakeRecord) bean).name, equalTo("Test"));
        assertThat(((FakeRecord) bean).shoeSize, equalTo(39));
        assertThat(((FakeRecord) bean).age, equalTo(40.25));
    }

    @Test
    void shouldProvideInstantiationForZeroArgConstructorClass() throws NoSuchFieldException {
        // given
        given(recordInspector.getRecordComponents(SampleBean.class)).willReturn(null);
        List<BeanFieldPropertyDescription> beanProperties = Arrays.asList(
            new BeanFieldPropertyDescription(SampleBean.class.getDeclaredField("name"), null, BeanPropertyComments.EMPTY),
            new BeanFieldPropertyDescription(SampleBean.class.getDeclaredField("shoeSize"), null, BeanPropertyComments.EMPTY),
            new BeanFieldPropertyDescription(SampleBean.class.getDeclaredField("age"), null, BeanPropertyComments.EMPTY));
        given(beanDescriptionFactory.collectProperties(SampleBean.class)).willReturn(beanProperties);

        // when
        Optional<BeanInstantiation> instantiation = beanInstantiationService.findInstantiation(SampleBean.class);

        // then
        assertThat(instantiation.isPresent(), equalTo(true));
        assertThat(instantiation.get(), instanceOf(BeanZeroArgConstructorInstantiation.class));
        assertThat(instantiation.get().getProperties(), hasSize(3));

        Object bean = instantiation.get().create(Arrays.asList("Test", 39, 40.25), new ConvertErrorRecorder());
        assertThat(bean, notNullValue());
        assertThat(((SampleBean) bean).name, equalTo("Test"));
        assertThat(((SampleBean) bean).shoeSize, equalTo(39));
        assertThat(((SampleBean) bean).age, equalTo(40.25));
    }

    @Test
    void shouldReturnEmptyOptionalForClassesWithNoInstantiationMethod() {
        // given / when
        Optional<BeanInstantiation> result1 = beanInstantiationService.findInstantiation(String.class);
        Optional<BeanInstantiation> result2 = beanInstantiationService.findInstantiation(TimeUnit.class);
        Optional<BeanInstantiation> result3 = beanInstantiationService.findInstantiation(StringProperty.class);

        // then
        assertThat(result1.isPresent(), equalTo(false));
        assertThat(result2.isPresent(), equalTo(false));
        assertThat(result3.isPresent(), equalTo(false));
    }

    @Test
    void shouldCacheInstantiations() throws NoSuchFieldException {
        // given
        // Set up record instantiation
        RecordComponent ageComponent = new RecordComponent("age", double.class, double.class);
        RecordComponent[] components = {ageComponent};
        given(recordInspector.getRecordComponents(FakeRecord.class)).willReturn(components);

        Field recordAgeField = FakeRecord.class.getDeclaredField("age");
        BeanPropertyComments recordAgeComments = new BeanPropertyComments(Arrays.asList("some", "comment"), UUID.randomUUID());
        BeanFieldPropertyDescription recordAgeProperty = new BeanFieldPropertyDescription(recordAgeField, null, recordAgeComments);
        given(beanDescriptionFactory.collectPropertiesForRecord(FakeRecord.class, components)).willReturn(Collections.singletonList(recordAgeProperty));

        // Set up zero-args constructor instantiation
        given(recordInspector.getRecordComponents(SampleBean.class)).willReturn(null);

        Field beanNameField = SampleBean.class.getDeclaredField("name");
        BeanPropertyComments beanNameComments = new BeanPropertyComments(Collections.singletonList("comment"), UUID.randomUUID());
        BeanFieldPropertyDescription beanNameProperty = new BeanFieldPropertyDescription(beanNameField, null, beanNameComments);
        given(beanDescriptionFactory.collectProperties(SampleBean.class)).willReturn(Collections.singletonList(beanNameProperty));

        // when
        Optional<BeanInstantiation> recordInstantiation1 = beanInstantiationService.findInstantiation(FakeRecord.class);
        Optional<BeanInstantiation> recordInstantiation2 = beanInstantiationService.findInstantiation(FakeRecord.class);
        Optional<BeanInstantiation> zeroArgsInstantiation1 = beanInstantiationService.findInstantiation(SampleBean.class);
        Optional<BeanInstantiation> zeroArgsInstantiation2 = beanInstantiationService.findInstantiation(SampleBean.class);

        // then
        assertThat(recordInstantiation1.get(), sameInstance(recordInstantiation2.get()));
        assertThat(recordInstantiation1.get().getProperties().get(0).getComments().getUuid(), equalTo(recordAgeComments.getUuid()));
        assertThat(recordInstantiation2.get().getProperties().get(0).getComments().getUuid(), equalTo(recordAgeComments.getUuid()));

        assertThat(zeroArgsInstantiation1.get(), sameInstance(zeroArgsInstantiation2.get()));
        assertThat(zeroArgsInstantiation1.get().getProperties().get(0).getComments().getUuid(), equalTo(beanNameComments.getUuid()));
        assertThat(zeroArgsInstantiation2.get().getProperties().get(0).getComments().getUuid(), equalTo(beanNameComments.getUuid()));

        assertThat(beanInstantiationService.getCachedInstantiationsByType().keySet(), containsInAnyOrder(FakeRecord.class, SampleBean.class));
    }

    @Test
    void shouldReturnFields() {
        // given / when
        RecordInspector returnedRecordInspector = beanInstantiationService.getRecordInspector();
        BeanDescriptionFactory returnedBeanDescriptionFactory = beanInstantiationService.getBeanDescriptionFactory();

        // then
        assertThat(returnedRecordInspector, sameInstance(recordInspector));
        assertThat(returnedBeanDescriptionFactory, sameInstance(beanDescriptionFactory));
    }

    private static final class FakeRecord { // #347: Change to record when the Java version allows it

        private final String name;
        private final int shoeSize;
        private final double age;

        public FakeRecord(String name, int shoeSize, double age) {
            this.name = name;
            this.shoeSize = shoeSize;
            this.age = age;
        }

        // Simpler constructor to keep some tests shorter
        public FakeRecord(double age) {
            this.age = age;
            this.shoeSize = -0;
            this.name = "-";
        }
    }

    private static final class SampleBean {

        private String name;
        private int shoeSize;
        private double age;

        private SampleBean() {
        }
    }
}
