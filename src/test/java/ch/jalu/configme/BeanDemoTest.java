package ch.jalu.configme;

import ch.jalu.configme.demo.beans.BeanPropertiesDemo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test for {@link BeanPropertiesDemo}.
 */
class BeanDemoTest {

    @Test
    void shouldOutputExpectedText() throws IOException {
        // given
        BeanPropertiesDemo beanDemo = new BeanPropertiesDemo();

        try {
            // Perform the actual test
            shouldOutputExpectedText(beanDemo);
        } finally {
            // Cleanup - delete the temporary file
            Path file = beanDemo.getConfigFile();
            if (file != null) {
                Files.delete(file);
            }
        }
    }

    private void shouldOutputExpectedText(BeanPropertiesDemo beanDemo) {
        // when
        String userInfo = beanDemo.generateUserInfo();

        // then
        String expectedText = "Saved locations of Richie: restaurant (47.5, 8.7), hospital (47.1, 8.8901)"
            + "\nNicknames of Bob: Bobby, Bobby boy"
            + "\nCountry 'Sweden' has neighbors: Norway, Finland, Denmark";
        assertThat(userInfo, equalTo(expectedText));
    }
}
