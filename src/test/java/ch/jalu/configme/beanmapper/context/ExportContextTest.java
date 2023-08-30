package ch.jalu.configme.beanmapper.context;

import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyComments;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test for {@link ExportContext}.
 */
class ExportContextTest {

    @Test
    void shouldCreateChildContext() {
        // given
        ExportContextImpl root = ExportContextImpl.createRoot();

        // when
        ExportContext childContext = root.createChildContext("b");

        // then
        assertThat(childContext.getBeanPath(), equalTo("b"));
    }

    @Test
    void shouldUseSameUuidCommentsSetAcrossAllChildContexts() {
        // given
        ExportContextImpl root1 = ExportContextImpl.createRoot();
        ExportContextImpl root2 = ExportContextImpl.createRoot();

        ExportContext root1Child = root1.createChildContext("alfa");
        ExportContext root1ChildChild = root1Child.createChildContext("am");
        ExportContext root2Child = root2.createChildContext("bravo");

        UUID uuid = UUID.randomUUID();
        root1Child.registerComment(new BeanPropertyComments(Collections.singletonList("test"), uuid));

        BeanPropertyComments comments = new BeanPropertyComments(Arrays.asList("1", "2"), uuid);

        // when / then
        assertThat(root1.shouldInclude(comments), equalTo(false));
        assertThat(root1Child.shouldInclude(comments), equalTo(false));
        assertThat(root1ChildChild.shouldInclude(comments), equalTo(false));

        assertThat(root2.shouldInclude(comments), equalTo(true));
        assertThat(root2Child.shouldInclude(comments), equalTo(true));
    }

    @Test
    void shouldConcatenatePathsAppropriately() {
        // given / when
        ExportContext context1 = ExportContextImpl.createRoot()
            .createChildContext("test")
            .createChildContext("toast");
        ExportContext context2 = ExportContextImpl.createRoot()
            .createChildContext("[k=name]")
            .createChildContext("nicks")
            .createChildContext("[1]");

        // then
        assertThat(context1.getBeanPath(), equalTo("test.toast"));
        assertThat(context2.getBeanPath(), equalTo("[k=name].nicks[1]"));
    }

    @Test
    void shouldSpecifyToIncludeNonUniqueComments() {
        // given
        ExportContextImpl context = ExportContextImpl.createRoot();
        BeanPropertyComments comments = new BeanPropertyComments(Arrays.asList("1", "2"), null);

        // when / then
        assertThat(context.shouldInclude(comments), equalTo(true));
    }

    @Test
    void shouldSpecifyToSkipEmptyComment() {
        // given
        ExportContextImpl context = ExportContextImpl.createRoot();
        BeanPropertyComments comments1 = new BeanPropertyComments(Collections.emptyList(), null);
        BeanPropertyComments comments2 = new BeanPropertyComments(Collections.emptyList(), UUID.randomUUID());

        // when / then
        assertThat(context.shouldInclude(comments1), equalTo(false));
        assertThat(context.shouldInclude(comments2), equalTo(false));
    }
}
