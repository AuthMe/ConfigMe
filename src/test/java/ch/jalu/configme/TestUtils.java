package ch.jalu.configme;

import org.hamcrest.Matcher;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Utilities for testing.
 */
public final class TestUtils {

    private TestUtils() {
    }

    // -------------
    // JAR resources
    // -------------

    /**
     * Returns a {@link File} to a file in the JAR's resources (main or test).
     *
     * @param path The absolute path to the file
     * @return The project file
     */
    public static File getJarFile(String path) {
        URI uri = getUriOrThrow(path);
        return new File(uri.getPath());
    }

    /**
     * Returns a {@link Path} to a file in the JAR's resources (main or test).
     *
     * @param path The absolute path to the file
     * @return The Path object to the file
     */
    public static Path getJarPath(String path) {
        String sqlFilePath = getUriOrThrow(path).getPath();
        // Windows preprends the path with a '/' or '\', which Paths cannot handle
        String appropriatePath = System.getProperty("os.name").contains("indow")
            ? sqlFilePath.substring(1)
            : sqlFilePath;
        return Paths.get(appropriatePath);
    }

    /**
     * Copies the resources file at the given path to a new file in the provided {@link TemporaryFolder} instance.
     *
     * @param path the path in the JAR's resources to copy from
     * @param temporaryFolder the temporary folder to copy into
     * @return the created copy
     */
    public static File copyFileFromResources(String path, TemporaryFolder temporaryFolder) {
        try {
            Path source = getJarPath(path);
            File destination = temporaryFolder.newFile();
            Files.copy(source, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destination;
        } catch (IOException e) {
            throw new IllegalStateException("Could not copy test file", e);
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
    // Constructor validation
    // -------------

    /**
     * Checks that a class only has a private, zero-argument constructor, preventing the
     * instantiation of such classes (utility classes). Invokes the hidden constructor
     * as to register the code coverage.
     *
     * @param clazz the class to validate
     */
    public static void validateHasOnlyPrivateEmptyConstructor(Class<?> clazz) {
        validateHasOnlyEmptyConstructorWithVisibility(clazz, Modifier.PRIVATE);
    }

    /**
     * Checks that a class only has a protected, zero-argument constructor, preventing the
     * instantiation of such classes (utility classes). Invokes the hidden constructor
     * as to register the code coverage.
     *
     * @param clazz the class to validate
     */
    public static void validateHasOnlyProtectedEmptyConstructor(Class<?> clazz) {
        validateHasOnlyEmptyConstructorWithVisibility(clazz, Modifier.PROTECTED);
    }

    private static void validateHasOnlyEmptyConstructorWithVisibility(Class<?> clazz,
                                                                     int visibilityModifierFlag) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length > 1) {
            throw new IllegalStateException("Class " + clazz.getSimpleName() + " has more than one constructor");
        } else if (constructors[0].getParameterTypes().length != 0) {
            throw new IllegalStateException("Constructor of " + clazz + " does not have empty parameter list");
        } else if ((constructors[0].getModifiers() & visibilityModifierFlag) != visibilityModifierFlag) {
            throw new IllegalStateException("Constructor of " + clazz + " does not have the desired visibility");
        }

        // Ugly hack to get coverage on the private constructors
        // http://stackoverflow.com/questions/14077842/how-to-test-a-private-constructor-in-java-application
        try {
            constructors[0].setAccessible(true);
            constructors[0].newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new UnsupportedOperationException(e);
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
     * Verifies that the provided runnable throws an exception of the given type.
     *
     * @param runnable the runnable to check
     * @param exceptionType the expected type of the exception
     */
    public static void verifyException(Runnable runnable, Class<? extends Exception> exceptionType) {
        verifyException(runnable, exceptionType, "");
    }

    /**
     * Verifies that the provided runnable throws an exception of the given type whose message contains
     * the provided message excerpt.
     *
     * @param runnable the runnable to check
     * @param exceptionType the expected type of the exception
     * @param messageExcerpt the text the exception message should contain
     */
    public static void verifyException(Runnable runnable, Class<? extends Exception> exceptionType,
                                       String messageExcerpt) {
        try {
            runnable.run();
            fail("Expected exception of type '" + exceptionType.getName() + "' to be thrown");
        } catch (Exception e) {
            if (!exceptionType.isInstance(e)) {
                e.printStackTrace();
                fail("Expected exception of type '" + exceptionType.getName() + "' but got '"
                    + e.getClass().getName() + "': " + e.getMessage());
            }
            if (!messageExcerpt.isEmpty()) {
                assertThat(e.getMessage(), containsString(messageExcerpt));
            }
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
}
