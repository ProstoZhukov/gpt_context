package ru.tensor.sbis.motivation_decl.features.achievements

import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.motivation_decl.features.common.ResultKeys
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Контракт экрана достижений
 *
 * @author ev.grigoreva
 */
interface AchievementListFeature : Feature {

    /**
     * Создание фрагмента экрана достижений для конкретного пользователя
     *
     * @param [personUUID] - идентификатор пользователя.
     * @param [personId] - числовой идентификатор пользователя на облаке.
     * @param [userName] - ФИО сотрудника
     * @param [userImageURL] - url фотографии сотрудника
     * @param [achievementsType] - тип мотивации (для отображения в подзаголове тулбара)
     * @param [args] - аргументы для открытия списка достижений.
     * @param [needAddDefaultTopPadding] - флаг добавления отступа под статус-бар
     * @param [needHideNavigation] - флаг для управления видимостью внутренней шапки навигации.
     * @param [requestKeys] - ключ для возврата результата с помощью setFragmentResult
     * @param [resultFactory] - фабрика для обработки результата с помощью setFragmentResult
     */
    fun newBadgesListFragmentForPerson(
        personUUID: UUID,
        personId: Long,
        userName: String? = null,
        userImageURL: String? = null,
        achievementsType: String? = null,
        args: AchievementListFilterOpenArgs,
        needAddDefaultTopPadding: Boolean = false,
        needHideNavigation: Boolean = false,
        requestKeys: ResultKeys,
        resultFactory: AchievementsResult.Factory
    ): Fragment

    /**
     * Аргументы для открытия списка достижений.
     *
     * @property selectedBadge - бейдж, по которому необходимо предварительно отфильтровать список.
     * @property filterByRelevanceWhenOpening - true, если при открытии список должен фильтроваться по типу "Актуальные"
     */
    @Parcelize
    data class AchievementListFilterOpenArgs(
        val selectedBadge: BadgeItem?,
        val filterByRelevanceWhenOpening: Boolean
    ) : Parcelable
}

/**
 * Контракт списка актуальных бейджей
 */
interface ActualBadgeListFeature : Feature {

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
        visibilityListener: ((visibility: Int) -> Unit)?
    ): View

    /** Слушатель изменения видимости бейджей */
    fun interface ActualBadgeViewClickListener {

        /** @SelfDocumented */
        fun onClick(selectedBadge: BadgeItem?)
    }

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

/**
 * Модель бейджа по которому необходимо отфильтровать список при первом открытии.
 *
 * @property type - тип бейджа
 * @property name - название бейджа.
 */
@Parcelize
data class BadgeItem(val type: Long, val name: String) : Parcelable