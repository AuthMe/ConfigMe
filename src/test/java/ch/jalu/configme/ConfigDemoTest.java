package ch.jalu.configme;

import ch.jalu.configme.demo.WelcomeWriter;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link WelcomeWriter}.
 */
public class ConfigDemoTest {

    @Test
    public void shouldGenerateExpectedHtml() {
        // given
        WelcomeWriter writer = new WelcomeWriter();

        try {
            // Perform the actual test
            shouldGenerateExpectedHtml(writer);
        } finally {
            // Cleanup - delete the temporary file
            File file = writer.getConfigFile();
            if (file != null) {
                file.delete();
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
