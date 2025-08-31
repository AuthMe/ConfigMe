# Contributing

Contributions are very much welcome! This can be in form of pull requests, issues, providing opinions on existing issues
or pull request reviews. Moreover, let us know if you have a nice project that can be added to the list of
[integrators](https://github.com/AuthMe/ConfigMe/wiki/Integrators)—seeing ConfigMe "out in the wild" helps understand
the needs and the pain points of users!

ConfigMe is managed heavily based on GitHub issues, so you're invited to open a new issue also for questions
(like [#126](https://github.com/AuthMe/ConfigMe/issues/126)) or for any other discussion points. Please discuss larger
code changes first, either by creating an issue or by writing in the [`#configme` channel](https://discord.com/channels/295623711485198357/1143605240520769547)
of the AuthMe Discord server.

## General conventions

- If you're using IntelliJ, please install the Checkstyle plugin and configure it to use the rules from
  `.checkstyle.xml`, located in the project's root directory.
- Commit messages ideally start with the issue number they relate to, e.g. `#229 Remove option to split map keys by "."`
- Please use sensibly-sized commits (e.g. don't commit every change separately).
- Use American English in code and comments: _initialize_, _color_, etc.

No sweat if some points aren't met since they can be addressed in pull requests, and PRs can be squashed.

## Code style

Please try to match the current code style. A few pointers:
- Indent with spaces, not tabs
- Max line length is 120 (Checkstyle, mentioned above, should catch lines that are too long)
- Do not use wildcard imports
- Use camel-case as defined in the [Google Java style guide](https://google.github.io/styleguide/javaguide.html#s5.3-camel-case)
  (i.e. use names like `setHttpLink`, not `setHTTPLink`)

### Javadoc

- Use the third-person form of verbs and not the imperative: "Gets all properties" and not "Get all properties".
- Avoid `@link` to common JDK types like `Map` or `String`. These types are visible from the method signature—manually
  linking them adds no value.
- Try to provide context rather than just un-camel-case-ing the thing you're describing
  (e.g. "Gets the result" on a method "getResult" does not provide any additional information).
- Do not close `<p>` tags in Javadoc text. This is in line with the JDK and Oracle's [recommendations](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html#format).
- Do not align parameter descriptions (in the form of `@param name     the name` because of
  another longer param name).
- Please refer to the existing Javadoc for preferences regarding casing, line breaks and similar.

Example of Javadoc on a method:
```java
    /**
     * Creates a builder, using the given YAML file to use as property resource.
     *
     * @param file the YAML file to use
     * @return settings manager builder
     */
    public static @NotNull SettingsManagerBuilder withYamlFile(@NotNull Path file) {
```

However, there's **no pressure** if some of these things are not met since they can be addressed afterwards :)

## Unit tests

Unit tests are written in BDD style. A normal test method:
- starts with "should" and describes the expected outcome, e.g. `shouldReadFromFile`
- has a section `// given` that sets everything up for the method to be tested
- has a section `// when` that calls the method to be tested
- has a section `// then` that verifies the expected outcome

Best way to get a feel for the desired structure of a test method is to view existing ones,
such as the methods in `MigrationUtilsTest`.
