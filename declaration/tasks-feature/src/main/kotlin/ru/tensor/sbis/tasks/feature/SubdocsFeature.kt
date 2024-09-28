package ru.tensor.sbis.tasks.feature

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * API подподдокументов.
 *
 * @author aa.sviridov
 */
interface SubdocsFeature : Feature {

    /**
     * Создаёт или возвращает экземпляр компонента поддокументов, см. [SubdocsComponent].
     * @param fragment фрагмент, на котором создаётся компонент, см. [Fragment].
     * @param documentUuid идентификатор документа, относительно которого будут набираться поддокументы.
     * @param ownerFaceUuid идентификатор лица-владельца.
     * @param customization кастомизация поддокументов, см. [SubdocsCustomization].
     * @param uniqueKey уникальный ключ для корректной инициализации компонента.
     */
    fun getOrCreateComponent(
        fragment: Fragment,
        documentUuid: UUID,
        ownerFaceUuid: UUID,
        customization: SubdocsCustomization = SubdocsCustomization(),
        uniqueKey: String = "",
    ): SubdocsComponent
}