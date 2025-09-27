package ch.jalu.configme.beanmapper;

import ch.jalu.configme.beanmapper.context.MappingContext;
import ch.jalu.configme.beanmapper.context.MappingContextImpl;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorderImpl;
import ch.jalu.typeresolver.TypeInfo;
import ch.jalu.typeresolver.reference.TypeReference;
import ch.jalu.typeresolver.typeimpl.WildcardTypeImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static ch.jalu.typeresolver.TypeInfo.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link MappingContextImpl}.
 */
@ExtendWith(MockitoExtension.class)
class MappingContextImplTest {

    @Test
    void shouldCreateProperPath() {
        // given
        TypeInfo typeInformation = of(Integer.class);
        MappingContext parent1 = MappingContextImpl.createRoot(typeInformation, new ConvertErrorRecorderImpl());
        MappingContext parent2 = MappingContextImpl.createRoot(typeInformation, new ConvertErrorRecorderImpl()).createChild("foo", typeInformation);

        // when
        MappingContext child1 = parent1.createChild("bar", typeInformation);
        MappingContext child2 = parent2.createChild("bar", typeInformation);

        // then
        assertThat(parent1.getBeanPath(), equalTo(""));
        assertThat(child1.getBeanPath(), equalTo("bar"));
        assertThat(child2.getBeanPath(), equalTo("foo.bar"));

        assertThat(parent1.toString(), equalTo("MappingContextImpl[Bean path: '', type: 'class java.lang.Integer']"));
        assertThat(child1.toString(), equalTo("MappingContextImpl[Bean path: 'bar', type: 'class java.lang.Integer']"));
        assertThat(child2.toString(), equalTo("MappingContextImpl[Bean path: 'foo.bar', type: 'class java.lang.Integer']"));
    }

    @Test
    void shouldCreateDescription() {
        // given
        MappingContext context = MappingContextImpl.createRoot(of(String.class), new ConvertErrorRecorderImpl())
            .createChild("oh.em.gee", of(ArrayList.class));

        // when
        String description = context.createDescription();

        // then
        assertThat(description, equalTo("Bean path: 'oh.em.gee', type: '" + ArrayList.class + "'"));
    }

    @Test
    void shouldReturnTargetTypeAsClass() {
        // given
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        MappingContextImpl context1 = MappingContextImpl.createRoot(of(String.class), errorRecorder);
        MappingContextImpl context2 = MappingContextImpl.createRoot(new TypeReference<List<String>>() { }, errorRecorder);

        // when / then
        assertThat(context1.getTargetTypeAsClassOrThrow(), equalTo(String.class));
        assertThat(context2.getTargetTypeAsClassOrThrow(), equalTo(List.class));
    }

    @Test
    void shouldThrowIfTargetTypeCannotBeConvertedToClass() {
        // given
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        MappingContextImpl context = MappingContextImpl.createRoot(of(WildcardTypeImpl.newWildcardExtends(Integer.class)), errorRecorder);

        // when
        ConfigMeMapperException ex = assertThrows(ConfigMeMapperException.class, context::getTargetTypeAsClassOrThrow);

        // then
        assertThat(ex.getMessage(), equalTo("The target type cannot be converted to a class, for mapping of: [Bean path: '', type: '? extends java.lang.Integer']"));
    }

    @Test
    void shouldReturnTargetTypeArgumentAsClass() {
        // given
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        MappingContextImpl context1 = MappingContextImpl.createRoot(new TypeReference<Map<String, TimeUnit>>() { }, errorRecorder);
        MappingContextImpl context2 = MappingContextImpl.createRoot(new TypeReference<List<Optional<Integer>>>() { }, errorRecorder);

        // when / then
        assertThat(context1.getTargetTypeArgumentOrThrow(1), equalTo(of(TimeUnit.class)));
        assertThat(context2.getTargetTypeArgumentOrThrow(0), equalTo(new TypeReference<Optional<Integer>>() { }));
    }

    @Test
    void shouldThrowIfTargetTypeArgumentCannotBeConvertedToClass() {
        // given
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        MappingContextImpl context1 = MappingContextImpl.createRoot(of(String.class), errorRecorder);
        MappingContextImpl context2 = MappingContextImpl.createRoot(new TypeReference<List<?>>() { }, errorRecorder);

        // when
        ConfigMeMapperException ex1 = assertThrows(ConfigMeMapperException.class,
            () -> context1.getTargetTypeArgumentOrThrow(0));
        ConfigMeMapperException ex2 = assertThrows(ConfigMeMapperException.class,
            () -> context2.getTargetTypeArgumentOrThrow(0));

        // then
        assertThat(ex1.getMessage(), equalTo("The type argument at index 0 is not well defined, for mapping of: [Bean path: '', type: 'class java.lang.String']"));
        assertThat(ex2.getMessage(), equalTo("The type argument at index 0 is not well defined, for mapping of: [Bean path: '', type: 'java.util.List<?>']"));
    }

    @Test
    void shouldConcatenatePathsAppropriately() {
        // given
        TypeInfo typeInfo = of(String.class);
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();

        // when
        MappingContext context = MappingContextImpl.createRoot(typeInfo, errorRecorder)
            .createChild("db", typeInfo)
            .createChild("driver", typeInfo)
            .createChild("[3]", typeInfo)
            .createChild("version", typeInfo)
            .createChild("$opt", typeInfo)
            .createChild("release", typeInfo);

        // then
        assertThat(context.getBeanPath(), equalTo("db.driver[3].version$opt.release"));
    }
}
