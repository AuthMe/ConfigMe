package ch.jalu.configme.samples.beanannotations;

import ch.jalu.configme.Comment;
import ch.jalu.configme.beanmapper.ExportName;

public class BeanWithExportNameExtension extends BeanWithExportName {

    @Comment("weight_com")
    @ExportName("d_weight")
    private double weight;

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
