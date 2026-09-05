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
import com.google.errorprone.predicates.TypePredicates;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.tools.javac.code.Symbol;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AutoService(AssertjChecker.class)
public final class AssertjSameSizeAs implements AssertjChecker {

    private static final String DESCRIPTION =
            "Prefer using AssertJ hasSameSizeAs for concise code and additional diagnostic information on failure.";

    private static final Matcher<ExpressionTree> hasSizeMatcher = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.EnumerableAssert")
            .named("hasSize");

    private static final Matcher<ExpressionTree> mapHasSizeMatcher = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.AbstractMapAssert")
            .named("hasSize");

    private static final Matcher<ExpressionTree> csHasSizeMatcher = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.AbstractCharSequenceAssert")
            .named("hasSize");

    private static final Matcher<ExpressionTree> sizeMatcher = Matchers.ignoreParens(Matchers.anyOf(
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

    private static final Matcher<ExpressionTree> mapSizeMatcher = Matchers.ignoreParens(MethodMatchers.instanceMethod()
            .onClass(TypePredicates.allOf(
                    TypePredicates.isDescendantOf(Map.class.getName()),
                    TypePredicates.not(TypePredicates.isDescendantOf(Iterable.class.getName()))))
            .named("size")
            .withNoParameters());

    private static final Matcher<ExpressionTree> csSizeMatcher = Matchers.ignoreParens(MethodMatchers.instanceMethod()
            .onDescendantOf(CharSequence.class.getName())
            .named("length")
            .withNoParameters());

    private static final Matcher<ExpressionTree> matcher = Matchers.anyOf(
            Matchers.methodInvocation(hasSizeMatcher, ChildMultiMatcher.MatchType.ALL, Matchers.anyOf(sizeMatcher)),
            Matchers.methodInvocation(
                    mapHasSizeMatcher, ChildMultiMatcher.MatchType.ALL, Matchers.anyOf(mapSizeMatcher)),
            Matchers.methodInvocation(
                    csHasSizeMatcher, ChildMultiMatcher.MatchType.ALL, Matchers.anyOf(csSizeMatcher)));

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
        return Optional.of(AssertjCheckerResult.builder()
                .description(DESCRIPTION)
                .fix(SuggestedFix.builder()
                        .merge(SuggestedFixes.renameMethodInvocation(tree, "hasSameSizeAs", state))
                        .replace(
                                argument,
                                state.getSourceForNode(ASTHelpers.getReceiver(ASTHelpers.stripParentheses(argument))))
                        .build())
                .build());
    }
}
