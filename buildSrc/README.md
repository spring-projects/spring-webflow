# Spring Webflow Build

This folder contains the custom plugins and conventions for the Spring Webflow build.
They are declared in the `build.gradle` file in this folder.

## Build Conventions

### Compiler conventions

The `org.springframework.build.compile` plugin applies the Java compiler conventions to the build.
By default, the build compiles sources with Java `1.8` source and target compatibility.
You can test a different source compatibility version on the CLI with a project property like:

```
./gradlew test -PjavaSourceVersion=11
```

## Build Plugins

## Optional dependencies

The `org.springframework.build.optional-dependencies` plugin creates a new `optional`
Gradle configuration - it adds the dependencies to the project's compile and runtime classpath
but doesn't affect the classpath of dependent projects.
This plugin does not provide a `provided` configuration, as the native `compileOnly` and `testCompileOnly`
configurations are preferred.