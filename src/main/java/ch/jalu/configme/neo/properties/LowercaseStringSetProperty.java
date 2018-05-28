package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.propertytype.LowercaseStringSetType;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class LowercaseStringSetProperty extends BaseProperty<Set<String>> {

    public LowercaseStringSetProperty(String path, String... defaultEntries) {
        super(path, toLinkedHashSet(defaultEntries), LowercaseStringSetType.instance());
    }

    private static Set<String> toLinkedHashSet(String... values) {
        return Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(values)));
    }
}
