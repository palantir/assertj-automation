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

package com.palantir.assertj.errorprone;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import org.junit.jupiter.api.Test;

class AssertjBooleanAssertTest {

    @Test
    public void fix_isFalse() {
        test().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "public class Test {",
                        "  void f(boolean bool) {",
                        "    assertThat(bool).isEqualTo(false);",
                        "    assertThat(bool).isEqualTo(Boolean.FALSE);",
                        "    assertThat(bool).isNotEqualTo(true);",
                        "    assertThat(bool).isNotEqualTo(Boolean.TRUE);",
                        "    assertThat(bool).describedAs(\"desc\").isEqualTo(false);",
                        "    assertThat(bool).describedAs(\"desc\").isEqualTo(false);",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "public class Test {",
                        "  void f(boolean bool) {",
                        "    assertThat(bool).isFalse();",
                        "    assertThat(bool).isFalse();",
                        "    assertThat(bool).isFalse();",
                        "    assertThat(bool).isFalse();",
                        "    assertThat(bool).describedAs(\"desc\").isFalse();",
                        "    assertThat(bool).describedAs(\"desc\").isFalse();",
                        "  }",
                        "}")
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    @Test
    public void fix_isTrue() {
        test().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "public class Test {",
                        "  void f(boolean bool) {",
                        "    assertThat(bool).isEqualTo(true);",
                        "    assertThat(bool).isEqualTo(Boolean.TRUE);",
                        "    assertThat(bool).isNotEqualTo(false);",
                        "    assertThat(bool).isNotEqualTo(Boolean.FALSE);",
                        "    assertThat(bool).isNotEqualTo(Boolean.FALSE);",
                        "    assertThat(bool).describedAs(\"desc\").isEqualTo(true);",
                        "    assertThat(bool).describedAs(\"desc\").isEqualTo(Boolean.TRUE);",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "public class Test {",
                        "  void f(boolean bool) {",
                        "    assertThat(bool).isTrue();",
                        "    assertThat(bool).isTrue();",
                        "    assertThat(bool).isTrue();",
                        "    assertThat(bool).isTrue();",
                        "    assertThat(bool).isTrue();",
                        "    assertThat(bool).describedAs(\"desc\").isTrue();",
                        "    assertThat(bool).describedAs(\"desc\").isTrue();",
                        "  }",
                        "}")
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    private RefactoringValidator test() {
        return RefactoringValidator.of(new AssertjRefactoring(new AssertjBooleanAssert()), getClass());
    }
}
