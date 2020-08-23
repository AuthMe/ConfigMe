package ch.jalu.configme.beanmapper

import ch.jalu.configme.beanmapper.leafvaluehandler.LeafValueHandler

/**
 * Kotlin implementation of [BaseMapperImpl].
 *
 *
 * Maps a section of a property resource to the provided data class. The mapping is based on the bean's properties,
 * whose names must correspond with the names in the property resource. For example, if a data class has a property
 * `length` and should be mapped from the property resource's value at path `definition`, the mapper will
 * look up `definition.length` to get the value of the data class property.
 *
 *
 * Classes must be data classs. These are simple classes with private fields, accompanied with getters and setters.
 * **The mapper only considers properties which have both a getter and a setter method.** Any Java class without
 * at least one property with both a getter *and* a setter is not considered as a data class. Such classes can
 * be supported by implementing a custom [LeafValueHandler] that performs the conversion from the value coming
 * from the property reader to an object of the class' type.
 *
 *
 * **Recursion:** the mapping of values to a data class is performed recursively, i.e. a data class may have other
 * data classs as fields and generic types at any arbitrary "depth."
 *
 *
 * **Collections** are only supported if they are explicitly typed, i.e. a field of `List<String>`
 * is supported but `List<?>` and `List<T extends Number>` are not supported. Specifically, you may
 * only declare fields of type [java.util.List] or [java.util.Set], or a parent type ([Collection]
 * or [Iterable]).
 * Fields of type **Map** are supported also, with similar limitations. Additionally, maps may only have
 * `String` as key type, but no restrictions are imposed on the value type.
 *
 *
 * data classs may have **optional fields**. If the mapper cannot map the property resource value to the corresponding
 * field, it only treats it as a failure if the field's value is `null`. If the field has a default value assigned
 * to it on initialization, the default value remains and the mapping process continues. A data class field whose value is
 * `null` signifies a failure and stops the mapping process immediately.
 */
class KotlinMapperImpl : BaseMapperImpl() {
    override fun createBean(context: MappingContext, value: Any?): Any? {
        // Ensure that the value is a map so we can map it to a bean
        /* TODO: REMOVE
        Alternative would be a separate check:
        if (value !is Map<*, *>) {
            return null
        }
        val entries = value as Map<*, *>
         */
        val entries = value as? Map<*, *> ?: return null
        val bean = context.typeInformation.safeToWriteClass
        val properties = beanDescriptionFactory.getAllProperties(bean)
        // Check that we have properties (or else we don't have a bean)
        if (properties.isEmpty()) {
            return null
        }
        val kwargs = emptyMap<String, Any>().toMutableMap()
        for (property in properties) {
            val result = convertValueForType(
                    context.createChild(property.name, property.typeInformation),
                    entries[property.name])
            if (result == null) {
                if (property.getValue(bean) == null) {
                    return null // We do not support beans with a null value
                }
                context.registerError("No value found, fallback to field default value")
            } else {
                kwargs[property.name] = result
            }
        }
        // TODO: This doesn't work with Java classes if Java is compiled without `-parameters` option
        // TODO: Since then the names are `arg0`, `arg1`, ...
        val ctor = bean.kotlin.constructors
                .sortedByDescending { it.parameters.size }
                .find { ctor ->
                    run {
                        val parameters = ctor.parameters
                        val paramNames = parameters.map { it.name } // TODO: See when name can be null
                        return paramNames.all { name -> name in kwargs.keys } // TODO: Make sure `kwargs` don't have more keys?
                    }
                }
        if (ctor == null) return null
        val ctorArgs = ctor.parameters.map { it to kwargs[it.name] }.toMap()
        return ctor.call(ctorArgs)
    }
}
