package ru.tensor.sbis.design.selection.bl.contract.model

/**
 * Тип идентификатора элементов для компонента выбора
 *
 * TODO: 3/30/2020 сейчас тип строковый. Нужно исследовать возможность требования более частного типа
 */
typealias SelectorItemId = String

/**
 * Модель элемента списка для компонента выбора. Подход с [meta] атрибутом позволяет менять информацию об элементах меню
 * без необходимости расширения интерфейса
 *
 * @author ma.kolpakov
 */
@Deprecated("TODO: 5/11/2020 https://online.sbis.ru/opendoc.html?guid=13e9a029-1ff2-470a-a535-dc4838bba736")
interface SelectorItem {

    /**
     * @SelfDocumented
     */
    val id: SelectorItemId

    /** @SelfDocumented */
    val parentId: SelectorItemId?
        get() = null

    /**
     * Вспомогательная информация об элементе. Устанавливается компонентом выбора
     */
    var meta: SelectorItemMeta
}
