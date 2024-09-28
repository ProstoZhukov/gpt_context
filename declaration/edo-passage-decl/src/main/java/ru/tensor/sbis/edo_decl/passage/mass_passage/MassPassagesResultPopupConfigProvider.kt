package ru.tensor.sbis.edo_decl.passage.mass_passage

import android.os.Parcelable
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle

/**
 * Интерфейс поставщика прикладной конфигурации popup-сообщения о результате выполнения массовых переходов
 *
 * Интерфейс сделан для возможности кастомизации сообщения и его стиля отображения со стороны прикладника
 * Например, вывести не "Обработано 20 документов", а "Обработано 20 задач"
 *
 * @author sa.nikitin
 */
interface MassPassagesResultPopupConfigProvider : Parcelable {

    /**
     * Вернуть конфиг popup-сообщения, сформированный на основе количества обработанных документов
     *
     * @param totalDocsCount                Общее количество документов, которые были задействованы в массовом переходе.
     *                                      Будет 0, если не было доступных документов для обработки
     * @param successProcessedDocsCount     Количество успешно обработанных документов.
     *                                      Может быть 0
     * @param failedProcessedDocsCount      Количество неуспешно обработанных документов, т.е. была какая-то ошибка.
     *                                      Может быть 0 и null
     *
     */
    suspend fun getResultPopupConfig(
        totalDocsCount: Int,
        successProcessedDocsCount: Int,
        failedProcessedDocsCount: Int?
    ): MassPassagesResultPopupConfig?
}

/**
 * @SelfDocumented
 *
 * @author sa.nikitin
 */
class MassPassagesResultPopupConfig(val message: String, val style: SbisPopupNotificationStyle)