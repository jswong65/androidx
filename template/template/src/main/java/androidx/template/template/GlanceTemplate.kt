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

package androidx.template.template

import androidx.compose.runtime.Composable

/** Transforms semantic data into a composable layout for any glanceable */
abstract class GlanceTemplate<T> {

    /** Defines the data associated with this template */
    abstract fun getData(state: Any?): T

    @Composable
    abstract fun WidgetLayoutCollapsed()

    @Composable
    abstract fun WidgetLayoutVertical()

    @Composable
    abstract fun WidgetLayoutHorizontal()

    // TODO: templates include layouts for every supported size and surface type
}
