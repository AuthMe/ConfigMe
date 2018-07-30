package ch.jalu.configme.samples.inheritance;

/**
 * Middle class.
 */
@Deprecated // has been moved
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
