# ConfigMe
[![Build Status](https://travis-ci.org/AuthMe/ConfigMe.svg?branch=master)](https://travis-ci.org/AuthMe/ConfigMe)
[![Coverage Status](https://coveralls.io/repos/github/AuthMe/ConfigMe/badge.svg?branch=master)](https://coveralls.io/github/AuthMe/ConfigMe?branch=master)
[![Javadocs](https://www.javadoc.io/badge/ch.jalu/configme.svg)](https://www.javadoc.io/doc/ch.jalu/configme)
[![Code Climate](https://codeclimate.com/github/AuthMe/ConfigMe/badges/gpa.svg)](https://codeclimate.com/github/AuthMe/ConfigMe)

A simple configuration management library, initially for but no longer limited to Bukkit plugins.

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
- The _property resource_ may be checked for completeness with the `MigrationService`, which allows you also to move
  renamed properties or to remove obsolete ones.
- The `SettingsManager` unifies the members above. On creation, it provokes a check by the migration service and
  allows the user to get property values from.

### Integration
Start using ConfigMe by adding this to your pom.xml:
```xml
<dependencies>
    <dependency>
        <groupId>ch.jalu</groupId>
        <artifactId>configme</artifactId>
        <version>0.4.1</version>
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
        SettingsManager settings = initSettings();
      
        // Get properties from the settings manager
        return "<font size=\"" 
            + settings.getProperty(TitleConfig.TITLE_SIZE) + "\">"
            + settings.getProperty(TitleConfig.TITLE_TEXT) + "</font>";
    }
  
    private SettingsManager initSettings() {
        // Create property resource
        PropertyResource resource = new YamlFileResource("config.yml");

        // Create migration service
        MigrationService migrationService = new PlainMigrationService();

        // Create settings manager
        return new SettingsManager(resource, migrationService, TitleConfig.class);
    }
}
```
:pencil: See a full working example based on this 
[here](https://github.com/AuthMe/ConfigMe/tree/master/src/test/java/ch/jalu/configme/demo).

:pencil: See how to use custom classes as property types in the 
[bean properties demo](https://github.com/AuthMe/ConfigMe/tree/master/src/test/java/ch/jalu/configme/demo/beans).
