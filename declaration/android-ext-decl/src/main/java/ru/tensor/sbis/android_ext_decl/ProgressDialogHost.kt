package ru.tensor.sbis.android_ext_decl

import androidx.lifecycle.LiveData

/**
 * Этот интерфейс должен быть имплементирован фрагментом,
 * который хочет показывать прогресс-диалог с динамическим текстом.
 * Для этого должен быть использован ProgressDialogHelperDynamicText
 * (пример использования в документации к этому классу).
 */
interface ProgressDialogHost {

    /**
     * Метод должен прокидывать наружу фрагмента LiveData
     * с текстом из ProgressDialogHelperDynamicText.
     */
    fun getProgressTextLiveData(): LiveData<CharSequence>
}