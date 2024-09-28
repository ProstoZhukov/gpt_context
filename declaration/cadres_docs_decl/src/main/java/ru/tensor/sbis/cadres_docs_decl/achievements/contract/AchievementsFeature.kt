package ru.tensor.sbis.cadres_docs_decl.achievements.contract

import androidx.fragment.app.Fragment
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSectionHolder
import ru.tensor.sbis.cadres_docs_decl.achievements.AchievementsScreenState
import ru.tensor.sbis.cadres_docs_decl.achievements.achievements_section.AchievementsSection
import ru.tensor.sbis.cadres_docs_decl.achievements.achievements_section.contract.AchievementsStubListener
import ru.tensor.sbis.mvp.presenter.DisplayErrorDelegate
import ru.tensor.sbis.mvp.presenter.loadcontent.LoadContentView
import ru.tensor.sbis.plugin_struct.feature.Feature

/** Фича модуля "Поощрения и взыскания" */
interface AchievementsFeature : AchievementsFragmentProvider, AchievementsPivSelectorProvider,
    AchievementsEditControlButtonsProvider, AchievementsFeatureAvailabilityProvider, Feature {

    /**
     * Получить секцию для отображения в [ru.tensor.sbis.base_components.adapter.sectioned.MultiSectionAdapter]
     * шапки документа поощрения/взыскания.
     * Шапка включает себя область от нижней границы toolbar, до эмоций (в новостях) или до ленты событий.
     * (http://axure.tensor.ru/MobileAPP/%D0%BF%D0%BE%D0%BE%D1%89%D1%80%D0%B5%D0%BD%D0%B8%D0%B5.html).
     *
     * @param holder - держатель секции.
     * @param errorDelegate - делегат для отображения простых ошибок. см. [DisplayErrorDelegate].
     * @param stubListener - делегат для отображения заглушки отображения.
     * @param loadingDelegate - делегат для оповещения о необходимости показать/скрыть индикатор загрузки.
     * @param screenState - состояние, в котором необходимо отобразить шапку. см. [AchievementsScreenState].
     *
     */
    fun <HOLDER> getAchievementsSection(
        holder: HOLDER,
        errorDelegate: DisplayErrorDelegate,
        stubListener: AchievementsStubListener,
        loadingDelegate: LoadContentView,
        screenState: AchievementsScreenState = AchievementsScreenState.SHOWING
    ): AchievementsSection
        where HOLDER : Fragment, HOLDER : ListSectionHolder
}