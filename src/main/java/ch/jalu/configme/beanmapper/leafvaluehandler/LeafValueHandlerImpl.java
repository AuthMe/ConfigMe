package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.beanmapper.context.ExportContext;
import ch.jalu.configme.beanmapper.context.MappingContext;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.types.BooleanType;
import ch.jalu.configme.properties.types.NumberType;
import ch.jalu.configme.properties.types.StringType;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default implementation of {@link LeafValueHandler}. A leaf value handler is used in
 * {@link ch.jalu.configme.beanmapper.MapperImpl} to provide "simple" values that the mapper does not have to
 * visit further. This implementation uses {@link MapperLeafType} instances, which perform the actual conversions.
 */
public class LeafValueHandlerImpl implements LeafValueHandler {

    private final List<MapperLeafType> leafTypes;

    /**
     * Constructor.
     *
     * @param leafTypes the leaf types to use
     */
    public LeafValueHandlerImpl(@NotNull List<@NotNull MapperLeafType> leafTypes) {
        this.leafTypes = leafTypes;
    }

    /**
     * Constructor.
     *
     * @param leafTypes the leaf types to use
     */
    public LeafValueHandlerImpl(@NotNull MapperLeafType @NotNull ... leafTypes) {
        this.leafTypes = Arrays.stream(leafTypes).collect(Collectors.toList());
    }

    /**
     * Returns a builder to create a leaf value handler.
     *
     * @return leaf value handler builder
     */
    public static @NotNull Builder builder() {
        return new Builder();
    }

    /**
     * Returns all leaf types used by the default implementation of this class. This method specifically returns
     * an ArrayList to guarantee that the list can be modified without the need of creating a copy beforehand.
     *
     * @return mutable list of leaf types that are used by default
     */
    public static @NotNull ArrayList<@NotNull MapperLeafType> createDefaultLeafTypes() {
        return Stream.of(
                BooleanType.BOOLEAN,
                StringType.STRING,
                NumberType.INTEGER,
                NumberType.DOUBLE,
                NumberType.LONG,
                new EnumLeafType(),
                NumberType.FLOAT,
                NumberType.BYTE,
                NumberType.SHORT,
                NumberType.BIG_INTEGER,
                NumberType.BIG_DECIMAL)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public @Nullable Object convert(@Nullable Object value, @NotNull MappingContext mappingContext) {
        TypeInfo targetType = mappingContext.getTargetType();
        ConvertErrorRecorder errorRecorder = mappingContext.getErrorRecorder();

        for (MapperLeafType leafType : leafTypes) {
            Object result = leafType.convert(value, targetType, errorRecorder);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public @Nullable Object toExportValue(@Nullable Object value, @NotNull ExportContext exportContext) {
        for (MapperLeafType leafType : leafTypes) {
            Object exportValue = leafType.toExportValueIfApplicable(value);
            if (exportValue != null) {
                return exportValue;
            }
        }
        return null;
    }

    /**
     * @return leaf types used by this instance
     */
    protected final @NotNull List<MapperLeafType> getLeafTypes() {
        return leafTypes;
    }

    /**
     * Builder for {@link LeafValueHandlerImpl}.
     */
    public static final class Builder {

        private final List<MapperLeafType> leafTypes = new ArrayList<>();

        /**
         * Adds the default leaf types from {@link #createDefaultLeafTypes()}.
         *
         * @return this builder
         */
        public @NotNull Builder addDefaults() {
            leafTypes.addAll(createDefaultLeafTypes());
            return this;
        }

        /**
         * Adds the given leaf type to this builder.
         *
         * @param typeToAdd the leaf type to add
         * @return this builder
         */
        public @NotNull Builder addType(@NotNull MapperLeafType typeToAdd) {
            leafTypes.add(typeToAdd);
            return this;
        }

        /**
         * Adds the given leaf types to this builder.
         *
         * @param typesToAdd the leaf types to add
         * @return this builder
         */
        public @NotNull Builder addTypes(@NotNull MapperLeafType... typesToAdd) {
            leafTypes.addAll(Arrays.asList(typesToAdd));
            return this;
        }

        /**
         * Adds the given leaf types to this builder.
         *
         * @param typesToAdd the leaf types to add
         * @return this builder
         */
        public @NotNull Builder addTypes(@NotNull Collection<MapperLeafType> typesToAdd) {
            leafTypes.addAll(typesToAdd);
            return this;
        }

        /**
         * Removes the given type from this builder's list of types. Useful if you
         * {@link #addDefaults() add the default types} but want to remove an entry.
         *
         * @param typeToRemove the type to remove
         * @return this builder
         */
        public @NotNull Builder removeType(@NotNull MapperLeafType typeToRemove) {
            leafTypes.remove(typeToRemove);
            return this;
        }

        /**
         * Removes all leaf types from this builder that match the given predicate.
         *
         * @param filterForRemoval predicate determining if a type should be removed
         * @return this builder
         */
        public @NotNull Builder removeMatchingTypes(@NotNull Predicate<MapperLeafType> filterForRemoval) {
            leafTypes.removeIf(filterForRemoval);
            return this;
        }

        /**
         * Creates a leaf value handler instance with all leaf types that were registered to this builder.
         *
         * @return new leaf value handler instance with all leaf types
         */
        public @NotNull LeafValueHandlerImpl build() {
            return new LeafValueHandlerImpl(new ArrayList<>(leafTypes));
        }
    }
}
