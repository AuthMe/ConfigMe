package ch.jalu.configme.beanmapper;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.TestUtils;
import ch.jalu.configme.properties.Property;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.newBeanProperty;
import static org.junit.Assert.fail;

/**
 * Test for bean types which have a property which is a collection of another bean type.
 *
 * @see <a href="https://github.com/AuthMe/ConfigMe/issues/55">#55: Nested bean serialization</a>
 */
@Ignore // TODO #55: Add support for nested beans
public class BeanWithCollectionOfBeanTypeTest {

    @Test
    public void shouldSerializeProperly() throws IOException {
        // given
        File file = TestUtils.getJarFile("/beanmapper/nested_chat_component.yml");
        SettingsManager settingsManager = SettingsManagerBuilder.withYamlFile(file)
            .configurationData(PropertyHolder.class).create();

        // when
        settingsManager.setProperty(PropertyHolder.TEST, createComplexComponent());
        settingsManager.save();

        // then
        fail(String.join("\n", Files.readAllLines(file.toPath())));
    }

    private static ChatComponent createComplexComponent() {
        ChatComponent comp = new ChatComponent("green", "outside");
        ChatComponent extra = new ChatComponent("yellow", "inner");
        comp.getExtra().add(extra);
        return comp;
    }

    public static final class PropertyHolder implements SettingsHolder {

        public static final Property<ChatComponent> TEST =
            newBeanProperty(ChatComponent.class, "message-key", new ChatComponent());
    }

    public static class ChatComponent {

        private String color;
        private String text;
        private List<ChatComponent> extra = new ArrayList<>();

        public ChatComponent() {
        }

        public ChatComponent(String color, String text) {
            this.color = color;
            this.text = text;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<ChatComponent> getExtra() {
            return extra;
        }

        public void setExtra(List<ChatComponent> extra) {
            this.extra = extra;
        }
    }
}
