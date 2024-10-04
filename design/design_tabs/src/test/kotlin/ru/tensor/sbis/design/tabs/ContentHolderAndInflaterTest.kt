package ru.tensor.sbis.design.tabs

import android.graphics.drawable.ColorDrawable
import android.os.Build
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.tabs.tabItem.ContentHolder
import ru.tensor.sbis.design.tabs.tabItem.ContentView
import ru.tensor.sbis.design.tabs.util.SbisTabItemContentViewInflater
import ru.tensor.sbis.design.tabs.util.singleTab
import ru.tensor.sbis.design.theme.res.PlatformSbisString


/**
 * Тесты [ContentHolder] и [SbisTabItemContentViewInflater].
 *
 * @author da.zolotarev
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
internal class ContentHolderAndInflaterTest {
    private lateinit var contentHolder: ContentHolder
    private lateinit var inflater: SbisTabItemContentViewInflater

    @Before
    fun setup() {
        inflater = SbisTabItemContentViewInflater(mock(), mock(), mock(), mock())
    }

    @Test
    fun `When text content is set, then ContentViewText is created`() {
        contentHolder = ContentHolder(singleTab { content { text(TEST_DATA) } }, inflater, mock())
        Assert.assertTrue(contentHolder.content[0] is ContentView.Text)
    }

    @Test
    fun `When additional text content is set, then ContentViewAdditionalText is created`() {
        contentHolder = ContentHolder(singleTab { content { additionalText(TEST_DATA) } }, inflater, mock())
        Assert.assertTrue(contentHolder.content[0] is ContentView.AdditionalText)
    }

    @Test
    fun `When image content is set, then ContentViewImage is created`() {
        contentHolder = ContentHolder(singleTab { content { image(ColorDrawable()) } }, inflater, mock())
        Assert.assertTrue(contentHolder.content[0] is ContentView.Image)
    }

    @Test
    fun `When icon content is set, then ContentViewIcon is created`() {
        contentHolder = ContentHolder(
            singleTab { content { icon(TEST_DATA) } },
            inflater,
            mock()
        )
        Assert.assertTrue(contentHolder.content[0] is ContentView.Icon)
    }

    @Test
    fun `When different content is set, then correct content is created`() {
        contentHolder = ContentHolder(
            singleTab {
                content {
                    text(TEST_DATA)
                    additionalText(TEST_DATA)
                    icon(TEST_DATA)
                    image(ColorDrawable())
                    text(TEST_DATA)
                }
            },
            inflater,
            mock()
        )
        Assert.assertEquals(5, contentHolder.content.size)
        Assert.assertTrue(contentHolder.content[0] is ContentView.Text)
        Assert.assertTrue(contentHolder.content[1] is ContentView.AdditionalText)
        Assert.assertTrue(contentHolder.content[2] is ContentView.Icon)
        Assert.assertTrue(contentHolder.content[3] is ContentView.Image)
        Assert.assertTrue(contentHolder.content[4] is ContentView.Text)
    }

    companion object {
        private val TEST_DATA = PlatformSbisString.Value("")
    }
}