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

public final class AssertjOptionalContainsTest {

    @Test
    public void test() {
        assumeThat(System.getProperty("java.specification.version"))
                .describedAs("Refaster does not currently support fluent refactors on java 11")
                .isEqualTo("1.8");
        RefasterTestHelper.forRefactoring(AssertjOptionalContains.class)
                .withInputLines(
                        "Test",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Optional;",
                        "public class Test<String> {",
                        "  void f(Optional<String> in, String out) {",
                        "    assertThat(in.get()).isEqualTo(out);",
                        "    assertThat(in).isEqualTo(Optional.of(out));",
                        "    assertThat(in.isPresent() && in.get().equals(out)).isTrue();",
                        "  }",
                        "  void g(Optional<String> in, String out) {",
                        "    assertThat(in).isPresent();",
                        "    assertThat(in).hasValue(out);",
                        "  }",
                        "  void h(Optional<String> in, String out) {",
                        "    assertThat(in).isPresent();",
                        "    assertThat(in).contains(out);",
                        "  }",
                        "}")
                .hasOutputLines(
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Optional;",
                        "public class Test<String> {",
                        "  void f(Optional<String> in, String out) {",
                        "    assertThat(in).contains(out);",
                        "    assertThat(in).contains(out);",
                        "    assertThat(in).contains(out);",
                        "  }",
                        "  void g(Optional<String> in, String out) {",
                        "    assertThat(in).contains(out);",
                        "    ",
                        "  }",
                        "  void h(Optional<String> in, String out) {",
                        "    assertThat(in).contains(out);",
                        "    ",
                        "  }",
                        "}");
    }

    @Test
    public void testWithDescription() {
        assumeThat(System.getProperty("java.specification.version"))
                .describedAs("Refaster does not currently support fluent refactors on java 11")
                .isEqualTo("1.8");
        RefasterTestHelper.forRefactoring(AssertjOptionalContainsWithDescription.class)
                .withInputLines(
                        "Test",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Optional;",
                        "public class Test<String> {",
                        "  void f(Optional<String> in, String out) {",
                        "    assertThat(in.get()).describedAs(\"desc\").isEqualTo(out);",
                        "    assertThat(in).describedAs(\"desc\").isEqualTo(Optional.of(out));",
                        "    assertThat(in.isPresent() && in.get().equals(out)).describedAs(\"desc\").isTrue();",
                        "  }",
                        "}")
                .hasOutputLines(
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Optional;",
                        "public class Test<String> {",
                        "  void f(Optional<String> in, String out) {",
                        "    assertThat(in).describedAs(\"desc\").contains(out);",
                        "    assertThat(in).describedAs(\"desc\").contains(out);",
                        "    assertThat(in).describedAs(\"desc\").contains(out);",
                        "  }",
                        "}");
    }

    @Test
    public void testWithDescriptionRedundant() {
        assumeThat(System.getProperty("java.specification.version"))
                .describedAs("Refaster does not currently support fluent refactors on java 11")
                .isEqualTo("1.8");
        RefasterTestHelper.forRefactoring(AssertjOptionalContainsRedundantWithDescription.class)
                .withInputLines(
                        "Test",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Optional;",
                        "public class Test<String> {",
                        "  void f(Optional<String> in, String out) {",
                        "    assertThat(in).describedAs(\"a\").isPresent();",
                        "    assertThat(in).describedAs(\"b\").hasValue(out);",
                        "  }",
                        "  void g(Optional<String> in, String out) {",
                        "    assertThat(in).describedAs(\"a\").isPresent();",
                        "    assertThat(in).describedAs(\"b\").contains(out);",
                        "  }",
                        "}")
                .hasOutputLines(
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Optional;",
                        "public class Test<String> {",
                        "  void f(Optional<String> in, String out) {",
                        "    assertThat(in).describedAs(\"b\").contains(out);",
                        "    ",
                        "  }",
                        "  void g(Optional<String> in, String out) {",
                        "    assertThat(in).describedAs(\"b\").contains(out);",
                        "    ",
                        "  }",
                        "}");
    }
}
