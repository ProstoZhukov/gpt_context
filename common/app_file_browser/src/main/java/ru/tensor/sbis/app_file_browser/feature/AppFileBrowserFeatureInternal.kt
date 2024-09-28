package ru.tensor.sbis.app_file_browser.feature

import ru.tensor.sbis.mfb.generated.MobileFileController

/**
 * Расширение [AppFileBrowserFeature] для операций, не входящих в API компонента.
 *
 * @author us.bessonov
 */
internal interface AppFileBrowserFeatureInternal : AppFileBrowserFeature {

    /**
     * Контроллер текущей сессии выбора файлов
     */
    val controller: MobileFileController

    /**
     * Событие подтверждения выбора, [selectedFiles] содержит пути выбранных файлов.
     */
    fun onSelectionChanged(selectedFiles: Set<String>)

    /** @SelfDocumented */
    fun onPanelClosed()
}