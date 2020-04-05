package ch.jalu.configme.beanmapper;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.TestUtils;
import ch.jalu.configme.properties.Property;
import org.hamcrest.Matchers;
import org.hamcrest.core.CombinableMatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ch.jalu.configme.properties.PropertyInitializer.newBeanProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;

/**
 * Test for bean types which have a property which is a collection of another bean type.
 *
 * @see <a href="https://github.com/AuthMe/ConfigMe/issues/55">#55: Nested bean serialization</a>
 */
class BeanWithCollectionOfBeanTypeTest {

    private static final String NESTED_CHAT_COMPONENT_YML = "/beanmapper/nested_chat_component.yml";

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldLoadValue() {
        // given
        Path file = TestUtils.copyFileFromResources(NESTED_CHAT_COMPONENT_YML, temporaryFolder);
        SettingsManager settingsManager = SettingsManagerBuilder.withYamlFile(file)
            .configurationData(PropertyHolder.class).create();

        // when
        ChatComponent value = settingsManager.getProperty(PropertyHolder.TEST);

        // then
        assertThat(value, hasColorAndText("blue", "outside"));
        assertThat(value.getExtra(), hasSize(2));
        assertThat(value.getExtra().get(0), hasColorAndText("green", "inner1"));
        assertThat(value.getExtra().get(0).getExtra(), empty());
        assertThat(value.getExtra().get(1), hasColorAndText("blue", "inner2"));
        assertThat(value.getExtra().get(1).getExtra(), empty());
    }

    @Test
    void shouldSerializeProperly() throws IOException {
        // given
        Path file = TestUtils.copyFileFromResources(NESTED_CHAT_COMPONENT_YML, temporaryFolder);
        SettingsManager settingsManager = SettingsManagerBuilder.withYamlFile(file)
            .configurationData(PropertyHolder.class).create();

        // when
        settingsManager.save();

        // then
        List<String> lines = Files.readAllLines(file);
        assertThat(lines, contains(
            "message-key:",
            "    color: blue",
            "    text: outside",
            "    extra: ",
            "    - color: green",
            "      text: inner1",
            "      extra: []",
            "    - color: blue",
            "      text: inner2",
            "      extra: []"
        ));
    }

    @Test
    void shouldSerializeComplexObject() throws IOException {
        // given
        Path file = TestUtils.copyFileFromResources(NESTED_CHAT_COMPONENT_YML, temporaryFolder);
        SettingsManager settingsManager = SettingsManagerBuilder.withYamlFile(file)
            .configurationData(PropertyHolder.class).create();
        settingsManager.setProperty(PropertyHolder.TEST, createComplexComponent());

        // when
        settingsManager.save();

        // then
        List<String> lines = Files.readAllLines(file);
        List<String> expectedLines = Files.readAllLines(TestUtils.getJarPath("/beanmapper/nested_chat_component_complex_expected.yml"));
        assertThat(lines, equalTo(expectedLines));

        // Some checks to ensure that we can read the file again
        settingsManager.reload();
        ChatComponent result = settingsManager.getProperty(PropertyHolder.TEST);
        assertThat(result, hasColorAndText("green", "outside"));
        assertThat(result.getConditionalElem().isPresent(), equalTo(true));
        ExtendedChatComponent extendedComp = result.getConditionalElem().get();
        assertThat(extendedComp.getConditionals().keySet(), contains("low", "med", "high"));
        assertThat(extendedComp.getConditionals().get("med").getExtra().get(0), hasColorAndText("green", "med child"));
        assertThat(extendedComp.getBold().isPresent(), equalTo(false));
        assertThat(extendedComp.getItalic().isPresent(), equalTo(false));
        assertThat(extendedComp.getConditionals().get("high").getConditionalElem().isPresent(), equalTo(true));
        ExtendedChatComponent highExtendedComp = extendedComp.getConditionals().get("high").getConditionalElem().get();
        assertThat(highExtendedComp.getConditionals(), anEmptyMap());
        assertThat(highExtendedComp.getBold(), equalTo(Optional.of(true)));
        assertThat(highExtendedComp.getItalic(), equalTo(Optional.of(false)));
    }

    private static CombinableMatcher<? super ChatComponent> hasColorAndText(String color, String text) {
        return Matchers.both(hasProperty("color", equalTo(color)))
            .and(hasProperty("text", equalTo(text)));
    }

    private static ChatComponent createComplexComponent() {
        ChatComponent comp = new ChatComponent("green", "outside");
        ChatComponent greenExtra = new ChatComponent("yellow", "inner1");
        comp.getExtra().add(greenExtra);
        ChatComponent blueExtra = new ChatComponent("blue", "inner2");
        comp.getExtra().add(blueExtra);
        ChatComponent nestedExtra = new ChatComponent("red", "level2 text");
        blueExtra.getExtra().add(nestedExtra);
        ExtendedChatComponent extendedOrange = new ExtendedChatComponent("orange", "orange extension");
        comp.setConditionalElem(Optional.of(extendedOrange));
        extendedOrange.getConditionals().put("low", new ExtendedChatComponent("white", "low text"));
        extendedOrange.getConditionals().put("med", new ExtendedChatComponent("gray", "med text"));
        extendedOrange.getConditionals().get("med").getExtra().add(new ChatComponent("green", "med child"));
        extendedOrange.getConditionals().put("high", new ExtendedChatComponent("black", "high text"));
        ExtendedChatComponent extendedHighChild = new ExtendedChatComponent("teal", "teal addition");
        extendedHighChild.setBold(Optional.of(true));
        extendedHighChild.setItalic(Optional.of(false));
        extendedOrange.getConditionals().get("high").setConditionalElem(Optional.of(extendedHighChild));
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
        private Optional<ExtendedChatComponent> conditionalElem = Optional.empty();

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

        @Override
        public String toString() {
            return "[color=" + color + ";text=" + text + ";extra="
                + TestUtils.transform(extra, Object::toString) + "]";
        }

        public Optional<ExtendedChatComponent> getConditionalElem() {
            return conditionalElem;
        }

        public void setConditionalElem(Optional<ExtendedChatComponent> conditionalElem) {
            this.conditionalElem = conditionalElem;
        }
    }

    public static class ExtendedChatComponent extends ChatComponent {

        private Map<String, ChatComponent> conditionals = new LinkedHashMap<>();
        private Optional<Boolean> italic = Optional.empty();
        private Optional<Boolean> bold = Optional.empty();

        public ExtendedChatComponent() {
        }

        public ExtendedChatComponent(String color, String text) {
            super(color, text);
        }

        public Map<String, ChatComponent> getConditionals() {
            return conditionals;
        }

        public void setConditionals(Map<String, ChatComponent> conditionals) {
            this.conditionals = conditionals;
        }

        public Optional<Boolean> getItalic() {
            return italic;
        }

        public void setItalic(Optional<Boolean> italic) {
            this.italic = italic;
        }

        public Optional<Boolean> getBold() {
            return bold;
        }

        public void setBold(Optional<Boolean> bold) {
            this.bold = bold;
        }
    }
}
