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

import org.junit.jupiter.api.Test;

class AssertjSameSizeAsTest {

    @Test
    void testFix() {
        fix().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array).hasSize(list.size());",
                        "    assertThat(array).hasSize(string.length());",
                        "    assertThat(string).hasSize(map.size());",
                        "    assertThat(list).hasSize(array.length);",
                        "    assertThat(string).hasSize((map.size()));",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array).hasSameSizeAs(list);",
                        "    assertThat(array).hasSameSizeAs(string);",
                        "    assertThat(string).hasSameSizeAs(map);",
                        "    assertThat(list).hasSameSizeAs(array);",
                        "    assertThat(string).hasSameSizeAs(map);",
                        "  }",
                        "}")
                .doTest();
    }

    private RefactoringValidator fix() {
        return RefactoringValidator.of(new AssertjRefactoring(new AssertjSameSizeAs()), getClass());
    }
}
