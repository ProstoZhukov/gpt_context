package ru.tensor.sbis.business.common.ui.base.contract

import android.content.Context
import androidx.databinding.ObservableBoolean
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.stubview.StubViewMode

/**
 * Контракт состояния VM с View заглушки для отображения ошибки или информации о отсутствии данных
 * Спецификация: http://axure.tensor.ru/MobileStandart8/#p=%D0%B7%D0%B0%D0%B3%D0%BB%D1%83%D1%88%D0%BA%D0%B8&g=1
 *
 * @author as.chadov
 *
 * @property show [ObservableBoolean] стоит ли отображать заглушку
 * @property shouldPlaceholderTakeAllAvailableHeightInParent должен ли View заглушки занимать всю доступную высоту в
 * родительском View, при наличии свободного места
 * @property shouldPlaceholderWrapContentByDefault должен ли View заглушки иметь высоту wrap content, если значение
 * [shouldPlaceholderTakeAllAvailableHeightInParent] ложно
 * @property stubMode режим отображения заглушки
 * @property stubMinHeight сжимать ли высоту до минимальной
 **/
interface InfoContract {

    val show: ObservableBoolean

    var shouldPlaceholderTakeAllAvailableHeightInParent: Boolean

    val shouldPlaceholderWrapContentByDefault: Boolean

    var stubMode: StubViewMode

    var stubMinHeight: Boolean

    /** Сформировать модель контента заглушки на основе вью-модели */
    fun content(context: Context): StubViewContent

    /** Освободить ресурсы занимаемые вью-моделью заглушки */
    fun dispose()
}