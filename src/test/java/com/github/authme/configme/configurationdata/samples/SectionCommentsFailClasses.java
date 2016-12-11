package com.github.authme.configme.configurationdata.samples;

import com.github.authme.configme.SectionComments;
import com.github.authme.configme.SettingsHolder;

import java.util.Collections;
import java.util.Map;

/**
 * Contains classes with wrong uses of {@link SectionComments} methods.
 */
public final class SectionCommentsFailClasses {

    private SectionCommentsFailClasses() {
    }

    /**
     * Method must return Map&lt;String, String>.
     */
    public final static class WrongReturnType implements SettingsHolder {
        @SectionComments
        public static String getComments() {
            return "wrong return type ;)";
        }
    }

    /**
     * Method must be static.
     */
    public final static class NonStaticMethod implements SettingsHolder {
        @SectionComments
        public Map<String, String> comments() {
            return Collections.emptyMap();
        }
    }

    /**
     * Method may not have any parameters.
     */
    public final static class MethodWithParameters implements SettingsHolder {
        @SectionComments
        public static Map<String, String> getComments(boolean b) {
            return Collections.emptyMap();
        }
    }

    /**
     * Method throws an exception.
     */
    public final static class ThrowingMethod implements SettingsHolder {
        @SectionComments
        public static Map<String, String> getComments() {
            throw new UnsupportedOperationException();
        }
    }
}
