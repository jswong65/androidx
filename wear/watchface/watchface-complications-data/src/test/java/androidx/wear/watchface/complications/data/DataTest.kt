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

package androidx.wear.watchface.complications.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.time.Instant

@RunWith(SharedRobolectricTestRunner::class)
public class AsWireComplicationDataTest {
    val resources = ApplicationProvider.getApplicationContext<Context>().resources

    @Test
    public fun noDataComplicationData() {
        val data = NoDataComplicationData()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_NO_DATA).build()
            )
        testRoundTripConversions(data)
        assertThat(serializeAndDeserialize(data)).isInstanceOf(NoDataComplicationData::class.java)

        assertThat(data).isEqualTo(NoDataComplicationData())
        assertThat(data.hashCode()).isEqualTo(NoDataComplicationData().hashCode())
        assertThat(data.toString()).isEqualTo("NoDataComplicationData()")
    }

    @Test
    public fun emptyComplicationData() {
        val data = EmptyComplicationData()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_EMPTY).build()
            )
        testRoundTripConversions(data)
        assertThat(serializeAndDeserialize(data)).isInstanceOf(EmptyComplicationData::class.java)

        assertThat(data).isEqualTo(EmptyComplicationData())
        assertThat(data.hashCode()).isEqualTo(EmptyComplicationData().hashCode())
        assertThat(data.toString()).isEqualTo("EmptyComplicationData()")
    }

    @Test
    public fun notConfiguredComplicationData() {
        val data = NotConfiguredComplicationData()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_NOT_CONFIGURED).build()
            )
        testRoundTripConversions(data)
        assertThat(serializeAndDeserialize(data))
            .isInstanceOf(NotConfiguredComplicationData::class.java)

        assertThat(data).isEqualTo(NotConfiguredComplicationData())
        assertThat(data.hashCode()).isEqualTo(NotConfiguredComplicationData().hashCode())
        assertThat(data.toString()).isEqualTo("NotConfiguredComplicationData()")
    }

    @Test
    public fun shortTextComplicationData() {
        val data = ShortTextComplicationData.Builder(
            "text".complicationText,
            "content description".complicationText
        )
            .setTitle("title".complicationText)
            .build()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_SHORT_TEXT)
                    .setShortText(WireComplicationText.plainText("text"))
                    .setShortTitle(WireComplicationText.plainText("title"))
                    .setContentDescription(WireComplicationText.plainText("content description"))
                    .build()
            )
        testRoundTripConversions(data)
        val deserialized = serializeAndDeserialize(data) as ShortTextComplicationData
        assertThat(deserialized.text.getTextAt(resources, Instant.EPOCH))
            .isEqualTo("text")
        assertThat(deserialized.contentDescription!!.getTextAt(resources, Instant.EPOCH))
            .isEqualTo("content description")
        assertThat(deserialized.title!!.getTextAt(resources, Instant.EPOCH))
            .isEqualTo("title")

        val data2 = ShortTextComplicationData.Builder(
            "text".complicationText,
            "content description".complicationText
        )
            .setTitle("title".complicationText)
            .build()
        val data3 = ShortTextComplicationData.Builder(
            "text3".complicationText,
            "content description3".complicationText
        )
            .setTitle("title3".complicationText)
            .build()

        assertThat(data).isEqualTo(data2)
        assertThat(data).isNotEqualTo(data3)
        assertThat(data.hashCode()).isEqualTo(data2.hashCode())
        assertThat(data.hashCode()).isNotEqualTo(data3.hashCode())
        assertThat(data.toString()).isEqualTo(
            "ShortTextComplicationData(text=ComplicationText{mSurroundingText=text, " +
                "mTimeDependentText=null}, title=ComplicationText{mSurroundingText=title, " +
                "mTimeDependentText=null}, monochromaticImage=null, contentDescription=" +
                "ComplicationText{mSurroundingText=content description, mTimeDependentText=null}," +
                " tapActionLostDueToSerialization=false, tapAction=null, validTimeRange=" +
                "TimeRange(startDateTimeMillis=-1000000000-01-01T00:00:00Z, " +
                "endDateTimeMillis=+1000000000-12-31T23:59:59.999999999Z))"
        )
    }

    @Test
    public fun longTextComplicationData() {
        val data = LongTextComplicationData.Builder(
            "text".complicationText,
            "content description".complicationText
        )
            .setTitle("title".complicationText)
            .build()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_LONG_TEXT)
                    .setLongText(WireComplicationText.plainText("text"))
                    .setLongTitle(WireComplicationText.plainText("title"))
                    .setContentDescription(WireComplicationText.plainText("content description"))
                    .build()
            )
        testRoundTripConversions(data)
        val deserialized = serializeAndDeserialize(data) as LongTextComplicationData
        assertThat(deserialized.text.getTextAt(resources, Instant.EPOCH))
            .isEqualTo("text")
        assertThat(deserialized.contentDescription!!.getTextAt(resources, Instant.EPOCH))
            .isEqualTo("content description")
        assertThat(deserialized.title!!.getTextAt(resources, Instant.EPOCH))
            .isEqualTo("title")

        val data2 = LongTextComplicationData.Builder(
            "text".complicationText,
            "content description".complicationText
        )
            .setTitle("title".complicationText)
            .build()
        val data3 = LongTextComplicationData.Builder(
            "text3".complicationText,
            "content description3".complicationText
        )
            .setTitle("title3".complicationText)
            .build()

        assertThat(data).isEqualTo(data2)
        assertThat(data).isNotEqualTo(data3)
        assertThat(data.hashCode()).isEqualTo(data2.hashCode())
        assertThat(data.hashCode()).isNotEqualTo(data3.hashCode())
        assertThat(data.toString()).isEqualTo(
            "LongTextComplicationData(text=ComplicationText{mSurroundingText=text, " +
                "mTimeDependentText=null}, title=ComplicationText{mSurroundingText=title, " +
                "mTimeDependentText=null}, monochromaticImage=null, smallImage=null, " +
                "contentDescription=ComplicationText{mSurroundingText=content description, " +
                "mTimeDependentText=null}), tapActionLostDueToSerialization=false, " +
                "tapAction=null, validTimeRange=TimeRange(startDateTimeMillis=" +
                "-1000000000-01-01T00:00:00Z, endDateTimeMillis=" +
                "+1000000000-12-31T23:59:59.999999999Z))"
        )
    }

    @Test
    public fun rangedValueComplicationData() {
        val data = RangedValueComplicationData.Builder(
            value = 95f, min = 0f, max = 100f,
            contentDescription = "content description".complicationText
        )
            .setTitle("battery".complicationText)
            .build()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_RANGED_VALUE)
                    .setRangedValue(95f)
                    .setRangedMinValue(0f)
                    .setRangedMaxValue(100f)
                    .setShortTitle(WireComplicationText.plainText("battery"))
                    .setContentDescription(WireComplicationText.plainText("content description"))
                    .build()
            )
        testRoundTripConversions(data)
        val deserialized = serializeAndDeserialize(data) as RangedValueComplicationData
        assertThat(deserialized.max).isEqualTo(100f)
        assertThat(deserialized.min).isEqualTo(0f)
        assertThat(deserialized.value).isEqualTo(95f)
        assertThat(deserialized.contentDescription!!.getTextAt(resources, Instant.EPOCH))
            .isEqualTo("content description")
        assertThat(deserialized.title!!.getTextAt(resources, Instant.EPOCH))
            .isEqualTo("battery")

        val data2 = RangedValueComplicationData.Builder(
            value = 95f, min = 0f, max = 100f,
            contentDescription = "content description".complicationText
        )
            .setTitle("battery".complicationText)
            .build()

        val data3 = RangedValueComplicationData.Builder(
            value = 95f, min = 0f, max = 100f,
            contentDescription = "content description2".complicationText
        )
            .setTitle("battery2".complicationText)
            .build()

        assertThat(data).isEqualTo(data2)
        assertThat(data).isNotEqualTo(data3)
        assertThat(data.hashCode()).isEqualTo(data2.hashCode())
        assertThat(data.hashCode()).isNotEqualTo(data3.hashCode())
        assertThat(data.toString()).isEqualTo(
            "RangedValueComplicationData(value=95.0, min=0.0, max=100.0, " +
                "monochromaticImage=null, title=ComplicationText{mSurroundingText=battery, " +
                "mTimeDependentText=null}, text=null, contentDescription=ComplicationText" +
                "{mSurroundingText=content description, mTimeDependentText=null}), " +
                "tapActionLostDueToSerialization=false, tapAction=null, validTimeRange=" +
                "TimeRange(startDateTimeMillis=-1000000000-01-01T00:00:00Z, endDateTimeMillis=" +
                "+1000000000-12-31T23:59:59.999999999Z))"
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @Test
    public fun monochromaticImageComplicationData() {
        val icon = Icon.createWithContentUri("someuri")
        val image = MonochromaticImage.Builder(icon).build()
        val data = MonochromaticImageComplicationData.Builder(
            image, "content description".complicationText
        ).build()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_ICON)
                    .setIcon(icon)
                    .setContentDescription(WireComplicationText.plainText("content description"))
                    .build()
            )
        testRoundTripConversions(data)
        val deserialized = serializeAndDeserialize(data) as MonochromaticImageComplicationData
        assertThat(deserialized.monochromaticImage.image.uri.toString())
            .isEqualTo("someuri")
        assertThat(deserialized.contentDescription!!.getTextAt(resources, Instant.EPOCH))
            .isEqualTo("content description")

        val icon2 = Icon.createWithContentUri("someuri")
        val image2 = MonochromaticImage.Builder(icon2).build()
        val data2 = MonochromaticImageComplicationData.Builder(
            image2, "content description".complicationText
        ).build()

        val icon3 = Icon.createWithContentUri("someuri3")
        val image3 = MonochromaticImage.Builder(icon3).build()
        val data3 = MonochromaticImageComplicationData.Builder(
            image3, "content description".complicationText
        ).build()

        assertThat(data).isEqualTo(data2)
        assertThat(data).isNotEqualTo(data3)
        assertThat(data.hashCode()).isEqualTo(data2.hashCode())
        assertThat(data.hashCode()).isNotEqualTo(data3.hashCode())
        assertThat(data.toString()).isEqualTo(
            "MonochromaticImageComplicationData(monochromaticImage=MonochromaticImage(image=" +
                "Icon(typ=URI uri=someuri), ambientImage=null), contentDescription=" +
                "ComplicationText{mSurroundingText=content description, mTimeDependentText=null})" +
                ", tapActionLostDueToSerialization=false, tapAction=null, validTimeRange=" +
                "TimeRange(startDateTimeMillis=-1000000000-01-01T00:00:00Z, endDateTimeMillis=" +
                "+1000000000-12-31T23:59:59.999999999Z))"
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @Test
    public fun smallImageComplicationData() {
        val icon = Icon.createWithContentUri("someuri")
        val image = SmallImage.Builder(icon, SmallImageType.PHOTO).build()
        val data = SmallImageComplicationData.Builder(
            image, "content description".complicationText
        ).build()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_SMALL_IMAGE)
                    .setSmallImage(icon)
                    .setSmallImageStyle(WireComplicationData.IMAGE_STYLE_PHOTO)
                    .setContentDescription(WireComplicationText.plainText("content description"))
                    .build()
            )
        testRoundTripConversions(data)
        val deserialized = serializeAndDeserialize(data) as SmallImageComplicationData
        assertThat(deserialized.smallImage.image.uri.toString())
            .isEqualTo("someuri")
        assertThat(deserialized.smallImage.type).isEqualTo(SmallImageType.PHOTO)
        assertThat(deserialized.contentDescription!!.getTextAt(resources, Instant.EPOCH))
            .isEqualTo("content description")

        val icon2 = Icon.createWithContentUri("someuri")
        val image2 = SmallImage.Builder(icon2, SmallImageType.PHOTO).build()
        val data2 = SmallImageComplicationData.Builder(
            image2, "content description".complicationText
        ).build()

        val icon3 = Icon.createWithContentUri("someuri3")
        val image3 = SmallImage.Builder(icon3, SmallImageType.PHOTO).build()
        val data3 = SmallImageComplicationData.Builder(
            image3, "content description".complicationText
        ).build()

        assertThat(data).isEqualTo(data2)
        assertThat(data).isNotEqualTo(data3)
        assertThat(data.hashCode()).isEqualTo(data2.hashCode())
        assertThat(data.hashCode()).isNotEqualTo(data3.hashCode())
        assertThat(data.toString()).isEqualTo(
            "SmallImageComplicationData(smallImage=SmallImage(image=Icon(typ=URI uri=someuri)" +
                ", type=PHOTO, ambientImage=null), contentDescription=ComplicationText{" +
                "mSurroundingText=content description, mTimeDependentText=null}), " +
                "tapActionLostDueToSerialization=false, tapAction=null, validTimeRange=" +
                "TimeRange(startDateTimeMillis=-1000000000-01-01T00:00:00Z, endDateTimeMillis=" +
                "+1000000000-12-31T23:59:59.999999999Z))"
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @Test
    public fun backgroundImageComplicationData() {
        val photoImage = Icon.createWithContentUri("someuri")
        val data = PhotoImageComplicationData.Builder(
            photoImage, "content description".complicationText
        ).build()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_LARGE_IMAGE)
                    .setLargeImage(photoImage)
                    .setContentDescription(WireComplicationText.plainText("content description"))
                    .build()
            )
        testRoundTripConversions(data)
        val deserialized = serializeAndDeserialize(data) as PhotoImageComplicationData
        assertThat(deserialized.photoImage.uri.toString())
            .isEqualTo("someuri")
        assertThat(deserialized.contentDescription!!.getTextAt(resources, Instant.EPOCH))
            .isEqualTo("content description")

        val photoImage2 = Icon.createWithContentUri("someuri")
        val data2 = PhotoImageComplicationData.Builder(
            photoImage2, "content description".complicationText
        ).build()

        val photoImage3 = Icon.createWithContentUri("someuri3")
        val data3 = PhotoImageComplicationData.Builder(
            photoImage3, "content description".complicationText
        ).build()

        assertThat(data).isEqualTo(data2)
        assertThat(data).isNotEqualTo(data3)
        assertThat(data.hashCode()).isEqualTo(data2.hashCode())
        assertThat(data.hashCode()).isNotEqualTo(data3.hashCode())
        assertThat(data.toString()).isEqualTo(
            "PhotoImageComplicationData(photoImage=Icon(typ=URI uri=someuri), contentDescription=" +
                "ComplicationText{mSurroundingText=content description, mTimeDependentText=null})" +
                ", tapActionLostDueToSerialization=false, tapAction=null, validTimeRange=" +
                "TimeRange(startDateTimeMillis=-1000000000-01-01T00:00:00Z, endDateTimeMillis=" +
                "+1000000000-12-31T23:59:59.999999999Z))"
        )
    }

    @Test
    public fun noPermissionComplicationData() {
        val data = NoPermissionComplicationData.Builder()
            .setText("needs location".complicationText)
            .build()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_NO_PERMISSION)
                    .setShortText(WireComplicationText.plainText("needs location"))
                    .build()
            )
        testRoundTripConversions(data)
        val deserialized = serializeAndDeserialize(data) as NoPermissionComplicationData
        assertThat(deserialized.text!!.getTextAt(resources, Instant.EPOCH))
            .isEqualTo("needs location")

        val data2 = NoPermissionComplicationData.Builder()
            .setText("needs location".complicationText)
            .build()

        val data3 = NoPermissionComplicationData.Builder()
            .setText("needs location3".complicationText)
            .build()

        assertThat(data).isEqualTo(data2)
        assertThat(data).isNotEqualTo(data3)
        assertThat(data.hashCode()).isEqualTo(data2.hashCode())
        assertThat(data.hashCode()).isNotEqualTo(data3.hashCode())
        assertThat(data.toString()).isEqualTo(
            "NoPermissionComplicationData(text=ComplicationText{mSurroundingText=needs location," +
                " mTimeDependentText=null}, title=null, monochromaticImage=null, " +
                "tapActionLostDueToSerialization=false, tapAction=null, validTimeRange=" +
                "TimeRange(startDateTimeMillis=-1000000000-01-01T00:00:00Z, endDateTimeMillis=" +
                "+1000000000-12-31T23:59:59.999999999Z))"
        )
    }

    private fun testRoundTripConversions(data: ComplicationData) {
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                data.asWireComplicationData().toApiComplicationData().asWireComplicationData()
            )
    }

    private fun serializeAndDeserialize(data: ComplicationData): ComplicationData {
        val stream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(stream)
        objectOutputStream.writeObject(data.asWireComplicationData())
        objectOutputStream.close()
        val byteArray = stream.toByteArray()

        val objectInputStream = ObjectInputStream(ByteArrayInputStream(byteArray))
        val wireData = objectInputStream.readObject() as WireComplicationData
        objectInputStream.close()
        return wireData.toApiComplicationData()
    }
}

@RunWith(SharedRobolectricTestRunner::class)
public class FromWireComplicationDataTest {
    @Test
    public fun noDataComplicationData() {
        assertRoundtrip(
            WireComplicationDataBuilder(WireComplicationData.TYPE_NO_DATA).build(),
            ComplicationType.NO_DATA
        )
    }

    @Test
    public fun emptyComplicationData() {
        assertRoundtrip(
            WireComplicationDataBuilder(WireComplicationData.TYPE_EMPTY).build(),
            ComplicationType.EMPTY
        )
    }

    @Test
    public fun notConfiguredComplicationData() {
        assertRoundtrip(
            WireComplicationDataBuilder(WireComplicationData.TYPE_NOT_CONFIGURED).build(),
            ComplicationType.NOT_CONFIGURED
        )
    }

    @Test
    public fun shortTextComplicationData() {
        assertRoundtrip(
            WireComplicationDataBuilder(WireComplicationData.TYPE_SHORT_TEXT)
                .setShortText(WireComplicationText.plainText("text"))
                .setShortTitle(WireComplicationText.plainText("title"))
                .setContentDescription(WireComplicationText.plainText("content description"))
                .build(),
            ComplicationType.SHORT_TEXT
        )
    }

    @Test
    public fun longTextComplicationData() {
        assertRoundtrip(
            WireComplicationDataBuilder(WireComplicationData.TYPE_LONG_TEXT)
                .setLongText(WireComplicationText.plainText("text"))
                .setLongTitle(WireComplicationText.plainText("title"))
                .setContentDescription(WireComplicationText.plainText("content description"))
                .build(),
            ComplicationType.LONG_TEXT
        )
    }

    @Test
    public fun rangedValueComplicationData() {
        assertRoundtrip(
            WireComplicationDataBuilder(WireComplicationData.TYPE_RANGED_VALUE)
                .setRangedValue(95f)
                .setRangedMinValue(0f)
                .setRangedMaxValue(100f)
                .setShortTitle(WireComplicationText.plainText("battery"))
                .setContentDescription(WireComplicationText.plainText("content description"))
                .build(),
            ComplicationType.RANGED_VALUE
        )
    }

    @Test
    public fun monochromaticImageComplicationData() {
        val icon = Icon.createWithContentUri("someuri")
        assertRoundtrip(
            WireComplicationDataBuilder(WireComplicationData.TYPE_ICON)
                .setIcon(icon)
                .setContentDescription(WireComplicationText.plainText("content description"))
                .build(),
            ComplicationType.MONOCHROMATIC_IMAGE
        )
    }

    @Test
    public fun smallImageComplicationData() {
        val icon = Icon.createWithContentUri("someuri")
        assertRoundtrip(
            WireComplicationDataBuilder(WireComplicationData.TYPE_SMALL_IMAGE)
                .setSmallImage(icon)
                .setSmallImageStyle(WireComplicationData.IMAGE_STYLE_PHOTO)
                .setContentDescription(WireComplicationText.plainText("content description"))
                .build(),
            ComplicationType.SMALL_IMAGE
        )
    }

    @Test
    public fun photoImageComplicationData() {
        val icon = Icon.createWithContentUri("someuri")
        assertRoundtrip(
            WireComplicationDataBuilder(WireComplicationData.TYPE_LARGE_IMAGE)
                .setLargeImage(icon)
                .setContentDescription(WireComplicationText.plainText("content description"))
                .build(),
            ComplicationType.PHOTO_IMAGE
        )
    }

    @Test
    public fun noPermissionComplicationData() {
        assertRoundtrip(
            WireComplicationDataBuilder(WireComplicationData.TYPE_NO_PERMISSION)
                .setShortText(WireComplicationText.plainText("needs location"))
                .build(),
            ComplicationType.NO_PERMISSION
        )
    }

    private fun assertRoundtrip(wireData: WireComplicationData, type: ComplicationType) {
        val data = wireData.toApiComplicationData()
        assertThat(data.type).isEqualTo(type)
        ParcelableSubject.assertThat(data.asWireComplicationData()).hasSameSerializationAs(wireData)
    }
}

@RunWith(SharedRobolectricTestRunner::class)
public class TapActionTest {
    private val mPendingIntent = PendingIntent.getBroadcast(
        ApplicationProvider.getApplicationContext(),
        0,
        Intent(),
        0
    )

    @Test
    public fun shortTextComplicationData() {
        assertThat(
            ShortTextComplicationData.Builder("text".complicationText, ComplicationText.EMPTY)
                .setTapAction(mPendingIntent)
                .build().asWireComplicationData().tapAction
        ).isEqualTo(mPendingIntent)
    }

    @Test
    public fun longTextComplicationData() {
        assertThat(
            LongTextComplicationData.Builder("text".complicationText, ComplicationText.EMPTY)
                .setTapAction(mPendingIntent)
                .build().asWireComplicationData().tapAction
        ).isEqualTo(mPendingIntent)
    }

    @Test
    public fun rangedValueComplicationData() {
        assertThat(
            RangedValueComplicationData.Builder(
                value = 95f, min = 0f, max = 100f,
                contentDescription = ComplicationText.EMPTY
            )
                .setTapAction(mPendingIntent)
                .build().asWireComplicationData().tapAction
        ).isEqualTo(mPendingIntent)
    }

    @Test
    public fun monochromaticImageComplicationData() {
        val icon = Icon.createWithContentUri("someuri")
        val image = MonochromaticImage.Builder(icon).build()
        assertThat(
            MonochromaticImageComplicationData.Builder(image, ComplicationText.EMPTY)
                .setTapAction(mPendingIntent)
                .build()
                .asWireComplicationData()
                .tapAction
        ).isEqualTo(mPendingIntent)
    }

    @Test
    public fun smallImageComplicationData() {
        val icon = Icon.createWithContentUri("someuri")
        val image = SmallImage.Builder(icon, SmallImageType.PHOTO).build()
        assertThat(
            SmallImageComplicationData.Builder(image, ComplicationText.EMPTY)
                .setTapAction(mPendingIntent).build()
                .asWireComplicationData()
                .tapAction
        ).isEqualTo(mPendingIntent)
    }

    @Test
    public fun photoImageComplicationData() {
        val icon = Icon.createWithContentUri("someuri")
        assertThat(
            PhotoImageComplicationData.Builder(icon, ComplicationText.EMPTY)
                .setTapAction(mPendingIntent).build()
                .asWireComplicationData()
                .tapAction
        ).isEqualTo(mPendingIntent)
    }
}

@RunWith(SharedRobolectricTestRunner::class)
public class RoundtripTapActionTest {
    private val mPendingIntent = PendingIntent.getBroadcast(
        ApplicationProvider.getApplicationContext(),
        0,
        Intent(),
        0
    )

    @Test
    public fun shortTextComplicationData() {
        assertThat(
            ShortTextComplicationData.Builder("text".complicationText, ComplicationText.EMPTY)
                .setTapAction(mPendingIntent)
                .build().asWireComplicationData().toApiComplicationData().tapAction
        ).isEqualTo(mPendingIntent)
    }

    @Test
    public fun longTextComplicationData() {
        assertThat(
            LongTextComplicationData.Builder("text".complicationText, ComplicationText.EMPTY)
                .setTapAction(mPendingIntent)
                .build().asWireComplicationData().toApiComplicationData().tapAction
        ).isEqualTo(mPendingIntent)
    }

    @Test
    public fun rangedValueComplicationData() {
        assertThat(
            RangedValueComplicationData.Builder(
                value = 95f, min = 0f, max = 100f,
                contentDescription = ComplicationText.EMPTY
            )
                .setTapAction(mPendingIntent)
                .build().asWireComplicationData().toApiComplicationData().tapAction
        ).isEqualTo(mPendingIntent)
    }

    @Test
    public fun monochromaticImageComplicationData() {
        val icon = Icon.createWithContentUri("someuri")
        val image = MonochromaticImage.Builder(icon).build()
        assertThat(
            MonochromaticImageComplicationData.Builder(image, ComplicationText.EMPTY)
                .setTapAction(mPendingIntent)
                .build()
                .asWireComplicationData()
                .toApiComplicationData()
                .tapAction
        ).isEqualTo(mPendingIntent)
    }

    @Test
    public fun smallImageComplicationData() {
        val icon = Icon.createWithContentUri("someuri")
        val image = SmallImage.Builder(icon, SmallImageType.PHOTO).build()
        assertThat(
            SmallImageComplicationData.Builder(image, ComplicationText.EMPTY)
                .setTapAction(mPendingIntent).build()
                .asWireComplicationData()
                .toApiComplicationData()
                .tapAction
        ).isEqualTo(mPendingIntent)
    }

    @Test
    public fun photoImageComplicationData() {
        val icon = Icon.createWithContentUri("someuri")
        assertThat(
            PhotoImageComplicationData.Builder(icon, ComplicationText.EMPTY)
                .setTapAction(mPendingIntent).build()
                .asWireComplicationData()
                .toApiComplicationData()
                .tapAction
        ).isEqualTo(mPendingIntent)
    }
}

@RunWith(SharedRobolectricTestRunner::class)
public class ValidTimeRangeTest {
    private val testStartInstant = Instant.ofEpochMilli(1000L)
    private val testEndDateInstant = Instant.ofEpochMilli(2000L)

    @Test
    public fun shortTextComplicationData() {
        val data = ShortTextComplicationData.Builder(
            "text".complicationText, ComplicationText.EMPTY
        )
            .setValidTimeRange(TimeRange.between(testStartInstant, testEndDateInstant))
            .build()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_SHORT_TEXT)
                    .setShortText(WireComplicationText.plainText("text"))
                    .setStartDateTimeMillis(testStartInstant.toEpochMilli())
                    .setEndDateTimeMillis(testEndDateInstant.toEpochMilli())
                    .build()
            )
    }

    @Test
    public fun longTextComplicationData() {
        val data = LongTextComplicationData.Builder("text".complicationText, ComplicationText.EMPTY)
            .setValidTimeRange(TimeRange.between(testStartInstant, testEndDateInstant))
            .build()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_LONG_TEXT)
                    .setLongText(WireComplicationText.plainText("text"))
                    .setStartDateTimeMillis(testStartInstant.toEpochMilli())
                    .setEndDateTimeMillis(testEndDateInstant.toEpochMilli())
                    .build()
            )
    }

    @Test
    public fun rangedValueComplicationData() {
        val data = RangedValueComplicationData.Builder(
            value = 95f, min = 0f, max = 100f,
            contentDescription = ComplicationText.EMPTY
        )
            .setValidTimeRange(TimeRange.between(testStartInstant, testEndDateInstant))
            .build()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_RANGED_VALUE)
                    .setRangedValue(95f)
                    .setRangedMinValue(0f)
                    .setRangedMaxValue(100f)
                    .setStartDateTimeMillis(testStartInstant.toEpochMilli())
                    .setEndDateTimeMillis(testEndDateInstant.toEpochMilli())
                    .build()
            )
    }

    @Test
    public fun monochromaticImageComplicationData() {
        val icon = Icon.createWithContentUri("someuri")
        val image = MonochromaticImage.Builder(icon).build()
        val data = MonochromaticImageComplicationData.Builder(image, ComplicationText.EMPTY)
            .setValidTimeRange(TimeRange.between(testStartInstant, testEndDateInstant))
            .build()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_ICON)
                    .setIcon(icon)
                    .setStartDateTimeMillis(testStartInstant.toEpochMilli())
                    .setEndDateTimeMillis(testEndDateInstant.toEpochMilli())
                    .build()
            )
    }

    @Test
    public fun smallImageComplicationData() {
        val icon = Icon.createWithContentUri("someuri")
        val image = SmallImage.Builder(icon, SmallImageType.PHOTO).build()
        val data = SmallImageComplicationData.Builder(image, ComplicationText.EMPTY)
            .setValidTimeRange(TimeRange.between(testStartInstant, testEndDateInstant))
            .build()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_SMALL_IMAGE)
                    .setSmallImage(icon)
                    .setSmallImageStyle(WireComplicationData.IMAGE_STYLE_PHOTO)
                    .setStartDateTimeMillis(testStartInstant.toEpochMilli())
                    .setEndDateTimeMillis(testEndDateInstant.toEpochMilli())
                    .build()
            )
    }

    @Test
    public fun photoImageComplicationData() {
        val photoImage = Icon.createWithContentUri("someuri")
        val data = PhotoImageComplicationData.Builder(photoImage, ComplicationText.EMPTY)
            .setValidTimeRange(TimeRange.between(testStartInstant, testEndDateInstant))
            .build()
        ParcelableSubject.assertThat(data.asWireComplicationData())
            .hasSameSerializationAs(
                WireComplicationDataBuilder(WireComplicationData.TYPE_LARGE_IMAGE)
                    .setLargeImage(photoImage)
                    .setStartDateTimeMillis(testStartInstant.toEpochMilli())
                    .setEndDateTimeMillis(testEndDateInstant.toEpochMilli())
                    .build()
            )
    }
}

private val String.complicationText get() = PlainComplicationText.Builder(this).build()
