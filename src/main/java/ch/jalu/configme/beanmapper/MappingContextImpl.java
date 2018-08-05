package ch.jalu.configme.beanmapper;

import ch.jalu.configme.utils.TypeInformation;

/**
 * Standard implementation of {@link MappingContext}.
 */
public class MappingContextImpl implements MappingContext {

    private final String path;
    private final TypeInformation typeInformation;

    protected MappingContextImpl(String path, TypeInformation typeInformation) {
        this.path = path;
        this.typeInformation = typeInformation;
    }

    public static MappingContextImpl createRoot(String path, TypeInformation typeInformation) {
        return new MappingContextImpl(path, typeInformation);
    }

    @Override
    public MappingContext createChild(String subPath, TypeInformation typeInformation) {
        if (path.isEmpty()) {
            return new MappingContextImpl(subPath, typeInformation);
        }
        return new MappingContextImpl(path + "." + subPath, typeInformation);
    }

    @Override
    public TypeInformation getTypeInformation() {
        return typeInformation;
    }

    @Override
    public String createDescription() {
        return "Path: '" + path + "', type: '" + typeInformation.getType() + "'";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + createDescription() + "]";
    }
}
