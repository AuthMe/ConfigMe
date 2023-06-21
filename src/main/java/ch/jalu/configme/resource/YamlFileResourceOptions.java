package ch.jalu.configme.resource;

import ch.jalu.configme.resource.PropertyPathTraverser.PathElement;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.ToIntFunction;

public class YamlFileResourceOptions {

    private final @NotNull Charset charset;
    private final @Nullable ToIntFunction<PathElement> numberOfLinesBeforeFunction;
    private final int indentationSize;
    private final boolean splitDotPaths;

    /**
     * Constructor. Use {@link #builder()} to instantiate option objects.
     *
     * @param charset the charset
     * @param numberOfLinesBeforeFunction function defining how many lines before a path element should be in the export
     * @param indentationSize number of spaces to use for each level of indentation
     * @param splitDotPaths whether compound keys (keys with ".") should be split into nested paths
     */
    protected YamlFileResourceOptions(@Nullable Charset charset,
                                      @Nullable ToIntFunction<PathElement> numberOfLinesBeforeFunction,
                                      int indentationSize,
                                      boolean splitDotPaths) {
        this.charset = charset == null ? StandardCharsets.UTF_8 : charset;
        this.numberOfLinesBeforeFunction = numberOfLinesBeforeFunction;
        this.indentationSize = indentationSize;
        this.splitDotPaths = splitDotPaths;
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

    public boolean splitDotPaths() {
        return splitDotPaths;
    }

    /**
     * @return the indentation to use for one level
     */
    public @NotNull String getIndentation() {
        if (indentationSize == 4) {
            return "    ";
        }
        StringBuilder sb = new StringBuilder(indentationSize);
        for (int i = 0; i < indentationSize; ++i) {
            sb.append(" ");
        }
        return sb.toString();
    }

    protected final @Nullable ToIntFunction<PathElement> getIndentFunction() {
        return numberOfLinesBeforeFunction;
    }

    public static class Builder {

        private Charset charset;
        private ToIntFunction<PathElement> numberOfLinesBeforeFunction;
        private int indentationSize = 4;
        private boolean splitDotPaths = true;

        public @NotNull Builder charset(Charset charset) {
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

        public Builder splitDotPaths(boolean splitDotPaths) {
            this.splitDotPaths = splitDotPaths;
            return this;
        }

        public @NotNull YamlFileResourceOptions build() {
            return new YamlFileResourceOptions(charset, numberOfLinesBeforeFunction, indentationSize, splitDotPaths);
        }
    }
}
