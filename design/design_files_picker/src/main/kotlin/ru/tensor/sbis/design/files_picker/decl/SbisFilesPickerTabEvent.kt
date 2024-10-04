package ru.tensor.sbis.design.files_picker.decl

/**
 * Событие от раздела.
 *
 * @author ai.abramenko
 */
sealed class SbisFilesPickerTabEvent {

    /**
     * Событие выбора элементов
     *
     * Если [pushRightNow] true, то следует передать выбранные элементы в вызывающий экран сразу
     */
    class OnItemsSelected(
        val selectedItems: List<SbisPickedItem>,
        val compressImages: Boolean = false,
        val pushRightNow: Boolean = false
    ) : SbisFilesPickerTabEvent()

    /** Событие изменения видимости контролов - шапки и панели вкладок. */
    class SwitchControlsVisibility(val isVisible: Boolean) : SbisFilesPickerTabEvent()

    /** Событие отмены работы раздела. */
    object Cancel : SbisFilesPickerTabEvent()
}