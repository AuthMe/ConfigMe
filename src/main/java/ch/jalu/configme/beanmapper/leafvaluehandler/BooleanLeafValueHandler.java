package ch.jalu.configme.beanmapper.leafvaluehandler;

/** Boolean leaf value handler. */
public class BooleanLeafValueHandler extends AbstractLeafValueHandler {

    @Override
    public Object convert(Class<?> clazz, Object value) {
        if ((clazz == boolean.class || clazz == Boolean.class) && value instanceof Boolean) {
            return value;
        }
        return null;
    }

    @Override
    public Object toExportValue(Object value) {
        return (value instanceof Boolean) ? value : null;
    }
}
