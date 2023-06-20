package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Set property of configurable type. The sets are immutable and preserve the order.
 *
 * @param <T> the set type
 */
public class SetProperty<T> extends BaseProperty<Set<T>> {

    private final PropertyType<T> type;

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param type the property type
     * @param defaultValue the values that make up the entries of the default set
     */
    @SafeVarargs
    public SetProperty(@NotNull String path, @NotNull PropertyType<T> type, @NotNull T @NotNull ... defaultValue) {
        this(path, type, newSet(defaultValue));
    }

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param type the property type
     * @param defaultValue the default value of the property
     */
    public SetProperty(@NotNull String path, @NotNull PropertyType<T> type, @NotNull Set<T> defaultValue) {
        super(path, Collections.unmodifiableSet(defaultValue));
        Objects.requireNonNull(type, "type");
        this.type = type;
    }

    @Override
    protected Set<T> getFromReader(@NotNull PropertyReader reader, @NotNull ConvertErrorRecorder errorRecorder) {
        List<?> list = reader.getList(getPath());

        if (list != null) {
            return list.stream()
                .map(elem -> type.convert(elem, errorRecorder))
                .filter(Objects::nonNull)
                .collect(setCollector());
        }
        return null;
    }

    @Override
    public @NotNull Object toExportValue(@NotNull Set<T> value) {
        return value.stream()
            .map(type::toExportValue)
            .collect(Collectors.toList());
    }

    protected @NotNull Collector<T, ?, Set<T>> setCollector() {
        return Collectors.collectingAndThen(Collectors.toCollection(LinkedHashSet::new), Collections::unmodifiableSet);
    }

    private static <E> @NotNull Set<E> newSet(E @NotNull [] array) {
        return Arrays.stream(array).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
