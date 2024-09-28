package ru.tensor.sbis.communicator.declaration.model

import ru.tensor.sbis.communicator.declaration.R

/**
 * @author Subbotenko Dmitry
 */
enum class DialogType constructor(private val mItemStringRes: Int) : EntitledItem {

    // Все без какой-либо дополнительной фильтрации
    ALL(R.string.communicator_declaration_filter_sort_by_all),
    // Диалоги с входящими сообщениями
    INCOMING(R.string.communicator_declaration_filter_sort_by_income),
    // Диалоги с непрочитанными сообщениями
    UNREAD(R.string.communicator_declaration_filter_sort_by_unread),
    // Диалоги с исходящими сообщениями
    UNANSWERED(R.string.communicator_declaration_filter_sort_by_not_answered),
    // Диалоги для сообщений архива
    DELETED(R.string.communicator_declaration_filter_sort_by_deleted);

    override fun getTitleRes(): Int {
        return mItemStringRes
    }

}
