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
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.IntersectionTypeTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import java.util.Optional;
import java.util.ServiceLoader;

@AutoService(BugChecker.class)
@BugPattern(
        name = "AssertjRefactoring",
        link = "https://github.com/palantir/assertj-automation",
        linkType = BugPattern.LinkType.CUSTOM,
        severity = BugPattern.SeverityLevel.WARNING,
        summary = "AssertJ statements may be refactored to be more readable or produce more helpful debugging "
                + "information on failure.")
public final class AssertjRefactoring extends BugChecker
        implements BugChecker.AnnotatedTypeTreeMatcher,
                BugChecker.AnnotationTreeMatcher,
                BugChecker.ArrayAccessTreeMatcher,
                BugChecker.ArrayTypeTreeMatcher,
                BugChecker.AssertTreeMatcher,
                BugChecker.AssignmentTreeMatcher,
                BugChecker.BinaryTreeMatcher,
                BugChecker.BlockTreeMatcher,
                BugChecker.BreakTreeMatcher,
                BugChecker.CaseTreeMatcher,
                BugChecker.CatchTreeMatcher,
                BugChecker.ClassTreeMatcher,
                BugChecker.CompilationUnitTreeMatcher,
                BugChecker.CompoundAssignmentTreeMatcher,
                BugChecker.ConditionalExpressionTreeMatcher,
                BugChecker.ContinueTreeMatcher,
                BugChecker.DoWhileLoopTreeMatcher,
                BugChecker.EmptyStatementTreeMatcher,
                BugChecker.EnhancedForLoopTreeMatcher,
                BugChecker.ExpressionStatementTreeMatcher,
                BugChecker.ForLoopTreeMatcher,
                BugChecker.IdentifierTreeMatcher,
                BugChecker.IfTreeMatcher,
                BugChecker.ImportTreeMatcher,
                BugChecker.InstanceOfTreeMatcher,
                BugChecker.IntersectionTypeTreeMatcher,
                BugChecker.LabeledStatementTreeMatcher,
                BugChecker.LambdaExpressionTreeMatcher,
                BugChecker.LiteralTreeMatcher,
                BugChecker.MemberReferenceTreeMatcher,
                BugChecker.MemberSelectTreeMatcher,
                BugChecker.MethodInvocationTreeMatcher,
                BugChecker.MethodTreeMatcher,
                BugChecker.ModifiersTreeMatcher,
                BugChecker.NewArrayTreeMatcher,
                BugChecker.NewClassTreeMatcher,
                BugChecker.ParameterizedTypeTreeMatcher,
                BugChecker.ParenthesizedTreeMatcher,
                BugChecker.PrimitiveTypeTreeMatcher,
                BugChecker.ReturnTreeMatcher,
                BugChecker.SwitchTreeMatcher,
                BugChecker.SynchronizedTreeMatcher,
                BugChecker.ThrowTreeMatcher,
                BugChecker.TryTreeMatcher,
                BugChecker.TypeCastTreeMatcher,
                BugChecker.TypeParameterTreeMatcher,
                BugChecker.UnaryTreeMatcher,
                BugChecker.UnionTypeTreeMatcher,
                BugChecker.VariableTreeMatcher,
                BugChecker.WhileLoopTreeMatcher,
                BugChecker.WildcardTreeMatcher {

    private static final ImmutableList<AssertjChecker> discoveredChecks =
            ImmutableList.copyOf(ServiceLoader.load(AssertjChecker.class, AssertjRefactoring.class.getClassLoader()));

    private final AssertjChecker[] checks;

    @SuppressWarnings("unused") // Required by ServiceLoader
    public AssertjRefactoring() {
        this(discoveredChecks.toArray(new AssertjChecker[0]));
    }

    @VisibleForTesting
    AssertjRefactoring(AssertjChecker... checks) {
        this.checks = checks;
    }

    @Override
    public Description matchAnnotatedType(AnnotatedTypeTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchAnnotatedType(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchAnnotation(AnnotationTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchAnnotation(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchArrayAccess(ArrayAccessTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchArrayAccess(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchArrayType(ArrayTypeTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchArrayType(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchAssert(AssertTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchAssert(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchAssignment(AssignmentTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchAssignment(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchBinary(BinaryTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchBinary(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchBlock(BlockTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchBlock(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchBreak(BreakTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchBreak(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchCase(CaseTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchCase(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchCatch(CatchTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchCatch(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchClass(ClassTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchClass(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchCompilationUnit(CompilationUnitTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchCompilationUnit(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchCompoundAssignment(CompoundAssignmentTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchCompoundAssignment(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchConditionalExpression(ConditionalExpressionTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchConditionalExpression(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchContinue(ContinueTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchContinue(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchDoWhileLoop(DoWhileLoopTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchDoWhileLoop(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchEmptyStatement(EmptyStatementTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchEmptyStatement(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchEnhancedForLoop(EnhancedForLoopTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchEnhancedForLoop(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchExpressionStatement(ExpressionStatementTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchExpressionStatement(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchForLoop(ForLoopTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchForLoop(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchIdentifier(IdentifierTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchIdentifier(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchIf(IfTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchIf(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchImport(ImportTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchImport(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchInstanceOf(InstanceOfTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchInstanceOf(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchIntersectionType(IntersectionTypeTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchIntersectionType(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchLabeledStatement(LabeledStatementTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchLabeledStatement(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchLambdaExpression(LambdaExpressionTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchLambdaExpression(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchLiteral(LiteralTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchLiteral(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchMemberReference(MemberReferenceTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchMemberReference(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchMemberSelect(MemberSelectTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchMemberSelect(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchMethodInvocation(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchMethod(MethodTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchMethod(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchModifiers(ModifiersTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchModifiers(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchNewArray(NewArrayTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchNewArray(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchNewClass(NewClassTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchNewClass(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchParameterizedType(ParameterizedTypeTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchParameterizedType(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchParenthesized(ParenthesizedTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchParenthesized(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchPrimitiveType(PrimitiveTypeTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchPrimitiveType(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchReturn(ReturnTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchReturn(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchSwitch(SwitchTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchSwitch(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchSynchronized(SynchronizedTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchSynchronized(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchThrow(ThrowTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchThrow(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchTry(TryTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchTry(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchTypeCast(TypeCastTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchTypeCast(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchTypeParameter(TypeParameterTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchTypeParameter(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchUnary(UnaryTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchUnary(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchUnionType(UnionTypeTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchUnionType(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchVariable(VariableTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchVariable(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchWhileLoop(WhileLoopTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchWhileLoop(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchWildcard(WildcardTree tree, VisitorState state) {
        for (AssertjChecker checker : checks) {
            describe(checker.matchWildcard(tree, state), tree, state);
        }
        return Description.NO_MATCH;
    }

    private void describe(Optional<AssertjCheckerResult> result, Tree tree, VisitorState state) {
        if (result.isPresent()) {
            AssertjCheckerResult value = result.get();
            state.reportMatch(buildDescription(tree)
                    .setMessage(value.description())
                    .addFix(value.fix())
                    .build());
        }
    }
}
