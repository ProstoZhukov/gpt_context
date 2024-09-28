package ru.tensor.sbis.communicator.dialog_selection.presentation

import ru.tensor.sbis.communicator.dialog_selection.data.factory.DialogSelectionDependencyFactory
import ru.tensor.sbis.communicator.dialog_selection.data.listener.DialogPrefetchCheckFunction
import ru.tensor.sbis.communicator.dialog_selection.data.listener.DialogSelectionCancelListener
import ru.tensor.sbis.communicator.dialog_selection.data.listener.DialogSelectionCompleteListener
import ru.tensor.sbis.design.selection.ui.factories.createShareMultiSelector

/**
 * Создает фрагмент выбора диалога/участников
 *
 * @author vv.chekurda
 */
internal fun createDialogSelectionFragment() =
    createShareMultiSelector(
        listDependenciesFactory = DialogSelectionDependencyFactory(),
        completeListener = DialogSelectionCompleteListener(),
        cancelListener = DialogSelectionCancelListener(),
        prefetchCheckFunction = DialogPrefetchCheckFunction()
    )

/** Максимальное количество получателей на экране */
internal const val RECIPIENT_LIST_SIZE = 5

/** Количество айтемов-заголовков в списке для получателей */
internal const val RECIPIENT_TITLE_ITEMS_COUNT = 1

/** Максимальное количество диалогов на экране */
internal const val DIALOG_LIST_SIZE = 10