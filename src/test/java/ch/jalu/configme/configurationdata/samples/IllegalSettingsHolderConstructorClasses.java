package ch.jalu.configme.configurationdata.samples;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;

/**
 * Contains SettingsHolder classes with wrong constructors.
 */
public final class IllegalSettingsHolderConstructorClasses {

    private IllegalSettingsHolderConstructorClasses() {
    }

    /**
     * Class doesn't have a no-args constructor.
     */
    public final static class MissingNoArgsConstructor implements SettingsHolder {

        MissingNoArgsConstructor(String str) {
        }
    }

    /**
     * Constructor throws exception.
     */
    public final static class ThrowingConstructor implements SettingsHolder {
        ThrowingConstructor() {
            throw new IllegalStateException("Exception for testing");
        }
    }

    /**
     * Class is abstract.
     */
    public static abstract class AbstractClass implements SettingsHolder {
        AbstractClass() {
        }
    }

    /**
     * Class is an interface.
     */
    public interface InterfaceSettingsHolder extends SettingsHolder {
        @Override
        default void registerComments(CommentsConfiguration conf) {
            conf.setComment("path", "comment");
        }
    }
}
