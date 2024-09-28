package ru.tensor.sbis.communicator.communicator_files.data

import ru.tensor.sbis.communicator.generated.CollectionOfThemeAttachmentViewModel
import ru.tensor.sbis.communicator.generated.ItemWithIndexOfThemeAttachmentViewModel
import ru.tensor.sbis.communicator.generated.PaginationOfThemeAttachmentAnchor
import ru.tensor.sbis.communicator.generated.ThemeAttachmentFilter
import ru.tensor.sbis.communicator.generated.ThemeAttachmentViewModel
import ru.tensor.sbis.crud3.domain.Wrapper

/**
 * Параметризованный Wrapper файлов переписки.
 * @see Wrapper
 *
 * @author da.zhukov
 */
internal interface CommunicatorFilesWrapper :
        Wrapper<CollectionOfThemeAttachmentViewModel,
                CommunicatorFilesObserver,
                ThemeAttachmentFilter,
                PaginationOfThemeAttachmentAnchor,
                ItemWithIndexOfThemeAttachmentViewModel,
                ThemeAttachmentViewModel>