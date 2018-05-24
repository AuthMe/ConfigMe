package ch.jalu.configme.neo.resource;

import javax.annotation.Nullable;
import java.util.List;

public interface PropertyReader {

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

}
