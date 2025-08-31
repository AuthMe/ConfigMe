# Demo with beans

This is a demo showing how you can use a custom bean with ConfigMe. The classes `Location`,
`User`, and `UserBase` are beans. ConfigMe can scan these classes to determine the properties
they have and will then use the config.yml file to create new objects with the data inside
config.yml. `BeanPropertiesDemo` creates a settings manager and uses it to get the
`DemoSettings.USER_BASE` setting.

The config YAML file is here:
[bean_demo_config.yml](https://github.com/AuthMe/ConfigMe/blob/master/src/test/resources/demo/bean_demo_config.yml).
Detailed information about using beans as properties can be found on the
[Bean properties page](https://github.com/AuthMe/ConfigMe/wiki/Bean-properties) of the Wiki.
