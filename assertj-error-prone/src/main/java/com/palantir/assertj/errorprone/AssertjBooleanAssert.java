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
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import java.util.List;
import java.util.Optional;

@AutoService(AssertjChecker.class)
public final class AssertjBooleanAssert implements AssertjChecker {

    private static final String DESCRIPTION =
            "Prefer using AssertJ boolean matchers instead of equality checks with a constant boolean.";

    private static final Matcher<ExpressionTree> booleanEqualMatcher = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.AbstractBooleanAssert")
            .named("isEqualTo");

    private static final Matcher<ExpressionTree> booleanNotEqualMatcher = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.AbstractBooleanAssert")
            .named("isNotEqualTo");

    private static final Matcher<ExpressionTree> booleanTrue =
            Matchers.ignoreParens(Matchers.anyOf(Matchers.booleanLiteral(true), booleanConstant(true)));

    private static final Matcher<ExpressionTree> booleanFalse =
            Matchers.ignoreParens(Matchers.anyOf(Matchers.booleanLiteral(false), booleanConstant(false)));

    private static final Matcher<ExpressionTree> matcher = Matchers.methodInvocation(
            Matchers.anyOf(booleanEqualMatcher, booleanNotEqualMatcher),
            ChildMultiMatcher.MatchType.LAST,
            Matchers.anyOf(booleanTrue, booleanFalse));

    /**
     * Matches references to Boolean.TRUE and Boolean.FALSE. This method differs from Matchers.booleanConstant because
     * it does not match the opposite value. See https://github.com/google/error-prone/issues/1454.
     */
    static Matcher<ExpressionTree> booleanConstant(boolean value) {
        return (expressionTree, state) -> {
            if (expressionTree instanceof JCTree.JCFieldAccess) {
                Symbol symbol = ASTHelpers.getSymbol(expressionTree);
                if (symbol.isStatic() && state.getTypes().unboxedTypeOrType(symbol.type).getTag() == TypeTag.BOOLEAN) {
                    if (value) {
                        return symbol.getSimpleName().contentEquals("TRUE");
                    } else {
                        return symbol.getSimpleName().contentEquals("FALSE");
                    }
                }
            }
            return false;
        };
    }

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
        boolean expectTrue = booleanTrue.matches(argument, state);
        if (booleanNotEqualMatcher.matches(tree, state)) {
            expectTrue = !expectTrue;
        }
        return Optional.of(AssertjCheckerResult.builder()
                .description(DESCRIPTION)
                .fix(SuggestedFix.builder()
                        .merge(SuggestedFixes.renameMethodInvocation(tree, expectTrue ? "isTrue" : "isFalse", state))
                        .replace(argument, "")
                        .build())
                .build());
    }
}
