package ru.tensor.sbis.cadres_docs_decl.achievements.contract

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import ru.tensor.sbis.cadres_docs_decl.achievements.AchievementsOpenArgs
import ru.tensor.sbis.cadres_docs_decl.achievements.AchievementsType
import ru.tensor.sbis.cadres_docs_decl.achievements.achievements_section.AchievementsSection
import ru.tensor.sbis.cadres_docs_decl.achievements.edit_control_buttons.AchievementsEditControlButtonsComponent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Провайдер [AchievementsEditControlButtonsComponent] с плавающей
 * управляющей кнопкой для секции ПиВ.
 *
 * Используется для взаимодействия с секцией ПиВ в режиме редактирования из
 * сторонних модулей.
 */
interface AchievementsEditControlButtonsProvider : Feature {

    /**
     * @param holder - фрагмент держатель секции ПиВ.
     * @param section - секция ПиВ.
     * @param rootContainer - идентификатор контайнера на [holder],
     * в который секция ПиВ будет вставлять фрагменты при необходимости.
     * @param openArgs - модель аргументов открытия фрагмента ПиВ.
     * В модели используются следующие параметры:
     * docType - тип документа ПиВ, см [AchievementsType]
     * screenState - первоначальное состояние, в котором необходимо отобразить управляющую кнопку.
     * Используется только при самой первой инициализации.
     * openFrom - флаг, указывающий на место, откуда происходит открытие ПиВ.
     * Подробнее см. [AchievementsOpenArgs].
     * При значении по умолчанию кнопка будет видна только в режиме редактирования.
     */
    fun getAchievementsEditControlButtons(
        holder: Fragment,
        section: AchievementsSection,
        @IdRes rootContainer: Int,
        openArgs: AchievementsOpenArgs
    ): AchievementsEditControlButtonsComponent
}