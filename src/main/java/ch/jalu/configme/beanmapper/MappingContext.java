package ch.jalu.configme.beanmapper;

import ch.jalu.configme.utils.TypeInformation;

/**
 * Holds necessary information for a certain value that is being mapped in the bean mapper.
 */
public interface MappingContext {

    /**
     * Creates a child context with the given path addition ("name") and type information.
     *
     * @param name additional path element to append to this context's path
     * @param typeInformation the required type
     * @return new child context
     */
    MappingContext createChild(String name, TypeInformation typeInformation);

    /**
     * @return the required type the value needs to be mapped to
     */
    TypeInformation getTypeInformation();

    /**
     * Convenience method: gets the generic type info for the given index and ensures that the generic type information
     * exists and that it can be converted into a safe-to-write class. Throws an exception otherwise.
     *
     * @param index the index to get generic type info for
     * @return the generic type info (throws exception if absent or not precise enough)
     */
    default TypeInformation getGenericTypeInfoOrFail(int index) {
        TypeInformation genericType = getTypeInformation().getGenericType(index);
        if (genericType == null || genericType.getSafeToWriteClass() == null) {
            throw new ConfigMeMapperException(this,
                "The generic type " + index + " is not well defined");
        }
        return getTypeInformation().getGenericType(index);
    }

    /**
     * @return textual representation of the info in the context, used in exceptions
     */
    String createDescription();

}
