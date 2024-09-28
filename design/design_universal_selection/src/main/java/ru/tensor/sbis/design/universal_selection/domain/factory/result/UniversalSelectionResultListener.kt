package ru.tensor.sbis.design.universal_selection.domain.factory.result

import androidx.fragment.app.FragmentActivity
import io.reactivex.internal.disposables.DisposableContainer
import ru.tensor.sbis.communication_decl.selection.universal.UniversalSelectionConfig
import ru.tensor.sbis.communication_decl.selection.universal.data.UniversalSelectionData
import ru.tensor.sbis.communication_decl.selection.universal.data.UniversalSelectionItem as UniversalSelectionItemDecl
import ru.tensor.sbis.communication_decl.selection.universal.manager.UniversalSelectionResultDelegate
import ru.tensor.sbis.design.universal_selection.contract.UniversalSelectionFeatureFacade
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalItem
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalSelectionItem
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalSelectionFolderItem
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener.SelectionComponentResult
import javax.inject.Inject

/**
 * Реализация слушателя результата компонента универсального выбора.
 *
 * @property config настройка компонента универсального выбора.
 *
 * @author vv.chekurda
 */
internal class UniversalSelectionResultListener @Inject constructor(
    private val config: UniversalSelectionConfig
) : SelectionResultListener<UniversalItem, FragmentActivity> {

    private val resultDelegate: UniversalSelectionResultDelegate
        get() = UniversalSelectionFeatureFacade.getUniversalSelectionResultDelegate()

    override fun onComplete(
        activity: FragmentActivity,
        result: SelectionComponentResult<UniversalItem>,
        requestKey: String,
        disposable: DisposableContainer
    ) {
        val data = UniversalSelectionData(items = result.items.asDeclarative())
        resultDelegate.onSuccess(
            data = data,
            requestKey = requestKey
        )
        if (config.closeOnComplete) {
            activity.onBackPressed()
        }
    }

    override fun onCancel(activity: FragmentActivity, requestKey: String) {
        resultDelegate.onCancel(requestKey)
        if (config.closeOnCancel) activity.onBackPressed()
    }

    private fun List<UniversalItem>.asDeclarative(): List<UniversalSelectionItemDecl> =
        mapNotNull {
            when (it) {
                is UniversalSelectionItem -> {
                    BaseUniversalSelectionItemModel(
                        id = it.id.value,
                        title = it.title,
                        subtitle = it.subtitle,
                        isFolder = false
                    )
                }
                is UniversalSelectionFolderItem -> {
                    BaseUniversalSelectionItemModel(
                        id = it.id.value,
                        title = it.title,
                        subtitle = it.subtitle,
                        isFolder = true
                    )
                }
            }
        }
}