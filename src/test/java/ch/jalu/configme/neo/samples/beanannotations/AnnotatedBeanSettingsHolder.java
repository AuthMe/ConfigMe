package ch.jalu.configme.neo.samples.beanannotations;

import ch.jalu.configme.Comment;
import ch.jalu.configme.neo.SettingsHolder;
import ch.jalu.configme.neo.properties.Property;

import static ch.jalu.configme.neo.properties.PropertyInitializer.newBeanProperty;

/**
 * SettingsHolder class for {@link AnnotatedBean} setting.
 */
@Deprecated // TODO: Add @Comment
public final class AnnotatedBeanSettingsHolder implements SettingsHolder {

    @Comment("Example: bean with annotated properties")
    public static final Property<AnnotatedBean> ANNOTATED_BEAN =
        newBeanProperty(AnnotatedBean.class, "", new AnnotatedBean());

    private AnnotatedBeanSettingsHolder() {
    }

}
