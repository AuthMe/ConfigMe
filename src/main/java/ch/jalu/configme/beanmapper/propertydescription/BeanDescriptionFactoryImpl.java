package ch.jalu.configme.beanmapper.propertydescription;

import ch.jalu.configme.Comment;
import ch.jalu.configme.beanmapper.ConfigMeMapperException;
import ch.jalu.configme.beanmapper.ExportName;
import ch.jalu.configme.beanmapper.Ignore;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.internal.record.RecordComponent;
import ch.jalu.typeresolver.FieldUtils;
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
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Creates all {@link BeanPropertyDescription} objects for a given class.
 * <p>
 * This description factory returns property descriptions for all properties on a class
 * for which a getter and setter is associated. Inherited properties are considered.
 * <p>
 * This implementation supports {@link ExportName} and transient properties, declared
 * with the {@code transient} keyword or by adding the {@link Ignore} annotation.
 */
public class BeanDescriptionFactoryImpl implements BeanDescriptionFactory {

    @Override
    public @NotNull List<BeanFieldPropertyDescription> createRecordProperties(@NotNull Class<?> clazz,
                                                                              RecordComponent @NotNull [] components) {
        Map<String, Field> instanceFieldsByName = FieldUtils.getAllFields(clazz)
            .filter(FieldUtils::isRegularInstanceField)
            .collect(FieldUtils.collectByName(false));

        List<BeanFieldPropertyDescription> relevantFields = new ArrayList<>();
        for (RecordComponent component : components) {
            Field field = instanceFieldsByName.get(component.getName());
            if (field == null) {
                throw new ConfigMeException("Record component '" + component.getName() + "' for " + clazz.getName()
                    + " does not have a field with the same name");
            }
            BeanFieldPropertyDescription property = convert(field);
            if (property == null) {
                throw new ConfigMeException("Record component '" + component.getName() + "' for " + clazz.getName()
                   + " has a field defined to be ignored: this is not supported for records");
            }
            relevantFields.add(property);
        }
        return relevantFields;
    }

    /**
     * Collects all properties available on the given class.
     *
     * @param clazz the class to process
     * @return properties of the class
     */
    public @NotNull List<BeanFieldPropertyDescription> getAllProperties(@NotNull Class<?> clazz) {
        LinkedHashMap<String, Field> instanceFieldsByName = FieldUtils.getAllFields(clazz)
            .filter(FieldUtils::isRegularInstanceField)
            .collect(FieldUtils.collectByName(false));

        List<BeanFieldPropertyDescription> properties = instanceFieldsByName.values().stream()
            .map(this::convert)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        validateProperties(clazz, properties);
        return properties;
    }

    protected @Nullable BeanFieldPropertyDescription convert(@NotNull Field field) {
        if (Modifier.isTransient(field.getModifiers()) || field.isAnnotationPresent(Ignore.class)) {
            return null;
        }

        return new BeanFieldPropertyDescription(field,
            getCustomExportName(field),
            getComments(field));
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
     * @param properties the properties that will be used on the class
     */
    protected void validateProperties(@NotNull Class<?> clazz,
                                      @NotNull Collection<? extends BeanPropertyDescription> properties) {
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

    protected @Nullable String getCustomExportName(@NotNull Field field) {
        return field.isAnnotationPresent(ExportName.class)
            ? field.getAnnotation(ExportName.class).value()
            : null;
    }
}
