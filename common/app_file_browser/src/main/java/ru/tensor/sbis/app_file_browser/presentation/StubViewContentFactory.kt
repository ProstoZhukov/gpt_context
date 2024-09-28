package ru.tensor.sbis.app_file_browser.presentation

import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.app_file_browser.R
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.StubType
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.stubview.StubViewImageType

/**
 * Выполняет создание заглушек, используемых при отсутствии содержимого в папке.
 *
 * @author us.bessonov
 */
internal object EmptyFolderStubViewContentFactory : StubFactory {

    override fun create(type: StubType): StubViewContent {
        return ImageStubContent(
            imageType = StubViewImageType.EMPTY,
            messageRes = R.string.app_file_browser_stub_folder_is_empty,
            detailsRes = ResourcesCompat.ID_NULL
        )
    }
}