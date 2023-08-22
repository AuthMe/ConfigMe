# Contributing

Contributions are very much welcome! This can be in form of pull requests, issues, as well as providing opinions
in existing issues—or on pull requests. If you have a nice project that integrates ConfigMe, let us know as that 
also helps us get a better picture of the way ConfigMe is being used.

ConfigMe is managed heavily based on GitHub issues, so you're very much invited to open a new issue also for 
[questions](https://github.com/AuthMe/ConfigMe/issues/126) or any other discussion points. Please discuss larger code
changes first, either by creating an issue or by contacting the main maintainer, ljacqu (Discord: `cyrkel`).

# General conventions

Ideally:
- If you use IntelliJ, consider installing the Checkstyle plugin and configuring it to use the rules in 
  `.checkstyle.xml` of this project's root.
- Commit messages ideally start with the issue number they reference, e.g. `#229 Remove option to split map keys by "."`
- Please use sensibly-sized commits (e.g. don't commit every change separately)
- Use American English in code and comments (e.g. _initialize_, _color_)

No sweat if some points aren't met since they can be addressed during pull request review, and we can squash
pull requests when needed.

# Code style

Please try to match the current code style. A few pointers:
- Indent with spaces, not tabs
- Max line length is 120 (Checkstyle, mentioned above, should catch this)
- Do not use wildcard imports
- Use camel-case as defined in the [Google Java style guide](https://google.github.io/styleguide/javaguide.html#s5.3-camel-case)
  (i.e. use `setHttpLink` and not `setHTTPLink`)

## Javadoc

- Use the third-person form of verbs and not the imperative (e.g. "Gets all properties" and not "Get all properties")
- Avoid `@link` to common JDK types like `Map` or `String`. These types are visible from the method signature—manually
  linking them adds no value.
- Try to provide context rather than just un-camel-case-ing the thing you're describing 
  (e.g. "Gets the result" on a method "getResult" does not provide any additional information). 
- Do not close `<p>` tags in Javadoc text. This is in line with the JDK and Oracle's [recommendations](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html#format).
- Do not align parameter descriptions (in the form of `@param name     the name` because of
  another longer param name)
- Please see existing Javadoc to see preferences for uppercase and lowercase, line breaks, etc.

Example:
```
    /**
     * Creates a builder, using the given YAML file to use as property resource.
     *
     * @param file the YAML file to use
     * @return settings manager builder
     */
    public static @NotNull SettingsManagerBuilder withYamlFile(@NotNull Path file) {
```

Again: **no pressure** if some of these things are not met since they can be addressed afterwards :)

## Unit tests

Unit tests are written in BDD style. A normal test method:
- starts with "should" and describes the expected outcome, e.g. `shouldReadFromFile`
- has a section `// given` that sets everything up for the method to be tested
- has a section `// when` that calls the method to be tested
- has a section `// then` that verifies the expected outcome

Best way to get a feel for the desired structure of a test method is to view the existing ones.


