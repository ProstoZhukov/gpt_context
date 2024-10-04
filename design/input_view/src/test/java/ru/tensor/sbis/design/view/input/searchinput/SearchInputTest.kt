package ru.tensor.sbis.design.view.input.searchinput

import android.app.Activity
import android.view.View
import com.facebook.drawee.drawable.Rounded
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.view.input.R

/**
 * @author ma.kolpakov
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], manifest = Config.NONE)
class SearchInputTest {

    private lateinit var searchPanel: SearchInput
    private lateinit var clearButton: View
    private lateinit var filterButton: View

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        activity.theme.applyStyle(ru.tensor.sbis.design.R.style.BaseAppTheme, false)
        initDecorView(activity)

        searchPanel = SearchInput(activity)
        clearButton = searchPanel.findViewById(R.id.search_input_clear_btn)
        filterButton = searchPanel.findViewById(R.id.search_input_filter)
    }

    @Test
    fun `When cancel search observable lose all observers and get the new one, then new events should be delivered`() {
        val observerA = searchPanel.cancelSearchObservable().test()
        val observerB = searchPanel.cancelSearchObservable().test()

        clearButton.performClick()

        // все подписки из первого набора получают события до вызова dispose() на каждой
        observerA.assertValueCount(1)
        observerB.assertValueCount(1)

        observerA.dispose()
        observerB.dispose()

        // возобновление подписок после того, как все прошлые отписались
        val observerC = searchPanel.cancelSearchObservable().test()

        clearButton.performClick()

        // новые подписки должны по-прежнему получать события
        observerC.assertValueCount(1)
    }

    @Test
    fun `Whens corner radius value is set for SearchInput, then corner radius value is set for filter button too`() {
        val radius = 10f

        // Устанавливаем радиус скругления для строки поиска
        searchPanel.cornerRadius = radius

        // Радиус скругления должен так же примениться для кнопки фильтра
        val filterBackground = filterButton.background as Rounded
        filterBackground.radii.forEach {
            assertEquals(it, radius)
        }
    }

    private fun initDecorView(activity: Activity) = activity.window.decorView
}