package ru.tensor.sbis.app_file_browser.fragment

import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable

/**
 * Реализация [ContentCreatorParcelable] для содержимого файлового браузера, отображаемого в контейнере.
 *
 * @author us.bessonov
 */
@Parcelize
internal class AppFileBrowserContentCreator : ContentCreatorParcelable {

    override fun createFragment(): Fragment = AppFileBrowserContent()
}