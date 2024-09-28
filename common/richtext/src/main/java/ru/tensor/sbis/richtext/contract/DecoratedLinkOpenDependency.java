package ru.tensor.sbis.richtext.contract;

import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature;
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController;

/**
 * Интерфейс для открытия модулей приложения по клику на декорированную ссылку
 *
 * @author am.boldinov
 */
public interface DecoratedLinkOpenDependency extends
        OpenLinkController.Provider,
        DocWebViewerFeature {

}
