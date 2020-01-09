/*
 * (c) Copyright 2020 Palantir Technologies Inc. All rights reserved.
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

import org.junit.jupiter.api.Test;

class AssertjBooleanConjunctionTest {

    @Test
    void testFix() {
        test().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "public class Test {",
                        "  void f(boolean bool1, boolean bool2) {",
                        "    assertThat(bool1 && bool2).isTrue();",
                        "  }",
                        "  void g(boolean bool1, boolean bool2) {",
                        "    assertThat(bool1 && bool2).describedAs(\"desc\").isTrue();",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "public class Test {",
                        "  void f(boolean bool1, boolean bool2) {",
                        "    assertThat(bool1).isTrue();",
                        "assertThat(bool2).isTrue();",
                        "  }",
                        "  void g(boolean bool1, boolean bool2) {",
                        "    assertThat(bool1).describedAs(\"desc\").isTrue();",
                        "assertThat(bool2).describedAs(\"desc\").isTrue();",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void testNegativeLambda() {
        test().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "public class Test {",
                        "  Runnable f(boolean bool1, boolean bool2) {",
                        // Rewriting this would break the lambda structure
                        "    return () -> assertThat(bool1 && bool2).isTrue();",
                        "  }",
                        "}")
                .expectUnchanged()
                .doTest();
    }

    private RefactoringValidator test() {
        return RefactoringValidator.of(new AssertjRefactoring(new AssertjBooleanConjunction()), getClass());
    }
}
