package ru.tensor.sbis.design.topNavigation.internal_view

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.graphics.TypefaceCompat
import androidx.core.view.isVisible
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounter
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.topNavigation.R
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationApi
import ru.tensor.sbis.design.utils.extentions.setRightPadding

/**
 * Фабрика создания элементов шапки, если они нужны.
 *
 * @author da.zolotarev
 */
internal class ChildViewFactory(context: Context, private val viewApi: SbisTopNavigationApi) {

    private val listeners: MutableList<(View) -> Unit> = mutableListOf()

    /**
     * Добавить слушателя созданных элементов шапки.
     */
    fun addListener(lambda: (View) -> Unit) {
        listeners.add(lambda)
    }

    private val counterContainer by lazy(LazyThreadSafetyMode.NONE) {
        FrameLayout(context).apply {
            id = R.id.top_navigation_counter_ct
            setRightPadding(resources.getDimensionPixelSize(R.dimen.sbis_top_navigation_counter_right_padding))
            addView(counter)
            isVisible = false
            listeners.forEach { it.invoke(this) }
        }
    }

    private val collapsedTitleView by lazy(LazyThreadSafetyMode.NONE) {
        SbisTextView(context).apply {
            id = R.id.top_navigation_collapsed_title_view
            isVisible = false
            textSize = FontSize.L.getScaleOnDimen(context)
            maxLines = 1
            isVisible = false
            ellipsize = TextUtils.TruncateAt.END
            typeface = makeSemiBoldRobotoFont(context)
            listeners.forEach { it.invoke(this) }
        }
    }

    private val backBtnInternal by lazy(LazyThreadSafetyMode.NONE) {
        SbisTextView(context, R.style.SbisTopNavigationLeftBackBtnStyle).apply {
            id = R.id.top_navigation_btn_back
            isVisible = false
            listeners.forEach { it.invoke(this) }
        }
    }

    private val counter by lazy(LazyThreadSafetyMode.NONE) {
        SbisCounter(context).apply {
            id = R.id.top_navigation_counter
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL
            )
            listeners.forEach { it.invoke(this) }
        }
    }

    private val leftCustomContentContainer by lazy(LazyThreadSafetyMode.NONE) {
        FrameLayout(context).apply {
            id = R.id.top_navigation_left_custom_content
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            isVisible = false
            listeners.forEach { it.invoke(this) }
        }
    }

    /**
     * Получить левый счетчик.
     */
    fun getLeftContentCounter(): SbisCounter? {
        if (!viewApi.content.childsContent.counter) return null
        createContentCounterCt()
        return counter
    }

    /**
     * Получить кнопку "Назад".
     */
    fun getBackBtn(): SbisTextView? {
        if (!viewApi.content.childsContent.backBtn) return null
        return backBtnInternal
    }

    /**
     * Получить левый контейнер прикладного контента.
     */
    fun getLeftCustomViewContainer(): FrameLayout? {
        if (!viewApi.content.childsContent.leftCustomContent) return null
        return leftCustomContentContainer
    }

    /** @SelfDocumented */
    fun getOrCreateCollapsedTitleView() = collapsedTitleView

    private fun createContentCounterCt() {
        counterContainer
    }

    private fun makeSemiBoldRobotoFont(context: Context) = TypefaceCompat.create(
        context,
        TypefaceManager.getRobotoRegularFont(context),
        600,
        false
    )
}