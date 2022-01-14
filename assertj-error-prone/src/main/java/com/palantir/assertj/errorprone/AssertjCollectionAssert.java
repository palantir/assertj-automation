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
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import java.util.Optional;

@AutoService(AssertjChecker.class)
public final class AssertjCollectionAssert implements AssertjChecker {

    private static final String DESCRIPTION = "Prefer using AssertJ collection assertions instead of isEqualTo.";

    private static final Matcher<ExpressionTree> mapEqualMatcher = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.AbstractMapAssert")
            .named("isEqualTo");
    private static final Matcher<ExpressionTree> iterableEqualMatcher = MethodMatchers.instanceMethod()
            .onDescendantOf("org.assertj.core.api.AbstractIterableAssert")
            .named("isEqualTo");

    private static final Matcher<ExpressionTree> mapFactoryMatcher = MethodMatchers.staticMethod()
            .onClassAny("java.util.Map", "com.google.common.collect.ImmutableMap")
            .named("of");
    private static final Matcher<ExpressionTree> iterableFactoryMatcher = MethodMatchers.staticMethod()
            .onClassAny(
                    "java.util.List",
                    "com.google.common.collect.ImmutableList",
                    "java.util.Set",
                    "com.google.common.collect.ImmutableSet")
            .named("of");

    private static final Matcher<ExpressionTree> listMatcher = Matchers.isSubtypeOf("java.util.List");
    private static final Matcher<ExpressionTree> setMatcher = Matchers.isSubtypeOf("java.util.Set");

    @Override
    public Optional<AssertjCheckerResult> matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        if (mapEqualMatcher.matches(tree, state)) {
            ExpressionTree argument = Iterables.getOnlyElement(tree.getArguments());

            if (mapFactoryMatcher.matches(argument, state)) {
                MethodInvocationTree mapFactoryTree = (MethodInvocationTree) argument;

                if (mapFactoryTree.getArguments().isEmpty()) {
                    return Optional.of(AssertjCheckerResult.builder()
                            .description(DESCRIPTION)
                            .fix(SuggestedFix.builder()
                                    .merge(SuggestedFixes.renameMethodInvocation(tree, "isEmpty", state))
                                    .replace(argument, "")
                                    .build())
                            .build());
                }
            }

            return Optional.of(AssertjCheckerResult.builder()
                    .description(DESCRIPTION)
                    .fix(SuggestedFixes.renameMethodInvocation(tree, "containsExactlyInAnyOrderEntriesOf", state))
                    .build());
        }

        if (iterableEqualMatcher.matches(tree, state)) {
            ExpressionTree argument = Iterables.getOnlyElement(tree.getArguments());

            IterableType iterableType = getIterableType(argument, state);
            if (iterableType == null) {
                return Optional.empty();
            }

            if (iterableFactoryMatcher.matches(argument, state)) {
                MethodInvocationTree iterableFactoryTree = (MethodInvocationTree) argument;

                if (iterableFactoryTree.getArguments().isEmpty()) {
                    return Optional.of(AssertjCheckerResult.builder()
                            .description(DESCRIPTION)
                            .fix(SuggestedFix.builder()
                                    .merge(SuggestedFixes.renameMethodInvocation(tree, "isEmpty", state))
                                    .replace(argument, "")
                                    .build())
                            .build());
                } else {
                    return Optional.of(AssertjCheckerResult.builder()
                            .description(DESCRIPTION)
                            .fix(SuggestedFix.builder()
                                    .merge(SuggestedFixes.renameMethodInvocation(
                                            tree, iterableType.containsElements, state))
                                    .replace(argument, getArgumentsSource(iterableFactoryTree, state))
                                    .build())
                            .build());
                }
            }

            return Optional.of(AssertjCheckerResult.builder()
                    .description(DESCRIPTION)
                    .fix(SuggestedFixes.renameMethodInvocation(tree, iterableType.containsIterable, state))
                    .build());
        }

        return Optional.empty();
    }

    private static String getArgumentsSource(MethodInvocationTree tree, VisitorState state) {
        return state.getSourceCode()
                .subSequence(
                        ASTHelpers.getStartPosition(tree.getArguments().get(0)),
                        state.getEndPosition(Iterables.getLast(tree.getArguments())))
                .toString();
    }

    private static IterableType getIterableType(ExpressionTree tree, VisitorState state) {
        if (listMatcher.matches(tree, state)) {
            return IterableType.LIST;
        } else if (setMatcher.matches(tree, state)) {
            return IterableType.SET;
        } else {
            return null;
        }
    }

    private enum IterableType {
        LIST("containsExactly", "containsExactlyElementsOf"),
        SET("containsExactlyInAnyOrder", "containsExactlyInAnyOrderElementsOf");

        private final String containsElements;
        private final String containsIterable;

        IterableType(String containsElements, String containsIterable) {
            this.containsElements = containsElements;
            this.containsIterable = containsIterable;
        }
    }
}
