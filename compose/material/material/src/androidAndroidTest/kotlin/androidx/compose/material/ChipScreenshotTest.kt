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

package androidx.compose.material

import android.os.Build
import androidx.compose.testutils.assertAgainstGolden
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.filters.SdkSuppress
import androidx.test.screenshot.AndroidXScreenshotTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class, ExperimentalTestApi::class)
class ChipScreenshotTest {

    @get:Rule
    val rule = createComposeRule()

    @get:Rule
    val screenshotRule = AndroidXScreenshotTestRule(GOLDEN_MATERIAL)

    @Test
    fun actionChip() {
        rule.setMaterialContent {
            Chip(
                onClick = {},
                enabled = true,
                modifier = Modifier.testTag(TestTag)
            ) {
                Text("Action Chip")
            }
        }
        assertChipAgainstGolden("actionChip")
    }

    @Test
    fun actionChip_disabled() {
        rule.setMaterialContent {
            Chip(
                onClick = {},
                enabled = false,
                modifier = Modifier.testTag(TestTag)
            ) {
                Text("Action Chip")
            }
        }
        assertChipAgainstGolden("actionChip_disabled")
    }

    @Test
    fun actionChip_outlined() {
        rule.setMaterialContent {
            Chip(
                onClick = {},
                border = ChipDefaults.outlinedBorder,
                colors = ChipDefaults.outlinedChipColors(),
                enabled = true,
                modifier = Modifier.testTag(TestTag)
            ) {
                Text("Action Chip")
            }
        }
        assertChipAgainstGolden("actionChip_outlined")
    }

    @Test
    fun actionChip_outlined_disabled() {
        rule.setMaterialContent {
            Chip(
                onClick = {},
                border = ChipDefaults.outlinedBorder,
                colors = ChipDefaults.outlinedChipColors(),
                enabled = false,
                modifier = Modifier.testTag(TestTag)
            ) {
                Text("Action Chip")
            }
        }
        assertChipAgainstGolden("actionChip_outlined_disabled")
    }

    private fun assertChipAgainstGolden(goldenIdentifier: String) {
        rule.onNodeWithTag(TestTag)
            .captureToImage()
            .assertAgainstGolden(screenshotRule, goldenIdentifier)
    }
}

private const val TestTag = "chip"
