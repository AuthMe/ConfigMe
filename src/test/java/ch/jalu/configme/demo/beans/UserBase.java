package ch.jalu.configme.demo.beans;

import java.beans.Transient;

/**
 * User base bean.
 */
public class UserBase {

    private User bobby;
    private User richie;
    private User lionel;
    private double version;
    private transient int build;

    public User getBobby() {
        return bobby;
    }

    public void setBobby(User bobby) {
        this.bobby = bobby;
    }

    public User getRichie() {
        return richie;
    }

    public void setRichie(User richie) {
        this.richie = richie;
    }

    public User getLionel() {
        return lionel;
    }

    public void setLionel(User lionel) {
        this.lionel = lionel;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    @Transient
    public int getBuild() {
        return build;
    }

    public void setBuild(int build) {
        this.build = build;
    }
}
