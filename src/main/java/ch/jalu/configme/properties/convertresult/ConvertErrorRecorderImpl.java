package ch.jalu.configme.properties.convertresult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation of {@link ConvertErrorRecorder}. Keeps the last error reason it was given.
 */
public class ConvertErrorRecorderImpl implements ConvertErrorRecorder {

    private @Nullable String errorReason;

    @Override
    public void setHasError(@NotNull String reason) {
        errorReason = reason;
    }

    @Override
    public boolean isFullyValid() {
        return errorReason == null;
    }
}
