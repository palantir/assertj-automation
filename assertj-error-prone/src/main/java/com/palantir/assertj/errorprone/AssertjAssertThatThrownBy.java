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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.fixes.Fix;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.tree.JCTree;
import java.util.List;
import java.util.Optional;

@AutoService(BugChecker.class)
@BugPattern(
        name = "AssertjAssertThatThrownBy",
        link = "https://github.com/palantir/assertj-automation",
        linkType = BugPattern.LinkType.CUSTOM,
        providesFix = BugPattern.ProvidesFix.REQUIRES_HUMAN_ATTENTION,
        severity = BugPattern.SeverityLevel.SUGGESTION,
        summary = "Prefer AssertJ assertThatThrownBy assertions over try-catch with fail statements")
public final class AssertjAssertThatThrownBy extends BugChecker implements BugChecker.TryTreeMatcher {
    private static final ImmutableList<String> IGNORED_FAIL_MESSAGES = ImmutableList.of("\"fail\"", "\"\"");

    private static final Matcher<StatementTree> FAIL_METHOD = Matchers.anyOf(
            Matchers.expressionStatement(
                    MethodMatchers.staticMethod().onClass("org.junit.Assert").named("fail")),
            Matchers.expressionStatement(MethodMatchers.staticMethod()
                    .onClass("org.junit.jupiter.api.Assertions")
                    .named("fail")),
            Matchers.expressionStatement(MethodMatchers.staticMethod()
                    .onClass("org.assertj.core.api.Assertions")
                    .named("fail")));

    @Override
    public Description matchTry(TryTree tree, VisitorState state) {
        List<? extends StatementTree> tryStatements = tree.getBlock().getStatements();
        if (tryStatements.isEmpty() || tree.getCatches().size() != 1 || tree.getFinallyBlock() != null) {
            return Description.NO_MATCH;
        }
        CatchTree catchTree = Iterables.getOnlyElement(tree.getCatches());
        if (!catchTree.getBlock().getStatements().isEmpty()
                || catchTree.getParameter().getType().getKind() == Tree.Kind.UNION_TYPE) {
            return Description.NO_MATCH;
        }
        StatementTree lastStatement = Iterables.getLast(tryStatements);
        // Collect all statements from the try-block without the last fail call
        List<? extends StatementTree> throwingStatements = tryStatements.subList(0, tryStatements.size() - 1);
        if (!FAIL_METHOD.matches(lastStatement, state) || throwingStatements.isEmpty()) {
            return Description.NO_MATCH;
        }
        if (!TestCheckUtils.isTestCode(state)) {
            return Description.NO_MATCH;
        }
        Optional<String> failMessage = getFailMessage(lastStatement, state);
        return buildDescription(tree)
                .addFix(tryFailToAssertThatThrownBy(
                        tree, throwingStatements, catchTree.getParameter(), failMessage, state))
                .build();
    }

    private static Optional<String> getFailMessage(StatementTree failStatement, VisitorState state) {
        Iterable<? extends ExpressionTree> failArgs =
                ((MethodInvocationTree) ((ExpressionStatementTree) failStatement).getExpression()).getArguments();
        return Optional.ofNullable(Iterables.get(failArgs, 0, null))
                .map(state::getSourceForNode)
                .filter(msg -> !IGNORED_FAIL_MESSAGES.contains(msg));
    }

    private static Fix tryFailToAssertThatThrownBy(
            TryTree tree,
            List<? extends StatementTree> throwingStatements,
            VariableTree catchParameter,
            Optional<String> failMessage,
            VisitorState state) {
        int startPos = ((JCTree) throwingStatements.iterator().next()).getStartPosition();
        int endPos = state.getEndPosition(Iterables.getLast(throwingStatements));
        CharSequence throwingStatementsLines = state.getSourceCode().subSequence(startPos, endPos);

        boolean useExpressionLambda = throwingStatements.size() == 1
                && Iterables.getOnlyElement(throwingStatements).getKind() == Tree.Kind.EXPRESSION_STATEMENT;

        StringBuilder replacement = new StringBuilder();
        replacement.append("assertThatThrownBy(() -> ");
        if (useExpressionLambda) {
            // Remove the semicolon from the (single) statement for the in-line lambda
            replacement.append(throwingStatementsLines
                    .subSequence(0, throwingStatementsLines.length() - 1)
                    .toString());
        } else {
            replacement.append("{");
            replacement.append(throwingStatementsLines.toString());
            replacement.append("}");
        }
        replacement.append(")");
        failMessage.ifPresent(msg -> replacement.append(String.format(".describedAs(%s)", msg)));
        replacement.append(String.format(".isInstanceOf(%s.class)", state.getSourceForNode(catchParameter.getType())));
        replacement.append(";");
        return SuggestedFix.builder()
                .addStaticImport("org.assertj.core.api.Assertions.assertThatThrownBy")
                .replace(tree, replacement.toString())
                .build();
    }
}
