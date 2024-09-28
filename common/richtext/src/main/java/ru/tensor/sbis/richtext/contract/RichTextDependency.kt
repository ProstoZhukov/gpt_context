package ru.tensor.sbis.richtext.contract

import ru.tensor.sbis.toolbox_decl.linkopener.service.LinkDecoratorServiceRepository

/**
 * Интерфейс, описывающий зависимости модуля "Богатый текст" от других компонентов
 *
 * @property decoratedLinkServiceRepository поставщик данных для декорирования ссылок
 *
 * @author am.boldinov
 */
internal interface RichTextDependency {

    val decoratedLinkServiceRepository: LinkDecoratorServiceRepository?
}