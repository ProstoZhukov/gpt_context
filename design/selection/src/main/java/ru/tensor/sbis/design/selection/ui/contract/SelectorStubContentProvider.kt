package ru.tensor.sbis.design.selection.ui.contract

import ru.tensor.sbis.design.selection.ui.utils.stub.DefaultSelectorStubContentProvider
import ru.tensor.sbis.list.base.utils.stub.StubContentProvider
import java.io.Serializable

/**
 * Сериализуемый вариант [StubContentProvider].
 * При использовании в компоненте выбора гарантируется доставка значения [SelectorStubInfo] в метод
 * [StubContentProvider.provideCreateContentFactory] - в реализации нужно требовать аргумент для безопасного
 * использования:
 * ```
 * override fun provideStubContent(result: SelectorStubInfo<Any>?): StubContent {
 *     requireNotNull(result)
 *     return StubContent { ... }
 * }
 * ```
 *
 * @sample DefaultSelectorStubContentProvider.provideCreateContentFactory
 *
 * @author ma.kolpakov
 */
interface SelectorStubContentProvider<in SERVICE_RESULT> :
    StubContentProvider<SelectorStubInfo<SERVICE_RESULT>>,
    Serializable