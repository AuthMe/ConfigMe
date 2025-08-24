package ch.jalu.configme.beanmapper.definition.properties;

import ch.jalu.configme.Comment;
import ch.jalu.configme.beanmapper.ConfigMeMapperException;
import ch.jalu.configme.beanmapper.ExportName;
import ch.jalu.configme.beanmapper.IgnoreInMapping;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.internal.record.RecordComponent;
import ch.jalu.typeresolver.reflect.FieldUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Default implementation of {@link BeanPropertyExtractor}: creates all {@link BeanPropertyDefinition}
 * objects for a given class. You can override the default name of a property with &#64;{@link ExportName}.
 * <p>
 * See <a href="https://github.com/AuthMe/ConfigMe/wiki/Bean-properties">Bean properties</a> on the ConfigMe wiki
 * for more details about how properties are extracted.
 */
public class BeanPropertyExtractorImpl implements BeanPropertyExtractor {

    @Override
    public @NotNull List<BeanPropertyDefinition> collectPropertiesForRecord(@NotNull Class<?> clazz,
                                                                            RecordComponent @NotNull [] components) {
        Map<String, Field> instanceFieldsByName = FieldUtils.getAllFields(clazz)
            .filter(FieldUtils::isRegularInstanceField)
            .collect(FieldUtils.collectByName(false));

        List<BeanPropertyDefinition> properties = new ArrayList<>(components.length);
        for (RecordComponent component : components) {
            Field field = instanceFieldsByName.get(component.getName());
            validateFieldForRecord(clazz, component, field);
            BeanFieldPropertyDefinition property = createDefinition(field);
            properties.add(property);
        }

        validateProperties(clazz, properties);
        return properties;
    }

    /**
     * Constructs property definitions based on the given class's instance fields (i.e. non-static), including the
     * instance fields of all parent classes. If a non-static field of a bean class has the same name as a field of a
     * parent type, the parent field is ignored.
     * <p>
     * Instance fields can be ignored by declaring them as {@code transient} or by annotating them with
     * &#64;{@link IgnoreInMapping}.
     *
     * @param clazz the bean type
     * @return the properties of the given bean type
     */
    @Override
    public @NotNull List<BeanFieldPropertyDefinition> collectProperties(@NotNull Class<?> clazz) {
        @SuppressWarnings("checkstyle:IllegalType") // LinkedHashMap indicates the values are ordered (important here)
        LinkedHashMap<String, Field> instanceFieldsByName = FieldUtils.getAllFields(clazz)
            .filter(FieldUtils::isRegularInstanceField)
            .collect(FieldUtils.collectByName(false));

        List<BeanFieldPropertyDefinition> properties = new ArrayList<>();
        for (Field field : instanceFieldsByName.values()) {
            if (!isFieldIgnored(field)) {
                validateFieldForBean(clazz, field);
                BeanFieldPropertyDefinition property = createDefinition(field);
                properties.add(property);
            }
        }

        validateProperties(clazz, properties);
        return properties;
    }

    /**
     * Validates the component and its associated field for a class that is a record.
     *
     * @param clazz the record type the component belongs to
     * @param component the record component to validate
     * @param field the field associated with the record (nullable)
     */
    protected void validateFieldForRecord(@NotNull Class<?> clazz, @NotNull RecordComponent component,
                                          @Nullable Field field) {
        if (field == null) {
            throw new ConfigMeException("Record component '" + component.getName() + "' for " + clazz.getName()
                + " does not have a field with the same name");
        } else if (isFieldIgnored(field)) {
            throw new ConfigMeException("Record component '" + component.getName() + "' for " + clazz.getName()
                + " has a field defined to be ignored: this is not supported for records");
        }
    }

    /**
     * Validates the given field that belongs to a bean class.
     *
     * @param clazz the class the field belongs to (the bean type)
     * @param field the field to validate
     */
    protected void validateFieldForBean(@NotNull Class<?> clazz, @NotNull Field field) {
        if (Modifier.isFinal(field.getModifiers())) {
            throw new ConfigMeException("Field '" + FieldUtils.formatField(field)
                + "' is final. Final fields cannot be set by the mapper. Remove final or mark it to be ignored.");
        }
    }

    protected @NotNull BeanFieldPropertyDefinition createDefinition(@NotNull Field field) {
        return new BeanFieldPropertyDefinition(field, getCustomExportName(field), getComments(field));
    }

    protected boolean isFieldIgnored(@NotNull Field field) {
        return Modifier.isTransient(field.getModifiers()) || field.isAnnotationPresent(IgnoreInMapping.class);
    }

    /**
     * Returns the comments that are defined on the property. Comments are found by looking for an &#64;{@link Comment}
     * annotation on a field with the same name as the property.
     *
     * @param field the field associated with the property (may be null)
     * @return comments for the property (never null)
     */
    protected @NotNull BeanPropertyComments getComments(@Nullable Field field) {
        Comment comment = field == null ? null : field.getAnnotation(Comment.class);
        if (comment != null) {
            UUID uniqueId = comment.repeat() ? null : UUID.randomUUID();
            return new BeanPropertyComments(Arrays.asList(comment.value()), uniqueId);
        }
        return BeanPropertyComments.EMPTY;
    }

    /**
     * Validates the class's properties.
     *
     * @param clazz the class to which the properties belong
     * @param properties the properties that were constructed from the given class
     */
    protected void validateProperties(@NotNull Class<?> clazz,
                                      @NotNull Collection<? extends BeanPropertyDefinition> properties) {
        Set<String> names = new HashSet<>(properties.size());
        properties.forEach(property -> {
            if (property.getName().isEmpty()) {
                throw new ConfigMeMapperException("Custom name of " + property + " may not be empty");
            }
            if (!names.add(property.getName())) {
                throw new ConfigMeMapperException(
                    clazz + " has multiple properties with name '" + property.getName() + "'");
            }
        });
    }

    /**
     * Returns a custom name that the property should have in property resources for reading and writing. This method
     * returns null if the field name should be used.
     *
     * @param field the field to process
     * @return the custom name the property has in resources, null otherwise
     */
    protected @Nullable String getCustomExportName(@NotNull Field field) {
        return field.isAnnotationPresent(ExportName.class)
            ? field.getAnnotation(ExportName.class).value()
            : null;
    }
}
