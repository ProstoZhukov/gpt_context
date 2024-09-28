package ru.tensor.sbis.video_monitoring_decl.model

import kotlinx.parcelize.Parcelize

@Parcelize
internal class DefaultCollectionFilter(
    override val tariff: String? = null,
    override val active: Boolean? = null,
    override val working: Boolean? = null,
    override val detection: Boolean? = null
) : CollectionFilter