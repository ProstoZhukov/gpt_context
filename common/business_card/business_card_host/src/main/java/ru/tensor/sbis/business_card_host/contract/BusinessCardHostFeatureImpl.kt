package ru.tensor.sbis.business_card_host.contract

import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.business.card.mobile.generated.BusinessCardManager
import ru.tensor.business.card.mobile.generated.CountType
import ru.tensor.sbis.business_card_host.presentation.view.BusinessCardHostFragment
import ru.tensor.sbis.business_card_host_decl.ui.BusinessCardHostFeature
import java.util.UUID

/**
 * Реализация фичи модуля визиток
 */
class BusinessCardHostFeatureImpl : BusinessCardHostFeature {

    /**
     * Получить информацию о доступности визитки
     */
    override suspend fun isBusinessCardAvailable(): Boolean =
        withContext(Dispatchers.IO) {
            val controller = BusinessCardManager.instance()
            controller.checkCount().countType != CountType.EMPTY
        }

    /**
     * Получить основной хост-фрагмент модуля визиток,
     * в зависимости от количества визиток будет определено, какой экран открыть следующим
     */
    override fun getBusinessCardHostFragment(personUuid: UUID): Fragment =
        BusinessCardHostFragment.newInstance(personUuid)
}