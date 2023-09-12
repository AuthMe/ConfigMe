package ch.jalu.configme.utils;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.PropertyResource;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Validates various characteristics of the property implementations of SettingsHolder classes for consistency.
 * Finds potential technical or quality issues with a project's property declarations. This class is intended to
 * be used in a unit test.
 * <p>
 * This class's methods can be overridden for custom behavior. Depending on your needs, you can call the main
 * {@code validate} method and override single validation methods you want to customize or disable, or call the
 * individual validation methods separately.
 */
public class SettingsHolderClassValidator {

    private static final int DEFAULT_MAX_COMMENTS_LENGTH = 90;

    // ---- Main validation methods (with default settings)

    /**
     * Runs all validations of this class with the given settings holder classes.
     * More details at {@link #validate(Iterable)}.
     *
     * @param settingHolders settings holder classes that make up the configuration data of the project
     */
    @SafeVarargs
    public final void validate(@NotNull Class<? extends SettingsHolder>... settingHolders) {
        validate(Arrays.asList(settingHolders));
    }

    /**
     * Runs all validations of this class with the given settings holder classes. Some of the validations
     * are not needed from a technical point of view and may be undesired in your project. They can all be
     * run individually and can be customized by supplying different method parameters or by overriding methods
     * in this class.
     *
     * @param settingHolders settings holder classes that make up the configuration data of the project
     */
    public void validate(@NotNull Iterable<Class<? extends SettingsHolder>> settingHolders) {
        validateAllPropertiesAreConstants(settingHolders);
        validateSettingsHolderClassesFinal(settingHolders);
        validateClassesHaveHiddenNoArgConstructor(settingHolders);

        // Note: creating the ConfigurationData with the default builder validates that
        // no properties have overlapping paths
        ConfigurationData configurationData = createConfigurationData(settingHolders);
        validateHasCommentOnEveryProperty(configurationData, null);
        validateCommentLengthsAreWithinBounds(configurationData, null, DEFAULT_MAX_COMMENTS_LENGTH);
        validateHasAllEnumEntriesInComment(configurationData, null);
    }

    /**
     * Validates that the migration service does not declare that a migration is required for the given
     * configuration data, which gets saved to the provided resource beforehand. This is intended to
     * validate that the default values of a configuration do not trigger a migration:
     * <ul>
     *   <li>the configuration data should only have default values</li>
     *   <li>the resource should only be for this method and thus use a temporary file</li>
     * </ul>
     *
     * @param configurationData the configuration data (with default values, i.e. as created from the properties)
     * @param resource property resource to save to and read from (temporary medium for testing)
     * @param migrationService the migration service to check
     */
    public void validateConfigurationDataValidForMigrationService(@NotNull ConfigurationData configurationData,
                                                                  @NotNull PropertyResource resource,
                                                                  @NotNull MigrationService migrationService) {
        resource.exportProperties(configurationData);

        PropertyReader reader = resource.createReader();
        if (migrationService.checkAndMigrate(reader, configurationData) == MigrationService.MIGRATION_REQUIRED) {
            throw new IllegalStateException("Migration service unexpectedly returned that a migration is required");
        }
    }


    // ---- Individual validations

    /**
     * Throws an exception if any Property field of the given classes is not public, static, or final.
     *
     * @param settingHolders the classes to check
     */
    public void validateAllPropertiesAreConstants(@NotNull Iterable<Class<? extends SettingsHolder>> settingHolders) {
        List<String> invalidFields = new ArrayList<>();

        for (Class<? extends SettingsHolder> clazz : settingHolders) {
            List<String> invalidFieldsForClazz = getAllFields(clazz)
                .filter(field -> Property.class.isAssignableFrom(field.getType()))
                .filter(field -> !isValidConstantField(field))
                .map(field -> field.getDeclaringClass().getSimpleName() + "#" + field.getName())
                .collect(Collectors.toList());
            invalidFields.addAll(invalidFieldsForClazz);
        }

        if (!invalidFields.isEmpty()) {
            throw new IllegalStateException("The following fields were found not to be public static final:\n- "
                + String.join("\n- ", invalidFields));
        }
    }

    /**
     * Throws an exception if any of the provided settings holder classes is not final.
     *
     * @param settingHolders the classes to check
     */
    public void validateSettingsHolderClassesFinal(@NotNull Iterable<Class<? extends SettingsHolder>> settingHolders) {
        List<String> invalidClasses = new ArrayList<>();

        for (Class<? extends SettingsHolder> clazz : settingHolders) {
            if (!Modifier.isFinal(clazz.getModifiers())) {
                invalidClasses.add(clazz.getCanonicalName());
            }
        }

        if (!invalidClasses.isEmpty()) {
            throw new IllegalStateException("The following classes are not final:\n- "
                + String.join("\n- ", invalidClasses));
        }
    }

    /**
     * Throws an exception if any of the provided setting holder classes does not have a single private
     * no-args constructor.
     *
     * @param settingHolders the classes to check
     */
    public void validateClassesHaveHiddenNoArgConstructor(
                                                    @NotNull Iterable<Class<? extends SettingsHolder>> settingHolders) {
        List<String> invalidClasses = new ArrayList<>();

        for (Class<? extends SettingsHolder> clazz : settingHolders) {
            if (!hasValidConstructorSetup(clazz)) {
                invalidClasses.add(clazz.getCanonicalName());
            }
        }

        if (!invalidClasses.isEmpty()) {
            throw new IllegalStateException("The following classes do not have a single no-args private constructor:"
                + "\n- " + String.join("\n- ", invalidClasses));
        }
    }

    /**
     * Throws an exception if there isn't a non-empty comment for every property in the configuration data.
     *
     * @param configurationData the configuration data to check
     * @param propertyFilter predicate determining which properties are checked (if null, are properties are checked)
     */
    public void validateHasCommentOnEveryProperty(@NotNull ConfigurationData configurationData,
                                                  @Nullable Predicate<Property<?>> propertyFilter) {
        Predicate<Property<?>> filter = propertyFilter == null ? (p -> true) : propertyFilter;
        List<String> invalidProperties = new ArrayList<>();

        Map<String, List<String>> comments = configurationData.getAllComments();
        for (Property<?> property : configurationData.getProperties()) {
            if (filter.test(property)) {
                List<String> commentEntry = comments.get(property.getPath());
                if (!hasNonEmptyComment(commentEntry)) {
                    invalidProperties.add(property.toString());
                }
            }
        }

        if (!invalidProperties.isEmpty()) {
            throw new IllegalStateException("The following properties do not have a comment:\n- "
                + String.join("\n- ", invalidProperties));
        }
    }

    /**
     * Throws an exception if any comment line in the configuration data has a length is not between the given
     * minLength and maxLength, inclusive: {@code minLength <= length <= maxLength}. Either argument is nullable
     * if no min or max, respectively, is desired, but both arguments may not be null.
     *
     * @param configurationData the configuration data with the comments to check
     * @param minLength the number of characters each comment line must at least have (null to disable check)
     * @param maxLength the number of characters each comment may not surpass (null to disable check)
     */
    public void validateCommentLengthsAreWithinBounds(@NotNull ConfigurationData configurationData,
                                                      @Nullable Integer minLength, @Nullable Integer maxLength) {
        Predicate<String> hasInvalidLengthPredicate = createValidLengthPredicate(minLength, maxLength).negate();

        List<String> invalidPaths = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : configurationData.getAllComments().entrySet()) {
            boolean hasInvalidLength = entry.getValue().stream().anyMatch(hasInvalidLengthPredicate);
            if (hasInvalidLength) {
                invalidPaths.add("Path '" + entry.getKey() + "'");
            }
        }

        if (!invalidPaths.isEmpty()) {
            String bound = minLength == null ? "" : "min length of " + minLength;
            if (maxLength != null) {
                bound += (bound.isEmpty() ? "" : ", ") + "max length of " + maxLength;
            }
            throw new IllegalStateException("The comments for the following paths are not within the bounds: " + bound
                + " characters:\n- " + String.join("\n- ", invalidPaths));
        }
    }

    /**
     * Throws an exception if the comments of an enum property do not list all entries of the enum type.
     *
     * @param configurationData the configuration data whose properties and comments should be checked
     * @param propertyFilter predicate determining which properties are checked (if null, are properties are checked)
     */
    public void validateHasAllEnumEntriesInComment(@NotNull ConfigurationData configurationData,
                                                   @Nullable Predicate<Property<?>> propertyFilter) {
        List<String> commentErrors = new ArrayList<>();

        for (Property<?> property : configurationData.getProperties()) {
            if (propertyFilter != null && !propertyFilter.test(property)) {
                continue;
            }

            Class<? extends Enum<?>> enumType = getEnumTypeOfProperty(property);
            if (enumType != null) {
                List<String> expectedEnums = gatherExpectedEnumNames(enumType);
                String comments = String.join("\n", configurationData.getCommentsForSection(property.getPath()));
                List<String> missingEnumEntries = expectedEnums.stream()
                    .filter(e -> !comments.contains(e))
                    .collect(Collectors.toList());
                if (!missingEnumEntries.isEmpty()) {
                    commentErrors.add("For " + property + ": missing " + String.join(", ", missingEnumEntries));
                }
            }
        }

        if (!commentErrors.isEmpty()) {
            throw new IllegalStateException("The following enum properties do not list all enum values:\n- "
                + String.join("\n- ", commentErrors));
        }
    }


    // ---- Validation helpers

    protected boolean isValidConstantField(@NotNull Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isPublic(modifiers)
            && Modifier.isStatic(modifiers)
            && Modifier.isFinal(modifiers);
    }

    protected @NotNull ConfigurationData createConfigurationData(
                                                           @NotNull Iterable<Class<? extends SettingsHolder>> classes) {
        return ConfigurationDataBuilder.createConfiguration(classes);
    }

    protected boolean hasNonEmptyComment(@Nullable List<String> comments) {
        return comments != null
            && comments.stream().anyMatch(line -> !line.trim().isEmpty());
    }

    /**
     * Creates a predicate based on input min length and max length that evaluates successfully for a string if
     * its length is within the given bounds: {@code minLength <= length <= maxLength}. Either length may be null,
     * but an exception is thrown if both are null.
     *
     * @param minLength the min length (nullable)
     * @param maxLength the max length (nullable)
     * @return predicate based on the supplied length parameters
     */
    protected @NotNull Predicate<String> createValidLengthPredicate(@Nullable Integer minLength,
                                                                    @Nullable Integer maxLength) {
        if (minLength == null && maxLength == null) {
            throw new IllegalArgumentException("min length or max length must be not null");
        }
        return string -> (minLength == null || minLength <= string.length())
                      && (maxLength == null || maxLength >= string.length());
    }

    /**
     * Returns the type of the given property if it is an enum, otherwise null.
     *
     * @param property the property to process
     * @return the enum type it wraps, or null if not applicable
     */
    @SuppressWarnings("unchecked")
    protected @Nullable Class<? extends Enum<?>> getEnumTypeOfProperty(@NotNull Property<?> property) {
        Class<?> defaultValueType = property.getDefaultValue().getClass();
        if (defaultValueType.isAnonymousClass()) {
            // If an enum entry implements methods, it is an anonymous class -> we're interested in the enclosing class
            defaultValueType = defaultValueType.getEnclosingClass();
        }
        return defaultValueType.isEnum() ? (Class<? extends Enum<?>>) defaultValueType : null;
    }

    protected @NotNull List<String> gatherExpectedEnumNames(@NotNull Class<? extends Enum<?>> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.toList());
    }

    protected boolean hasValidConstructorSetup(@NotNull Class<? extends SettingsHolder> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        return constructors.length == 1
            && constructors[0].getParameterCount() == 0
            && Modifier.isPrivate(constructors[0].getModifiers());
    }

    /**
     * Returns all fields of the class, including all fields of parent classes, recursively.
     *
     * @param clazz the class whose fields should be retrieved
     * @return all fields of the class, including its parents
     */
    protected @NotNull Stream<Field> getAllFields(@NotNull Class<?> clazz) {
        // Shortcut: Class does not inherit from another class, so just go through its fields
        if (Object.class.equals(clazz.getSuperclass())) {
            return Arrays.stream(clazz.getDeclaredFields());
        }

        // Collect all classes incl. parents
        Class<?> currentClass = clazz;
        List<Class<?>> classes = new ArrayList<>();
        while (currentClass != null && !currentClass.equals(Object.class)) {
            classes.add(currentClass);
            currentClass = currentClass.getSuperclass();
        }

        // Go through all fields incl. parents
        return classes.stream().flatMap(clz -> Arrays.stream(clz.getDeclaredFields()));
    }
}
