package ru.tensor.sbis.recipient_selection.profile.interactor

import io.reactivex.Single
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.GroupProfilesResult
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.GroupItem

/**
 * Интерфейс интерактора выбора получателей
 *
 * @vv.chekurda
 */
internal interface RecipientSelectionInteractor {

    /**
     * Загрузка профилей из групп (папок)
     * @param groups - список групп (папок)
     * @return список профилей получателей из переданных групп (папок)
     */
    fun loadProfilesByGroups(groups: List<GroupItem>): Single<GroupProfilesResult>
}