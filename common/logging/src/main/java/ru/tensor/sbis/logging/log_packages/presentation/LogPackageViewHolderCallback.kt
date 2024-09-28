package ru.tensor.sbis.logging.log_packages.presentation

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.list.view.binding.DataBindingViewHolder
import ru.tensor.sbis.list.view.binding.ViewHolderCallback
import ru.tensor.sbis.logging.R
import ru.tensor.sbis.logging.log_packages.domain.LogPackageInteractor
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.api.menu.IconItem
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeIcon
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeItemStyle
import timber.log.Timber
import javax.inject.Inject

/**
 * Делегат для [RecyclerView.Adapter] позволяющий осуществить связывание свайп-меню элементов списка. Используется чтобы
 * избежать одновременного открытия нескольких свайп-меню.
 */
class LogPackageViewHolderCallback @Inject constructor(
    private val interactor: LogPackageInteractor
) : ViewHolderCallback {

    @SuppressLint("CheckResult")
    override fun afterBindToViewHolder(data: Any, viewHolder: DataBindingViewHolder) {
        if (data is LogPackageItemViewModel) {
            viewHolder.itemView.findViewById<SwipeableLayout>(R.id.logging_swipeable_layout)
                ?.also { layout ->
                    layout.itemUuid = data.uuid.toString()
                    layout.setMenu(
                        listOf(
                            IconItem(SwipeIcon(SbisMobileIcon.Icon.smi_delete), SwipeItemStyle.RED) {
                                interactor.removeLogPackage(data.uuid)
                                    .subscribe({}, {
                                        Timber.e(it)
                                    })
                            }
                        )
                    )
                }
        }
    }
}