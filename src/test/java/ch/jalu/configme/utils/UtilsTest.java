package ch.jalu.configme.utils;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.exception.ConfigMeException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static ch.jalu.configme.TestUtils.verifyException;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

/**
 * Test for {@link Utils}.
 */
public class UtilsTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldCreateFile() throws IOException {
        // given
        File folder = temporaryFolder.newFolder();
        File file = new File(folder, "hello.txt");
        File otherFile = new File(folder, "big/path/in/middle/toFile.png");

        // when
        Utils.createFileIfNotExists(file);
        Utils.createFileIfNotExists(otherFile);

        // then
        assertThat(file.exists(), equalTo(true));
        assertThat(otherFile.exists(), equalTo(true));
    }

    @Test
    public void shouldThrowForFolderAsFile() throws IOException {
        // given
        File folder = temporaryFolder.newFolder();

        // when / then
        verifyException(
            () -> Utils.createFileIfNotExists(folder),
            ConfigMeException.class,
            "Expected file");
    }

    @Test
    public void shouldHaveHiddenConstructor() {
        TestUtils.validateHasOnlyPrivateEmptyConstructor(Utils.class);
    }

    @Test
    public void shouldThrowIfDirsCannotBeCreated() throws IOException {
        // given
        File folder = temporaryFolder.newFolder();
        File parent = new File(folder, "parent");
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
        File folder = temporaryFolder.newFolder();
        File parent = new File(folder, "parent");
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
        File folder = temporaryFolder.newFolder();
        File parent = new File(folder, "parent");
        File fileSpy = Mockito.spy(new File(parent, "file.txt"));
        doThrow(IOException.class).when(fileSpy).createNewFile();

        // when / then
        verifyException(
            () -> Utils.createFileIfNotExists(fileSpy),
            ConfigMeException.class,
            "Failed to create file");
    }
}
