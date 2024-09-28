package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.header

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.YearLabelListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.YearLabelModel
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.Offset

/**
 * Адаптер, который хранит заголовки годов и обеспечивает их синхронизацию.
 *
 * @author mb.kruglova
 */
internal class YearLabelAdapter(
    internal val listener: YearLabelListener
) :
    RecyclerView.Adapter<YearLabelViewHolder>() {

    private var labels: MutableList<YearLabelModel> = mutableListOf()
    private var isEnabled: Boolean = true
    private val shift = 3

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearLabelViewHolder {
        val view = SbisTextView(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.CENTER
        }

        val params = (view.layoutParams as ViewGroup.MarginLayoutParams)
        params.bottomMargin = Offset.XS.getDimenPx(view.context)

        return YearLabelViewHolder(view, listener, isEnabled)
    }

    override fun getItemCount(): Int = labels.size

    override fun onBindViewHolder(holder: YearLabelViewHolder, position: Int) {
        holder.bind(labels[position])
    }

    /** @SelfDocumented */
    @SuppressLint("NotifyDataSetChanged")
    internal fun update(newItems: List<YearLabelModel>) {
        labels.clear()
        labels.addAll(newItems)
        notifyDataSetChanged()
    }

    /**
     * Получить позицию года с учетом, что год прибит к правому краю.
     */
    internal fun getYearPositionWithShift(year: Int): Int {
        return if (labels.isEmpty()) year else year - shift - labels[0].year
    }

    /**
     * Получить позицию года.
     */
    internal fun getYearPosition(year: Int): Int {
        return if (labels.isEmpty()) -1 else year - labels[0].year
    }

    /**
     * Получить год по позиции.
     */
    internal fun getYearByPosition(position: Int): Int = labels[position].year

    /** Обновить заголовок года. */
    internal fun updateYearLabel(year: Int) {
        listener.onUpdateYearLabel(year)
    }

    /** Выполнить догрузку календаря. */
    internal fun performCalendarReloading(isNextPage: Boolean, year: Int) {
        listener.onReloadCalendar(isNextPage, year)
    }

    /** Настроить доступность для взаимодействия. */
    internal fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }
}