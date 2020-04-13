package ch.jalu.configme.resource;

import ch.jalu.configme.resource.PropertyPathTraverser.PathElement;

import javax.annotation.Nullable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.ToIntFunction;

public class YamlFileResourceOptions {

    private final Charset charset;
    private final ToIntFunction<PathElement> numberOfLinesBeforeFunction;
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

    public static Builder builder() {
        return new Builder();
    }

    public Charset getCharset() {
        return charset;
    }

    public int getNumberOfEmptyLinesBefore(PathElement pathElement) {
        return numberOfLinesBeforeFunction == null ? 0 : numberOfLinesBeforeFunction.applyAsInt(pathElement);
    }

    public int getIndentationSize() {
        return indentationSize;
    }

    @Nullable
    protected final ToIntFunction<PathElement> getIndentFunction() {
        return numberOfLinesBeforeFunction;
    }

    public static class Builder {
        private Charset charset;
        private ToIntFunction<PathElement> numberOfLinesBeforeFunction;
        private int indentationSize = 4;

        public Builder charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder numberOfLinesBeforeFunction(ToIntFunction<PathElement> numberOfLinesBeforeFunction) {
            this.numberOfLinesBeforeFunction = numberOfLinesBeforeFunction;
            return this;
        }

        public Builder indentationSize(final int indentationSize) {
            this.indentationSize = indentationSize;
            return this;
        }

        public YamlFileResourceOptions build() {
            return new YamlFileResourceOptions(charset, numberOfLinesBeforeFunction, indentationSize);
        }
    }
}
