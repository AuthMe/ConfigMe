# ConfigMe Changelog

#### 0.3 (2016-12-23)
- Now available from Maven Central!
  - The project package is now `ch.jalu.configme`
- Enhance handling of bean properties
  - Support ignored properties on beans (`@Transient` on a property accessor)
  - `@ExportName` on a property accessor allows to define a different resource name
  - Improve support of inherited properties
  - Create example and Wiki page for bean properties
  - Better performance by caching mapper instance and class configurations
- Bug fixes:
  - Export all collections as YAML lists ([#27](https://github.com/AuthMe/ConfigMe/issues/27))
  - Detect fields with a property child type ([#28](https://github.com/AuthMe/ConfigMe/issues/28))
  - Support comment to be placed on root path ([#25](https://github.com/AuthMe/ConfigMe/issues/25))
- Rename knownproperties package to configurationdata
- Add method taking Iterable&lt;Class...> in ConfigurationDataBuilder

:blue_book: [All changes in 0.3](https://github.com/AuthMe/ConfigMe/milestone/3?closed=1)

#### 0.2.1 (2016-10-22)
Bugfix release:
- Fix YAML export of a beans property at root path
- Allow values of bean properties to be set via SettingsManager
- Fix null pointer exception after an empty YML file is loaded

:blue_book: [All changes in 0.2.1](https://github.com/AuthMe/ConfigMe/milestone/5?closed=1)


#### 0.2 (2016-10-16)
- Allow mapping of configurations to JavaBeans classes
- Allow to provide comments for sections

:blue_book: [All changes in 0.2](https://github.com/AuthMe/ConfigMe/milestone/2?closed=1)


#### 0.1 (2016-09-04)
Initial release:
- Cleanup of previously internally used configuration management
  - Changes to make more generic
  - Use SnakeYAML instead of Bukkit classes

:blue_book: [All changes in 0.1](https://github.com/AuthMe/ConfigMe/milestone/1?closed=1)
