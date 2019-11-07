package ch.jalu.configme.utils;

import ch.jalu.configme.exception.ConfigMeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static ch.jalu.configme.TestUtils.verifyException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

/**
 * Test for {@link Utils}.
 */
public class UtilsTest {

    @TempDir
    public File temporaryFolder;

    @Test
    public void shouldCreateFile() {
        // given
        File file = new File(temporaryFolder, "hello.txt");
        File otherFile = new File(temporaryFolder, "big/path/in/middle/toFile.png");

        // when
        Utils.createFileIfNotExists(file);
        Utils.createFileIfNotExists(otherFile);

        // then
        assertThat(file.exists(), equalTo(true));
        assertThat(otherFile.exists(), equalTo(true));
    }

    @Test
    public void shouldThrowForFolderAsFile() {
        // given / when / then
        verifyException(
            () -> Utils.createFileIfNotExists(temporaryFolder),
            ConfigMeException.class,
            "Expected file");
    }

    @Test
    public void shouldThrowIfDirsCannotBeCreated() {
        // given
        File parent = new File(temporaryFolder, "parent");
        File parentSpy = Mockito.spy(parent);
        File fileSpy = Mockito.spy(new File(parent, "file.txt"));
        given(fileSpy.getParentFile()).willReturn(parentSpy);
        doReturn(false).when(parentSpy).mkdirs();

        // when / then
        verifyException(
            () -> Utils.createFileIfNotExists(fileSpy),
            ConfigMeException.class,
            "Failed to create parent folder");
    }

    @Test
    public void shouldThrowIfFileCannotBeCreated() throws IOException {
        // given
        File parent = new File(temporaryFolder, "parent");
        File fileSpy = Mockito.spy(new File(parent, "file.txt"));
        doReturn(false).when(fileSpy).createNewFile();

        // when / then
        verifyException(
            () -> Utils.createFileIfNotExists(fileSpy),
            ConfigMeException.class,
            "Could not create file");
    }

    @Test
    public void shouldWrapException() throws IOException {
        // given
        File parent = new File(temporaryFolder, "parent");
        File fileSpy = Mockito.spy(new File(parent, "file.txt"));
        doThrow(IOException.class).when(fileSpy).createNewFile();

        // when / then
        verifyException(
            () -> Utils.createFileIfNotExists(fileSpy),
            ConfigMeException.class,
            "Failed to create file");
    }
}
