package ch.jalu.configme;

import ch.jalu.configme.demo.WelcomeWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test for {@link WelcomeWriter}.
 */
class ConfigDemoTest {

    @Test
    void shouldGenerateExpectedHtml() throws IOException {
        // given
        WelcomeWriter writer = new WelcomeWriter();

        try {
            // Perform the actual test
            shouldGenerateExpectedHtml(writer);
        } finally {
            // Cleanup - delete the temporary file
            Path file = writer.getConfigFile();
            if (file != null) {
                Files.delete(file);
            }
        }
    }

    private void shouldGenerateExpectedHtml(WelcomeWriter writer) {
        // when
        String welcomeFile = writer.generateWelcomeFile();

        // then
        String expectedText = "<h1 style='font-size: 11pt'>Hello</h1>"
            + System.getProperty("line.separator")
            + "<span style='color: #00f; font-size: 9pt'>Welcome!</span>";
        assertThat(welcomeFile, equalTo(expectedText));
    }
}
