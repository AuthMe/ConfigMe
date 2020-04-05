package ch.jalu.configme.utils;

import ch.jalu.configme.exception.ConfigMeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

import static ch.jalu.configme.TestUtils.verifyException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link Utils}.
 */
class UtilsTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldCreateFile() {
        // given
        Path file = temporaryFolder.resolve("hello.txt");
        Path otherFile = temporaryFolder.resolve("big/path/in/middle/toFile.png");

        // when
        Utils.createFileIfNotExists(file);
        Utils.createFileIfNotExists(otherFile);

        // then
        assertThat(Files.exists(file), equalTo(true));
        assertThat(Files.exists(otherFile), equalTo(true));
    }

    @Test
    void shouldCreateFileLegacy() {
        // given
        File file = new File(temporaryFolder.toFile(), "inter/mediate/parents/test.yml");

        // when
        Utils.createFileIfNotExists(file);

        // then
        assertThat(file.exists(), equalTo(true));
    }

    @Test
    void shouldThrowForFolderAsFile() {
        // given / when / then
        verifyException(
            () -> Utils.createFileIfNotExists(temporaryFolder),
            ConfigMeException.class,
            "Expected file");
    }

    @Test
    void shouldThrowIfDirsCannotBeCreated() {
        // given
        Path parent = temporaryFolder.resolve("foo");
        Path file = temporaryFolder.resolve("foo/foo.txt");
        Utils.createFileIfNotExists(parent);

        // when / then
        verifyException(
            () -> Utils.createFileIfNotExists(file),
            ConfigMeException.class,
            "Failed to create parent folder");
    }

    @Test
    void shouldIfFileCannotBeCreated() throws IOException {
        // given
        FileSystemProvider provider = mock(FileSystemProvider.class);
        FileSystem fileSystem = mock(FileSystem.class);
        given(fileSystem.provider()).willReturn(provider);
        Path child = mock(Path.class);
        given(child.getFileSystem()).willReturn(fileSystem);
        doThrow(NoSuchFileException.class).when(provider).checkAccess(child); // for Files#exists
        doThrow(new IOException("File creation not supported")).when(provider).newByteChannel(eq(child), anySet(), any());

        Path parent = temporaryFolder.resolve("parent");
        given(child.getParent()).willReturn(parent);

        // when / then
        verifyException(
            () -> Utils.createFileIfNotExists(child),
            ConfigMeException.class,
            "Failed to create file");
    }
}
