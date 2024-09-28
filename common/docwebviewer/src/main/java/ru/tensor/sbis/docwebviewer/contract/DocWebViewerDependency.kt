package ru.tensor.sbis.docwebviewer.contract

import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenHandlerCreator

/**
 * Перечень зависимостей необходимых для работы DocWebViewer
 *
 * @author ma.kolpakov
 */
interface DocWebViewerDependency :
    LinkOpenHandlerCreator.Provider
