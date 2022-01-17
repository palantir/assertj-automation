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

class AssertjCollectionAssertTest {

    @Test
    public void fix_map() {
        test().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import com.google.common.collect.ImmutableMap;",
                        "import java.util.HashMap;",
                        "import java.util.Map;",
                        "public class Test {",
                        "  void f(Map<String, String> actual, Map<String, String> expected) {",
                        "    assertThat(actual).isEqualTo(expected);",
                        "    assertThat(actual).isEqualTo(new HashMap<>());",
                        "    assertThat(actual).isEqualTo(Map.of());",
                        "    assertThat(actual).isEqualTo(ImmutableMap.of());",
                        "    assertThat(actual).isEqualTo(Map.of(\"foo\", \"bar\"));",
                        "    assertThat(actual).isEqualTo(ImmutableMap.of(\"foo\", \"bar\"));",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import com.google.common.collect.ImmutableMap;",
                        "import java.util.HashMap;",
                        "import java.util.Map;",
                        "public class Test {",
                        "  void f(Map<String, String> actual, Map<String, String> expected) {",
                        "    assertThat(actual).containsExactlyInAnyOrderEntriesOf(expected);",
                        "    assertThat(actual).containsExactlyInAnyOrderEntriesOf(new HashMap<>());",
                        "    assertThat(actual).isEmpty();",
                        "    assertThat(actual).isEmpty();",
                        "    assertThat(actual).containsExactlyInAnyOrderEntriesOf(Map.of(\"foo\", \"bar\"));",
                        "    assertThat(actual).containsExactlyInAnyOrderEntriesOf(ImmutableMap.of(\"foo\", \"bar\"));",
                        "  }",
                        "}")
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    @Test
    public void fix_list() {
        test().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import com.google.common.collect.ImmutableList;",
                        "import java.util.ArrayList;",
                        "import java.util.List;",
                        "public class Test {",
                        "  void f(List<String> actual, List<String> expected) {",
                        "    assertThat(actual).isEqualTo(expected);",
                        "    assertThat(actual).isEqualTo(new ArrayList<>());",
                        "    assertThat(actual).isEqualTo(List.of());",
                        "    assertThat(actual).isEqualTo(ImmutableList.of());",
                        "    assertThat(actual).isEqualTo(List.of(\"foo\", \"bar\"));",
                        "    assertThat(actual).isEqualTo(ImmutableList.of(\"foo\", \"bar\"));",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import com.google.common.collect.ImmutableList;",
                        "import java.util.ArrayList;",
                        "import java.util.List;",
                        "public class Test {",
                        "  void f(List<String> actual, List<String> expected) {",
                        "    assertThat(actual).containsExactlyElementsOf(expected);",
                        "    assertThat(actual).containsExactlyElementsOf(new ArrayList<>());",
                        "    assertThat(actual).isEmpty();",
                        "    assertThat(actual).isEmpty();",
                        "    assertThat(actual).containsExactly(\"foo\", \"bar\");",
                        "    assertThat(actual).containsExactly(\"foo\", \"bar\");",
                        "  }",
                        "}")
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    @Test
    public void fix_set() {
        test().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import com.google.common.collect.ImmutableSet;",
                        "import java.util.HashSet;",
                        "import java.util.Set;",
                        "public class Test {",
                        "  void f(Set<String> actual, Set<String> expected) {",
                        "    assertThat(actual).isEqualTo(expected);",
                        "    assertThat(actual).isEqualTo(new HashSet<>());",
                        "    assertThat(actual).isEqualTo(Set.of());",
                        "    assertThat(actual).isEqualTo(ImmutableSet.of());",
                        "    assertThat(actual).isEqualTo(Set.of(\"foo\", \"bar\"));",
                        "    assertThat(actual).isEqualTo(ImmutableSet.of(\"foo\", \"bar\"));",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import com.google.common.collect.ImmutableSet;",
                        "import java.util.HashSet;",
                        "import java.util.Set;",
                        "public class Test {",
                        "  void f(Set<String> actual, Set<String> expected) {",
                        "    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);",
                        "    assertThat(actual).containsExactlyInAnyOrderElementsOf(new HashSet<>());",
                        "    assertThat(actual).isEmpty();",
                        "    assertThat(actual).isEmpty();",
                        "    assertThat(actual).containsExactlyInAnyOrder(\"foo\", \"bar\");",
                        "    assertThat(actual).containsExactlyInAnyOrder(\"foo\", \"bar\");",
                        "  }",
                        "}")
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    private RefactoringValidator test() {
        return RefactoringValidator.of(new AssertjRefactoring(new AssertjCollectionAssert()), getClass());
    }
}
