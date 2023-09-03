package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.samples.TestEnum;
import ch.jalu.typeresolver.reference.TypeReference;
import ch.jalu.typeresolver.typeimpl.WildcardTypeImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static ch.jalu.typeresolver.TypeInfo.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Test for {@link EnumLeafType}.
 */
@ExtendWith(MockitoExtension.class)
class EnumLeafTypeTest {

    private final EnumLeafType enumLeafType = new EnumLeafType();

    @Test
    void shouldConvertEnum() {
        // given
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when / then
        assertThat(enumLeafType.convert("second", of(TestEnum.class), errorRecorder), equalTo(TestEnum.SECOND));
        assertThat(enumLeafType.convert("FIRST", of(TestEnum.class), errorRecorder), equalTo(TestEnum.FIRST));
        assertThat(enumLeafType.convert("Hours", of(TimeUnit.class), errorRecorder), equalTo(TimeUnit.HOURS));
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldNotConvertIfTargetTypeIsNotEnum() {
        // given
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when / then
        assertThat(enumLeafType.convert("second", of(String.class), errorRecorder), nullValue());
        assertThat(enumLeafType.convert("FIRST", of(int.class), errorRecorder), nullValue());
        assertThat(enumLeafType.convert("Hours", new TypeReference<Set<TimeUnit>>() { }, errorRecorder), nullValue());
        assertThat(enumLeafType.convert("Hours", of(WildcardTypeImpl.newUnboundedWildcard()), errorRecorder), nullValue());
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldNotConvertIfValueIsNotString() {
        // given
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when / then
        assertThat(enumLeafType.convert(null, of(TestEnum.class), errorRecorder), nullValue());
        assertThat(enumLeafType.convert('s', of(TestEnum.class), errorRecorder), nullValue());
        assertThat(enumLeafType.convert(456, of(TestEnum.class), errorRecorder), nullValue());
        assertThat(enumLeafType.convert(new byte[0], of(TimeUnit.class), errorRecorder), nullValue());
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldReturnExportValueIfIsEnum() {
        // given / when / then
        assertThat(enumLeafType.toExportValueIfApplicable(TestEnum.SECOND), equalTo("SECOND"));
        assertThat(enumLeafType.toExportValueIfApplicable(TimeUnit.DAYS), equalTo("DAYS"));

        assertThat(enumLeafType.toExportValueIfApplicable(32), nullValue());
        assertThat(enumLeafType.toExportValueIfApplicable(false), nullValue());
        assertThat(enumLeafType.toExportValueIfApplicable("MINUTES"), nullValue());
    }
}
