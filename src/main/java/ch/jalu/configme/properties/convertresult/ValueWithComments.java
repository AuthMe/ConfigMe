package ch.jalu.configme.properties.convertresult;

import java.util.List;

/**
 * Wraps a value and allows to associate it with comments. Can be used as return type from
 * {@link ch.jalu.configme.properties.Property#toExportValue}.
 * <p>
 * Prefer defining comments in {@link ch.jalu.configme.SettingsHolder} classes whenever possible.
 */
public class ValueWithComments {

    private final Object value;
    private final List<String> comments;

    /**
     * Constructor.
     *
     * @param value the value to wrap
     * @param comments the comments associated with the value
     */
    public ValueWithComments(Object value, List<String> comments) {
        this.value = value;
        this.comments = comments;
    }

    /**
     * @return the value wrapped by this instance
     */
    public Object getValue() {
        return value;
    }

    /**
     * @return the comments associated with the value
     */
    public List<String> getComments() {
        return comments;
    }

    /**
     * Unwraps the value and returns the actual value contained in this wrapper if the object is an instance of
     * this class. Otherwise, the same value as the parameter is returned.
     *
     * @param object the object to potentially unwrap
     * @return the value of {@link ValueWithComments} if the object is of this type, otherwise the object itself
     */
    public static Object unwrapValue(Object object) {
        if (object instanceof ValueWithComments) {
            return ((ValueWithComments) object).getValue();
        }
        return object;
    }
}
