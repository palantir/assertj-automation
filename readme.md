<p align="right">
<a href="https://autorelease.general.dmz.palantir.tech/palantir/assertj-automation"><img src="https://img.shields.io/badge/Perform%20an-Autorelease-success.svg" alt="Autorelease"></a>
</p>

# assertj-automation [ ![Download](https://api.bintray.com/packages/palantir/releases/assertj-automation/images/download.svg) ](https://bintray.com/palantir/releases/assertj-automation/_latestVersion)

_Automatic code rewriting for AssertJ using error-prone and refaster._

## Why

### (1) Improve failure messages in existing codebases

By making code changes like the following, we can drastically improve failure messages:

```diff
-assertTrue(continents.size() == 7);
+assertThat(continents).hasSize(7);
```

From this hard-to-debug message:

```
Expected :true
Actual   :false
```

To this clearer example. An engineer can now quickly diagnose what went wrong - perhaps they left a `foo` in recently:

```
java.lang.AssertionError:
Expected size:<7> but was:<8> in:
<["Africa", "Asia", "Europe", "North America", "South America", "Antarctica", "Australia", "foo"]>
```

There _many_ more sub-optimal patterns that this automation can detect and fix. By codifying these patterns into tooling that can be run local and on CI, we can reduce the burden on code reviewers because all contributions will already comply with AssertJ best practises.

These fixes also help facilitate the move from JUnit 4 -> JUnit 5, as raw JUnit4 assertions can be eliminated (e.g. [assertTrue and assertEquals](https://junit.org/junit4/javadoc/4.8/org/junit/Assert.html)) before actually doing the JUnit 4 -> 5 migration.

### (2) Consolidate on a single assertions library

When codebases use a mixture of Hamcrest, AssertJ, raw JUnit assertions (both 4 and 5), Google Truth etc, contributors can be unsure which to use, leading to unnecessary back-and-forth during code review if they happened to pick the wrong one.

In practise, because many of these libraries are quite similar, just picking one and committing to it is a reasonable strategy.  We picked AssertJ because its fluent API is has nice auto-completion properties and it's nicely extensible.

## Usage: `net.ltgt.errorprone`

Error-prone is maintained by Google and can be used with [Bazel, Maven, Gradle, Ant](https://errorprone.info/docs/installation). Use the following Gradle:

```gradle
plugins {
  // we assume you are already using the Java plugin
  id "net.ltgt.errorprone" version "0.6"
}

dependencies {
  annotationProcessor "com.palantir.assertj-automation:assertj-error-prone:<latest>" // see badge above

  errorprone "com.google.errorprone:error_prone_core:2.3.4"
  errorproneJavac "com.google.errorprone:javac:9+181-r4173-1"
}

tasks.withType(JavaCompile) {
  options.errorprone.errorproneArgs += [
    '-Xep:PreferAssertj:ERROR',
    // ... include other rules too
  ]
}
```

_Note: refaster rules can't yet be applied from the `net.ltgt.errorprone` plugin, see the `baseline` plugin below._

## Alternative usage: `com.palantir.baseline`

Palantir's [Baseline](https://github.com/palantir/gradle-baseline) family of plugins sets up error-prone and allows applying auto-fixes from both refaster and error-prone. Run `./gradlew compileTestJava -PerrorProneApply -PrefasterApply` to apply the fixes.

```
plugins {
  id 'com.palantir.baseline` version '2.43.0'
}
```

To ensure the automatically refactored code remains readable by humans, we then run the `./gradlew formatDiff` command provided by [palantir-java-format](https://github.com/palantir/palantir-java-format). This surgically reformats the lines of code that were touched, while preserving the rest of the file.

```gradle
buildscript {
  dependencies{
    classpath 'com.palantir.javaformat:gradle-palantir-java-format:0.3.9'
  }
}
apply plugin: 'com.palantir.java-format'
```
