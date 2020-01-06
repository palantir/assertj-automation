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
                        "    assertThat(list).hasSize((array.length));",
                        "    assertThat(string).hasSize(map.size());",
                        "    assertThat(map).hasSize(map.size());",
                        "    assertThat(string).hasSize(string.length());",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.*;",
                        "public class Test {",
                        "  void test(List<String> list, String string, Map<String, String> map, String[] array) {",
                        "    assertThat(array).hasSameSizeAs(list);",
                        "    assertThat(array).hasSize(string.length());",
                        "    assertThat(string).hasSize(map.size());",
                        "    assertThat(list).hasSameSizeAs(array);",
                        "    assertThat(string).hasSize(map.size());",
                        "    assertThat(map).hasSameSizeAs(map);",
                        "    assertThat(string).hasSameSizeAs(string);",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void test() {
        fix().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.List;",
                        "import java.util.Collection;",
                        "public class Test {",
                        "  void f(List<String> a, Collection<String> b, Iterable<String> c, List<String> target) {",
                        "    assertThat(a).hasSize(target.size());",
                        "    assertThat(b).hasSize(target.size());",
                        "    assertThat(c).hasSize(target.size());",
                        "    assertThat(a).describedAs(\"desc\").hasSize(target.size());",
                        "    assertThat(b).describedAs(\"desc\").hasSize(target.size());",
                        "    assertThat(c).describedAs(\"desc\").hasSize(target.size());",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.List;",
                        "import java.util.Collection;",
                        "public class Test {",
                        "  void f(List<String> a, Collection<String> b, Iterable<String> c, List<String> target) {",
                        "    assertThat(a).hasSameSizeAs(target);",
                        "    assertThat(b).hasSameSizeAs(target);",
                        "    assertThat(c).hasSameSizeAs(target);",
                        "    assertThat(a).describedAs(\"desc\").hasSameSizeAs(target);",
                        "    assertThat(b).describedAs(\"desc\").hasSameSizeAs(target);",
                        "    assertThat(c).describedAs(\"desc\").hasSameSizeAs(target);",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void testArray() {
        fix().addInputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.List;",
                        "import java.util.Collection;",
                        "public class Test {",
                        "  void f(List<String> a, Collection<String> b, Iterable<String> c, String[] target) {",
                        "    assertThat(a).hasSize(target.length);",
                        "    assertThat(b).hasSize(target.length);",
                        "    assertThat(c).hasSize(target.length);",
                        "    assertThat(a).describedAs(\"desc\").hasSize(target.length);",
                        "    assertThat(b).describedAs(\"desc\").hasSize(target.length);",
                        "    assertThat(c).describedAs(\"desc\").hasSize(target.length);",
                        "    assertThat(c).describedAs(\"foo %s\", \"bar\").hasSize(target.length);",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import java.util.List;",
                        "import java.util.Collection;",
                        "public class Test {",
                        "  void f(List<String> a, Collection<String> b, Iterable<String> c, String[] target) {",
                        "    assertThat(a).hasSameSizeAs(target);",
                        "    assertThat(b).hasSameSizeAs(target);",
                        "    assertThat(c).hasSameSizeAs(target);",
                        "    assertThat(a).describedAs(\"desc\").hasSameSizeAs(target);",
                        "    assertThat(b).describedAs(\"desc\").hasSameSizeAs(target);",
                        "    assertThat(c).describedAs(\"desc\").hasSameSizeAs(target);",
                        "    assertThat(c).describedAs(\"foo %s\", \"bar\").hasSameSizeAs(target);",
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
                        "  void f(IterableMap imap, Map<String, String> map) {",
                        "    assertThat(map).hasSize(imap.size());",
                        "  }",
                        "  interface IterableMap extends Iterable<String>, Map<String, String> {}",
                        "}")
                .expectUnchanged()
                .doTest();
    }

    private RefactoringValidator fix() {
        return RefactoringValidator.of(new AssertjRefactoring(new AssertjSameSizeAs()), getClass());
    }
}
