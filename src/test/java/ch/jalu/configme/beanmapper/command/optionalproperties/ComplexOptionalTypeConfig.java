package ch.jalu.configme.beanmapper.command.optionalproperties;

import java.util.Map;
import java.util.Optional;

/**
 * Sample class with complex type inside an Optional.
 */
public class ComplexOptionalTypeConfig {

    private Optional<Map<String, Object>> commandconfig;

    public Optional<Map<String, Object>> getCommandconfig() {
        return commandconfig;
    }

    public void setCommandconfig(Optional<Map<String, Object>> commandconfig) {
        this.commandconfig = commandconfig;
    }
}
