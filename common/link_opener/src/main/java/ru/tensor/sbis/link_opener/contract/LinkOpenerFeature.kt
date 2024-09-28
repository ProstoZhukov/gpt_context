package ru.tensor.sbis.link_opener.contract

import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerPendingLinkFeature
import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerRegistrar
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController
import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenHandlerCreator

/**
 * Интерфейс описывающий публичное api использования модуля "Открытия документов по ссылкам".
 *
 * @author as.chadov
 *
 * @see [OpenLinkController]
 * @see [LinkOpenerRegistrar]
 * @see [LinkOpenHandlerCreator]
 * @see [LinkOpenerPendingLinkFeature]
 *
 * [ТЗ Document Opener](https://online.sbis.ru/shared/disk/6563ec7b-9ef7-44c7-9781-3c1dda584393)
 */
internal interface LinkOpenerFeature :
    OpenLinkController.Provider,
    LinkOpenerRegistrar.Provider,
    LinkOpenHandlerCreator.Provider,
    LinkOpenerPendingLinkFeature.Provider