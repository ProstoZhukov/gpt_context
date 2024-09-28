package ru.tensor.sbis.crud3.view

import androidx.annotation.AnyThread
import ru.tensor.sbis.design.stubview.StubViewContent

/**
 * Используется моделью представления списочного компонента для предоставления заглушки, соответствующей полученному от
 * микросервиса типу заглушки для показа.
 */
fun interface StubFactory {

    /**
     * Создать заглушку для указанного типа [StubType].
     */
    @AnyThread
    fun create(type: StubType): StubViewContent
}

/**
 * Реализация всегда отдает переданный в [factory] тип заглушки для любого типа заглушки, который микросервис требует
 * показать.
 * Использовать можно как быстрое решения, когда проектирование не предоставило макеты заглушек для всех
 * случаев(см. [StubType]). НО! Рекомендуется, все-таки, затребовать от проектировщиков правильные заглушки для всех
 * случаев.
 */
class StubFactoryOneForAll(private val factory: StubFactory) : StubFactory {

    override fun create(type: StubType) = factory.create(type)
}

/**
 * Тип данных полноэкранной заглушки.
 */
enum class StubType {
    /** Нет данных по дефолтному фильтру. */
    NO_DATA,

    /** Нет данных по недефолтному фильтру. */
    BAD_FILTER,

    /** Нет сети. */
    NO_NETWORK,

    /** Проблемы с сервером. */
    SERVER_TROUBLE,
}
