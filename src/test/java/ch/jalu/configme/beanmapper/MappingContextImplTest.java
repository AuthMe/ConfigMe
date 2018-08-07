package ch.jalu.configme.beanmapper;

import ch.jalu.configme.utils.TypeInformation;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link MappingContextImpl}.
 */
public class MappingContextImplTest {

    @Test
    public void shouldCreateProperPath() {
        // given
        MappingContextImpl root1 = MappingContextImpl.createRoot("", new TypeInformation(String.class));
        MappingContextImpl root2 = MappingContextImpl.createRoot("foo", new TypeInformation(Integer.class));

        // when
        MappingContext child1 = root1.createChild("bar", new TypeInformation(String.class));
        MappingContext child2 = root2.createChild("bar", new TypeInformation(Integer.class));

        // then
        assertThat(child1.toString(), containsString("Path: 'bar'"));
        assertThat(child2.toString(), containsString("Path: 'foo.bar'"));
    }

    @Test
    public void shouldCreateDescription() {
        // given
        MappingContextImpl context = MappingContextImpl.createRoot("oh.em.gee", new TypeInformation(ArrayList.class));

        // when
        String description = context.createDescription();

        // then
        assertThat(description, equalTo("Path: 'oh.em.gee', type: '" + ArrayList.class + "'"));
    }
}
