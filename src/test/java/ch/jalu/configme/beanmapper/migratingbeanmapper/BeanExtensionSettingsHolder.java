package ch.jalu.configme.beanmapper.migratingbeanmapper;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.BeanProperty;
import ch.jalu.configme.properties.Property;

import java.util.ArrayList;
import java.util.List;

public class BeanExtensionSettingsHolder implements SettingsHolder {

    public static final Property<CollectionChatComponent> CHAT_COMPONENT = new BeanProperty<>(
        CollectionChatComponent.class, "message-key", new CollectionChatComponent(), new SingleValueToCollectionMapper());


    public static class CollectionChatComponent {

        private List<String> color;
        private String text;
        private List<CollectionChatComponent> extra = new ArrayList<>();

        public List<String> getColor() {
            return color;
        }

        public void setColor(List<String> color) {
            this.color = color;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<CollectionChatComponent> getExtra() {
            return extra;
        }

        public void setExtra(List<CollectionChatComponent> extra) {
            this.extra = extra;
        }
    }
}
