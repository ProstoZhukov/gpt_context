package ru.tensor.sbis.list.view.adapter.progress_loadmore

import android.widget.FrameLayout
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import ru.tensor.sbis.list.utils.BaseThemedActivity
import ru.tensor.sbis.list.view.adapter.ProgressViewHolderHelper
import ru.tensor.sbis.list.view.utils.ProgressItemPlace.TOP

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class ProgressViewHolderHelperTest {

    private val activity = Robolectric.buildActivity(BaseThemedActivity::class.java).setup().get()
    private val helper = ProgressViewHolderHelper()
    private val view = FrameLayout(activity)

    @Test
    fun noErrors() {
        val vh = helper.createViewHolder(view)
        helper.bindToViewHolder(TOP, vh)
    }
}