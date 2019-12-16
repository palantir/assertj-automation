<p align="right">
<a href="https://autorelease.general.dmz.palantir.tech/palantir/assertj-automation"><img src="https://img.shields.io/badge/Perform%20an-Autorelease-success.svg" alt="Autorelease"></a>
</p>

# assertj-automation

_Automatic code rewriting for AssertJ using error-prone and refaster._

This repo publishes two jars:

- `com.palantir.assertj-automation:assertj-error-prone`
- `com.palantir.assertj-automation:assertj-refaster-rules`

## Usage

Error-prone is maintained by Google and can be used with [Bazel, Maven, Gradle, Ant](https://errorprone.info/docs/installation). Use the following Gradle:

```gradle
plugins {
  // we assume you are already using the Java plugin
  id "net.ltgt.errorprone" version "0.6"
}

dependencies {
  annotationProcessor "com.palantir.assertj:assertj-error-prone:<latest>" // see badge above

  errorprone "com.google.errorprone:error_prone_core:2.3.4"
  errorproneJavac "com.google.errorprone:javac:9+181-r4173-1"
}

tasks.withType(JavaCompile) {
  options.errorprone.errorproneArgs += [
    '-Xep:AssertJPrimitiveComparison:ERROR',
    // ... include other rules too
  ]
}
```

<!-- TODO(dfox): I don't think refaster is actually usable from the ltgt plugin?? -->

### Shorthand usage

Palantir's [Baseline](https://github.com/palantir/gradle-baseline) family of plugins includes all this assertj automation. Run `./gradlew compileTestJava -PerrorProneApply -PrefasterApply` to apply the fixes.

```
plugins {
  id 'com.palantir.baseline` version '2.43.0'
}
```

<!-- TODO(dfox): mention the formatDiff command -->

## Why

### Improve failure messages in existing codebases

Test failures like the following can be quite frustrating to debug:

```
Expected :true
Actual   :false
```

By tweaking the assertion code slightly, the failure message can be vastly improved, allowing an engineer to quickly diagnose what went wrong - perhaps they left a `foo` in recently:

```
java.lang.AssertionError:
Expected size:<7> but was:<8> in:
<["Africa", "Asia", "North America", "South America", "Antarctica", "Australia", "foo"]>
```

Here is the code-change for the example above.

```diff
-assertTrue(continents.size() == 7);
+assertThat(continents).hasSize(7);
```

There _many_ more sub-optimal patterns that this automation can detect and fix. By codifying these patterns into tooling that can be run local and on CI, we can reduce the burden on code reviewers because all contributions will already comply with AssertJ best practises.

These fixes were also helpful to facilitate the move from JUnit 4 -> JUnit 5, as we were able to automatically replace raw JUnit4 assertions (e.g. [assertTrue and assertEquals](https://junit.org/junit4/javadoc/4.8/org/junit/Assert.html)) in hundreds of internal codebases before actually doing the migration.

### Consolidate on a single assertions library

When codebases use a mixture of Hamcrest, AssertJ, raw JUnit assertions (both 4 and 5), Google Truth etc, contributors can be unsure which to use, leading to unnecessary back-and-forth during code review if they happened to pick the wrong one.

In practise, because many of these libraries are quite similar, just picking one and committing to it is a reasonable strategy.  We picked AssertJ because its fluent API is has nice auto-completion properties and it's nicely extensible.
