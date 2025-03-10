/*
 * Copyright 2022 The Android Open Source Project
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

package androidx.compose.foundation.relocation

import android.graphics.Rect as AndroidRect
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewResponder.Companion.ModifierLocalBringIntoViewResponder
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.plusAssign
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@MediumTest
@RunWith(AndroidJUnit4::class)
class BringIntoViewRequesterViewIntegrationTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun bringIntoView_callsViewRequestRectangleOnScreen_whenNoResponder() {
        val requesterOffset = IntOffset(1, 2)
        val rectangleToRequest = Rect(Offset(10f, 20f), Size(30f, 40f))
        val expectedRectangle = AndroidRect(11, 22, 41, 62)
        lateinit var scope: CoroutineScope
        lateinit var parent: FakeScrollable
        val bringIntoViewRequester = BringIntoViewRequester()
        rule.setContent {
            scope = rememberCoroutineScope()
            AndroidView({ context ->
                parent = FakeScrollable(context)
                val child = ComposeView(context)
                parent += child
                child.setContent {
                    Box(
                        Modifier
                            // Give it an offset to ensure the rectangle is being correctly
                            // translated.
                            .offset { requesterOffset }
                            .bringIntoViewRequester(bringIntoViewRequester)
                    )
                }
                return@AndroidView parent
            })
        }

        rule.waitForIdle()
        scope.launch {
            bringIntoViewRequester.bringIntoView(rectangleToRequest)
        }

        rule.runOnIdle {
            val request = parent.requests.single()
            assertThat(request.rectangle).isEqualTo(expectedRectangle)
            assertThat(request.immediate).isFalse()
        }
    }

    @Test
    fun bringIntoView_callsViewRequestRectangleOnScreen_whenResponderPresent() {
        val requesterOffset = IntOffset(1, 2)
        val scrollOffset = 3
        val rectangleToRequest = Rect(Offset(10f, 20f), Size(30f, 40f))
        val expectedRectangle = AndroidRect(11, 38, 41, 78)
        lateinit var scope: CoroutineScope
        lateinit var parent: FakeScrollable
        val bringIntoViewRequester = BringIntoViewRequester()
        rule.setContent {
            scope = rememberCoroutineScope()
            AndroidView({ context ->
                parent = FakeScrollable(context)
                val child = ComposeView(context)
                parent += child
                child.setContent {
                    Box(
                        Modifier
                            .size(10.dp)
                            .verticalScroll(rememberScrollState(scrollOffset))
                    ) {
                        Box(
                            Modifier
                                // Make it bigger than the scrollable so it can actually scroll.
                                .size(20.dp)
                                // Give it an offset to ensure the rectangle is being correctly
                                // translated.
                                .offset { requesterOffset }
                                .bringIntoViewRequester(bringIntoViewRequester)
                        )
                    }
                }
                return@AndroidView parent
            })
        }

        rule.waitForIdle()
        scope.launch {
            bringIntoViewRequester.bringIntoView(rectangleToRequest)
        }

        rule.runOnIdle {
            val request = parent.requests.single()
            assertThat(request.rectangle).isEqualTo(expectedRectangle)
            assertThat(request.immediate).isFalse()
        }
    }

    @Ignore("b/216652644")
    @Test
    fun bringIntoView_propagatesThroughIntermediateView() {
        val requesterOffset = IntOffset(1, 2)
        val rectangleToRequest = Rect(Offset(10f, 20f), Size(30f, 40f))
        val expectedRectangle = AndroidRect(11, 22, 41, 62)
        val requests = mutableListOf<Rect>()
        lateinit var scope: CoroutineScope
        val bringIntoViewRequester = BringIntoViewRequester()
        rule.setContent {
            scope = rememberCoroutineScope()
            AndroidView(
                modifier = Modifier.testBringIntoViewResponder { requests += it },
                factory = { context ->
                    val parent = FakeScrollable(context)
                    val child = ComposeView(context)
                    parent += child
                    child.setContent {
                        Box(
                            Modifier
                                // Give it an offset to ensure the rectangle is being correctly
                                // translated.
                                .offset { requesterOffset }
                                .bringIntoViewRequester(bringIntoViewRequester)
                        )
                    }
                    return@AndroidView parent
                }
            )
        }

        rule.waitForIdle()
        scope.launch {
            bringIntoViewRequester.bringIntoView(rectangleToRequest)
        }

        rule.runOnIdle {
            assertThat(requests.single()).isEqualTo(expectedRectangle)
        }
    }

    private fun Modifier.testBringIntoViewResponder(onBringIntoView: (Rect) -> Unit): Modifier =
        composed {
            val screenRequester = remember { BringRectangleOnScreenRequester() }
            val responder = remember {
                object : BringIntoViewResponder {
                    override suspend fun bringIntoView(rect: Rect) {
                        onBringIntoView(rect)
                    }

                    override fun toLocalRect(
                        rect: Rect,
                        layoutCoordinates: LayoutCoordinates
                    ): Rect = rect
                }
            }

            bringRectangleOnScreenRequester(screenRequester)
                .modifierLocalProvider(ModifierLocalBringIntoViewResponder) { responder }
        }

    /** A view that records calls to [requestChildRectangleOnScreen] for testing. */
    private class FakeScrollable(context: Context) : FrameLayout(context) {
        val requests = mutableListOf<RectangleRequest>()

        data class RectangleRequest(
            val rectangle: AndroidRect,
            val immediate: Boolean
        )

        override fun requestChildRectangleOnScreen(
            child: View,
            rectangle: AndroidRect,
            immediate: Boolean
        ): Boolean {
            requests += RectangleRequest(rectangle, immediate)
            return false
        }
    }
}