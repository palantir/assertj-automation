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

import com.google.errorprone.VisitorState;
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
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import java.util.Optional;

/**
 * {@link AssertjChecker} is functionally similar to BugChecker, but allows us to implement individual components of a
 * single check.
 */
interface AssertjChecker {

    default Optional<AssertjCheckerResult> matchAnnotatedType(AnnotatedTypeTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchAnnotation(AnnotationTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchArrayAccess(ArrayAccessTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchArrayType(ArrayTypeTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchAssert(AssertTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchAssignment(AssignmentTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchBinary(BinaryTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchBlock(BlockTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchBreak(BreakTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchCase(CaseTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchCatch(CatchTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchClass(ClassTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchCompilationUnit(CompilationUnitTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchCompoundAssignment(CompoundAssignmentTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchConditionalExpression(
            ConditionalExpressionTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchContinue(ContinueTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchDoWhileLoop(DoWhileLoopTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchEmptyStatement(EmptyStatementTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchEnhancedForLoop(EnhancedForLoopTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchExpressionStatement(ExpressionStatementTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchForLoop(ForLoopTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchIdentifier(IdentifierTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchIf(IfTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchImport(ImportTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchInstanceOf(InstanceOfTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchIntersectionType(IntersectionTypeTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchLabeledStatement(LabeledStatementTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchLambdaExpression(LambdaExpressionTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchLiteral(LiteralTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchMemberReference(MemberReferenceTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchMemberSelect(MemberSelectTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchMethod(MethodTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchModifiers(ModifiersTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchNewArray(NewArrayTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchNewClass(NewClassTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchParameterizedType(ParameterizedTypeTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchParenthesized(ParenthesizedTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchPrimitiveType(PrimitiveTypeTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchReturn(ReturnTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchSwitch(SwitchTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchSynchronized(SynchronizedTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchThrow(ThrowTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchTry(TryTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchTypeCast(TypeCastTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchTypeParameter(TypeParameterTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchUnary(UnaryTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchUnionType(UnionTypeTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchVariable(VariableTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchWhileLoop(WhileLoopTree tree, VisitorState state) {
        return Optional.empty();
    }

    default Optional<AssertjCheckerResult> matchWildcard(WildcardTree tree, VisitorState state) {
        return Optional.empty();
    }
}
