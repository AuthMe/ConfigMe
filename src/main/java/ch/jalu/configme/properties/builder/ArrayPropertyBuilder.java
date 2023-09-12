package ch.jalu.configme.properties.builder;

import ch.jalu.configme.properties.ArrayProperty;
import ch.jalu.configme.properties.InlineArrayProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.types.ArrayPropertyType;
import ch.jalu.configme.properties.types.InlineArrayPropertyType;
import ch.jalu.configme.properties.types.PropertyType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

/**
 * Builder for properties of array type.
 *
 * @param <E> the type of elements in the property's array
 * @param <P> the property type the builder creates
 */
public class ArrayPropertyBuilder<E, P extends Property<E[]>> {

    private String path;
    private final List<E> defaultValue = new ArrayList<>();
    private final BiFunction<String, E[], P> createPropertyFunction;
    private final IntFunction<E[]> arrayProducer;

    /**
     * Constructor. Prefer using one of the {@link #arrayProducer} static methods.
     *
     * @param createPropertyFunction function taking path and default value and returning a property with these values
     * @param arrayProducer function which creates an array of the given capacity
     */
    public ArrayPropertyBuilder(@NotNull BiFunction<String, E[], P> createPropertyFunction,
                                @NotNull IntFunction<E[]> arrayProducer) {
        this.createPropertyFunction = createPropertyFunction;
        this.arrayProducer = arrayProducer;
    }

    /**
     * Creates a new array property builder. The given {@code entryType} defines how each array element will behave.
     *
     * @param entryType property type for the array's elements
     * @param arrayProducer function which creates an array of the given capacity
     * @param <E> the type of the array's elements
     * @return new array builder for the given entry type
     */
    public static <E> @NotNull ArrayPropertyBuilder<E, ArrayProperty<E>> arrayBuilder(
                                                                              @NotNull PropertyType<E> entryType,
                                                                              @NotNull IntFunction<E[]> arrayProducer) {
        return new ArrayPropertyBuilder<>(
            (path, defVal) -> new ArrayProperty<>(path, defVal, entryType, arrayProducer),
            arrayProducer);
    }

    /**
     * Creates a new array property builder using the given array property type.
     *
     * @param arrayType property type handling the behavior of the array
     * @param <E> the type of the array's elements
     * @return new array builder for the given array type
     */
    public static <E> @NotNull ArrayPropertyBuilder<E, ArrayProperty<E>> arrayBuilder(
                                                                              @NotNull ArrayPropertyType<E> arrayType) {
        return new ArrayPropertyBuilder<>(
            (path, defVal) -> new ArrayProperty<>(path, arrayType, defVal),
            arrayType.getArrayProducer());
    }

    /**
     * Creates a builder for inline array properties: properties which ConfigMe holds as array, but whose elements are
     * read and written as a single string (using a specific separator to join elements).
     *
     * @param inlineArrayType inline array type the property will use
     * @param <E> the type of the elements in the property's array
     * @return new builder to create an inline array property
     */
    public static <E> @NotNull ArrayPropertyBuilder<E, InlineArrayProperty<E>> inlineArrayBuilder(
                                                                  @NotNull InlineArrayPropertyType<E> inlineArrayType) {
        return new ArrayPropertyBuilder<>(
            (String path, E[] defVal) -> new InlineArrayProperty<>(path, defVal, inlineArrayType),
            inlineArrayType.getArrayProducer());
    }

    /**
     * Sets the path of the property to create.
     *
     * @param path the property path to set
     * @return this instance
     */
    public @NotNull ArrayPropertyBuilder<E, P> path(@NotNull String path) {
        this.path = path;
        return this;
    }

    /**
     * Registers the given entries as default value. This method throws an exception if a default value was already
     * set; use either this method once to define all array entries, or use {@link #addToDefaultValue} repeatedly to
     * define all entries individually. It is not recommended to mix both methods.
     *
     * @param entries the entries to use as the property's default value
     * @return this instance
     */
    @SafeVarargs
    public final @NotNull ArrayPropertyBuilder<E, P> defaultValue(@NotNull E @NotNull ... entries) {
        PropertyBuilderUtils.verifyDefaultValueIsEmpty(defaultValue.isEmpty());
        defaultValue.addAll(Arrays.asList(entries));
        return this;
    }

    /**
     * Adds the given entry to this builder to be part of the property's default value. Entries can be added
     * successively to the property's default value (which is an array) with this method.
     *
     * @param entry the entry to add to the default value array
     * @return this instance
     */
    public @NotNull ArrayPropertyBuilder<E, P> addToDefaultValue(@NotNull E entry) {
        defaultValue.add(entry);
        return this;
    }

    /**
     * @return array property with the path and default value provided to this builder
     */
    public @NotNull P build() {
        PropertyBuilderUtils.requireNonNullPath(path);
        E[] defaultValueArray = defaultValue.stream().toArray(arrayProducer);
        return createPropertyFunction.apply(path, defaultValueArray);
    }
}
