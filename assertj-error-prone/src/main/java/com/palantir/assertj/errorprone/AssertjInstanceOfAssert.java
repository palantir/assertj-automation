/*
 * (c) Copyright 2024 Palantir Technologies Inc. All rights reserved.
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
import com.google.errorprone.VisitorState;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import java.util.Optional;

@AutoService(AssertjChecker.class)
public final class AssertjInstanceOfAssert implements AssertjChecker {

    private static final String DESCRIPTION =
            "Prefer using AssertJ fluent comparisons over logic in an assertThat statement for better "
                    + "failure output. assertThat(a instanceof b).isTrue() failures report 'expected true' where "
                    + "assertThat(a).isInstanceOf(b) provides the expected and actual values.";

    private static final Matcher<ExpressionTree> IS_TRUE = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.Assert")
            .named("isTrue")
            .withNoParameters();

    private static final Matcher<ExpressionTree> IS_FALSE = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.Assert")
            .named("isFalse")
            .withNoParameters();

    private static final Matcher<ExpressionTree> BOOLEAN_ASSERT = Matchers.anyOf(IS_TRUE, IS_FALSE);

    private final AssertjSingleAssertMatcher matcher = AssertjSingleAssertMatcher.of(this::match);

    @Override
    public Optional<AssertjCheckerResult> matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        if (BOOLEAN_ASSERT.matches(tree, state)) {
            return matcher.matches(tree, state);
        }
        return Optional.empty();
    }

    private Optional<AssertjCheckerResult> match(
            AssertjSingleAssertMatcher.SingleAssertMatch match, VisitorState state) {
        boolean negatedAssertion = IS_FALSE.matches(match.getCheck(), state);
        if (!negatedAssertion && !IS_TRUE.matches(match.getCheck(), state)) {
            return Optional.empty();
        }
        ExpressionTree target = match.getAssertThat().getArguments().get(0);
        ExpressionTree instanceOfExpression = target;

        if (target instanceof UnaryTree) {
            UnaryTree unaryTree = (UnaryTree) target;
            if (unaryTree.getExpression() instanceof ParenthesizedTree) {
                negatedAssertion = !negatedAssertion;
                ParenthesizedTree parenthesizedTree = (ParenthesizedTree) unaryTree.getExpression();
                instanceOfExpression = parenthesizedTree.getExpression();
            }
        }
        if (!(instanceOfExpression instanceof InstanceOfTree)) {
            return Optional.empty();
        }
        InstanceOfTree instanceOfTree = (InstanceOfTree) instanceOfExpression;

        ExpressionTree actual = instanceOfTree.getExpression();
        Tree expected = instanceOfTree.getType();

        SuggestedFix fix = SuggestedFix.builder()
                .replace(
                        state.getEndPosition(
                                ((MemberSelectTree) match.getCheck().getMethodSelect()).getExpression()),
                        state.getEndPosition(match.getCheck()),
                        String.format(
                                negatedAssertion ? ".isNotInstanceOf(%s.class)" : ".isInstanceOf(%s.class)", expected))
                .replace(target, state.getSourceForNode(actual))
                .build();
        return Optional.of(
                AssertjCheckerResult.builder().description(DESCRIPTION).fix(fix).build());
    }
}
