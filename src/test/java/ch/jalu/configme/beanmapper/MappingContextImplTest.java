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
        TypeInformation typeInformation = new TypeInformation(Integer.class);
        MappingContext parent1 = MappingContextImpl.createRoot(typeInformation);
        MappingContext parent2 = MappingContextImpl.createRoot(typeInformation).createChild("foo", typeInformation);

        // when
        MappingContext child1 = parent1.createChild("bar", typeInformation);
        MappingContext child2 = parent2.createChild("bar", typeInformation);

        // then
        assertThat(child1.toString(), containsString("Path: 'bar'"));
        assertThat(child2.toString(), containsString("Path: 'foo.bar'"));
    }

    @Test
    public void shouldCreateDescription() {
        // given
        MappingContext context = MappingContextImpl.createRoot(new TypeInformation(String.class))
            .createChild("oh.em.gee", new TypeInformation(ArrayList.class));

        // when
        String description = context.createDescription();

        // then
        assertThat(description, equalTo("Path: 'oh.em.gee', type: '" + ArrayList.class + "'"));
    }
}
