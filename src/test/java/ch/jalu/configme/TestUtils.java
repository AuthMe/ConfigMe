package ch.jalu.configme;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

/**
 * Utilities for testing.
 */
public final class TestUtils {

    private static Class<? extends Exception> expectedNullArgExceptionType;

    private TestUtils() {
    }

    // -------------
    // File helpers
    // -------------

    /**
     * Returns a {@link Path} to a file in the JAR's resources (main or test).
     *
     * @param path the absolute path to the file
     * @return the Path object to the file
     */
    public static Path getJarPath(@NotNull String path) {
        String filePath = getUriOrThrow(path).getPath();
        // Windows prepends the path with a '/' or '\', which Paths cannot handle
        String appropriatePath = System.getProperty("os.name").contains("indow")
            ? filePath.substring(1)
            : filePath;
        return Paths.get(appropriatePath);
    }

    /**
     * Copies the resources file at the given path to a new file in the given temporary folder.
     *
     * @param path the path in the JAR's resources to copy from
     * @param temporaryFolder the temporary folder to copy into
     * @return the created copy
     */
    public static @NotNull Path copyFileFromResources(@NotNull String path, @NotNull Path temporaryFolder) {
        try {
            Path source = getJarPath(path);
            Path destination = temporaryFolder.resolve(source.getFileName());
            Files.createFile(destination);
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            return destination;
        } catch (IOException e) {
            throw new IllegalStateException("Could not copy test file", e);
        }
    }

    public static @NotNull Path createTemporaryFile(@NotNull Path folder) {
        try {
            return Files.createTempFile(folder, "configme", "test");
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create temporary file in " + folder, e);
        }
    }

    private static @NotNull URI getUriOrThrow(@NotNull String path) {
        URL url = TestUtils.class.getResource(path);
        if (url == null) {
            throw new IllegalStateException("File '" + path + "' could not be loaded");
        }
        try {
            return new URI(url.toString());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("File '" + path + "' cannot be converted to a URI", e);
        }
    }

    // -------------
    // Matchers
    // -------------

    /**
     * Creates a matcher equivalent to {@link org.hamcrest.Matchers#contains(Object[])} with an iterable as input.
     *
     * @param elements the elements that need to be present in order
     * @param <E> the element's type
     * @return the created matcher
     */
    public static <E> @NotNull Matcher<Iterable<? extends E>> containsAll(@NotNull Iterable<E> elements) {
        List<Matcher<? super E>> matchers = new ArrayList<>();
        for (E elem : elements) {
            matchers.add(equalTo(elem));
        }
        return contains(matchers);
    }

    // -------------
    // Exception verification
    // -------------

    /**
     * Returns the expected exception type when a null argument is supplied where it is not allowed.
     * <p>
     * Background: Due to usage of IntelliJ's &#64;{@link NotNull} annotation, when compiled locally, IntelliJ adds
     * bytecode to guard against null which throws an IllegalStateException or an IllegalArgumentException. When code
     * is built outside of IntelliJ (e.g. Maven build), the guards are not added to the bytecode. This results in
     * different exceptions being thrown.
     *
     * @return the expected exception type for a null argument where it is not allowed (NPE or IllegalArgumentException)
     */
    public static @NotNull Class<? extends Exception> getExceptionTypeForNullArg() {
        return getOrCaptureNullExceptionType();
    }

    /**
     * Returns whether the code was compiled such that {@link NotNull} parameters are checked to ensure they are not
     * null, which happens when the code is locally built in IntelliJ.
     *
     * @return true if NotNull is checked in methods (= local IntelliJ builds), false otherwise
     */
    public static boolean hasBytecodeCheckForNotNullAnnotation() {
        return !getOrCaptureNullExceptionType().equals(NullPointerException.class);
    }

    // -------------
    // Convenience methods
    // -------------

    /**
     * Transforms all elements of the provided collection with the given function.
     *
     * @param coll the collection to transform
     * @param transformer the function to use
     * @param <T> the collection type
     * @param <R> the result type
     * @return the transformed list
     */
    public static <T, R> @NotNull List<R> transform(@NotNull Collection<T> coll,
                                                    @NotNull Function<? super T, ? extends R> transformer) {
        return coll.stream().map(transformer).collect(Collectors.toList());
    }

    /**
     * Returns a matcher for {@link PropertyValue} which evaluates successfully only if the property value contains
     * the given value and its validity flag is set to true.
     *
     * @param expectedValue the value expected to be contained in the property value
     * @param <T> the value type
     * @return matcher for fully valid property value
     */
    public static <T> @NotNull Matcher<PropertyValue<T>> isValidValueOf(@NotNull T expectedValue) {
        return isPropertyValueOf(expectedValue, true);
    }

    /**
     * Returns a matcher for {@link PropertyValue} which evaluates successfully only if the property value contains
     * the given value and its validity flag is set to false.
     *
     * @param expectedValue the value expected to be contained in the property value
     * @param <T> the value type
     * @return matcher for property value with error
     */
    public static <T> @NotNull Matcher<PropertyValue<T>> isErrorValueOf(@NotNull T expectedValue) {
        return isPropertyValueOf(expectedValue, false);
    }

    private static <T> @NotNull Matcher<PropertyValue<T>> isPropertyValueOf(@Nullable T expectedValue,
                                                                            boolean expectedValid) {
        Matcher<PropertyValue<T>> valueMatcher = hasProperty("value", equalTo(expectedValue));
        Matcher<PropertyValue<T>> validFlagMatcher = hasProperty("validInResource", equalTo(expectedValid));
        return both(valueMatcher).and(validFlagMatcher);
    }

    private static @NotNull Class<? extends Exception> getOrCaptureNullExceptionType() {
        if (expectedNullArgExceptionType == null) {
            try {
                notNullMethod(null);
                throw new IllegalStateException("Expected NPE or IAE");
            } catch (NullPointerException | IllegalArgumentException e) {
                expectedNullArgExceptionType = e.getClass();
            }
        }
        return expectedNullArgExceptionType;
    }

    private static void notNullMethod(@NotNull Object arg) {
        arg.toString();
    }
}
