package ru.tensor.sbis.list.view.background

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import ru.tensor.sbis.list.utils.BaseThemedActivity

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class ColorProviderTest {

    private val activity = Robolectric.buildActivity(BaseThemedActivity::class.java).setup().get()

    @Test
    fun providerContentDarkBackground() {
        ColorProvider(activity).contentDarkBackground
    }

    @Test
    fun providerDark() {
        ColorProvider(activity).itemColorStateList
    }

    @Test
    fun providerWhite() {
        ColorProvider(activity).contentBackground
    }
}