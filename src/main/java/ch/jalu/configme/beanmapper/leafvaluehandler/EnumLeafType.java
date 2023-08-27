package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.EnumUtils;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles enum type conversions for the bean mapper.
 */
public class EnumLeafType implements MapperLeafType {

    @Override
    public @Nullable Object convert(@Nullable Object value, @NotNull TypeInfo targetType,
                                    @NotNull ConvertErrorRecorder errorRecorder) {
        if (value instanceof String) {
            return EnumUtils.asEnumClassIfPossible(targetType.toClass())
                .flatMap(clz -> EnumUtils.tryValueOfCaseInsensitive(clz, (String) value))
                .orElse(null);
        }
        return null;
    }

    @Override
    public @Nullable Object toExportValueIfApplicable(@Nullable Object value) {
        if (value instanceof Enum<?>) {
            return ((Enum<?>) value).name();
        }
        return null;
    }
}
