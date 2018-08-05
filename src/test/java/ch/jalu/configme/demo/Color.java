package ch.jalu.configme.demo;

/**
 * Colors.
 */
public enum Color {

    GREEN("#0f0"),

    BLUE("#00f"),

    RED("#f00"),

    ORANGE("#f50");

    public final String hexCode;

    Color(String hexCode) {
        this.hexCode = hexCode;
    }
}
