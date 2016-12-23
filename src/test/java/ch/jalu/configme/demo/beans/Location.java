package ch.jalu.configme.demo.beans;

/**
 * Location bean.
 */
public class Location {

    private float longitude;
    private float latitude;
    private CoordinateSystem coordinateType = CoordinateSystem.ITRS;

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public CoordinateSystem getCoordinateType() {
        return coordinateType;
    }

    public void setCoordinateType(CoordinateSystem coordinateType) {
        this.coordinateType = coordinateType;
    }

    @Override
    public String toString() {
        return "(" + longitude + ", " + latitude + ")";
    }
}
