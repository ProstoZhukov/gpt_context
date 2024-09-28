package ru.tensor.sbis.design.files_picker.view.store

import com.arkivanov.mvikotlin.core.store.Reducer

/**
 * MVI Reducer для экрана "Компонент выбора файлов".
 *
 * @author ai.abramenko
 */
internal class FilesPickerReducer : Reducer<FilesPickerStore.State, FilesPickerStore.Message> {

    override fun FilesPickerStore.State.reduce(msg: FilesPickerStore.Message): FilesPickerStore.State =
        when (msg) {
            is FilesPickerStore.Message.SetSelectedPickedItems -> reduceMessage(msg)
            is FilesPickerStore.Message.SetSelectedTabPanelItem -> reduceMessage(msg)
            is FilesPickerStore.Message.SetControlsVisible -> reduceMessage(msg)
            is FilesPickerStore.Message.SetAppliedHeaderColor -> reduceMessage(msg)
            is FilesPickerStore.Message.SetMenuVisible -> reduceMessage(msg)
        }

    private fun FilesPickerStore.State.reduceMessage(
        msg: FilesPickerStore.Message.SetSelectedPickedItems
    ): FilesPickerStore.State =
        copy(
            selectedPickedItems = msg.selectedPickedItems,
            isCompressImages = msg.isCompressImages
        )

    private fun FilesPickerStore.State.reduceMessage(
        msg: FilesPickerStore.Message.SetSelectedTabPanelItem
    ): FilesPickerStore.State =
        if (msg.tabPanelItem == selectedTabPanelItem) {
            this
        } else {
            copy(
                selectedPickedItems = emptyList(),
                isCompressImages = false,
                selectedTabPanelItem = msg.tabPanelItem,
                isControlsVisible = true,
            )
        }

    private fun FilesPickerStore.State.reduceMessage(
        msg: FilesPickerStore.Message.SetControlsVisible
    ): FilesPickerStore.State =
        copy(isControlsVisible = msg.isVisible)

    private fun FilesPickerStore.State.reduceMessage(
        msg: FilesPickerStore.Message.SetAppliedHeaderColor
    ): FilesPickerStore.State =
        copy(appliedHeaderColor = msg.color)

    private fun FilesPickerStore.State.reduceMessage(
        msg: FilesPickerStore.Message.SetMenuVisible
    ): FilesPickerStore.State =
        copy(isMenuVisible = msg.isVisible)
}