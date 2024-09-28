package ru.tensor.sbis.design.files_picker.decl

import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.design.tab_panel.TabPanelItem

typealias AddButtonClickAction = () -> Unit

/**
 * Фича раздела пикера.
 *
 * @author ai.abramenko
 */
interface SbisFilesPickerTabFeature<TAB : SbisFilesPickerTab> {

    /** [TabPanelItem], который небходимо отобразить в панели табов */
    val tabPanelItem: TabPanelItem

    /** Действие по клику на раздел */
    val clickAction: SbisFilesPickerTabClickAction

    /** Действие по клику на кнопку "Добавить", по умолчанию - null */
    val addButtonCustomClickAction: AddButtonClickAction?
        get() = null

    /** Наблюдаемые события от раздела */
    val event: Flow<SbisFilesPickerTabEvent>

    /** Настройка заголовка */
    val tabSettings: SbisFilesPickerTabSettings
        get() = SbisFilesPickerTabSettings()
}