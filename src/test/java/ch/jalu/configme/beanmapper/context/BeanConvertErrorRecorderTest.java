package ch.jalu.configme.beanmapper.context;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link BeanConvertErrorRecorder}.
 */
class BeanConvertErrorRecorderTest {

    @Test
    void shouldLogWithBeanPathPrefix() {
        // given
        ConvertErrorRecorder rootErrorRecorder = mock(ConvertErrorRecorder.class);
        BeanConvertErrorRecorder beanConvertErrorRecorder = new BeanConvertErrorRecorder(rootErrorRecorder, "config.logSecrets");

        // when
        beanConvertErrorRecorder.setHasError("Uppercase value instead of lowercase");

        // then
        verify(rootErrorRecorder).setHasError("For bean path 'config.logSecrets': Uppercase value instead of lowercase");
    }

    @Test
    void shouldReturnErrorStatusFromRootErrorRecorder() {
        // given
        ConvertErrorRecorder rootErrorRecorder = mock(ConvertErrorRecorder.class);
        given(rootErrorRecorder.isFullyValid()).willReturn(true, false);
        BeanConvertErrorRecorder beanConvertErrorRecorder = new BeanConvertErrorRecorder(rootErrorRecorder, "config.logSecrets");

        // when / then
        assertThat(beanConvertErrorRecorder.isFullyValid(), equalTo(true));
        assertThat(beanConvertErrorRecorder.isFullyValid(), equalTo(false));
    }
}
