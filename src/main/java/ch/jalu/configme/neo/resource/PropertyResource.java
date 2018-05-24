package ch.jalu.configme.neo.resource;

// TODO: Naming to be revised (better names for PropertyResource / PropertyReader)
public interface PropertyResource {

    PropertyReader createReader();

    // TODO: method for exporting properties. Would a separate interface parallel to the method above make sense?

}
