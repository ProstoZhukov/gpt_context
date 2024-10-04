package ru.tensor.sbis.design.selection.ui.contract

import ru.tensor.sbis.design.selection.ui.utils.stub.DefaultSelectorStubContentProvider
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.list.base.data.ResultHelper

/**
 * Информация для создания заглушки в [SelectorStubContentProvider]
 *
 * @author ma.kolpakov
 */
sealed class SelectorStubInfo<out SERVICE_RESULT>

/**
 * Прикладные данные, для которых запрошено отображение заглушки
 *
 * @see ResultHelper.isStub
 */
data class Data<SERVICE_RESULT>(
    val data: SERVICE_RESULT
) : SelectorStubInfo<SERVICE_RESULT>()

/**
 * Заглушка в связи с выбором всех доступных элементов
 */
object AllItemsSelected : SelectorStubInfo<Nothing>()

/**
 * Служебный объект, обозначающий отсутствие данных. Возможная причина: данные ещё не загружались.
 * Так как ситуация спорная, рекомендуется отображать как ошибку [StubViewCase.SBIS_ERROR] и публиковать дополнительную
 * информацию в аналитику
 *
 * @sample DefaultSelectorStubContentProvider.provideStubViewContentFactory
 */
object NoData : SelectorStubInfo<Nothing>()