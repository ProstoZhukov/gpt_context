package ru.tensor.sbis.main_screen.widget.view

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import ru.tensor.sbis.common.provider.BottomBarProvider
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonDrawableIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.main_screen.widget.view.fabcontainer.FabContainer
import ru.tensor.sbis.main_screen.widget.view.fabcontainer.createInflatedFabContainer
import ru.tensor.sbis.main_screen.widget.view.fabcontainer.createLazyFabContainer
import ru.tensor.sbis.main_screen_decl.env.BottomBarContainer
import ru.tensor.sbis.main_screen_decl.env.BottomBarContainerDelegate
import ru.tensor.sbis.main_screen_decl.env.BottomBarProviderExt
import java.util.Date

/**
 * BottomBarProvider с круглыми кнопками SbisRoundButton.
 *
 * @param extraFabContainerId Идентификатор view контейнера для размещения кнопок.
 * @param todayButtonConfig Конфигурация кнопки сегодняшнего дня.
 * @param containerDelegate Делегат для динамического добавления и удаления прикладных кнопок.
 * @param fabContainer Предоставляет view используемых кнопок. В общем случае, необходимо использовать
 * [createLazyFabContainer].
 */
class SbisRoundButtonBottomBarProvider(
    @IdRes private val extraFabContainerId: Int,
    private val todayButtonConfig: TodayButtonConfig? = null,
    private val containerDelegate: BottomBarContainerDelegate = BottomBarContainerDelegate(),
    private val fabContainer: FabContainer = createLazyFabContainer(extraFabContainerId)
) : BottomBarProvider,
    BottomBarProviderExt,
    BottomBarContainer by containerDelegate,
    FabContainer by fabContainer {

    @Deprecated(
        "Используйте основной конструктор для ленивой инициализации кнопок. " +
            "Будет удалено по https://online.sbis.ru/opendoc.html?guid=40cbe6fa-f3ad-428c-8073-55872c2179bf&client=3"
    )
    constructor(
        @IdRes mainFabId: Int,
        @IdRes extraFabContainerId: Int,
        @IdRes extraFabId: Int? = null,
        @IdRes extraFab2Id: Int? = null,
        @IdRes extraFab3Id: Int? = null,
        @IdRes extraFab4Id: Int? = null,
        todayButtonConfig: TodayButtonConfig? = null,
        containerDelegate: BottomBarContainerDelegate = BottomBarContainerDelegate()
    ) : this(
        extraFabContainerId,
        todayButtonConfig,
        containerDelegate,
        createInflatedFabContainer(
            extraFabContainerId,
            mainFabId,
            extraFabId,
            extraFab2Id,
            extraFab3Id,
            extraFab4Id,
            todayButtonConfig
        )
    )

    private var defaultMainFabStyle: SbisButtonStyle? = null

    private lateinit var activity: Activity

    override val extraFabContainer: View?
        get() = activity.findViewById(extraFabContainerId)

    override fun setActivity(activity: Activity) {
        this.activity = activity
        fabContainer.setActivity(activity)
        containerDelegate.setupContainer { extraFabContainer as ViewGroup }
    }

    // region Today button
    override fun setTodayExtraFabClickListener(extraFabClickListener: View.OnClickListener?) {
        updateClickListener(extraFabClickListener, peekTodayExtraFab) { todayExtraFab }
    }

    override fun showTodayExtraFabButton(date: Date, isWorkDay: Boolean, animated: Boolean) {
        todayButtonConfig?.showDate?.invoke(todayExtraFab!!, date, isWorkDay, animated)
    }

    override fun hideTodayExtraFabButton(animated: Boolean) {
        peekTodayExtraFab?.hide(animated)
    }

    override fun setTodayFabIcon(drawable: Drawable?) {
        drawable?.let { todayExtraFab?.icon = SbisButtonDrawableIcon(it) }
    }
    // endregion

    // region extraFab button
    override fun setExtraFabClickListener(extraFabClickListener: View.OnClickListener?) {
        updateClickListener(extraFabClickListener, peekExtraFab) { extraFab }
    }

    override fun showExtraFabButton() {
        extraFab?.show()
    }

    override fun hideExtraFabButton() {
        peekExtraFab?.hide()
    }

    override fun isExtraFabShown() = peekExtraFab?.isVisible == true

    override fun setExtraFabIcon(drawable: Drawable?) {
        drawable?.let { extraFab?.icon = SbisButtonDrawableIcon(it) }
    }

    override fun setExtraFabStyle(style: SbisButtonStyle) {
        extraFab?.style = style
    }
    // endregion

    // region extraFab2 button
    override fun setExtraFab2ClickListener(extraFabClickListener: View.OnClickListener?) {
        updateClickListener(extraFabClickListener, peekExtraFab2) { extraFab2 }
    }

    override fun showExtraFab2Button() {
        extraFab2?.show()
    }

    override fun hideExtraFab2Button() {
        peekExtraFab2?.hide()
    }

    override fun isExtraFab2Shown() = peekExtraFab2?.isVisible == true

    override fun setExtraFab2Icon(drawable: Drawable?) {
        drawable?.let { extraFab2?.icon = SbisButtonDrawableIcon(it) }
    }

    override fun setExtraFab2Style(style: SbisButtonStyle) {
        extraFab2?.style = style
    }
    // endregion

    // region extraFab3 button
    override fun setExtraFab3ClickListener(extraFabClickListener: View.OnClickListener?) {
        updateClickListener(extraFabClickListener, peekExtraFab3) { extraFab3 }
    }

    override fun showExtraFab3Button() {
        extraFab3?.show()
    }

    override fun hideExtraFab3Button() {
        peekExtraFab3?.hide()
    }

    override fun isExtraFab3Shown() = peekExtraFab3?.isVisible == true

    override fun setExtraFab3Icon(drawable: Drawable?) {
        drawable?.let { extraFab3?.icon = SbisButtonDrawableIcon(it) }
    }

    override fun setExtraFab3Style(style: SbisButtonStyle) {
        extraFab3?.style = style
    }
    // endregion

    // region extraFab4 button
    override fun setExtraFab4ClickListener(extraFabClickListener: View.OnClickListener?) {
        updateClickListener(extraFabClickListener, peekExtraFab4) { extraFab4 }
    }

    override fun showExtraFab4Button() {
        extraFab4?.show()
    }

    override fun hideExtraFab4Button() {
        peekExtraFab4?.hide()
    }

    override fun isExtraFab4Shown() = peekExtraFab4?.isVisible == true

    override fun setExtraFab4Icon(drawable: Drawable?) {
        drawable?.let { extraFab4?.icon = SbisButtonDrawableIcon(it) }
    }

    override fun setExtraFab4Style(style: SbisButtonStyle) {
        extraFab4?.style = style
    }
    // endregion

    override fun setNavigationFabClickListener(navigationFabClickListener: View.OnClickListener?) {
        updateClickListener(navigationFabClickListener, peekMainFab) { mainFab }

        swapFabButton(navigationFabClickListener != null)
    }

    override fun resetNavigationFabClickListener() {
        peekMainFab?.setOnClickListener(null)
    }

    override fun setNavigationFabIcon(drawable: Drawable?) {
        drawable?.let { mainFab.icon = SbisButtonDrawableIcon(it) }
    }

    override fun setNavigationFabStyle(style: SbisButtonStyle) {
        if (defaultMainFabStyle == null) {
            defaultMainFabStyle = mainFab.style
        }
        mainFab.style = style
    }

    override fun resetNavigationFabStyle() {
        defaultMainFabStyle?.let { peekMainFab?.style = it }
    }

    override fun swapFabButton(isAddItemEnabled: Boolean) {
        if (isAddItemEnabled) {
            mainFab.show()
        } else {
            peekMainFab?.hide()
        }
    }

    override fun isNavigationFabShown() = peekMainFab?.isVisible == true

    private fun updateClickListener(
        listener: View.OnClickListener?,
        peekFab: SbisRoundButton?,
        getFab: () -> SbisRoundButton?
    ) {
        val fab = if (listener == null) peekFab else getFab()
        fab?.setOnClickListener(listener)
    }

    /**
     * Содержит id для поиска кнопки текущего дня, а также лямбду для
     * отображения даты в кнопке.
     */
    class TodayButtonConfig(
        @Deprecated("Не требуется. Необходимо удалить и использовать основной конструктор SbisRoundButtonBottomBarProvider")
        @IdRes internal val todayButtonId: Int = ResourcesCompat.ID_NULL,
        internal val showDate: (button: SbisRoundButton, date: Date, isWorkDay: Boolean, animated: Boolean) -> Unit
    )
}