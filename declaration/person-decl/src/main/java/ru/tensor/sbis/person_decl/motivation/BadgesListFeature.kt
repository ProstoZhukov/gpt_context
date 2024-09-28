package ru.tensor.sbis.person_decl.motivation

import android.os.Parcelable
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Контракт экрана достижений
 */
interface BadgesListFeature : Feature {

    /**
     * Создание фрагмента экрана достижений для конкретного пользователя
     *
     * @param [personUUID] - идентификатор пользователя.
     * @param [personId] - числовой идентификатор пользователя на облаке.
     * @param [userName] - ФИО сотрудника
     * @param [userDepartment] - текущая должность сотрудника (для свернутого вида toolbar)
     * @param [userImageURL] - url фотографии сотрудника
     * @param [args] - аргументы для открытия списка бейджей.
     * @param [needAddDefaultTopPadding] - флаг добавления отступа под статус-бар
     * @param [needHideNavigation] - флаг для управления видимостью внутренней шапки навигации.
     */
    fun newBadgesListFragmentForPerson(
        personUUID: UUID,
        personId: Long,
        userName: String? = null,
        userDepartment: String? = null,
        userImageURL: String? = null,
        args: BadgesListFilterOpenArgs,
        @IdRes hostContainerId: Int,
        needAddDefaultTopPadding: Boolean = false,
        needHideNavigation: Boolean = false
    ): Fragment

    /**
     * Возвращает View для отображения актуальных бейджей по персоне
     *
     * @param profileUuid - идентификатор персоны
     * @param fragment - фрагмент, к которому необходимо будет приаттачить View. Необходим для сохранения состояния View
     * @param badgeClickListener - слушатель клика по бейджам.
     * @param visibilityListener - слушатель изменения видимости бейджей. См. [BADGE_VISIBILITY_ACTUAL].
     */
    fun getActualBadgesView(
        profileUuid: UUID,
        fragment: Fragment,
        badgeClickListener: ActualBadgeViewClickListener,
        visibilityListener: ((visibility: Int) -> Unit)? = null
    ): View


    fun interface ActualBadgeViewClickListener {
        fun onClick(selectedBadge: BadgeItem?)
    }

    /**
     * Аргументы для открытия списка бейджей.
     *
     * @property selectedBadge - бейдж, по которому необходимо предварительно отфильтровать список.
     * @property filterByRelevanceWhenOpening - true, если при открытии список должен
     * быть отфильтрован по типу "Актуальные"
     */
    @Parcelize
    data class BadgesListFilterOpenArgs(
        val selectedBadge: BadgeItem?,
        val filterByRelevanceWhenOpening: Boolean
    ) : Parcelable

    /**
     * Модель бейджа по которому необходимо отфильтровать список при первом открытии.
     *
     * @property type - тип бейджа
     * @property name - название бейджа.
     */
    @Parcelize
    data class BadgeItem(val type: Long, val name: String) : Parcelable

    companion object {
        /**
         * Константы состояния видимости бейджей. Передаются в visibilityListener.
         * @property BADGE_VISIBILITY_NOTHING - состояние при котором у сотрудника отсутствуют бейджи.
         * @property BADGE_VISIBILITY_WITHOUT_ACTUAL - состояние при котором у сотрудника есть хотя бы 1 бейдж, но нет актуальных.
         * @property BADGE_VISIBILITY_ACTUAL - состояние при котором у сотрудника есть актуальные бейджи.
         */
        const val BADGE_VISIBILITY_NOTHING = 0
        const val BADGE_VISIBILITY_WITHOUT_ACTUAL = 1
        const val BADGE_VISIBILITY_ACTUAL = 2
    }

}