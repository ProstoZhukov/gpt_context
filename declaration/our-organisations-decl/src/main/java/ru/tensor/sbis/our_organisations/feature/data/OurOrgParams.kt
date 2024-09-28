package ru.tensor.sbis.our_organisations.feature.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Параметры для списка наших организаций.
 *
 * @param selectedOrganisations идентификаторы выбранных организаций (будет по умолчанию выбрана в списке, если пустой список не будет выбрано ничего).
 * @param withEliminated отображать организации включая ликвидированные.
 * @param headsOnly отображать только головные организации.
 * @param needHideOnScrollSearchPanel необходимо ли скрывать строку поиска при прокрутке списка.
 * @param autoApplyAtFirstSelect закрыть окно при выборе организации.
 * @param scopesAreas список зон доступа, по которым отбирать компании.
 * @param isMultipleChoice включен ли множественный выбор (поддерживается только в окнах не обязательного выбора).
 * @param showInnerCompany Отбирать также запись 'Управленческий учет'.
 * @param displayIds Список идентификаторов для отображения только этих организаций.
 * @param hasFilter Показывать ли фильтр Действующие/Все.
 *
 * @author mv.ilin
 */
@Parcelize
data class OurOrgParams(
    val selectedOrganisations: List<Int>,
    val withEliminated: Boolean = false,
    val headsOnly: Boolean = false,
    val needHideOnScrollSearchPanel: Boolean = true,
    val autoApplyAtFirstSelect: Boolean = false,
    val scopesAreas: List<String> = emptyList(),
    val isMultipleChoice: Boolean = false,
    val showInnerCompany: Boolean = false,
    val displayIds: List<Int> = emptyList(),
    val hasFilter: Boolean = true
) : Parcelable
