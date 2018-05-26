package ch.jalu.configme.neo.resource;

import java.io.File;

public class YamlFileResource implements PropertyResource {

    protected final File file;

    public YamlFileResource(File file) {
        this.file = file;
    }

    @Override
    public PropertyReader createReader() {
        return new YamlFileReader(file);
    }
}
