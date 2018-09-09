package ch.jalu.configme.properties.helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link StandardInlineArrayConverters}.
 */
@RunWith(MockitoJUnitRunner.class)
public class StandardInlineArrayConvertersTest {

    InlineArrayConverter<Float> convertHelper = new StandardInlineArrayConverters<>(", ", Float.class, Float::parseFloat);

    Float[] targetArray = new Float[] {123.223F, 666.111F, (float) Math.PI};
    String targetString;

    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < targetArray.length; i++) {
            if (i != 0)
                sb = sb.append(", ");

            sb = sb.append(targetArray[i]);
        }

        targetString = sb.toString();
    }

    @Test
    public void shouldReturnValueAsExportValue() {
        Object exportValue = convertHelper.toExportValue(targetArray);

        assertThat(exportValue, equalTo(targetString));
    }

    @Test
    public void shouldReturnFromStringValue() {
        Float[] fromString = convertHelper.fromString(targetString);

        assertThat(fromString, equalTo(targetArray));
    }

}
