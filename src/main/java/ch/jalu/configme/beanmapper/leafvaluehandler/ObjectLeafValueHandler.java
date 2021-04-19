package ch.jalu.configme.beanmapper.leafvaluehandler;

/** Object handler. */
public class ObjectLeafValueHandler extends AbstractLeafValueHandler {

    @Override
    public Object convert(Class<?> clazz, Object value) {
        if (clazz == Object.class) {
            return value;
        }
        return null;
    }

    @Override
    public Object toExportValue(Object value) {
        return null;
    }
}
