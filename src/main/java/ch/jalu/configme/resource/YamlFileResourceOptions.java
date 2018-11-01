package ch.jalu.configme.resource;

import javax.annotation.Nullable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.IntUnaryOperator;

public class YamlFileResourceOptions {

    private final Charset charset;
    private final IntUnaryOperator indentFunction;

    protected YamlFileResourceOptions(@Nullable Charset charset, @Nullable IntUnaryOperator indentFunction) {
        this.charset = charset == null ? StandardCharsets.UTF_8 : charset;
        this.indentFunction = indentFunction;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Charset getCharset() {
        return charset;
    }

    @Nullable
    public IntUnaryOperator getIndentFunction() {
        return indentFunction;
    }

    public int getNumberOfEmptyLines(int indentLevel) {
        return indentFunction == null ? 0 : indentFunction.applyAsInt(indentLevel);
    }

    public static class Builder {
        private Charset charset;
        private IntUnaryOperator indentFunction;

        public Builder charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder indentFunction(IntUnaryOperator indentFunction) {
            this.indentFunction = indentFunction;
            return this;
        }

        public YamlFileResourceOptions build() {
            return new YamlFileResourceOptions(charset, indentFunction);
        }
    }
}
