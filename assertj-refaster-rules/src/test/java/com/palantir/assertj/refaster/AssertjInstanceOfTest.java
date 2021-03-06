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

import com.palantir.baseline.refaster.RefasterTestHelper;
import org.junit.jupiter.api.Test;

public class AssertjInstanceOfTest {

    @Test
    public void simple() {
        RefasterTestHelper.forRefactoring(AssertjInstanceOf.class)
                .withInputLines(
                        "Test",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import com.google.common.collect.ImmutableList;",
                        "import java.util.List;",
                        "public class Test {",
                        "  void f(List<String> in, Object obj) {",
                        "    assertThat(in instanceof ImmutableList).isTrue();",
                        "    assertThat(obj instanceof String).isTrue();",
                        "  }",
                        "}")
                .hasOutputLines(
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import com.google.common.collect.ImmutableList;",
                        "import java.util.List;",
                        "public class Test {",
                        "  void f(List<String> in, Object obj) {",
                        "    assertThat(in).isInstanceOf(ImmutableList.class);",
                        "    assertThat(obj).isInstanceOf(String.class);",
                        "  }",
                        "}");
    }

    @Test
    public void description() {
        RefasterTestHelper.forRefactoring(AssertjInstanceOfWithDescription.class)
                .withInputLines(
                        "Test",
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import com.google.common.collect.ImmutableList;",
                        "import java.util.List;",
                        "public class Test {",
                        "  void f(List<String> in, Object obj) {",
                        "    assertThat(in instanceof ImmutableList).describedAs(\"desc\").isTrue();",
                        "    assertThat(obj instanceof String).describedAs(\"desc\").isTrue();",
                        "  }",
                        "}")
                .hasOutputLines(
                        "import static org.assertj.core.api.Assertions.assertThat;",
                        "import com.google.common.collect.ImmutableList;",
                        "import java.util.List;",
                        "public class Test {",
                        "  void f(List<String> in, Object obj) {",
                        "    assertThat(in).describedAs(\"desc\").isInstanceOf(ImmutableList.class);",
                        "    assertThat(obj).describedAs(\"desc\").isInstanceOf(String.class);",
                        "  }",
                        "}");
    }
}
