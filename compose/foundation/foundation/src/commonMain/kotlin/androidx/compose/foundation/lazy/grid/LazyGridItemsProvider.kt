/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.foundation.lazy.grid

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.GridItemSpan
import androidx.compose.foundation.lazy.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.layout.LazyLayoutItemsProvider

@OptIn(ExperimentalFoundationApi::class)
internal interface LazyGridItemsProvider : LazyLayoutItemsProvider {
    /** Returns the span for the given [index] */
    fun LazyGridItemSpanScope.getSpan(index: Int): GridItemSpan

    /** Whether the grid has custom spans. Otherwise, it means all items are 1x1 */
    val hasCustomSpans: Boolean
}
