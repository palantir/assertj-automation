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

import com.google.common.base.Preconditions;
import com.google.errorprone.fixes.Fix;
import java.util.Optional;

final class AssertjCheckerResult {

    private final String description;
    private final Optional<? extends Fix> fix;

    private AssertjCheckerResult(String description, Optional<? extends Fix> fix) {
        this.description = description;
        this.fix = fix;
    }

    String description() {
        return description;
    }

    Optional<? extends Fix> fix() {
        return fix;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        AssertjCheckerResult that = (AssertjCheckerResult) other;
        if (!description.equals(that.description)) {
            return false;
        }
        return fix.equals(that.fix);
    }

    @Override
    public int hashCode() {
        int result = description.hashCode();
        result = 31 * result + fix.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AssertjCheckerResult{description='" + description + '\'' + ", fix=" + fix + '}';
    }

    static Builder builder() {
        return new Builder();
    }

    static final class Builder {

        private String description;
        private Optional<? extends Fix> fix = Optional.empty();

        private Builder() {}

        Builder description(String value) {
            this.description = Preconditions.checkNotNull(value, "Description is required");
            return this;
        }

        Builder fix(Fix value) {
            this.fix = Optional.of(Preconditions.checkNotNull(value, "Fix is required"));
            return this;
        }

        Builder fix(Optional<? extends Fix> value) {
            this.fix = Preconditions.checkNotNull(value, "Fix is required");
            return this;
        }

        AssertjCheckerResult build() {
            return new AssertjCheckerResult(Preconditions.checkNotNull(description, "Description is required"), fix);
        }
    }
}
