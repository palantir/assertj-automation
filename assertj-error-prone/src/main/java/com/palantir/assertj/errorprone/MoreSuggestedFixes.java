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
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.fixes.SuggestedFixes;
import com.sun.tools.javac.code.Type;
import javax.annotation.Nullable;

/** Additional utility functionality for {@link SuggestedFix} objects. */
final class MoreSuggestedFixes {

    /**
     * Identical to {@link SuggestedFixes#qualifyType(VisitorState, SuggestedFix.Builder, String)} unless the compiling
     * JVM is not supported by error-prone (JDK13) in which case a fallback is attempted.
     */
    static String qualifyType(VisitorState state, SuggestedFix.Builder fix, String typeName) {
        try {
            return SuggestedFixes.qualifyType(state, fix, typeName);
        } catch (LinkageError e) {
            // Work around https://github.com/google/error-prone/issues/1432
            // by avoiding the findIdent function. It's possible this may result
            // in colliding imports when classes have the same simple name, but
            // the output is correct in most cases, in the failures are easy for
            // humans to fix.
            for (int startOfClass = typeName.indexOf('.');
                    startOfClass > 0;
                    startOfClass = typeName.indexOf('.', startOfClass + 1)) {
                int endOfClass = typeName.indexOf('.', startOfClass + 1);
                if (endOfClass < 0) {
                    endOfClass = typeName.length();
                }
                if (!Character.isUpperCase(typeName.charAt(startOfClass + 1))) {
                    continue;
                }
                String className = typeName.substring(startOfClass + 1);
                fix.addImport(typeName.substring(0, endOfClass));
                return className;
            }
            return typeName;
        }
    }

    /**
     * Identical to {@link SuggestedFixes#prettyType(VisitorState, SuggestedFix.Builder, Type)} unless the compiling JVM
     * is not supported by error-prone (JDK13) in which case a fallback is attempted.
     */
    static String prettyType(@Nullable VisitorState state, @Nullable SuggestedFix.Builder fix, Type type) {
        try {
            return SuggestedFixes.prettyType(state, fix, type);
        } catch (LinkageError e) {
            // Work around https://github.com/google/error-prone/issues/1432
            // by using a path which cannot add imports, this does not throw on jdk13.
            return SuggestedFixes.prettyType(null, null, type);
        }
    }

    private MoreSuggestedFixes() {}
}
