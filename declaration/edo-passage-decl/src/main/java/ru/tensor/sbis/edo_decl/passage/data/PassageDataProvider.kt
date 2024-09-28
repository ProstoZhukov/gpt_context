package ru.tensor.sbis.edo_decl.passage.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.edo_decl.passage.data.external_source.PassageDataExternalSource
import ru.tensor.sbis.edo_decl.passage.data.external_source.PassageDataExternalSourceUi

/**
 * Поставщик данных для перехода
 * Дополнительными данными являются комментарий и вложения
 *
 * @author sa.nikitin
 */
sealed class PassageDataProvider : Parcelable {

    /**
     * Источником является пользователь, комментарий и вложения добавляются им через панель ввода
     */
    @Parcelize
    object Default : PassageDataProvider()

    /**
     * Источником является пользователь, комментарий и вложения добавляются им через панель ввода
     * Можно настроить доступность прикрепления вложений через [isCanAttachFiles]
     */
    @Parcelize
    class CustomizableDefault(val isCanAttachFiles: Boolean) : PassageDataProvider()

    /**
     * Источником является [PassageDataExternalSource], панель ввода скрыта
     *
     * Если используется [PassageDataExternalSourceUi], то в соответствующие точки жизненного цикла будут вызваны
     * [PassageDataExternalSourceUi.setFragmentManager], [PassageDataExternalSourceUi.removeFragmentManager]
     */
    @Parcelize
    class ExternalSource(val source: PassageDataExternalSource) : PassageDataProvider()
}