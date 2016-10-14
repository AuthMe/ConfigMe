package com.github.authme.configme.beanmapper;

/**
 * Context in which a mapping takes place.
 */
public class MappingContext {

    private final int level;
    private final Class<?> parentType;

    public MappingContext(int level, Class<?> parentType) {
        this.level = level;
        this.parentType = parentType;
    }

    public static MappingContext root(Class<?> parentType) {
        return new MappingContext(1, parentType);
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
