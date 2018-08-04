## Demo with beans

This is a demo showing how you can use custom JavaBeans with ConfigMe. The classes `Location`,
`User`, and `UserBase` are JavaBeans. ConfigMe can scan these classes to determine the properties
they have and will then use the config.yml file to create new objects with the data inside
config.yml. `BeanPropertiesDemo` creates a settings manager and uses it to get the 
`DemoSettings.USER_BASE` setting.

You can find the config YAML file [here](https://github.com/AuthMe/ConfigMe/blob/master/src/test/resources/demo/bean_demo_config.yml).
Detailed information about using JavaBeans as properties can be found on the
[Bean properties page](https://github.com/AuthMe/ConfigMe/wiki/Bean-properties) of the Wiki.
