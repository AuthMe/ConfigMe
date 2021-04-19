package ch.jalu.configme.beanmapper.leafvaluehandler;

/** String handler. */
public class StringLeafValueHandler extends AbstractLeafValueHandler {

    @Override
    public Object convert(Class<?> clazz, Object value) {
        if (clazz == String.class
            && (value instanceof String || value instanceof Number || value instanceof Boolean)) {
            return value.toString();
        }
        return null;
    }

    @Override
    public Object toExportValue(Object value) {
        return (value instanceof String) ? value : null;
    }
}
