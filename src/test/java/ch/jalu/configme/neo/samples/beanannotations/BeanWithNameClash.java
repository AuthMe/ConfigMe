package ch.jalu.configme.neo.samples.beanannotations;

import ch.jalu.configme.beanmapper.ExportName;

/**
 * Sample bean with two properties declared with the same name.
 */
@Deprecated // TODO: Add bean property to neo
public class BeanWithNameClash {

    private String location;
    private String otherProperty;
    private int threshold;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @ExportName("threshold")
    public String getOtherProperty() {
        return otherProperty;
    }

    public void setOtherProperty(String otherProperty) {
        this.otherProperty = otherProperty;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
