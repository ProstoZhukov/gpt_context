package ru.tensor.sbis.logging.log_packages.presentation

import androidx.lifecycle.LiveData

interface LogPackageRouter {

    /**
     * Live-data открытости файлового браузера.
     */
    val fileBrowserOpenedLiveData: LiveData<Boolean>

    /**
     * Показать файловый браузер.
     */
    fun showAppFileBrowser()

    /**
     * Показать диалог подтверждения отправки логов
     *
     * @param totalSize строковое представление размера отправляемых логов
     */
    fun showConfirmationDialog(totalSize: String)

    /** @SelfDocumented */
    fun backPressed()
}