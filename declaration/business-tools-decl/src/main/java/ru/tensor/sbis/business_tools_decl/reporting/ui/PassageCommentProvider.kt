package ru.tensor.sbis.business_tools_decl.reporting.ui

/**
 * Интерфейс, реализуемым провайдером комментария для перехода в дзз
 *
 * @author ev.grigoreva
 */
interface PassageCommentProvider {

    /**
     * Запросить у провайдера комментарий к переходу
     *
     * @param passageUuid uuid перехода, используемый для определения необходимости комментария
     */
    fun requestComment(passageUuid: String)
}