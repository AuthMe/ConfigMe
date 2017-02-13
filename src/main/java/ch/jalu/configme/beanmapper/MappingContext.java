package ch.jalu.configme.beanmapper;

import ch.jalu.configme.utils.TypeInformation;

/**
 * Context in which a mapping takes place.
 */
public class MappingContext {

    private final int level;
    private final TypeInformation<?> parentType;

    private MappingContext(int level, TypeInformation<?> parentType) {
        this.level = level;
        this.parentType = parentType;
    }

    public static MappingContext root() {
        return new MappingContext(1, null);
    }

    public MappingContext createChild(TypeInformation<?> parentType) {
        return new MappingContext(level + 1, parentType);
    }

    public int getLevel() {
        return level;
    }

    public TypeInformation<?> getParentType() {
        return parentType;
    }
}
