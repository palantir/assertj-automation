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

class AssertjEmptyAssertTest {

    @Test
    public void fix_isEmpty() {
        test().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "public class Test {",
                        "  void f(String str) {",
                        "    assertThat(str).isEqualTo(\"\");",
                        "    assertThat(str).describedAs(\"desc\").isEqualTo(\"\");",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "public class Test {",
                        "  void f(String str) {",
                        "    assertThat(str).isEmpty();",
                        "    assertThat(str).describedAs(\"desc\").isEmpty();",
                        "  }",
                        "}")
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    @Test
    public void fix_isNotEmpty() {
        test().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "public class Test {",
                        "  void f(String str) {",
                        "    assertThat(str).isNotEqualTo(\"\");",
                        "    assertThat(str).describedAs(\"desc\").isNotEqualTo(\"\");",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "public class Test {",
                        "  void f(String str) {",
                        "    assertThat(str).isNotEmpty();",
                        "    assertThat(str).describedAs(\"desc\").isNotEmpty();",
                        "  }",
                        "}")
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    private RefactoringValidator test() {
        return RefactoringValidator.of(new AssertjRefactoring(new AssertjEmptyStringAssert()), getClass());
    }
}
