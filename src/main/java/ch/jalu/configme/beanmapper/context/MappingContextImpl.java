package ch.jalu.configme.beanmapper.context;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.utils.PathUtils;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;

/**
 * Standard implementation of {@link MappingContext}.
 */
public class MappingContextImpl implements MappingContext {

    private final String beanPath;
    private final TypeInfo typeInformation;
    private final ConvertErrorRecorder errorRecorder;

    protected MappingContextImpl(@NotNull String beanPath, @NotNull TypeInfo typeInformation,
                                 @NotNull ConvertErrorRecorder errorRecorder) {
        this.beanPath = beanPath;
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
    public static @NotNull MappingContextImpl createRoot(@NotNull TypeInfo typeInformation,
                                                         @NotNull ConvertErrorRecorder errorRecorder) {
        return new MappingContextImpl("", typeInformation, errorRecorder);
    }

    @Override
    public @NotNull MappingContext createChild(@NotNull String subPath, @NotNull TypeInfo typeInformation) {
        String childPath = PathUtils.concatSpecifierAware(beanPath, subPath);
        return new MappingContextImpl(childPath, typeInformation, errorRecorder);
    }

    public @NotNull String getBeanPath() {
        return beanPath;
    }

    @Override
    public @NotNull TypeInfo getTargetType() {
        return typeInformation;
    }

    @Override
    public @NotNull String createDescription() {
        return "Bean path: '" + beanPath + "', type: '" + typeInformation.getType() + "'";
    }

    @Override
    public void registerError(@NotNull String reason) {
        errorRecorder.setHasError("For bean path '" + beanPath + "': " + reason);
    }

    @Override
    public @NotNull ConvertErrorRecorder getErrorRecorder() {
        return errorRecorder;
    }

    @Override
    public @NotNull String toString() {
        return getClass().getSimpleName() + "[" + createDescription() + "]";
    }
}
