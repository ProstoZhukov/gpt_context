package ru.tensor.sbis.retail_decl.devices

/**
 * Контракт для диалога "Подтвердить закрытие фискального накопителя".
 */
object FiscalApproveDialogContract {

    /**
     * Ключ действия [ContentActionHandler.onContentAction] выбор даты.
     */
    const val DIALOG_ACTION_OK = "FiscalApproveDialogContract.DIALOG_ACTION_OK"

    /**
     * Ключ результата [ContentActionHandler.onContentAction] выбор даты.
     */
    const val DIALOG_RESULT_DATE_CHOOSE = "FiscalApproveDialogContract.DIALOG_RESULT_DATE_CHOOSE"
}