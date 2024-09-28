/**
 * Содержит инструмент для получения реализации AppFileBrowserFeature.
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.app_file_browser.feature

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Создаёт фичу для отображения браузера файлов приложения и взаимодействия с ним.
 */
fun createAppFileBrowserFeature(viewModelStoreOwner: ViewModelStoreOwner): AppFileBrowserFeature =
    ViewModelProvider(viewModelStoreOwner)[AppFileBrowserFeatureImpl::class.java]