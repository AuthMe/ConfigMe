package ch.jalu.configme.samples.inheritance;

/**
 * Middle class.
 */
public class Middle extends Parent {

    private String name;
    private float ratio;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }
}
