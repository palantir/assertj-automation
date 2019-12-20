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

class AssertjSizeTest {

    @Test
    void testFix_equal() {
        fix().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array.length).isEqualTo(1);",
                        "    assertThat(string.length()).isEqualTo(1);",
                        "    assertThat(map.size()).isEqualTo(1);",
                        "    assertThat(list.size()).isEqualTo(1);",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array).hasSize(1);",
                        "    assertThat(string).hasSize(1);",
                        "    assertThat(map).hasSize(1);",
                        "    assertThat(list).hasSize(1);",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void testFix_lt() {
        fix().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array.length).isLessThan(1);",
                        "    assertThat(string.length()).isLessThan(1);",
                        "    assertThat(map.size()).isLessThan(1);",
                        "    assertThat(list.size()).isLessThan(1);",
                        "    assertThat(map.keySet().size()).isLessThan(1);",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array).hasSizeLessThan(1);",
                        "    assertThat(string).hasSizeLessThan(1);",
                        "    assertThat(map).hasSizeLessThan(1);",
                        "    assertThat(list).hasSizeLessThan(1);",
                        "    assertThat(map.keySet()).hasSizeLessThan(1);",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void testFix_lte() {
        fix().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array.length).isLessThanOrEqualTo(1);",
                        "    assertThat(string.length()).isLessThanOrEqualTo(1);",
                        "    assertThat(map.size()).isLessThanOrEqualTo(1);",
                        "    assertThat(list.size()).isLessThanOrEqualTo(1);",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array).hasSizeLessThanOrEqualTo(1);",
                        "    assertThat(string).hasSizeLessThanOrEqualTo(1);",
                        "    assertThat(map).hasSizeLessThanOrEqualTo(1);",
                        "    assertThat(list).hasSizeLessThanOrEqualTo(1);",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void testFix_gt() {
        fix().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array.length).isGreaterThan(1);",
                        "    assertThat(string.length()).isGreaterThan(1);",
                        "    assertThat(map.size()).isGreaterThan(1);",
                        "    assertThat(list.size()).isGreaterThan(1);",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array).hasSizeGreaterThan(1);",
                        "    assertThat(string).hasSizeGreaterThan(1);",
                        "    assertThat(map).hasSizeGreaterThan(1);",
                        "    assertThat(list).hasSizeGreaterThan(1);",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void testFix_gte() {
        fix().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array.length).isGreaterThanOrEqualTo(1);",
                        "    assertThat(string.length()).isGreaterThanOrEqualTo(1);",
                        "    assertThat(map.size()).isGreaterThanOrEqualTo(1);",
                        "    assertThat(list.size()).isGreaterThanOrEqualTo(1);",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array).hasSizeGreaterThanOrEqualTo(1);",
                        "    assertThat(string).hasSizeGreaterThanOrEqualTo(1);",
                        "    assertThat(map).hasSizeGreaterThanOrEqualTo(1);",
                        "    assertThat(list).hasSizeGreaterThanOrEqualTo(1);",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void testFix_empty() {
        fix().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array.length).isZero();",
                        "    assertThat(string.length()).isZero();",
                        "    assertThat(map.size()).isZero();",
                        "    assertThat(list.size()).isZero();",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array).isEmpty();",
                        "    assertThat(string).isEmpty();",
                        "    assertThat(map).isEmpty();",
                        "    assertThat(list).isEmpty();",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void testFix_notEmpty() {
        fix().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array.length).isNotZero();",
                        "    assertThat(string.length()).isNotZero();",
                        "    assertThat(map.size()).isNotZero();",
                        "    assertThat(list.size()).isNotZero();",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array).isNotEmpty();",
                        "    assertThat(string).isNotEmpty();",
                        "    assertThat(map).isNotEmpty();",
                        "    assertThat(list).isNotEmpty();",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void testIterableMap() {
        fix().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(IMap<String, String> map) {",
                        "    assertThat(map.size()).isNotZero();",
                        "    assertThat(map.size()).isEqualTo(1);",
                        "  }",
                        "  interface IMap<K, V> extends Map<K, V>, Iterable<K> {}",
                        "}")
                .expectUnchanged()
                .doTest();
    }

    @Test
    void testIncompatibleTypes() {
        fix().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(Map<String, String> map) {",
                        "    assertThat(map.size()).isEqualTo(1L);",
                        "    assertThat(map.size()).isEqualTo(1L + 2L);",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(Map<String, String> map) {",
                        "    assertThat(map).hasSize((int) 1L);",
                        "    assertThat(map).hasSize((int) (1L + 2L));",
                        "  }",
                        "}")
                .doTest();
    }

    private RefactoringValidator fix() {
        return RefactoringValidator.of(new AssertjRefactoring(new AssertjSize()), getClass());
    }
}
