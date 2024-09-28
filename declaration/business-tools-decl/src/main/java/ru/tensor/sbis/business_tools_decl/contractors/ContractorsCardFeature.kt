package ru.tensor.sbis.business_tools_decl.contractors

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.business_tools_decl.contractors.data.ContractorHeaderMode
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс модуля карточки компании, описывающий функционал, который доступен внешним модулям
 *
 * @author sr.golovkin on 23.04.2020
 */
interface ContractorsCardFeature : Feature {

    /**
     * Создаёт интент для перехода на экран отображения информации о компании
     */
    fun getContractorInfoIntent(
        context: Context,
        url: String,
        headerMode: ContractorHeaderMode = ContractorHeaderMode.Banner
    ): Intent

    /**
     * @see getContractorInfoIntent
     * Необходимо использовать при отстутствии контекста Activity для открытия в новой таске.
     */
    fun getContractorInfoIntent(
        url: String,
        headerMode: ContractorHeaderMode = ContractorHeaderMode.Banner
    ): Intent

    /**
     * Создает интент для перехода на экран приобретения лицензии на сервис компаний.
     */
    fun getPurchaseLicenseIntent(context: Context): Intent

    /**
     * @see getPurchaseLicenseIntent
     * Необходимо использовать при отстутствии контекста Activity для открытия в новой таске.
     */
    fun getPurchaseLicenseIntent(): Intent

}