/*
 * (c) Copyright 2019 Palantir Technologies Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.assertj.refaster;

import static org.assertj.core.api.Assumptions.assumeThat;

import com.palantir.baseline.refaster.RefasterTestHelper;
import org.junit.jupiter.api.Test;

public class AssertjOptionalPresenceTest {

    @Test
    public void isPresent_simple() {
        RefasterTestHelper.forRefactoring(AssertjOptionalIsPresent.class)
                .withInputLines(
                        "Test",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Optional;",
                        "public class Test {",
                        "  void f(Optional<String> in) {",
                        "    assertThat(in.isPresent()).isTrue();",
                        "    assertThat(!in.isPresent()).isFalse();",
                        "    assertThat(in.isEmpty()).isFalse();",
                        "    assertThat(!in.isEmpty()).isTrue();",
                        "  }",
                        "}")
                .hasOutputLines(
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Optional;",
                        "public class Test {",
                        "  void f(Optional<String> in) {",
                        "    assertThat(in).isPresent();",
                        "    assertThat(in).isPresent();",
                        "    assertThat(in).isPresent();",
                        "    assertThat(in).isPresent();",
                        "  }",
                        "}");
    }

    @Test
    public void isPresent_description() {
        RefasterTestHelper.forRefactoring(AssertjOptionalIsPresentWithDescription.class)
                .withInputLines(
                        "Test",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Optional;",
                        "public class Test {",
                        "  void f(Optional<String> in) {",
                        "    assertThat(in.isPresent()).describedAs(\"desc\").isTrue();",
                        "    assertThat(!in.isPresent()).describedAs(\"desc\").isFalse();",
                        "    assertThat(in.isEmpty()).describedAs(\"desc\").isFalse();",
                        "    assertThat(!in.isEmpty()).describedAs(\"desc\").isTrue();",
                        "  }",
                        "}")
                .hasOutputLines(
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Optional;",
                        "public class Test {",
                        "  void f(Optional<String> in) {",
                        "    assertThat(in).describedAs(\"desc\").isPresent();",
                        "    assertThat(in).describedAs(\"desc\").isPresent();",
                        "    assertThat(in).describedAs(\"desc\").isPresent();",
                        "    assertThat(in).describedAs(\"desc\").isPresent();",
                        "  }",
                        "}");
    }

    @Test
    public void isEmpty_simple() {
        assumeThat(System.getProperty("java.specification.version"))
                .describedAs("Refaster does not currently support fluent refactors on java 11")
                .isEqualTo("1.8");
        RefasterTestHelper.forRefactoring(AssertjOptionalIsEmpty.class)
                .withInputLines(
                        "Test",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Optional;",
                        "public class Test {",
                        "  void f(Optional<String> in) {",
                        "    assertThat(in.isPresent()).isFalse();",
                        "    assertThat(!in.isPresent()).isTrue();",
                        "    assertThat(in.isEmpty()).isTrue();",
                        "    assertThat(!in.isEmpty()).isFalse();",
                        "    assertThat(in).isEqualTo(Optional.empty());",
                        "    assertThat(Optional.empty()).isEqualTo(in);",
                        "  }",
                        "}")
                .hasOutputLines(
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Optional;",
                        "public class Test {",
                        "  void f(Optional<String> in) {",
                        "    assertThat(in).isEmpty();",
                        "    assertThat(in).isEmpty();",
                        "    assertThat(in).isEmpty();",
                        "    assertThat(in).isEmpty();",
                        "    assertThat(in).isEmpty();",
                        "    assertThat(in).isEmpty();",
                        "  }",
                        "}");
    }

    @Test
    public void isEmpty_description() {
        assumeThat(System.getProperty("java.specification.version"))
                .describedAs("Refaster does not currently support fluent refactors on java 11")
                .isEqualTo("1.8");
        RefasterTestHelper.forRefactoring(AssertjOptionalIsEmptyWithDescription.class)
                .withInputLines(
                        "Test",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Optional;",
                        "public class Test {",
                        "  void f(Optional<String> in) {",
                        "    assertThat(in.isPresent()).describedAs(\"desc\").isFalse();",
                        "    assertThat(!in.isPresent()).describedAs(\"desc\").isTrue();",
                        "    assertThat(in.isEmpty()).describedAs(\"desc\").isTrue();",
                        "    assertThat(!in.isEmpty()).describedAs(\"desc\").isFalse();",
                        "    assertThat(in).describedAs(\"desc\").isEqualTo(Optional.empty());",
                        "    assertThat(Optional.empty()).describedAs(\"desc\").isEqualTo(in);",
                        "  }",
                        "}")
                .hasOutputLines(
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Optional;",
                        "public class Test {",
                        "  void f(Optional<String> in) {",
                        "    assertThat(in).describedAs(\"desc\").isEmpty();",
                        "    assertThat(in).describedAs(\"desc\").isEmpty();",
                        "    assertThat(in).describedAs(\"desc\").isEmpty();",
                        "    assertThat(in).describedAs(\"desc\").isEmpty();",
                        "    assertThat(in).describedAs(\"desc\").isEmpty();",
                        "    assertThat(in).describedAs(\"desc\").isEmpty();",
                        "  }",
                        "}");
    }
}
