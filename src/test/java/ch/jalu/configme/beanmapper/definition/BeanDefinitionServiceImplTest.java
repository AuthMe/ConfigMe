package ch.jalu.configme.beanmapper.definition;

import ch.jalu.configme.beanmapper.definition.properties.BeanFieldPropertyDefinition;
import ch.jalu.configme.beanmapper.definition.properties.BeanPropertyComments;
import ch.jalu.configme.beanmapper.definition.properties.BeanPropertyDefinition;
import ch.jalu.configme.beanmapper.definition.properties.BeanPropertyExtractor;
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
 * Test for {@link BeanDefinitionServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class BeanDefinitionServiceImplTest {

    private BeanDefinitionServiceImpl beanDefinitionService;

    @Mock
    private RecordInspector recordInspector;

    @Mock
    private BeanPropertyExtractor beanPropertyExtractor;

    @BeforeEach
    void createBeanDefinitionService() {
        this.beanDefinitionService = new BeanDefinitionServiceImpl(recordInspector, beanPropertyExtractor);
    }

    @Test
    void shouldProvideBeanDefinitionForRecord() throws NoSuchFieldException {
        // given
        RecordComponent nameComponent = new RecordComponent("name", String.class, String.class);
        RecordComponent shoeSizeComponent = new RecordComponent("shoeSize", int.class, int.class);
        RecordComponent ageComponent = new RecordComponent("age", double.class, double.class);
        RecordComponent[] components = {nameComponent, shoeSizeComponent, ageComponent};
        given(recordInspector.getRecordComponents(FakeRecord.class))
            .willReturn(components);

        List<BeanPropertyDefinition> beanProperties = Arrays.asList(
            new BeanFieldPropertyDefinition(FakeRecord.class.getDeclaredField("name"), null, BeanPropertyComments.EMPTY),
            new BeanFieldPropertyDefinition(FakeRecord.class.getDeclaredField("shoeSize"), null, BeanPropertyComments.EMPTY),
            new BeanFieldPropertyDefinition(FakeRecord.class.getDeclaredField("age"), null, BeanPropertyComments.EMPTY));
        given(beanPropertyExtractor.collectPropertiesForRecord(FakeRecord.class, components)).willReturn(beanProperties);

        // when
        Optional<BeanDefinition> definition = beanDefinitionService.findDefinition(FakeRecord.class);

        // then
        assertThat(definition.isPresent(), equalTo(true));
        assertThat(definition.get(), instanceOf(RecordBeanDefinition.class));
        assertThat(definition.get().getProperties(), hasSize(3));

        Object bean = definition.get().create(Arrays.asList("Test", 39, 40.25), new ConvertErrorRecorder());
        assertThat(bean, notNullValue());
        assertThat(((FakeRecord) bean).name, equalTo("Test"));
        assertThat(((FakeRecord) bean).shoeSize, equalTo(39));
        assertThat(((FakeRecord) bean).age, equalTo(40.25));
    }

    @Test
    void shouldProvideDefinitionForZeroArgConstructorClass() throws NoSuchFieldException {
        // given
        given(recordInspector.getRecordComponents(SampleBean.class)).willReturn(null);
        List<BeanFieldPropertyDefinition> beanProperties = Arrays.asList(
            new BeanFieldPropertyDefinition(SampleBean.class.getDeclaredField("name"), null, BeanPropertyComments.EMPTY),
            new BeanFieldPropertyDefinition(SampleBean.class.getDeclaredField("shoeSize"), null, BeanPropertyComments.EMPTY),
            new BeanFieldPropertyDefinition(SampleBean.class.getDeclaredField("age"), null, BeanPropertyComments.EMPTY));
        given(beanPropertyExtractor.collectProperties(SampleBean.class)).willReturn(beanProperties);

        // when
        Optional<BeanDefinition> definition = beanDefinitionService.findDefinition(SampleBean.class);

        // then
        assertThat(definition.isPresent(), equalTo(true));
        assertThat(definition.get(), instanceOf(ZeroArgConstructorBeanDefinition.class));
        assertThat(definition.get().getProperties(), hasSize(3));

        Object bean = definition.get().create(Arrays.asList("Test", 39, 40.25), new ConvertErrorRecorder());
        assertThat(bean, notNullValue());
        assertThat(((SampleBean) bean).name, equalTo("Test"));
        assertThat(((SampleBean) bean).shoeSize, equalTo(39));
        assertThat(((SampleBean) bean).age, equalTo(40.25));
    }

    @Test
    void shouldReturnEmptyOptionalForClassesWithNoInstantiationMethod() {
        // given / when
        Optional<BeanDefinition> result1 = beanDefinitionService.findDefinition(String.class);
        Optional<BeanDefinition> result2 = beanDefinitionService.findDefinition(TimeUnit.class);
        Optional<BeanDefinition> result3 = beanDefinitionService.findDefinition(StringProperty.class);

        // then
        assertThat(result1.isPresent(), equalTo(false));
        assertThat(result2.isPresent(), equalTo(false));
        assertThat(result3.isPresent(), equalTo(false));
    }

    @Test
    void shouldCacheBeanDefinitions() throws NoSuchFieldException {
        // given
        // Set up record-based bean definition
        RecordComponent ageComponent = new RecordComponent("age", double.class, double.class);
        RecordComponent[] components = {ageComponent};
        given(recordInspector.getRecordComponents(FakeRecord.class)).willReturn(components);

        Field recordAgeField = FakeRecord.class.getDeclaredField("age");
        BeanPropertyComments recordAgeComments = new BeanPropertyComments(Arrays.asList("some", "comment"), UUID.randomUUID());
        BeanFieldPropertyDefinition recordAgeProperty = new BeanFieldPropertyDefinition(recordAgeField, null, recordAgeComments);
        given(beanPropertyExtractor.collectPropertiesForRecord(FakeRecord.class, components)).willReturn(Collections.singletonList(recordAgeProperty));

        // Set up zero-args constructor bean definition
        given(recordInspector.getRecordComponents(SampleBean.class)).willReturn(null);

        Field beanNameField = SampleBean.class.getDeclaredField("name");
        BeanPropertyComments beanNameComments = new BeanPropertyComments(Collections.singletonList("comment"), UUID.randomUUID());
        BeanFieldPropertyDefinition beanNameProperty = new BeanFieldPropertyDefinition(beanNameField, null, beanNameComments);
        given(beanPropertyExtractor.collectProperties(SampleBean.class)).willReturn(Collections.singletonList(beanNameProperty));

        // when
        Optional<BeanDefinition> recordDefinition1 = beanDefinitionService.findDefinition(FakeRecord.class);
        Optional<BeanDefinition> recordDefinition2 = beanDefinitionService.findDefinition(FakeRecord.class);
        Optional<BeanDefinition> zeroArgsDefinition1 = beanDefinitionService.findDefinition(SampleBean.class);
        Optional<BeanDefinition> zeroArgsDefinition2 = beanDefinitionService.findDefinition(SampleBean.class);

        // then
        assertThat(recordDefinition1.get(), sameInstance(recordDefinition2.get()));
        assertThat(recordDefinition1.get().getProperties().get(0).getComments().getUuid(), equalTo(recordAgeComments.getUuid()));
        assertThat(recordDefinition2.get().getProperties().get(0).getComments().getUuid(), equalTo(recordAgeComments.getUuid()));

        assertThat(zeroArgsDefinition1.get(), sameInstance(zeroArgsDefinition2.get()));
        assertThat(zeroArgsDefinition1.get().getProperties().get(0).getComments().getUuid(), equalTo(beanNameComments.getUuid()));
        assertThat(zeroArgsDefinition2.get().getProperties().get(0).getComments().getUuid(), equalTo(beanNameComments.getUuid()));

        assertThat(beanDefinitionService.getCachedDefinitionsByType().keySet(), containsInAnyOrder(FakeRecord.class, SampleBean.class));
    }

    @Test
    void shouldReturnFields() {
        // given / when
        RecordInspector returnedRecordInspector = beanDefinitionService.getRecordInspector();
        BeanPropertyExtractor returnedBeanPropertyExtractor = beanDefinitionService.getBeanPropertyExtractor();

        // then
        assertThat(returnedRecordInspector, sameInstance(recordInspector));
        assertThat(returnedBeanPropertyExtractor, sameInstance(beanPropertyExtractor));
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
