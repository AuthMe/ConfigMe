package ch.jalu.configme.properties.convertresult;

import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;

/**
 * Records errors during the conversion of a property to its Java value.
 * <p>
 * This object is passed around during conversion to determine whether an error has occurred during the conversion that
 * should trigger a rewrite. It is not necessary to register an error with this class if the return value of the
 * conversion implies that the representation in the resource is wrong altogether.
 * Instead, errors are typically registered with this recorder when an object <b>can</b> be created, but there is some
 * error in the representation that should be corrected (e.g. a value is missing but there is a sensible fallback).
 *
 * @see ch.jalu.configme.properties.BaseProperty#determineValue(PropertyReader)
 */
public class ConvertErrorRecorder {

    private boolean hasError;

    /**
     * Registers that some error occurred during the conversion of the value. See class javadoc: no need to register
     * an error if the return value of the conversion implies there is an issue (such as returning null).
     *
     * @param reason the reason (not used in this implementation but may be extended for debugging)
     */
    public void setHasError(@NotNull String reason) {
        hasError = true;
    }

    /**
     * Returns whether the value that was returned from the property or property type was fully valid and
     * therefore doesn't need a rewrite. Even if a value is returned, this method may return {@code false} in case
     * a recoverable error was detected. This method may return {@code true} if the return value of the conversion is
     * null or otherwise indicates that there is no proper representation of it in the property resource.
     *
     * @return true if no error was registered, false otherwise (see class Javadoc for semantics)
     */
    public boolean isFullyValid() {
        return !hasError;
    }
}
