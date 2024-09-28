package ru.tensor.sbis.business.common.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.localfeaturetoggle.data.FeatureSet
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import ru.tensor.sbis.manage_features.domain.ManageFeaturesFeature
import ru.tensor.sbis.verification_decl.account.UserAccount

/** Источник состояния активности редизайна компонентов Продаж. */
object RedesignState {

    private var isLocalActive: Boolean = false
    private var isCloudActive: Boolean = false

    /** Раздел запущен под фичей редизайна. */
    val isInRedesign: Boolean
        get() = isLocalActive || isCloudActive

    /** Раздел запущен в prod дизайне. */
    val isInProdDesign: Boolean
        get() = !isInRedesign

    /** Раздел запущен под фичей редизайна. */
    val isRedesign: StateFlow<Boolean> by ::_isRedesign
    private val _isRedesign = MutableStateFlow(false)

    /** @SelfDocumented */
    fun ManageFeaturesFeature.updateCloud(account: UserAccount) {
        if (account.clientId == 0 || account.userId == 0) return
        isCloudActive = isManageFeaturesEnabled(REDESIGN_FEATURE, account.userId, account.clientId)
        _isRedesign.value = isInRedesign
    }

    /** @SelfDocumented */
    fun LocalFeatureToggleService.updateLocal() {
        isLocalActive = isFeatureActivated(FeatureSet.NEW_RETAIL_REPORTS)
        _isRedesign.value = isInRedesign
    }

    /** Фича редизайна раздела Продажи. */
    private const val REDESIGN_FEATURE = "business_new_design"
}
