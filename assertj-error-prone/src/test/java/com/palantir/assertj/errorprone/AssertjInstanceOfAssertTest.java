/*
 * (c) Copyright 2024 Palantir Technologies Inc. All rights reserved.
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

package com.palantir.assertj.errorprone;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import org.junit.jupiter.api.Test;

class AssertjInstanceOfAssertTest {

    @Test
    void test_instanceof() {
        fix().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "public class Test {",
                        "  void f(Object o) {",
                        "    assertThat(o instanceof String).isTrue();",
                        "    assertThat(o instanceof String).describedAs(\"desc\").isTrue();",
                        "    assertThat(o instanceof String).isFalse();",
                        "    assertThat(o instanceof String).describedAs(\"desc\").isFalse();",
                        "",
                        "    assertThat(!(o instanceof String)).isTrue();",
                        "    assertThat(!(o instanceof String)).describedAs(\"desc\").isTrue();",
                        "    assertThat(!(o instanceof String)).isFalse();",
                        "    assertThat(!(o instanceof String)).describedAs(\"desc\").isFalse();",
                        "",
                        "    assertThat(o.toString() instanceof String).isTrue();",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "public class Test {",
                        "  void f(Object o) {",
                        "    assertThat(o).isInstanceOf(String.class);",
                        "    assertThat(o).describedAs(\"desc\").isInstanceOf(String.class);",
                        "    assertThat(o).isNotInstanceOf(String.class);",
                        "    assertThat(o).describedAs(\"desc\").isNotInstanceOf(String.class);",
                        "",
                        "    assertThat(o).isNotInstanceOf(String.class);",
                        "    assertThat(o).describedAs(\"desc\").isNotInstanceOf(String.class);",
                        "    assertThat(o).isInstanceOf(String.class);",
                        "    assertThat(o).describedAs(\"desc\").isInstanceOf(String.class);",
                        "",
                        "    assertThat(o.toString()).isInstanceOf(String.class);",
                        "  }",
                        "}")
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    private RefactoringValidator fix() {
        return RefactoringValidator.of(new AssertjRefactoring(new AssertjInstanceOfAssert()), getClass());
    }
}
