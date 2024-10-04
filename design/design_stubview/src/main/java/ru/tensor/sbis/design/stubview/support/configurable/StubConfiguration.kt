package ru.tensor.sbis.design.stubview.support.configurable

import androidx.annotation.StringRes
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.R
import ru.tensor.sbis.design.text_span.SimpleInformationView
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewContent

/**
 * Сущность, обеспечивающая конфигурацию отображения поддерживаемых заглушек из стандарта.
 * Построение объекта осуществляется с помощью [StubConfiguration.Builder].
 * Объект делегирует конструирование заглушек иному объекту (например, фрагменту)
 * Объект правил дистанцирует логику сопоставления ресурсов с заглушками от компонента списка, что позволяет:
 * - тонко настраивать заглушки в зависимости от экрана
 * - описывать и изменять правила отображения заглушек обособленно от компонента списка (переиспользование при необходимости)
 * - опционально описать заглушки для реестра/модуля/приложения в одном месте / пробрасывать правила через DI
 *
 * @author s.r.golovkin
 */
class StubConfiguration private constructor(private val associations: Map<Int, () -> StubViewContent>){

    /**
     * Получить готовую заглушку по переданному экземпляру контента заглушки
     * @param[content] Контент заглушки
     * @return готовая к отображению заглушка или null,
     * если среди ресурсов в переданном [SimpleInformationView.Content] не найдется ключевого,
     * по которому будет определена и создана заглушка по стандарту
     */
    fun getStubContentByInformationContent(content: SimpleInformationView.Content): StubViewContent? {
        val idArray = intArrayOf(content.headerResId, content.baseInfoResId, content.detailsResId)
        return getStubContentById(idArray.firstOrNull { it > 0 })
    }

    /**
     * Получить готовую заглушку по идентификатору произошедшей ошибки
     * @param[resId] идентификатор ресурса, ссылающегося на конкретную ошибку
     * @return готовая к отображению заглушка или null
     */
    private fun getStubContentById(@StringRes resId: Int?) = associations[resId]?.invoke()

    @Suppress("KDocUnresolvedReference")
    class Builder {
        private val associationMap = mutableMapOf<Int, () -> StubViewContent>()

        /**
         * Модифицирует [Builder], добавляя новую ассоциацию "ресурс - заглушка",
         * после чего возвращает его.
         * @param[resId] идентификатор ресурса, используемый в качестве ключа.
         * Для единообразия при обработке типовых ошибок следует использовать ресурсы, указанные в
         * [StubViewCase]. Перед использованием убедиться, что требуемые заглушки не реализованы
         * функцией [default].
         * @param[case] лямбда-функция, возвращающая экземпляр контента заглушки
         * @return [Builder]
         *
         * @see [default]
         * @see [StubContent]
         */
        fun configureByCase(@StringRes resId: Int, case: () -> StubViewContent): Builder {
            associationMap[resId] = case
            return this
        }

        /**
         * Добавить правило для заглушки не из стандарта
         */
        fun configureUnknownStub(@StringRes resId: Int, @StringRes descrtiptionRes: Int): Builder {
            return configureByCase(resId) {
                ImageStubContent(
                    StubViewCase.NO_SEARCH_RESULTS.imageType,
                    resId,
                    descrtiptionRes
                )
            }
        }

        /**
         * Установить правила по-умолчанию.
         * Поддержаны основные заглушки - отсутствие сети, данных, ошибка при фильтрации и неопознанная ошибка
         * @param[context] контекст
         * @param[actionOnClick] Действие, которое должно происходить при клике на заглушку с кликабельным текстом
         * @return [Builder]
         */
        @JvmOverloads
        fun default(forCard: Boolean = false, actionOnClick: (() -> Unit)? = null): Builder {

            fun StubViewCase.getContentInternal(key: Int, actionOnClick: (() -> Unit)?): StubViewContent {
                return actionOnClick?.let {
                    getContent(mapOf(key to actionOnClick))
                } ?: getContent()
            }

            return configureByCase(StubViewCase.SBIS_ERROR.messageRes) {
                StubViewCase.SBIS_ERROR.getContentInternal(R.string.design_stub_view_sbis_error_details_clickable, actionOnClick)
            }
                .configureByCase(StubViewCase.NO_SEARCH_RESULTS.messageRes) {
                    if (forCard) {
                        StubViewCase.PAGE_NOT_FOUND.getContent()
                    } else {
                        StubViewCase.NO_SEARCH_RESULTS.getContent()
                    }
                }
                .configureByCase(StubViewCase.NO_CONNECTION.messageRes) {

                    StubViewCase.NO_CONNECTION.getContentInternal(R.string.design_stub_view_no_connection_details_clickable, actionOnClick)
                }
                .configureByCase(StubViewCase.NO_FILTER_RESULTS.messageRes) { StubViewCase.NO_FILTER_RESULTS.getContent() }
        }

        /**@SelfDocumented*/
        fun build() = StubConfiguration(associationMap)
    }
}