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
package androidx.compose.material3

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.assertTouchWidthIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
/**
 * Test for [IconButton] and [IconToggleButton].
 */
class IconButtonTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun iconButton_size() {
        val width = 48.dp
        val height = 48.dp
        rule
            .setMaterialContentForSizeAssertions {
                IconButtonContent()
            }
            .assertWidthIsEqualTo(width)
            .assertHeightIsEqualTo(height)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun iconButton_size_withoutMinimumTouchTarget() {
        val width = 24.dp
        val height = 24.dp
        rule
            .setMaterialContentForSizeAssertions {
                CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                    IconButtonContent()
                }
            }
            .assertWidthIsEqualTo(width)
            .assertHeightIsEqualTo(height)
    }

    @Test
    fun iconButton_defaultSemantics() {
        rule.setMaterialContent(lightColorScheme()) {
            IconButtonContent()
        }
        rule.onNode(hasClickAction()).apply {
            assertIsEnabled()
        }
    }

    @Test
    fun iconButton_disabledSemantics() {
        rule.setMaterialContent(lightColorScheme()) {
            IconButton(onClick = {}, enabled = false) {}
        }
        rule.onNode(hasClickAction()).apply {
            assertIsNotEnabled()
        }
    }

    @Test
    fun iconButton_materialIconSize_iconPositioning() {
        val diameter = 24.dp
        rule.setMaterialContent(lightColorScheme()) {
            Box {
                IconButton(onClick = {}) {
                    Box(Modifier.size(diameter).testTag("icon"))
                }
            }
        }

        // Icon should be centered inside the IconButton
        rule.onNodeWithTag("icon", useUnmergedTree = true)
            .assertLeftPositionInRootIsEqualTo(24.dp / 2)
            .assertTopPositionInRootIsEqualTo(24.dp / 2)
    }

    @Test
    fun iconButton_customIconSize_iconPositioning() {
        val width = 36.dp
        val height = 14.dp
        rule.setMaterialContent(lightColorScheme()) {
            Box {
                IconButton(onClick = {}) {
                    Box(Modifier.size(width, height).testTag("icon"))
                }
            }
        }

        // Icon should be centered inside the IconButton
        rule.onNodeWithTag("icon", useUnmergedTree = true)
            .assertLeftPositionInRootIsEqualTo((48.dp - width) / 2)
            .assertTopPositionInRootIsEqualTo((48.dp - height) / 2)
    }

    @Test
    fun iconToggleButton_size() {
        val width = 48.dp
        val height = 48.dp
        rule
            .setMaterialContentForSizeAssertions {
                IconToggleButtonContent()
            }
            .assertWidthIsEqualTo(width)
            .assertHeightIsEqualTo(height)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun iconToggleButton_size_withoutMinimumTouchTarget() {
        val width = 24.dp
        val height = 24.dp
        rule
            .setMaterialContentForSizeAssertions {
                CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                    IconToggleButtonContent()
                }
            }
            .assertWidthIsEqualTo(width)
            .assertHeightIsEqualTo(height)
    }

    @Test
    fun iconToggleButton_defaultSemantics() {
        rule.setMaterialContent(lightColorScheme()) {
            IconToggleButtonContent()
        }
        rule.onNode(isToggleable()).apply {
            assertIsEnabled()
            assertIsOff()
            performClick()
            assertIsOn()
        }
    }

    @Test
    fun iconToggleButton_disabledSemantics() {
        rule.setMaterialContent(lightColorScheme()) {
            IconToggleButton(checked = false, onCheckedChange = {}, enabled = false) {}
        }
        rule.onNode(isToggleable()).apply {
            assertIsNotEnabled()
            assertIsOff()
        }
    }

    @Test
    fun iconToggleButton_materialIconSize_iconPositioning() {
        val diameter = 24.dp
        rule.setMaterialContent(lightColorScheme()) {
            Box {
                IconToggleButton(checked = false, onCheckedChange = {}) {
                    Box(Modifier.size(diameter).testTag("icon"))
                }
            }
        }

        // Icon should be centered inside the IconButton
        rule.onNodeWithTag("icon", useUnmergedTree = true)
            .assertLeftPositionInRootIsEqualTo(24.dp / 2)
            .assertTopPositionInRootIsEqualTo(24.dp / 2)
    }

    @Test
    fun iconToggleButton_customIconSize_iconPositioning() {
        val width = 36.dp
        val height = 14.dp
        rule.setMaterialContent(lightColorScheme()) {
            Box {
                IconToggleButton(checked = false, onCheckedChange = {}) {
                    Box(Modifier.size(width, height).testTag("icon"))
                }
            }
        }

        // Icon should be centered inside the IconButton
        rule.onNodeWithTag("icon", useUnmergedTree = true)
            .assertLeftPositionInRootIsEqualTo((48.dp - width) / 2)
            .assertTopPositionInRootIsEqualTo((48.dp - height) / 2)
    }

    @Test
    fun iconToggleButton_clickInMinimumTouchTarget(): Unit = with(rule.density) {
        val tag = "iconToggleButton"
        var checked by mutableStateOf(false)
        rule.setMaterialContent(lightColorScheme()) {
            // Box is needed because otherwise the control will be expanded to fill its parent
            Box(Modifier.fillMaxSize()) {
                IconToggleButton(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    modifier = Modifier.align(Alignment.Center).requiredSize(2.dp).testTag(tag)
                ) {
                    Box(Modifier.size(2.dp))
                }
            }
        }
        rule.onNodeWithTag(tag)
            .assertIsOff()
            .assertWidthIsEqualTo(2.dp)
            .assertHeightIsEqualTo(2.dp)
            .assertTouchWidthIsEqualTo(48.dp)
            .assertTouchHeightIsEqualTo(48.dp)
            .performTouchInput {
                click(position = Offset(-1f, -1f))
            }.assertIsOn()
    }

    @Composable
    private fun IconButtonContent() {
        IconButton(onClick = { /* doSomething() */ }) {
            Icon(Icons.Filled.Favorite, contentDescription = "Localized description")
        }
    }

    @Composable
    private fun IconToggleButtonContent() {
        var checked by remember { mutableStateOf(false) }

        IconToggleButton(checked = checked, onCheckedChange = { checked = it }) {
            val tint by animateColorAsState(if (checked) Color(0xFFEC407A) else Color(0xFFB0BEC5))
            Icon(Icons.Filled.Favorite, contentDescription = "Localized description", tint = tint)
        }
    }
}
