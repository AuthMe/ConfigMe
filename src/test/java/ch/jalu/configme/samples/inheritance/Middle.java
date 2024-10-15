package ch.jalu.configme.samples.inheritance;

/**
 * Middle class.
 */
public class Middle extends Parent {

    private String name;
    private float ratio;


    public String readName() {
        return name;
    }

    public void writeName(String name) {
        this.name = name;
    }

    public float readRatio() {
        return ratio;
    }

    public void writeRatio(float ratio) {
        this.ratio = ratio;
    }

}
