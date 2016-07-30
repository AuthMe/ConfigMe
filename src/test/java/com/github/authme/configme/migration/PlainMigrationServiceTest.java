package com.github.authme.configme.migration;

import com.github.authme.configme.TestUtils;
import com.github.authme.configme.propertymap.PropertyMap;
import com.github.authme.configme.resource.PropertyResource;
import com.github.authme.configme.resource.YamlFileResource;
import com.github.authme.configme.samples.TestConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link PlainMigrationService}.
 */
public class PlainMigrationServiceTest {

    private static final PropertyMap PROPERTY_MAP = TestConfiguration.generatePropertyMap();
    private PlainMigrationService service = Mockito.spy(new PlainMigrationServiceTestImpl());

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldReturnTrueForAllPropertiesPresent() throws IOException {
        // given
        PropertyResource resource = createResourceSpy("/config-sample.yml");

        // when
        boolean result = service.checkAndMigrate(resource, PROPERTY_MAP);

        // then
        assertThat(result, equalTo(false));
        verify(service).performMigrations(resource, PROPERTY_MAP);
    }

    @Test
    public void shouldReturnFalseForMissingProperty() throws IOException {
        // given
        PropertyResource resource = createResourceSpy("/config-incomplete-sample.yml");

        // when
        boolean result = service.checkAndMigrate(resource, PROPERTY_MAP);

        // then
        assertThat(result, equalTo(true));
        // Verify that performMigrations was called; it should be called before our generic property check
        verify(service).performMigrations(resource, PROPERTY_MAP);
    }

    private PropertyResource createResourceSpy(String file) throws IOException {
        // It's a little difficult to set up mock behavior for all cases, so use config-sample.yml
        Path sampleJarFile = TestUtils.getJarPath(file);
        File tempFolder = temporaryFolder.newFolder();
        Path tempFile = new File(tempFolder, "sample.yml").toPath();
        Files.copy(sampleJarFile, tempFile);
        return Mockito.spy(new YamlFileResource(tempFile.toFile()));
    }

    private static class PlainMigrationServiceTestImpl extends PlainMigrationService {
        @Override
        protected boolean performMigrations(PropertyResource resource, PropertyMap propertyMap) {
            return false;
        }
    }

}
