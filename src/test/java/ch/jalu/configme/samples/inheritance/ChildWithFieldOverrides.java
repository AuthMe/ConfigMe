package ch.jalu.configme.samples.inheritance;

import ch.jalu.configme.beanmapper.ExportName;
import ch.jalu.configme.beanmapper.Ignore;

public class ChildWithFieldOverrides extends Middle {

    @Ignore
    private String name; // Ignore name of parent

    @ExportName("o_ratio")
    private float ratio; // Override name of Middle#ratio


    public String readChildName() {
        return name;
    }

    public void writeChildName(String childName) {
        this.name = childName;
    }

    public float readChildRatio() {
        return ratio;
    }

    public void writeChildRatio(float childRatio) {
        this.ratio = childRatio;
    }

}
