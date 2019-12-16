<p align="right">
<a href="https://autorelease.general.dmz.palantir.tech/palantir/assertj-automation"><img src="https://img.shields.io/badge/Perform%20an-Autorelease-success.svg" alt="Autorelease"></a>
</p>

# assertj-automation

_Automatic code rewriting for AssertJ using error-prone and refaster._

## Usage



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

Note that this is just one example, there _many_ more sub-optimal patterns that this automation can auto-fix.

