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

import androidx.compose.runtime.mutableStateOf

/**
 * Contains the current scroll position represented by the first visible item index and the first
 * visible item scroll offset.
 *
 * Allows reading the values without recording the state read: [index] and [scrollOffset].
 * And with recording the state read which makes such reads observable: [observableIndex] and
 * [observableScrollOffset]. It is important to not record the state read inside the measure
 * block as otherwise the extra remeasurement will be scheduled once we update the values in the
 * end of the measure block.
 */
internal class LazyGridScrollPosition(
    initialIndex: Int = 0,
    initialScrollOffset: Int = 0
) {
    var index = ItemIndex(initialIndex)
        private set

    var scrollOffset = initialScrollOffset
        private set

    private val indexState = mutableStateOf(index.value)
    val observableIndex get() = indexState.value

    private val scrollOffsetState = mutableStateOf(scrollOffset)
    val observableScrollOffset get() = scrollOffsetState.value

    private var hadFirstNotEmptyLayout = false

    /** The last known key of the first item at [index] line. */
    private var lastKnownFirstItemKey: Any? = null

    /**
     * Updates the current scroll position based on the results of the last measurement.
     */
    fun updateFromMeasureResult(measureResult: LazyGridMeasureResult) {
        lastKnownFirstItemKey = measureResult.firstVisibleLine?.items?.firstOrNull()?.key
        // we ignore the index and offset from measureResult until we get at least one
        // measurement with real items. otherwise the initial index and scroll passed to the
        // state would be lost and overridden with zeros.
        if (hadFirstNotEmptyLayout || measureResult.totalItemsCount > 0) {
            hadFirstNotEmptyLayout = true
            update(
                ItemIndex(measureResult.firstVisibleLine?.items?.firstOrNull()?.index?.value ?: 0),
                measureResult.firstVisibleLineScrollOffset
            )
        }
    }

    /**
     * Updates the scroll position - the passed values will be used as a start position for
     * composing the items during the next measure pass and will be updated by the real
     * position calculated during the measurement. This means that there is guarantee that
     * exactly this index and offset will be applied as it is possible that:
     * a) there will no item at this index in reality
     * b) item at this index will be smaller than the asked scrollOffset, which means we would
     * switch to the next item
     * c) there will be not enough items to fill the viewport after the requested index, so we
     * would have to compose few elements before the asked index, changing the first visible item.
     */
    fun requestPosition(index: ItemIndex, scrollOffset: Int) {
        update(index, scrollOffset)
        // clear the stored key as we have a direct request to scroll to [index] position and the
        // next [checkIfFirstVisibleItemWasMoved] shouldn't override this.
        lastKnownFirstItemKey = null
    }

    /**
     * In addition to keeping the first visible item index we also store the key of this item.
     * When the user provided custom keys for the items this mechanism allows us to detect when
     * there were items added or removed before our current first visible item and keep this item
     * as the first visible one even given that its index has been changed.
     */
    fun updateScrollPositionIfTheFirstItemWasMoved(itemsProvider: LazyGridItemsProvider) {
        update(findLazyGridIndexByKey(lastKnownFirstItemKey, index, itemsProvider), scrollOffset)
    }

    private fun update(index: ItemIndex, scrollOffset: Int) {
        require(index.value >= 0f) { "Index should be non-negative (${index.value})" }
        require(scrollOffset >= 0f) { "scrollOffset should be non-negative ($scrollOffset)" }
        if (index != this.index) {
            this.index = index
            indexState.value = index.value
        }
        if (scrollOffset != this.scrollOffset) {
            this.scrollOffset = scrollOffset
            scrollOffsetState.value = scrollOffset
        }
    }

    private companion object {
        /**
         * Finds a position of the item with the given key in the grid. This logic allows us to
         * detect when there were items added or removed before our current first item.
         */
        private fun findLazyGridIndexByKey(
            key: Any?,
            lastKnownIndex: ItemIndex,
            itemsProvider: LazyGridItemsProvider
        ): ItemIndex {
            if (key == null) {
                // there were no real item during the previous measure
                return lastKnownIndex
            }
            if (lastKnownIndex.value < itemsProvider.itemsCount &&
                key == itemsProvider.getKey(lastKnownIndex.value)
            ) {
                // this item is still at the same index
                return lastKnownIndex
            }
            val newIndex = itemsProvider.keyToIndexMap[key]
            if (newIndex != null) {
                return ItemIndex(newIndex)
            }
            // fallback to the previous index if we don't know the new index of the item
            return lastKnownIndex
        }
    }
}
