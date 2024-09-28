package ru.tensor.sbis.onboarding_tour.ui.views

import android.view.View
import androidx.constraintlayout.helper.widget.Carousel
import ru.tensor.sbis.onboarding_tour.ui.TourView

/**@SelfDocumented */
internal class TourCarouselAdapter(
    private val onPopulateItem: (index: Int, model: TourView.Model) -> Unit,
    private val onItemSelected: (index: Int) -> Unit
) : Carousel.Adapter {

    private lateinit var data: TourView.Model

    /**@SelfDocumented */
    fun setModel(model: TourView.Model) {
        data = model
    }

    override fun count(): Int =
        if (::data.isInitialized) {
            data.count
        } else {
            0
        }

    override fun populate(view: View?, index: Int) {
        val newModel = data.copy(position = index)
        onPopulateItem(index, newModel)
    }

    override fun onNewItem(index: Int) =
        onItemSelected(index)
}