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

public class AssertjMapContainsKeyTest {

    @Test
    public void contains_simple() {
        assumeThat(System.getProperty("java.specification.version"))
                .describedAs("Refaster does not currently support fluent refactors on java 11")
                .isEqualTo("1.8");
        RefasterTestHelper.forRefactoring(AssertjMapContainsKey.class)
                .withInputLines(
                        "Test",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Map;",
                        "public class Test {",
                        "  void f(Map<String, Object> in) {",
                        "    assertThat(in.keySet().contains(\"foo\")).isTrue();",
                        "    assertThat(in.containsKey(\"foo\")).isTrue();",
                        "    assertThat(in.get(\"foo\")).isNotNull();",
                        "  }",
                        "}")
                .hasOutputLines(
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Map;",
                        "public class Test {",
                        "  void f(Map<String, Object> in) {",
                        "    assertThat(in).containsKey(\"foo\");",
                        "    assertThat(in).containsKey(\"foo\");",
                        "    assertThat(in).containsKey(\"foo\");",
                        "  }",
                        "}");
    }

    @Test
    public void contains_description() {
        assumeThat(System.getProperty("java.specification.version"))
                .describedAs("Refaster does not currently support fluent refactors on java 11")
                .isEqualTo("1.8");
        RefasterTestHelper.forRefactoring(AssertjMapContainsKeyWithDescription.class)
                .withInputLines(
                        "Test",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Map;",
                        "public class Test {",
                        "  void f(Map<String, Object> in) {",
                        "    assertThat(in.keySet().contains(\"foo\")).describedAs(\"desc\").isTrue();",
                        "    assertThat(in.containsKey(\"foo\")).describedAs(\"desc\").isTrue();",
                        "    assertThat(in.get(\"foo\")).describedAs(\"desc\").isNotNull();",
                        "  }",
                        "}")
                .hasOutputLines(
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Map;",
                        "public class Test {",
                        "  void f(Map<String, Object> in) {",
                        "    assertThat(in).describedAs(\"desc\").containsKey(\"foo\");",
                        "    assertThat(in).describedAs(\"desc\").containsKey(\"foo\");",
                        "    assertThat(in).describedAs(\"desc\").containsKey(\"foo\");",
                        "  }",
                        "}");
    }

    @Test
    public void notContain_simple() {
        assumeThat(System.getProperty("java.specification.version"))
                .describedAs("Refaster does not currently support fluent refactors on java 11")
                .isEqualTo("1.8");
        RefasterTestHelper.forRefactoring(AssertjMapDoesNotContainKey.class)
                .withInputLines(
                        "Test",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Map;",
                        "public class Test {",
                        "  void f(Map<String, Object> in) {",
                        "    assertThat(in.keySet().contains(\"foo\")).isFalse();",
                        "    assertThat(in.containsKey(\"foo\")).isFalse();",
                        "    assertThat(in.get(\"foo\")).isNull();",
                        "  }",
                        "}")
                .hasOutputLines(
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Map;",
                        "public class Test {",
                        "  void f(Map<String, Object> in) {",
                        "    assertThat(in).doesNotContainKey(\"foo\");",
                        "    assertThat(in).doesNotContainKey(\"foo\");",
                        "    assertThat(in).doesNotContainKey(\"foo\");",
                        "  }",
                        "}");
    }

    @Test
    public void notContain_description() {
        assumeThat(System.getProperty("java.specification.version"))
                .describedAs("Refaster does not currently support fluent refactors on java 11")
                .isEqualTo("1.8");
        RefasterTestHelper.forRefactoring(AssertjMapDoesNotContainKeyWithDescription.class)
                .withInputLines(
                        "Test",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Map;",
                        "public class Test {",
                        "  void f(Map<String, Object> in) {",
                        "    assertThat(in.keySet().contains(\"foo\")).describedAs(\"desc\").isFalse();",
                        "    assertThat(in.containsKey(\"foo\")).describedAs(\"desc\").isFalse();",
                        "    assertThat(in.get(\"foo\")).describedAs(\"desc\").isNull();",
                        "  }",
                        "}")
                .hasOutputLines(
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.Map;",
                        "public class Test {",
                        "  void f(Map<String, Object> in) {",
                        "    assertThat(in).describedAs(\"desc\").doesNotContainKey(\"foo\");",
                        "    assertThat(in).describedAs(\"desc\").doesNotContainKey(\"foo\");",
                        "    assertThat(in).describedAs(\"desc\").doesNotContainKey(\"foo\");",
                        "  }",
                        "}");
    }
}
