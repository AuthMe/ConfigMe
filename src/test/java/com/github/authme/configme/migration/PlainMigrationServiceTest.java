package com.github.authme.configme.migration;

import com.github.authme.configme.TestUtils;
import com.github.authme.configme.knownproperties.PropertyEntry;
import com.github.authme.configme.knownproperties.PropertyFieldsCollector;
import com.github.authme.configme.resource.PropertyResource;
import com.github.authme.configme.resource.YamlFileResource;
import com.github.authme.configme.samples.TestConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link PlainMigrationService}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PlainMigrationServiceTest {

    private static final List<PropertyEntry> PROPERTY_ENTRIES =
        PropertyFieldsCollector.getAllProperties(TestConfiguration.class);

    @Spy
    private PlainMigrationService service;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldReturnNoSaveNecessaryForAllPropertiesPresent() throws IOException {
        // given
        PropertyResource resource = createResourceSpy("/config-sample.yml");

        // when
        boolean result = service.checkAndMigrate(resource, PROPERTY_ENTRIES);

        // then
        assertThat(result, equalTo(false));
        verify(service).performMigrations(resource, PROPERTY_ENTRIES);
    }

    @Test
    public void shouldReturnTrueForMissingProperty() throws IOException {
        // given
        PropertyResource resource = createResourceSpy("/config-incomplete-sample.yml");

        // when
        boolean result = service.checkAndMigrate(resource, PROPERTY_ENTRIES);

        // then
        assertThat(result, equalTo(true));
        // Verify that performMigrations was called; it should be called before our generic property check
        verify(service).performMigrations(resource, PROPERTY_ENTRIES);
    }

    private PropertyResource createResourceSpy(String file) throws IOException {
        // It's a little difficult to set up mock behavior for all cases, so use a YML file from test/resources
        Path sampleJarFile = TestUtils.getJarPath(file);
        File tempFolder = temporaryFolder.newFolder();
        Path tempFile = new File(tempFolder, "sample.yml").toPath();
        Files.copy(sampleJarFile, tempFile);
        return Mockito.spy(new YamlFileResource(tempFile.toFile()));
    }

}
