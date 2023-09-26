package ch.jalu.configme.resource;

import ch.jalu.configme.resource.PropertyPathTraverser.PathElement;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.ToIntFunction;

/**
 * Options for {@link YamlFileResource} to configure reading and writing YAML.
 */
public class YamlFileResourceOptions {

    private final @NotNull Charset charset;
    private final @Nullable ToIntFunction<PathElement> numberOfLinesBeforeFunction;
    private final int indentationSize;

    /**
     * Constructor. Use {@link #builder()} to instantiate option objects.
     *
     * @param charset the charset
     * @param numberOfLinesBeforeFunction function defining how many lines before a path element should be in the export
     * @param indentationSize number of spaces to use for each level of indentation
     */
    protected YamlFileResourceOptions(@Nullable Charset charset,
                                      @Nullable ToIntFunction<PathElement> numberOfLinesBeforeFunction,
                                      int indentationSize) {
        this.charset = charset == null ? StandardCharsets.UTF_8 : charset;
        this.numberOfLinesBeforeFunction = numberOfLinesBeforeFunction;
        this.indentationSize = indentationSize;
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public @NotNull Charset getCharset() {
        return charset;
    }

    public int getNumberOfEmptyLinesBefore(@NotNull PathElement pathElement) {
        return numberOfLinesBeforeFunction == null ? 0 : numberOfLinesBeforeFunction.applyAsInt(pathElement);
    }

    public int getIndentationSize() {
        return indentationSize;
    }

    protected final @Nullable ToIntFunction<PathElement> getIndentFunction() {
        return numberOfLinesBeforeFunction;
    }

    /**
     * Builder to create YAML file resource options.
     */
    public static class Builder {

        private static final int DEFAULT_INDENTATION_SIZE = 4;

        private Charset charset;
        private ToIntFunction<PathElement> numberOfLinesBeforeFunction;
        private int indentationSize = DEFAULT_INDENTATION_SIZE;

        public @NotNull Builder charset(@Nullable Charset charset) {
            this.charset = charset;
            return this;
        }

        public @NotNull Builder numberOfLinesBeforeFunction(
                                                      @NotNull ToIntFunction<PathElement> numberOfLinesBeforeFunction) {
            this.numberOfLinesBeforeFunction = numberOfLinesBeforeFunction;
            return this;
        }

        public @NotNull Builder indentationSize(final int indentationSize) {
            this.indentationSize = indentationSize;
            return this;
        }

        public @NotNull YamlFileResourceOptions build() {
            return new YamlFileResourceOptions(charset, numberOfLinesBeforeFunction, indentationSize);
        }
    }
}
