package ru.tensor.sbis.communicator.declaration.model

import ru.tensor.sbis.communicator.declaration.R

/**
 * @author Subbotenko Dmitry
 */
enum class ChatType constructor(private val mItemStringRes: Int) : EntitledItem {

    // Все без какой-либо дополнительной фильтрации
    ALL(R.string.communicator_declaration_filter_sort_by_all),
    // Чаты с непрочитанными сообщениями
    UNREAD(R.string.communicator_declaration_filter_sort_by_unread),
    // Удаленные чаты
    HIDDEN(R.string.communicator_declaration_filter_sort_by_deleted);

    override fun getTitleRes(): Int {
        return mItemStringRes
    }
}
