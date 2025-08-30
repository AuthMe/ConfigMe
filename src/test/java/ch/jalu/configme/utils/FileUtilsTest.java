package ch.jalu.configme.utils;

import ch.jalu.configme.exception.ConfigMeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link FileUtils}.
 */
@ExtendWith(MockitoExtension.class)
class FileUtilsTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldCreateFile() {
        // given
        Path file = temporaryFolder.resolve("hello.txt");
        Path otherFile = temporaryFolder.resolve("big/path/in/middle/toFile.png");

        // when
        FileUtils.createFileIfNotExists(file);
        FileUtils.createFileIfNotExists(otherFile);

        // then
        assertThat(Files.exists(file), equalTo(true));
        assertThat(Files.exists(otherFile), equalTo(true));
    }

    @Test
    void shouldThrowForFolderAsFile() {
        // given / when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> FileUtils.createFileIfNotExists(temporaryFolder));

        // then
        assertThat(ex.getMessage(), matchesPattern("Expected file but '.*?' is not a file"));
        assertThat(ex.getCause(), nullValue());
    }

    @Test
    void shouldThrowIfDirsCannotBeCreated() {
        // given
        Path parent = temporaryFolder.resolve("foo");
        Path file = temporaryFolder.resolve("foo/foo.txt");
        FileUtils.createFileIfNotExists(parent);

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> FileUtils.createFileIfNotExists(file));

        // then
        assertThat(ex.getMessage(), matchesPattern("Failed to create parent folders for '.*?foo.txt'"));
        assertThat(ex.getCause(), instanceOf(FileAlreadyExistsException.class));
    }

    @Test
    void shouldThrowIfFileCannotBeCreated() throws IOException {
        // given
        FileSystemProvider provider = mock(FileSystemProvider.class);
        FileSystem fileSystem = mock(FileSystem.class);
        given(fileSystem.provider()).willReturn(provider);
        Path child = mock(Path.class);
        given(child.getFileSystem()).willReturn(fileSystem);
        // #347: JDK21 does not call checkAccess anymore
        lenient().doThrow(NoSuchFileException.class).when(provider).checkAccess(child); // for Files#exists
        IOException ioException = new IOException("File creation not supported");
        given(provider.newByteChannel(eq(child), anySet(), any(FileAttribute[].class))).willThrow(ioException);

        Path parent = temporaryFolder.resolve("parent");
        given(child.getParent()).willReturn(parent);

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> FileUtils.createFileIfNotExists(child));

        // then
        assertThat(ex.getMessage(), matchesPattern("Failed to create file '.*?'"));
        assertThat(ex.getCause(), sameInstance(ioException));
    }
}
