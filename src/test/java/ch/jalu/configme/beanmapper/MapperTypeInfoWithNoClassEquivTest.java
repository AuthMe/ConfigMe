package ch.jalu.configme.beanmapper;

import ch.jalu.configme.beanmapper.context.MappingContext;
import ch.jalu.configme.beanmapper.context.MappingContextImpl;
import ch.jalu.configme.beanmapper.leafvaluehandler.LeafValueHandlerImpl;
import ch.jalu.configme.beanmapper.leafvaluehandler.MapperLeafType;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactoryImpl;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.TypeInfo;
import ch.jalu.typeresolver.typeimpl.WildcardTypeImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration test for {@link MapperImpl} where the target type has no {@link Class} equivalent.
 * This test ensures that all standard leaf value handlers are called before
 * {@link MappingContext#getTargetTypeAsClassOrThrow()} is called, which would produce an exception.
 */
class MapperTypeInfoWithNoClassEquivTest {

    @Test
    void shouldNotThrowForTypeInfoWithNoClassEquivalentTooEarly() {
        // given
        TypeInfo targetType = new TypeInfo(WildcardTypeImpl.newWildcardExtends(Number.class));
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // validate assumption -> #getTargetTypeAsClassOrThrow throws an exception
        assertThrows(ConfigMeException.class,
            () -> MappingContextImpl.createRoot(targetType, errorRecorder).getTargetTypeAsClassOrThrow());

        ExtendsNumberLeafType extNumberLeafType = new ExtendsNumberLeafType();
        LeafValueHandlerImpl leafValueHandler = LeafValueHandlerImpl.builder()
            .addDefaults()
            .addType(extNumberLeafType)
            .build();
        MapperImpl mapper = new MapperImpl(new BeanDescriptionFactoryImpl(), leafValueHandler);

        // when
        Object result = mapper.convertToBean(3.2, targetType, errorRecorder);

        // then - no exception
        assertThat(result, equalTo(3));
        assertThat(extNumberLeafType.wasCalled, equalTo(true));
    }

    private static final class ExtendsNumberLeafType implements MapperLeafType {

        private boolean wasCalled = false;

        @Override
        public @Nullable Object convert(@Nullable Object value, @NotNull TypeInfo targetType,
                                        @NotNull ConvertErrorRecorder errorRecorder) {
            Type target = targetType.getType();
            if (target instanceof WildcardType
                    && Arrays.equals(((WildcardType) target).getUpperBounds(), new Type[]{ Number.class })
                    && value instanceof Number) {
                wasCalled = true;
                return ((Number) value).intValue();
            }
            return null;
        }

        @Override
        public @Nullable Object toExportValueIfApplicable(@Nullable Object value) {
            throw new UnsupportedOperationException();
        }
    }
}
