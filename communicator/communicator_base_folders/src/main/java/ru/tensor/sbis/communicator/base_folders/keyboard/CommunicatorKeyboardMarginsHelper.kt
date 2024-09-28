package ru.tensor.sbis.communicator.base_folders.keyboard

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import ru.tensor.sbis.common.util.AdjustResizeHelper.KeyboardEventListener
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.folders.support.utils.stub_integration.StubViewMediator
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.R as RDesign

/**
 * Интерфейс вспомогательного класса для обработки событий подъема клавиатуры в реестрах модуля коммуникатор.
 * Для предотвращения белых смаргиваний элементов списка при скрытии клавиатуры
 * необходимо выставлять паддинг ресайклеру напрямую, чтобы он добавлялся к последнему элементу в скролируемом списке,
 * вместо изменения размеров контейнера.
 *
 * @author vv.chekurda
 */
interface CommunicatorKeyboardMarginsHelper : KeyboardEventListener {

    /**
     * Инициализация
     * @param listView вью списка в реестре
     */
    fun initKeyboardHelper(
        listView: AbstractListView<*, *>,
        stubViewMediator: StubViewMediator? = null
    )

    /**
     * Чистка сохраненных ссылок
     */
    fun clearKeyboardHelper()
}

/**
 * Реализация вспомогательного класса для обработки событий подъема клавиатуры в реестрах модуля коммуникатор
 */
class CommunicatorKeyboardMarginsHelperImpl : CommunicatorKeyboardMarginsHelper {

    private var listView: AbstractListView<*, *>? = null
    private var stubViewMediator: StubViewMediator? = null
    private var progressSize: Int = 0
    private var keyboardHeight: Int = 0
    private var navigationHeight: Int = 0

    /**
     * При опущенной клавиатуры необходимо учитывать размер ннп
     */
    private val requiredPadding: Int
        get() = if (keyboardHeight > 0) keyboardHeight else navigationHeight

    override fun initKeyboardHelper(listView: AbstractListView<*, *>, stubViewMediator: StubViewMediator?) {
        this.listView = listView
        this.stubViewMediator = stubViewMediator
        progressSize = listView.resources.getDimensionPixelSize(RDesign.dimen.default_progress_bar_size)
        listView.resources.run {
            navigationHeight =
                // В альбомке на планшета ннп находится слева
                if (DeviceConfigurationUtils.isTablet(listView.context)
                    && DeviceConfigurationUtils.isLandscape(listView.context)) 0
                else getDimensionPixelSize(RDesign.dimen.tab_navigation_menu_horizontal_height)
        }
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        this.keyboardHeight = keyboardHeight
        updateListContainerMargins()
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        this.keyboardHeight = 0
        updateListContainerMargins()
        return true
    }

    /**
     * Обновление отступов дочерних элементов в контейнере списка
     */
    private fun updateListContainerMargins() {
        listView?.recyclerViewBottomPadding = keyboardHeight
        if (listView?.resources?.configuration?.orientation == ORIENTATION_PORTRAIT) {
            stubViewMediator?.setPadding(keyboardHeight)
            listView?.setInformationViewPaddingBottom(requiredPadding)
        }
        updateProgressMargin()
    }

    /**
     * Progress выравнивается по центру контейнера, поэтому необходимо добавлять половину размера и отступа клавиатуры
     */
    private fun updateProgressMargin() {
        listView?.run {
            val margin = progressSize + requiredPadding
            setProgressBarVerticalMargin(0, margin / 2)
        }
    }

    override fun clearKeyboardHelper() {
        listView = null
        stubViewMediator = null
    }
}