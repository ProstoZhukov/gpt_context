package ru.tensor.sbis.crud4.view

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import ru.tensor.sbis.crud4.ListComponentView
import ru.tensor.sbis.crud4.R as RCrud
import ru.tensor.sbis.crud4.test.R as RTest

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class ListComponentViewLayoutTest {

    @Test
    fun `When layout has child in ListComponentView, then component has correct layout`() {
        createScenario().onActivity {
            val listComponentView = it.findViewById<ListComponentView>(RTest.id.test_list_component)
            assert(listComponentView.childCount == 1)

            assert(
                listComponentView.getChildAt(0).id == RCrud.id.crud4_refresh_layout_id ||
                    listComponentView.getChildAt(0).id == RCrud.id.crud4_list_container_id
            )
        }
    }

    @Test
    fun `When layout has child in ListComponentView, then child move to inner container`() {
        createScenario().onActivity {
            val listContainer = it.findViewById<FrameLayout>(RCrud.id.crud4_list_container_id)
            val listContainerChild = it.findViewById<TextView>(RTest.id.test_list_component_child)
            assert(listContainerChild.parent == listContainer)
        }
    }

    private fun createScenario() = ActivityScenario
        .launch(TestListComponentViewLayoutActivity::class.java)
        .apply {
            moveToState(Lifecycle.State.RESUMED)
        }
}

class TestListComponentViewLayoutActivity : AppCompatActivity(RTest.layout.crud4_list_component_view_activity) {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }


}