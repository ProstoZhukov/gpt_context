package ru.tensor.sbis.design_selection.contract.customization.selected.panel

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import androidx.annotation.AttrRes
import androidx.core.animation.doOnEnd
import androidx.core.view.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.utils.showPreview
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.adapter.SelectedItemsAdapter
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.R

/**
 * Панель для работы со списком выбранных элементов в компоненте выбора.
 *
 * @author vv.chekurda
 */
class SelectionPanel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private lateinit var adapter: SelectedItemsAdapter

    private var lastIsVisible = isVisible
    private val originHeight by lazy {
        layoutParams?.height?.takeIf { it > 0 }
            ?: resources.getDimensionPixelSize(R.dimen.selection_selected_items_view_height)
    }
    private var showingAnimator: ValueAnimator? = null
    private val showingInterpolator = DecelerateInterpolator()

    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        (itemAnimator as DefaultItemAnimator).apply {
            addDuration = ITEM_ANIMATION_DURATION_MS
            moveDuration = ITEM_ANIMATION_DURATION_MS
            removeDuration = ITEM_ANIMATION_DURATION_MS
            supportsChangeAnimations = false
        }
        clipToPadding = false

        if (isInEditMode) showPreview()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        showingAnimator?.cancel()
    }

    /**
     * Инициализировать панель для начала работы.
     */
    fun init(
        adapter: SelectedItemsAdapter
    ) {
        this.adapter = adapter
        setAdapter(adapter)
    }

    /**
     * Установить данные для отображения списка выбранных элементов.
     */
    fun setData(data: SelectedData<SelectionItem>) {
        val newIsVisible = data.items.isNotEmpty()
        when {
            newIsVisible == lastIsVisible -> adapter.setData(data.items)
            data.isUserSelection -> setDataWithVisibilityAnimation(newIsVisible, data.items)
            else -> setDataWithoutAnimation(newIsVisible, data.items)
        }
        lastIsVisible = newIsVisible
    }

    /**
     * Установить список выбранных элементов.
     */
    fun setSelectedItems(items: List<SelectionItem>) {
        updateItemsAnimationDuration(0)
        adapter.setData(items)
        if (items.isNotEmpty()) {
            scrollToPosition(items.lastIndex)
        }
    }

    private fun setDataWithVisibilityAnimation(show: Boolean, items: List<SelectionItem>) {
        showingAnimator?.cancel()
        showingAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = PANEL_SHOWING_DURATION_MS
            if (show) {
                val currentHeight = height
                val heightDiff = originHeight - currentHeight

                // Показывам список без анимации элементов
                updateItemsAnimationDuration(0)
                adapter.setData(items)
                isVisible = true

                addUpdateListener {
                    val fraction = it.animatedFraction
                    val interpolation = showingInterpolator.getInterpolation(fraction)
                    val newHeight = currentHeight + interpolation * heightDiff
                    updateLayoutParams { height = newHeight.toInt() }
                }
                doOnEnd {
                    // Возвращаем анимации элементов списка после показа панели.
                    updateItemsAnimationDuration(ITEM_ANIMATION_DURATION_MS)
                }
            } else {
                if (height == 0) {
                    doOnNextLayout { setDataWithVisibilityAnimation(false, items) }
                    return@apply
                }

                val currentHeight = height
                addUpdateListener {
                    val fraction = 1f - it.animatedFraction
                    val interpolation = showingInterpolator.getInterpolation(fraction)
                    val newHeight = interpolation * currentHeight
                    updateLayoutParams { height = newHeight.toInt() }
                }
                doOnEnd {
                    adapter.setData(items)
                    isVisible = false
                }
            }
            start()
            pause()
            doOnNextLayout { resume() }
        }
    }

    private fun setDataWithoutAnimation(show: Boolean, items: List<SelectionItem>) {
        showingAnimator?.cancel()
        updateItemsAnimationDuration(0)
        adapter.setData(items)
        updateItemsAnimationDuration(ITEM_ANIMATION_DURATION_MS)
        if (show) {
            scrollToPosition(items.lastIndex)
            if (height != originHeight) {
                updateLayoutParams { height = originHeight }
            }
        }
        isVisible = show
    }

    private fun updateItemsAnimationDuration(durationMs: Long) {
        (itemAnimator as DefaultItemAnimator).apply {
            addDuration = durationMs
            removeDuration = durationMs
        }
    }
}

private const val PANEL_SHOWING_DURATION_MS = 120L
private const val ITEM_ANIMATION_DURATION_MS = 70L