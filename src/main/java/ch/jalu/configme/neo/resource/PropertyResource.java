package ch.jalu.configme.neo.resource;

import javax.annotation.Nullable;
import java.util.List;

public interface PropertyResource {

    void reload();

    void clear(); // TODO: hmm... See comment on old EnumProperty.
    // Or should we have a Map<String, Object> read? That seems ugly and offers less freedom than if we have an
    // interface with which we can specifically serve strings, ints and so forth.

    boolean contains(String path);

    @Nullable
    String getString(String path);

    @Nullable
    Object getObject(String path);

    @Nullable
    Integer getInteger(String path);

    @Nullable
    Double getDouble(String path);

    @Nullable
    List<?> getList(String path);

    // TODO: method for exporting properties
    // or do we want a separate interface? In terms of implementing them it's quite convenient plus would allow
    // to import and export from different sources, but semantically wouldn't it be confusing?

}
