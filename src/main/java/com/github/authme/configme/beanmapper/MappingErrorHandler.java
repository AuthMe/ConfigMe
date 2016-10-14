package com.github.authme.configme.beanmapper;

import static java.lang.String.format;

/**
 * Error handler when a mapping is unsuccessful.
 */
public interface MappingErrorHandler {

    void handleError(Class<?> type, MappingContext context);


    /**
     * Implementations.
     */
    enum Impl implements MappingErrorHandler {
        /** Silent: ignore invalid entries. */
        SILENT {
            @Override
            public void handleError(Class<?> type, MappingContext context) {
                // noop
            }
        },

        /** Throws an exception as soon as a field cannot be mapped. */
        THROWING {
            @Override
            public void handleError(Class<?> type, MappingContext context) {
                throw new ConfigMeMapperException(format("Could not map property of type '%s' (level=%d, parent='%s')",
                    type, context.getLevel(), context.getParentType()));
            }
        }
    }
}
