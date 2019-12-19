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

class AssertjEqualityOrderTest {

    @Test
    void testFix() {
        fix().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.List;",
                        "public class Test {",
                        "  void test(int a, int b, String desc, boolean bool) {",
                        "    assertThat(a).describedAs(desc).isEqualTo(1);",
                        "    assertThat(1).describedAs(desc).isEqualTo(a);",
                        "    assertThat(a).isEqualTo(1);",
                        "    assertThat(1).isEqualTo(a);",
                        "    assertThat(a).isEqualTo(b);",
                        "    assertThat(1).isEqualTo(2);",
                        "    assertThat(1).isNotEqualTo(a);",
                        "    assertThat(1).isSameAs(a);",
                        "    assertThat(1).isNotSameAs(a);",
                        "    assertThat(false).isEqualTo(bool);",
                        "    assertThat(Boolean.FALSE).isEqualTo(bool);",
                        "    assertThat((1)).isEqualTo(a);",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.List;",
                        "public class Test {",
                        "  void test(int a, int b, String desc, boolean bool) {",
                        "    assertThat(a).describedAs(desc).isEqualTo(1);",
                        "    assertThat(a).describedAs(desc).isEqualTo(1);",
                        "    assertThat(a).isEqualTo(1);",
                        "    assertThat(a).isEqualTo(1);",
                        "    assertThat(a).isEqualTo(b);",
                        "    assertThat(1).isEqualTo(2);",
                        "    assertThat(a).isNotEqualTo(1);",
                        "    assertThat(a).isSameAs(1);",
                        "    assertThat(a).isNotSameAs(1);",
                        "    assertThat(bool).isEqualTo(false);",
                        "    assertThat(bool).isEqualTo(Boolean.FALSE);",
                        "    assertThat(a).isEqualTo((1));",
                        "  }",
                        "}")
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    private RefactoringValidator fix() {
        return RefactoringValidator.of(new AssertjRefactoring(new AssertjEqualityOrder()), getClass());
    }
}
