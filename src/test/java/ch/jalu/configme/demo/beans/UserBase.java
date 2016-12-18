package ch.jalu.configme.demo.beans;

import java.util.Map;

/**
 * User base bean.
 */
public class UserBase {

    private Map<String, User> users;
    private double version;
    private transient int build;

    public Map<String, User> getUsers() {
        return users;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }
}
