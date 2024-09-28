package ru.tensor.sbis.feature_ctrl

import ru.tensor.sbis.feature_service.generated.Specification

internal class SbisFeatureMapper {
    fun map(spec: Specification): SbisFeatureInfo =
        SbisFeatureInfo(
            spec.feature,
            spec.client,
            spec.user,
            spec.lastUpdate,
            spec.lastGenError,
            spec.data,
            spec.type,
            spec.typeV2,
            spec.invalidation,
            spec.state,
            spec.needUpdate,
            spec.sendEvent
        )
}