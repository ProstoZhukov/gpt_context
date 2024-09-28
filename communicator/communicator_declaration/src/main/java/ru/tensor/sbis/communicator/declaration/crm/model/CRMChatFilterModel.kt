package ru.tensor.sbis.communicator.declaration.crm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Модель фильтра консультации.
 *
 * @property isExpired включена просрочка или нет.
 * @property type текущий выбранный тип фильтра по операторам.
 * @property operatorIds идентификаторы ответственных за консультацию.
 * @property clientIds идентификаторы клиентов для фильтрации.
 * @property channelIds идентификаторы каналов для фильтрации.
 * @property sourceIds идентификаторы источников для фильтрации.
 *
 * @author da.zhukov
 */
@Parcelize
data class CRMChatFilterModel(
    val isExpired: Boolean = false,
    val type: CRMRadioButtonFilterType = CRMRadioButtonFilterType.MY,
    val operatorIds: Pair<ArrayList<UUID>, ArrayList<String>> = arrayListOf<UUID>() to arrayListOf(),
    val clientIds: Pair<ArrayList<UUID>, ArrayList<String>> = arrayListOf<UUID>() to arrayListOf(),
    val channelIds: Pair<ArrayList<UUID>, ArrayList<String>> = arrayListOf<UUID>() to arrayListOf(),
    val sourceIds: Pair<ArrayList<UUID>, ArrayList<String>> = arrayListOf<UUID>() to arrayListOf()
) : Parcelable