package ru.tensor.sbis.crud.sale.model

import ru.tensor.devices.kkmservice.generated.KkmWarning

/**
 * Объект предупреждения от контроллера при проверке KKM
 *
 * @property title заголовок предупреждения
 * @property message текст предупрждения
 */
data class KkmWarningInside(
    val title: String,
    val message: String
)

/**@SelfDocumented */
fun KkmWarning.map(): KkmWarningInside {
    return KkmWarningInside(
        this.title,
        this.message
    )
}