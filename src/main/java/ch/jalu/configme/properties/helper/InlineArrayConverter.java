package ch.jalu.configme.properties.helper;

public interface InlineArrayConverter<T> {

    T[] fromString(String in);

    String toExportValue(T[] value);

}
