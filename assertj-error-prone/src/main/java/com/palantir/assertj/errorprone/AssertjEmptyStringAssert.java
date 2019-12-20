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

import com.google.auto.service.AutoService;
import com.google.common.collect.Iterables;
import com.google.errorprone.VisitorState;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.fixes.SuggestedFixes;
import com.google.errorprone.matchers.ChildMultiMatcher;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import java.util.List;
import java.util.Optional;

@AutoService(AssertjChecker.class)
public final class AssertjEmptyStringAssert implements AssertjChecker {

    private static final String DESCRIPTION =
            "Prefer using AssertJ isEmpty/isNotEmpty matchers instead of equality checks with a constant \"\".";

    private static final Matcher<ExpressionTree> stringEqualMatcher = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.AbstractCharSequenceAssert")
            .named("isEqualTo");

    private static final Matcher<ExpressionTree> stringNotEqualMatcher = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.AbstractCharSequenceAssert")
            .named("isNotEqualTo");

    private static final Matcher<ExpressionTree> empty = Matchers.ignoreParens(Matchers.stringLiteral(""));

    private static final Matcher<ExpressionTree> matcher = Matchers.methodInvocation(
            Matchers.anyOf(stringEqualMatcher, stringNotEqualMatcher),
            ChildMultiMatcher.MatchType.LAST,
            Matchers.anyOf(empty));

    @Override
    public Optional<AssertjCheckerResult> matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        if (!matcher.matches(tree, state)) {
            return Optional.empty();
        }
        List<? extends ExpressionTree> arguments = tree.getArguments();
        if (arguments.size() != 1) {
            return Optional.empty();
        }
        ExpressionTree argument = Iterables.getOnlyElement(arguments);
        boolean expectEmpty = stringEqualMatcher.matches(tree, state);
        return Optional.of(AssertjCheckerResult.builder()
                .description(DESCRIPTION)
                .fix(SuggestedFix.builder()
                        .merge(SuggestedFixes.renameMethodInvocation(
                                tree, expectEmpty ? "isEmpty" : "isNotEmpty", state))
                        .replace(argument, "")
                        .build())
                .build());
    }
}
