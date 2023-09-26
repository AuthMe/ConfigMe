# ConfigMe Changelog

## ConfigMe 1.x
#### 1.4.1 (2023-08-28)
- `@ExportName` can be added on bean fields (previously, it was only possible on accessor methods) ([#366](https://github.com/AuthMe/ConfigMe/issues/366))
- Bug fix: unique comment is repeated if it's in a MapProperty, ListProperty or similar ([#362](https://github.com/AuthMe/ConfigMe/issues/362))
- Bug fix: Property at root path `""` could be defined with other properties ([#363](https://github.com/AuthMe/ConfigMe/issues/363))
- Add getter for property type in TypeBasedProperty ([#333](https://github.com/AuthMe/ConfigMe/issues/333))

:blue_book: [All changes in 1.4.1](https://github.com/AuthMe/ConfigMe/milestone/16?closed=1)

#### 1.4.0 (2023-08-12)
- Annotated all methods with `@NotNull` and `@Nullable` for better type inference in Kotlin ([#235](https://github.com/AuthMe/ConfigMe/pull/235), [#318](https://github.com/AuthMe/ConfigMe/issues/318))
- Support `@Comment` on fields of bean properties ([#18](https://github.com/AuthMe/ConfigMe/issues/18))
  - Please read about [breaking changes](https://github.com/AuthMe/ConfigMe/issues/18#issuecomment-1663973493) if you extend `YamlFileResource` or other internals.
- New: `VersionMigrationService` to run migrations based on a version in the config file ([#344](https://github.com/AuthMe/ConfigMe/issues/344))
  - Breaking change: PlainMigrationService#moveProperty has been moved to MigrationUtils#moveProperty
- `@Comment` now supports new lines within texts, meaning you can use text blocks to define comments ([#334](https://github.com/AuthMe/ConfigMe/issues/334))
- Private `Property` fields are supported in SettingsHolder classes (integrates better with Kotlin) ([#133](https://github.com/AuthMe/ConfigMe/issues/133))
- PropertyInitializer: added `optionalDoubleProperty` and other optional flavors ([#311](https://github.com/AuthMe/ConfigMe/issues/311))
- The concrete Property type is used as the return value for all PropertyInitializer methods ([#321](https://github.com/AuthMe/ConfigMe/issues/321))

:blue_book: [All changes in 1.4.0](https://github.com/AuthMe/ConfigMe/milestone/13?closed=1)

#### 1.3.1 (2023-06-20)
- Update SnakeYAML dependency for security reasons ([#310](https://github.com/AuthMe/ConfigMe/issues/310))
- Introduce ShortProperty, LongProperty and FloatProperty for convenience ([#260](https://github.com/AuthMe/ConfigMe/issues/268)) 

:blue_book: [All changes in 1.3.1](https://github.com/AuthMe/ConfigMe/milestone/14?closed=1)

#### 1.3.0 (2021-10-17)
- Introduce option to NOT split paths that contain dots in the YAML file ([#214](https://github.com/AuthMe/ConfigMe/issues/214))
- Add support for BigInteger and BigDecimal in bean properties ([#182](https://github.com/AuthMe/ConfigMe/issues/182))
- Support map properties at root path-level properly when they are empty ([#191](https://github.com/AuthMe/ConfigMe/issues/191))
- Introduce RegexProperty to safely handle configurable regex patterns ([#145](https://github.com/AuthMe/ConfigMe/issues/145))
- Introduce StringSetProperty class ([#211](https://github.com/AuthMe/ConfigMe/pull/211))

:blue_book: [All changes in 1.3.0](https://github.com/AuthMe/ConfigMe/milestone/11?closed=1)

#### 1.2.0 (2020-09-04)
- Better ability to trigger a resave for partially valid values ([#19](https://github.com/AuthMe/ConfigMe/issues/19))
  - See [comment about breaking changes](https://github.com/AuthMe/ConfigMe/issues/19#issuecomment-569066960)
- Indentation size and number of empty lines are now configurable in YamlFileResourceOptions ([#127](https://github.com/AuthMe/ConfigMe/issues/127))
- Introduce SetProperty to create properties of a Set type ([#111](https://github.com/AuthMe/ConfigMe/issues/111))
- New method `getChildKeys` on PropertyReader to get direct children ([#73](https://github.com/AuthMe/ConfigMe/issues/73))
- Create SettingsHolderClassValidator with utilities to check proper setup of ConfigMe (for unit tests) ([#5](https://github.com/AuthMe/ConfigMe/issues/5))
- Various bug fixes and improvements. Extensive documentation is now also available on the [ConfigMe wiki](https://github.com/AuthMe/ConfigMe/wiki)

:blue_book: [All changes in 1.2.0](https://github.com/AuthMe/ConfigMe/milestone/9?closed=1)

#### 1.1.0 (2019-02-27)
- New property structure allowing to easily define maps and lists, see PropertyInitializer
- Support paths written together (e.g. "path.foo") in YML files
- Fix bug in which paths with special sequences (like numbers) cannot be read
- Allow to pass options into YamlReader to define how many empty lines should separate properties
  - Files now no longer have an empty line at the top and none at the end by default
- Write the properties of bean properties in the same order as the fields in the Java class

:blue_book: [All changes in 1.1.0](https://github.com/AuthMe/ConfigMe/milestone/6?closed=1)

#### 1.0.1 (2018-09-08)
- Support all Collection types for export values (not only lists)
  - Fixes built-in lowercase String Set property creating output which cannot be read again
- Add getter on ConfigurationData to get all comments
- If not defined, SettingsManagerBuilder creates a manager that uses no migration service (instead of the default one)

:blue_book: [All changes in 1.0.1](https://github.com/AuthMe/ConfigMe/milestone/10?closed=1)

#### 1.0 (2018-09-08)
- Large refactoring: see [Migrating from 0.x to 1.x](https://github.com/AuthMe/ConfigMe/wiki/Migrating-from-0.x-to-1.x) for migration guide
  - Improve codebase, especially the export of values and the bean mapper ([#56](https://github.com/AuthMe/ConfigMe/issues/56))
  - Fix serialization of nested beans ([#55](https://github.com/AuthMe/ConfigMe/issues/55))
  - Ensure that classes can be extended properly ([#54](https://github.com/AuthMe/ConfigMe/issues/54))
  - Replace LowercaseStringListProperty with LowercaseStringSetProperty
- New method: PropertyReader#getKeys
- Introduce new built-in property type for Doubles ([#60](https://github.com/AuthMe/ConfigMe/issues/60))
- Improve behavior of String properties (more values mapped to String) ([#58](https://github.com/AuthMe/ConfigMe/issues/58))
- Use UTF-8 as default charset & allow to override it in YAML resource and reader

:blue_book: [All changes in 1.0](https://github.com/AuthMe/ConfigMe/milestone/8?closed=1)

## ConfigMe 0.x
#### 0.4.1 (2018-01-14)
- Fix `Optional` fields not being able to be saved in bean properties ([#51](https://github.com/AuthMe/ConfigMe/issues/51))

:blue_book: [All changes in 0.4.1](https://github.com/AuthMe/ConfigMe/milestone/7?closed=1)

#### 0.4 (2017-02-19)
- Add support for `Optional`
  - New optional property type `OptionalProperty`
  - Support `Optional<?>` fields in beans
- Fix export of empty map (now `{}`)
- Add shorthand method for creating a SettingsManager from a YAML file
- Improve handling of generics in the mapper
- Technical improvements
  - Avoid Map with `Class<?>` keys
  - Add `@Documented` on relevant annotations

:blue_book: [All changes in 0.4](https://github.com/AuthMe/ConfigMe/milestone/4?closed=1)

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
