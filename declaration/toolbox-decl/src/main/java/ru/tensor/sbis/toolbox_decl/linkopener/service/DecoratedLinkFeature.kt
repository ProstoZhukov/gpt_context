package ru.tensor.sbis.toolbox_decl.linkopener.service

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * API модуля decorated_link.
 *
 * @author us.bessonov
 */
interface DecoratedLinkFeature : Feature {

    /** @SelfDocumented */
    val linkDecoratorServiceRepository: LinkDecoratorServiceRepository

}