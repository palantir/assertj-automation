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
import com.google.errorprone.matchers.CompileTimeConstantExpressionMatcher;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import java.util.List;
import java.util.Optional;

@AutoService(AssertjChecker.class)
public final class AssertjEqualityOrder implements AssertjChecker {

    private static final String DESCRIPTION =
            "AssertJ assertThat values should be the result, which is compared to an expected value later in the call"
                    + " chain. Inverting the order results in confusing messaging on failure.";

    private static final Matcher<ExpressionTree> EQUAL_TO = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.Assert")
            .namedAnyOf("isEqualTo", "isNotEqualTo", "isSameAs", "isNotSameAs");

    private final Matcher<ExpressionTree> constant = Matchers.ignoreParens(Matchers.anyOf(
            Matchers.nonNullLiteral(),
            Matchers.booleanConstant(true),
            Matchers.booleanConstant(false),
            new CompileTimeConstantExpressionMatcher()));

    private final AssertjSingleAssertMatcher matcher = AssertjSingleAssertMatcher.of(this::match);

    @Override
    public Optional<AssertjCheckerResult> matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        if (EQUAL_TO.matches(tree, state)) {
            return matcher.matches(tree, state);
        }
        return Optional.empty();
    }

    private Optional<AssertjCheckerResult> match(
            AssertjSingleAssertMatcher.SingleAssertMatch match, VisitorState state) {
        List<? extends ExpressionTree> checkArguments = match.getCheck().getArguments();
        if (checkArguments.size() != 1) {
            return Optional.empty();
        }
        List<? extends ExpressionTree> assertThatArguments = match.getAssertThat().getArguments();
        if (assertThatArguments.size() != 1) {
            return Optional.empty();
        }
        ExpressionTree expectedTree = Iterables.getOnlyElement(checkArguments);
        ExpressionTree actualTree = Iterables.getOnlyElement(assertThatArguments);
        if (constant.matches(actualTree, state) && !constant.matches(expectedTree, state)) {
            return Optional.of(AssertjCheckerResult.builder()
                    .description(DESCRIPTION)
                    .fix(SuggestedFix.builder()
                            .replace(actualTree, state.getSourceForNode(expectedTree))
                            .replace(expectedTree, state.getSourceForNode(actualTree))
                            .build())
                    .build());
        }
        return Optional.empty();
    }
}
