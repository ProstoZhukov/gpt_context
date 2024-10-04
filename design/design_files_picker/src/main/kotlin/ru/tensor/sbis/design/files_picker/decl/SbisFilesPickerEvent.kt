package ru.tensor.sbis.design.files_picker.decl

/**
 * Событие от пикера
 *
 * @author ai.abramenko
 */
sealed class SbisFilesPickerEvent {

    /**
     * Событие, происходящее, когда пользователь выбрал элементы.
     */
    class OnItemsSelected(
        val selectedItems: List<SbisPickedItem>,
        val compressImages: Boolean
    ) : SbisFilesPickerEvent()

    /**
     * Событие, происходящее по клику пользователя на отмену.
     */
    object OnCancel : SbisFilesPickerEvent()

    /**
     * Событие, происходящее в результате получения ошибки при выборе.
     */
    class OnError(val throwable: Throwable) : SbisFilesPickerEvent()
}
