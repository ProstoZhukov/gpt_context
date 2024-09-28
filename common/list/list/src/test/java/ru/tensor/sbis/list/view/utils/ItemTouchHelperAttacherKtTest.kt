package ru.tensor.sbis.list.view.utils

import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import ru.tensor.sbis.list.utils.BaseThemedActivity
import ru.tensor.sbis.list.view.ListDataHolder
import ru.tensor.sbis.list.view.calback.ItemTouchCallback

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class ItemTouchHelperAttacherKtTest {

    private val activity = Robolectric
        .buildActivity(BaseThemedActivity::class.java)
        .setup()
        .get()

    @Test
    fun attach() {
        attach(RecyclerView(activity), ItemTouchCallback(ListDataHolder()))
    }
}