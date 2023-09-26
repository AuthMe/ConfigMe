package ch.jalu.configme.samples.beanannotations;

import ch.jalu.configme.Comment;
import ch.jalu.configme.beanmapper.ExportName;

public class BeanWithExportName {

    @Comment("name_com")
    @ExportName("s_name")
    private String name;

    @Comment("active_com")
    @ExportName("b_active")
    private boolean active;

    @Comment("size_com")
    @ExportName("i_size")
    private int size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
