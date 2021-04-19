package ch.jalu.configme.beanmapper.leafvaluehandler;

/** Enum handler. */
public class EnumLeafValueHandler extends AbstractLeafValueHandler {

    @Override
    public Object convert(Class<?> clazz, Object value) {
        if (value instanceof String && Enum.class.isAssignableFrom(clazz)) {
            String givenText = (String) value;
            for (Enum e : (Enum[]) clazz.getEnumConstants()) {
                if (e.name().equalsIgnoreCase(givenText)) {
                    return e;
                }
            }
        }
        return null;
    }

    @Override
    public Object toExportValue(Object value) {
        if (value instanceof Enum<?>) {
            return ((Enum<?>) value).name();
        }
        return null;
    }
}
