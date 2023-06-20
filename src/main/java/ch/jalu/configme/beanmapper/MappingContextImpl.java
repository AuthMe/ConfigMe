package ch.jalu.configme.beanmapper;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.utils.TypeInformation;
import org.jetbrains.annotations.NotNull;

/**
 * Standard implementation of {@link MappingContext}.
 */
public class MappingContextImpl implements MappingContext {

    private final String path;
    private final TypeInformation typeInformation;
    private final ConvertErrorRecorder errorRecorder;

    protected MappingContextImpl(@NotNull String path, @NotNull TypeInformation typeInformation,
                                 @NotNull ConvertErrorRecorder errorRecorder) {
        this.path = path;
        this.typeInformation = typeInformation;
        this.errorRecorder = errorRecorder;
    }

    /**
     * Creates an initial context (used at the start of a mapping process).
     *
     * @param typeInformation the required type
     * @param errorRecorder error recorder to register errors even if a valid value is returned
     * @return root mapping context
     */
    public static @NotNull MappingContextImpl createRoot(@NotNull TypeInformation typeInformation,
                                                         @NotNull ConvertErrorRecorder errorRecorder) {
        return new MappingContextImpl("", typeInformation, errorRecorder);
    }

    @Override
    public @NotNull MappingContext createChild(@NotNull String subPath, @NotNull TypeInformation typeInformation) {
        if (path.isEmpty()) {
            return new MappingContextImpl(subPath, typeInformation, errorRecorder);
        }
        return new MappingContextImpl(path + "." + subPath, typeInformation, errorRecorder);
    }

    @Override
    public @NotNull TypeInformation getTypeInformation() {
        return typeInformation;
    }

    @Override
    public @NotNull String createDescription() {
        return "Path: '" + path + "', type: '" + typeInformation.getType() + "'";
    }

    @Override
    public void registerError(@NotNull String reason) {
        errorRecorder.setHasError("At path '" + path + "': " + reason);
    }

    @Override
    public @NotNull String toString() {
        return getClass().getSimpleName() + "[" + createDescription() + "]";
    }
}
