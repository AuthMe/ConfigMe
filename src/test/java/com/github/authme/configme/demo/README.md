## ConfigMe demo

This folder contains a small demonstration showing how ConfigMe can be used.
You can find the config.yml file [here](https://github.com/AuthMe/ConfigMe/blob/master/src/test/resources/demo/config.yml).

TitleConfig contains some `Property` fields which represent the properties in the config.yml file.
These sample properties are then used in `WelcomeWriter` in order to generate a short text with
configurable elements.

Feel free to modify the config.yml file to see how this will change in behavior.
The PlainMigrationService that is passed to the SettingsManager checks that all known properties
are present in the config.yml file; if not, it will save the file, which triggers all absent
properties to be saved with their default value.

You can also add a new Property field to the `TitleConfig` and see how you can immediately use it
in your application, even without worrying about `null` values. Users with an old config.yml file 
will automatically be migrated and will find the new property with its default value next time they
start the application.
