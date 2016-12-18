package ch.jalu.configme.beanmapper;

/**
 * Context in which a mapping takes place.
 */
public class MappingContext {

    private final int level;
    private final Class<?> parentType;

    private MappingContext(int level, Class<?> parentType) {
        this.level = level;
        this.parentType = parentType;
    }

    public static MappingContext root() {
        return new MappingContext(1, null);
    }

    public MappingContext createChild(Class<?> parentType) {
        return new MappingContext(level + 1, parentType);
    }

    public int getLevel() {
        return level;
    }

    public Class<?> getParentType() {
        return parentType;
    }
}
