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

import com.google.auto.service.AutoService;
import com.google.common.base.Preconditions;
import com.google.errorprone.VisitorState;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import java.util.List;
import java.util.Optional;

@AutoService(AssertjChecker.class)
public final class AssertjBooleanConjunction implements AssertjChecker {

    private static final String DESCRIPTION = "AssertJ assertThat conjunctions can be separated into two "
            + "separate asserts to allow additional optimizations";

    private static final Matcher<ExpressionTree> isTrue = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.AbstractBooleanAssert")
            .named("isTrue")
            .withParameters();

    private static final Matcher<Tree> isStatement =
            Matchers.parentNode(Matchers.kindIs(Tree.Kind.EXPRESSION_STATEMENT));

    private final Matcher<ExpressionTree> conditionalAnd = Matchers.kindIs(Tree.Kind.CONDITIONAL_AND);

    private final AssertjSingleAssertMatcher matcher = AssertjSingleAssertMatcher.of(this::match);

    @Override
    public Optional<AssertjCheckerResult> matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        if (isTrue.matches(tree, state) && isStatement.matches(tree, state)) {
            return matcher.matches(tree, state);
        }
        return Optional.empty();
    }

    private Optional<AssertjCheckerResult> match(
            AssertjSingleAssertMatcher.SingleAssertMatch match, VisitorState state) {
        List<? extends ExpressionTree> arguments = match.getAssertThat().getArguments();
        if (arguments.size() != 1) {
            return Optional.empty();
        }
        ExpressionTree argument =
                ASTHelpers.stripParentheses(match.getAssertThat().getArguments().get(0));
        if (!conditionalAnd.matches(argument, state)) {
            return Optional.empty();
        }
        BinaryTree binaryTree = (BinaryTree) argument;
        ExpressionTree firstExpression = ASTHelpers.stripParentheses(binaryTree.getLeftOperand());
        ExpressionTree secondExpression = ASTHelpers.stripParentheses(binaryTree.getRightOperand());
        int startPosition = getStartPosition(match.getCheck());
        int endPosition = state.getEndPosition(match.getCheck());
        int firstSegmentStart = getStartPosition(argument);
        int firstSegmentEnd = state.getEndPosition(argument);
        CharSequence sourceCode = Preconditions.checkNotNull(state.getSourceCode(), "Unable to find source");
        return Optional.of(AssertjCheckerResult.builder()
                .description(DESCRIPTION)
                .fix(SuggestedFix.builder()
                        .replace(
                                match.getCheck(),
                                sourceCode.subSequence(startPosition, firstSegmentStart)
                                        + state.getSourceForNode(firstExpression)
                                        + sourceCode.subSequence(firstSegmentEnd, endPosition)
                                        + ";\n"
                                        + sourceCode.subSequence(startPosition, firstSegmentStart)
                                        + state.getSourceForNode(secondExpression)
                                        + sourceCode.subSequence(firstSegmentEnd, endPosition))
                        .build())
                .build());
    }

    private static int getStartPosition(Tree tree) {
        return ((JCTree) tree).getStartPosition();
    }
}
