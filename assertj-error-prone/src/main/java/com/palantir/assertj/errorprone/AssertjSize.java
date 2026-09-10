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
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.google.errorprone.predicates.TypePredicates;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.tools.javac.code.Symbol;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@AutoService(AssertjChecker.class)
public final class AssertjSize implements AssertjChecker {

    private static final String DESCRIPTION =
            "Prefer AssertJ size asserts for more debugging information than simple integer comparisons.";

    private static final Matcher<ExpressionTree> sizeMatcher = Matchers.ignoreParens(Matchers.anyOf(
            MethodMatchers.instanceMethod()
                    .onDescendantOf(CharSequence.class.getName())
                    .named("length")
                    .withNoParameters(),
            MethodMatchers.instanceMethod()
                    // Avoid refactoring maps which implement iterable due to ambiguity between assertThat(Iterable)
                    // and assertThat(Map). This could be improved in a later change to cast to a Map and refactor.
                    .onClass(TypePredicates.allOf(
                            TypePredicates.isDescendantOf(Map.class.getName()),
                            TypePredicates.not(TypePredicates.isDescendantOf(Iterable.class.getName()))))
                    .named("size")
                    .withNoParameters(),
            MethodMatchers.instanceMethod()
                    .onDescendantOf(Collection.class.getName())
                    .named("size")
                    .withNoParameters(),
            (Matcher<ExpressionTree>) (expressionTree, state) -> {
                Symbol symbol = ASTHelpers.getSymbol(expressionTree);
                return symbol != null
                        && symbol.getKind().isField()
                        && !symbol.isStatic()
                        && symbol.getSimpleName().contentEquals("length")
                        && state.getTypes().isArray(ASTHelpers.getReceiverType(expressionTree));
            }));

    private static final Matcher<ExpressionTree> isEqualTo = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.Assert")
            .named("isEqualTo");
    private static final Matcher<ExpressionTree> isLessThan = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.Assert")
            .namedAnyOf("isLessThan");
    private static final Matcher<ExpressionTree> isLessThanOrEqualTo = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.Assert")
            .namedAnyOf("isLessThanOrEqualTo");
    private static final Matcher<ExpressionTree> isGreaterThan = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.Assert")
            .namedAnyOf("isGreaterThan");
    private static final Matcher<ExpressionTree> isGreaterThanOrEqualTo = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.Assert")
            .namedAnyOf("isGreaterThanOrEqualTo");
    private static final Matcher<ExpressionTree> isZero = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.Assert")
            .namedAnyOf("isZero")
            .withNoParameters();
    private static final Matcher<ExpressionTree> isNotZero = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.Assert")
            .namedAnyOf("isNotZero")
            .withNoParameters();
    private static final Matcher<ExpressionTree> comparisonTo =
            Matchers.anyOf(isEqualTo, isLessThan, isLessThanOrEqualTo, isGreaterThan, isGreaterThanOrEqualTo);

    private static final Matcher<ExpressionTree> globalComparison = Matchers.anyOf(isZero, isNotZero);

    private final AssertjSingleAssertMatcher matcher = AssertjSingleAssertMatcher.of(this::match);

    @Override
    public Optional<AssertjCheckerResult> matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        int arguments = tree.getArguments().size();
        if ((arguments == 1 && comparisonTo.matches(tree, state))
                || (arguments == 0 && globalComparison.matches(tree, state))) {
            return matcher.matches(tree, state);
        }
        return Optional.empty();
    }

    private Optional<AssertjCheckerResult> match(
            AssertjSingleAssertMatcher.SingleAssertMatch match, VisitorState state) {
        ExpressionTree tree =
                ASTHelpers.stripParentheses(match.getAssertThat().getArguments().get(0));
        if (!sizeMatcher.matches(tree, state)) {
            return Optional.empty();
        }
        MethodInvocationTree check = match.getCheck();
        String replacementName = sizeComparisonName(check, state);
        SuggestedFix.Builder fix = SuggestedFix.builder()
                .replace(tree, state.getSourceForNode(ASTHelpers.getReceiver(ASTHelpers.stripParentheses(tree))))
                .merge(SuggestedFixes.renameMethodInvocation(check, replacementName, state));
        if (check.getArguments().size() == 1) {
            ExpressionTree rawArgument = Iterables.getOnlyElement(check.getArguments());
            ExpressionTree argument = ASTHelpers.stripParentheses(rawArgument);
            if (!state.getTypes().isAssignable(ASTHelpers.getType(argument), state.getSymtab().intType)) {
                fix.replace(rawArgument, SuggestedFixes.castTree(argument, "int", state));
            }
        }
        return Optional.of(AssertjCheckerResult.builder()
                .description(DESCRIPTION)
                .fix(fix.build())
                .build());
    }

    private static String sizeComparisonName(ExpressionTree tree, VisitorState state) {
        if (isEqualTo.matches(tree, state)) {
            return "hasSize";
        }
        if (isLessThan.matches(tree, state)) {
            return "hasSizeLessThan";
        }
        if (isLessThanOrEqualTo.matches(tree, state)) {
            return "hasSizeLessThanOrEqualTo";
        }
        if (isGreaterThan.matches(tree, state)) {
            return "hasSizeGreaterThan";
        }
        if (isGreaterThanOrEqualTo.matches(tree, state)) {
            return "hasSizeGreaterThanOrEqualTo";
        }
        if (isZero.matches(tree, state)) {
            return "isEmpty";
        }
        if (isNotZero.matches(tree, state)) {
            return "isNotEmpty";
        }
        throw new IllegalArgumentException("Unexpected expression: " + state.getSourceForNode(tree));
    }
}
