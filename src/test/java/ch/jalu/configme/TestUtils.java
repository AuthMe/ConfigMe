package ch.jalu.configme;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.function.Executable;

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Utilities for testing.
 */
public final class TestUtils {

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
    public static Path getJarPath(String path) {
        String filePath = getUriOrThrow(path).getPath();
        // Windows preprends the path with a '/' or '\', which Paths cannot handle
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
    public static Path copyFileFromResources(String path, Path temporaryFolder) {
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

    public static Path createTemporaryFile(Path folder) {
        try {
            return Files.createTempFile(folder, "configme", "test");
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create temporary file in " + folder, e);
        }
    }

    private static URI getUriOrThrow(String path) {
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
    public static <E> Matcher<Iterable<? extends E>> containsAll(Iterable<E> elements) {
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
     * Verifies that the provided executable throws an exception of the given type.
     *
     * @param executable the executable to check
     * @param exceptionType the expected type of the exception
     */
    public static void verifyException(Executable executable, Class<? extends Exception> exceptionType) {
        verifyException(executable, exceptionType, "");
    }

    /**
     * Verifies that the provided executable throws an exception of the given type whose message contains
     * the provided message excerpt.
     *
     * @param executable the executable to check
     * @param exceptionType the expected type of the exception
     * @param messageExcerpt the text the exception message should contain
     */
    public static void verifyException(Executable executable, Class<? extends Exception> exceptionType,
                                       String messageExcerpt) {
        Exception e = assertThrows(exceptionType, executable);
        if (!messageExcerpt.isEmpty()) {
            assertThat(e.getMessage(), containsString(messageExcerpt));
        }
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
    public static <T, R> List<R> transform(Collection<T> coll, Function<? super T, ? extends R> transformer) {
        return coll.stream().map(transformer).collect(Collectors.toList());
    }

    /**
     * Returns a matcher for {@link PropertyValue} which evaluates successfully only if the property value contains
     * the given value and its validity flag is set to true.
     *
     * @param expectedValue the value expected to be contained in the property value
     * @return matcher for fully valid property value
     */
    public static Matcher<PropertyValue> isValidValueOf(Object expectedValue) {
        return isPropertyValueOf(expectedValue, true);
    }

    /**
     * Returns a matcher for {@link PropertyValue} which evaluates successfully only if the property value contains
     * the given value and its validity flag is set to false.
     *
     * @param expectedValue the value expected to be contained in the property value
     * @return matcher for property value with error
     */
    public static Matcher<PropertyValue> isErrorValueOf(Object expectedValue) {
        return isPropertyValueOf(expectedValue, false);
    }

    private static Matcher<PropertyValue> isPropertyValueOf(Object expectedValue, boolean expectedValid) {
        Matcher<PropertyValue> valueMatcher = hasProperty("value", equalTo(expectedValue));
        Matcher<PropertyValue> validFlagMatcher = hasProperty("validInResource", equalTo(expectedValid));
        return both(valueMatcher).and(validFlagMatcher);
    }
}
