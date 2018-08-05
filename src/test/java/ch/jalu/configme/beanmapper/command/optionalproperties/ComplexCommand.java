package ch.jalu.configme.beanmapper.command.optionalproperties;

import ch.jalu.configme.beanmapper.command.Command;
import ch.jalu.configme.samples.TestEnum;

import java.util.Optional;

/**
 * Extension of sample {@link Command} class.
 */
public class ComplexCommand extends Command {

    private Optional<String> nameStartsWith;
    private Optional<Integer> nameHasLength;
    private Optional<TestEnum> testEnumProperty;
    private Optional<Double> doubleOptional;

    public Optional<String> getNameStartsWith() {
        return nameStartsWith;
    }

    public void setNameStartsWith(Optional<String> nameStartsWith) {
        this.nameStartsWith = nameStartsWith;
    }

    public Optional<Integer> getNameHasLength() {
        return nameHasLength;
    }

    public void setNameHasLength(Optional<Integer> nameHasLength) {
        this.nameHasLength = nameHasLength;
    }

    public Optional<TestEnum> getTestEnumProperty() {
        return testEnumProperty;
    }

    public void setTestEnumProperty(Optional<TestEnum> testEnumProperty) {
        this.testEnumProperty = testEnumProperty;
    }

    public Optional<Double> getDoubleOptional() {
        return doubleOptional;
    }

    public void setDoubleOptional(Optional<Double> doubleOptional) {
        this.doubleOptional = doubleOptional;
    }
}
