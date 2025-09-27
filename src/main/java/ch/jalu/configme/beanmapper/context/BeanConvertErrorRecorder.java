package ch.jalu.configme.beanmapper.context;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of {@link ConvertErrorRecorder} for the conversion of a value during the mapping of a bean:
 * wraps the error recorder of the bean property and prefixes any errors with the path of the value.
 */
public class BeanConvertErrorRecorder implements ConvertErrorRecorder {

    private final ConvertErrorRecorder beanRootErrorRecorder;
    private final String beanPath;

    public BeanConvertErrorRecorder(@NotNull ConvertErrorRecorder beanRootErrorRecorder, @NotNull String beanPath) {
        this.beanRootErrorRecorder = beanRootErrorRecorder;
        this.beanPath = beanPath;
    }

    @Override
    public void setHasError(@NotNull String reason) {
        beanRootErrorRecorder.setHasError("For bean path '" + beanPath + "': " + reason);
    }

    @Override
    public boolean isFullyValid() {
        return beanRootErrorRecorder.isFullyValid();
    }
}
