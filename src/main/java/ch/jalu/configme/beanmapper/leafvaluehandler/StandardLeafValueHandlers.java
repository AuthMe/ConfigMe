package ch.jalu.configme.beanmapper.leafvaluehandler;

import org.jetbrains.annotations.NotNull;

/**
 * Provides the leaf value handler which is used by default.
 *
 * @see #getDefaultLeafValueHandler()
 */
// Rename class in ConfigMe 2.0
public final class StandardLeafValueHandlers {

    private static LeafValueHandler defaultHandler;

    private StandardLeafValueHandlers() {
    }

    /**
     * Returns the default leaf value handler used in ConfigMe.
     *
     * @return default leaf value handler
     */
    public static @NotNull LeafValueHandler getDefaultLeafValueHandler() {
        if (defaultHandler == null) {
            defaultHandler = new CombiningLeafValueHandler(new StringLeafValueHandler(), new EnumLeafValueHandler(),
                new BooleanLeafValueHandler(), new NumberLeafValueHandler(), new BigNumberLeafValueHandler(),
                new ObjectLeafValueHandler());
        }
        return defaultHandler;
    }
}
