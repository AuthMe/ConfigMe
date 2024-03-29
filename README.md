# ConfigMe
[![Build Status](https://github.com/AuthMe/ConfigMe/actions/workflows/maven_jdk8.yml/badge.svg)](https://github.com/AuthMe/ConfigMe/actions?query=branch%3Amaster)
[![Coverage Status](https://coveralls.io/repos/github/AuthMe/ConfigMe/badge.svg?branch=master)](https://coveralls.io/github/AuthMe/ConfigMe?branch=master)
[![Javadocs](https://www.javadoc.io/badge/ch.jalu/configme.svg)](https://www.javadoc.io/doc/ch.jalu/configme)
[![Code Climate](https://codeclimate.com/github/AuthMe/ConfigMe/badges/gpa.svg)](https://codeclimate.com/github/AuthMe/ConfigMe)

A simple configuration management library with YAML support out of the box.

- Lightweight
- Flexible
- Out of the box support for YAML
- Allows migrations / config file checks
- Null-safe
- Unit testing friendly

## How it works
- Each configurable value is a `Property` in ConfigMe. Properties are declared as `public static final` fields
  in classes which implement the `SettingsHolder` interface.
- Configurations are read from a `PropertyResource` (e.g. the provided `YamlFileResource`), which abstracts reading
  and writing.
- The _property resource_ may be checked for completeness with the `MigrationService`, which allows you also to rename
  properties or to remove obsolete ones.
- The `SettingsManager` unifies the members above. On creation, it calls the migration service and allows you to get
  and change property values.

### Integration
Start using ConfigMe by adding this to your pom.xml:
```xml
<dependencies>
    <dependency>
        <groupId>ch.jalu</groupId>
        <artifactId>configme</artifactId>
        <version>1.4.1</version>
    </dependency>
</dependencies>
```
  
### Example
**config.yml**
```yml
title:
    text: 'Hello'
    size: 12
```

**TitleConfig.java**
```java
public class TitleConfig implements SettingsHolder {

    public static final Property<String> TITLE_TEXT =
        newProperty("title.text", "-Default-");

    public static final Property<Integer> TITLE_SIZE =
        newProperty("title.size", 10);

    private TitleConfig() {
        // prevent instantiation
    }
}
```

**WelcomeWriter.java**
```java
public class WelcomeWriter {
    public String generateWelcomeMessage() {
        SettingsManager settings = SettingsManagerBuilder
            .withYamlFile(Path.of("config.yml"))
            .configurationData(TitleConfig.class)
            .useDefaultMigrationService()
            .create();

        // Get properties from the settings manager
        return "<font size=\""
            + settings.getProperty(TitleConfig.TITLE_SIZE) + "\">"
            + settings.getProperty(TitleConfig.TITLE_TEXT) + "</font>";
    }
}
```
:pencil: Read the full documentation in the [ConfigMe Wiki](https://github.com/AuthMe/ConfigMe/wiki).

:pencil: See a full working example based on this
[here](https://github.com/AuthMe/ConfigMe/tree/master/src/test/java/ch/jalu/configme/demo).

:pencil: See how to use custom classes as property types in the
[bean properties demo](https://github.com/AuthMe/ConfigMe/tree/master/src/test/java/ch/jalu/configme/demo/beans).
