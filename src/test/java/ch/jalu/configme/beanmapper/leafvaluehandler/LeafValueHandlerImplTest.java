package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.beanmapper.context.ExportContext;
import ch.jalu.configme.beanmapper.context.ExportContextImpl;
import ch.jalu.configme.beanmapper.context.MappingContext;
import ch.jalu.configme.beanmapper.context.MappingContextImpl;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.types.BooleanType;
import ch.jalu.configme.properties.types.NumberType;
import ch.jalu.configme.properties.types.StringType;
import ch.jalu.typeresolver.typeimpl.WildcardTypeImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ch.jalu.typeresolver.TypeInfo.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link LeafValueHandlerImpl}.
 */
class LeafValueHandlerImplTest {

    @Test
    void shouldCreateInstanceWithProvidedHandlers() {
        // given
        MapperLeafType[] leafTypes = {
            mock(MapperLeafType.class),
            mock(MapperLeafType.class)
        };

        // when
        LeafValueHandlerImpl handler1 = new LeafValueHandlerImpl(leafTypes);
        LeafValueHandlerImpl handler2 = new LeafValueHandlerImpl(Arrays.asList(leafTypes));

        // then
        assertThat(handler1.getLeafTypes(), contains(leafTypes));
        assertThat(handler2.getLeafTypes(), contains(leafTypes));
    }

    @Test
    void shouldReturnDefaultLeafTypes() {
        // given / when
        List<MapperLeafType> leafTypes = LeafValueHandlerImpl.createDefaultLeafTypes();

        // then
        assertThat(leafTypes, hasSize(11));
        assertThat(leafTypes.get(0), sameInstance(BooleanType.BOOLEAN));
        assertThat(leafTypes.get(1), sameInstance(StringType.STRING));
        assertThat(leafTypes.get(2), sameInstance(NumberType.INTEGER));
        assertThat(leafTypes.get(3), sameInstance(NumberType.DOUBLE));
        assertThat(leafTypes.get(4), sameInstance(NumberType.LONG));
        assertThat(leafTypes.get(5), instanceOf(EnumLeafType.class));
        assertThat(leafTypes.get(6), sameInstance(NumberType.FLOAT));
        assertThat(leafTypes.get(7), sameInstance(NumberType.BYTE));
        assertThat(leafTypes.get(8), sameInstance(NumberType.SHORT));
        assertThat(leafTypes.get(9), sameInstance(NumberType.BIG_INTEGER));
        assertThat(leafTypes.get(10), sameInstance(NumberType.BIG_DECIMAL));
    }

    @Test
    void shouldReturnDefaultLeafTypesAsMutableList() {
        // given / when
        // Note that this can be typed to ArrayList to make it explicit that the list can be modified
        ArrayList<MapperLeafType> leafTypes = LeafValueHandlerImpl.createDefaultLeafTypes();

        // then - no exception
        leafTypes.add(NumberType.FLOAT);
        leafTypes.remove(StringType.STRING);
    }

    @Test
    void shouldCreateValueHandlerWithBuilder() {
        // given
        MapperLeafType leafType1 = mock(MapperLeafType.class);
        MapperLeafType leafType2 = mock(MapperLeafType.class);

        // when
        LeafValueHandlerImpl valueHandler = LeafValueHandlerImpl.builder()
            .addType(leafType1)
            .addDefaults()
            .addType(leafType2)
            .removeMatchingTypes(type -> type instanceof NumberType)
            .build();

        // then
        List<MapperLeafType> leafTypes = valueHandler.getLeafTypes();
        assertThat(leafTypes, hasSize(5));
        assertThat(leafTypes.get(0), sameInstance(leafType1));
        assertThat(leafTypes.get(1), sameInstance(BooleanType.BOOLEAN));
        assertThat(leafTypes.get(2), sameInstance(StringType.STRING));
        assertThat(leafTypes.get(3), instanceOf(EnumLeafType.class));
        assertThat(leafTypes.get(4), sameInstance(leafType2));
    }

    @Test
    void shouldCopyLeafTypesAfterCreation() {
        // given
        MapperLeafType leafType1 = mock(MapperLeafType.class);
        MapperLeafType leafType2 = mock(MapperLeafType.class);

        LeafValueHandlerImpl.Builder builder = LeafValueHandlerImpl.builder()
            .addTypes(leafType1, leafType2);
        LeafValueHandlerImpl valueHandler1 = builder.build();

        // when
        builder.removeType(leafType1);

        // then
        assertThat(valueHandler1.getLeafTypes(), contains(leafType1, leafType2)); // assert unchanged
    }

    @Test
    void shouldCreateValueHandlerWithBuilderAndAllowToRemoveTypes() {
        // given
        MapperLeafType leafType1 = mock(MapperLeafType.class);
        MapperLeafType leafType2 = mock(MapperLeafType.class);
        MapperLeafType leafType3 = mock(MapperLeafType.class);

        // when
        LeafValueHandlerImpl valueHandler = LeafValueHandlerImpl.builder()
            .addTypes(Arrays.asList(leafType1, leafType2, leafType3))
            .removeType(leafType2)
            .build();

        // then
        assertThat(valueHandler.getLeafTypes(), contains(leafType1, leafType3));
    }

    @Test
    void shouldConvertToLeafType() {
        // given
        Object object = 12;

        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        MappingContext stringContext = MappingContextImpl.createRoot(of(String.class), errorRecorder);
        MappingContext floatContext = MappingContextImpl.createRoot(of(float.class), errorRecorder);

        LeafValueHandlerImpl leafValueHandler = new LeafValueHandlerImpl(LeafValueHandlerImpl.createDefaultLeafTypes());

        // when / then
        assertThat(leafValueHandler.convert(object, stringContext), equalTo("12"));
        assertThat(leafValueHandler.convert(object, floatContext), equalTo(12.0f));
    }

    @Test
    void shouldNotConvertForUnsupportedTargetTypes() {
        // given
        Object object = "2020-02-13";

        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        MappingContext dateContext = MappingContextImpl.createRoot(of(LocalDate.class), errorRecorder);
        MappingContext wildcardContext = MappingContextImpl.createRoot(of(WildcardTypeImpl.newUnboundedWildcard()), errorRecorder);

        LeafValueHandlerImpl leafValueHandler = new LeafValueHandlerImpl(LeafValueHandlerImpl.createDefaultLeafTypes());

        // when / then
        assertThat(leafValueHandler.convert(object, dateContext), nullValue());
        assertThat(leafValueHandler.convert(object, wildcardContext), nullValue());
    }

    @Test
    void shouldConvertToExportValue() {
        // given
        LeafValueHandlerImpl leafValueHandler = new LeafValueHandlerImpl(LeafValueHandlerImpl.createDefaultLeafTypes());
        ExportContext exportContext = ExportContextImpl.createRoot();

        // when / then
        assertThat(leafValueHandler.toExportValue(TimeUnit.DAYS, exportContext), equalTo("DAYS"));
        assertThat(leafValueHandler.toExportValue(12.0f, exportContext), equalTo(12.0f));
    }

    @Test
    void shouldNotConvertUnsupportedValuesToExportValues() {
        // given
        LeafValueHandlerImpl leafValueHandler = new LeafValueHandlerImpl(LeafValueHandlerImpl.createDefaultLeafTypes());
        ExportContext exportContext = ExportContextImpl.createRoot();

        // when / then
        assertThat(leafValueHandler.toExportValue(LocalDate.now(), exportContext), nullValue());
        assertThat(leafValueHandler.toExportValue(new Object(), exportContext), nullValue());
    }

    @Test
    void shouldReturnNullForNullArg() {
        // given
        LeafValueHandlerImpl leafValueHandler = new LeafValueHandlerImpl(LeafValueHandlerImpl.createDefaultLeafTypes());

        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        MappingContext stringContext = MappingContextImpl.createRoot(of(String.class), errorRecorder);
        ExportContext exportContext = ExportContextImpl.createRoot();

        // when / then
        assertThat(leafValueHandler.convert(null, stringContext), nullValue());
        assertThat(leafValueHandler.toExportValue(null, exportContext), nullValue());
    }
}
