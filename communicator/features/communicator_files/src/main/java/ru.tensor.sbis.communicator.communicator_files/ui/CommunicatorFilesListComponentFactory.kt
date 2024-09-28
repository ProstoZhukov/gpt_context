package ru.tensor.sbis.communicator.communicator_files.ui

import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.communicator.base_folders.list_section.CommunicatorBaseListFolderViewSectionFactory
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFilesWrapper
import ru.tensor.sbis.communicator.communicator_files.mapper.CommunicatorFilesMapper
import ru.tensor.sbis.communicator.generated.ThemeAttachmentFilter
import ru.tensor.sbis.communicator.generated.ThemeAttachmentViewModel
import ru.tensor.sbis.crud3.ListComponent
import ru.tensor.sbis.crud3.ListComponentView
import ru.tensor.sbis.communicator.communicator_files.R
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.StubType
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Фабрика списочного компонента.
 *
 * @author da.zhukov
 */
internal class CommunicatorFilesListComponentFactory(
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val wrapper: CommunicatorFilesWrapper,
    private val mapper: CommunicatorFilesMapper,
    private val listViewSectionFactory: CommunicatorBaseListFolderViewSectionFactory
) {

    private val stubFactory = StubFactory { type ->
        when (type) {
            StubType.NO_DATA -> {
                StubViewCase.NO_DATA.getContent(
                    image = StubViewImageType.EMPTY_STUB_IMAGE,
                    message = ResourcesCompat.ID_NULL,
                    details = R.string.conversation_files_no_data_message
                )
            }
            StubType.BAD_FILTER -> StubViewCase.NO_FILTER_RESULTS.getContent()
            StubType.NO_NETWORK -> StubViewCase.NO_CONNECTION.getContent()
            StubType.SERVER_TROUBLE -> StubViewCase.SERVICE_UNAVAILABLE.getContent()
        }
    }

    private var listComponent: ListComponent<ThemeAttachmentFilter, ThemeAttachmentViewModel, AnyItem>? =
        null

    /** @SelfDocumented */
    fun create(
        view: ListComponentView
    ) = view.inject(
        viewModelStoreOwner = viewModelStoreOwner,
        wrapper = lazy { wrapper },
        mapper = lazy { mapper },
        stubFactory = lazy { stubFactory },
        firstItemFactory =  lazy { listViewSectionFactory }
    ).also { listComponent = it }

    /** @SelfDocumented */
    fun get() = listComponent
}