package ru.tensor.sbis.design.selection.ui.contract.list

import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.utils.stub.StubContentProvider

/**
 * Функция трансляции прикладной модели данных в [SelectorItemModel]. Для преобразования прикладной модели в заглушку
 * используется [StubContentProvider.provideStubContent].
 *
 * Если из _DataRefreshCallback_ необходимо передавать мета данные, в качестве [SERVICE_RESULT] можно использовать
 * обёртку [ServiceResult]
 *
 * @param SERVICE_RESULT тип прикладной модели данных
 *
 * @see StubContentProvider
 * @see ResultHelper.isStub
 *
 * @author ma.kolpakov
 */
interface ListMapper<in SERVICE_RESULT, out DATA : SelectorItemModel> : (SERVICE_RESULT) -> List<DATA>