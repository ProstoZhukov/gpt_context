package ru.tensor.sbis.mvp.presenter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter

/**
 * Набор делегатов для использования в реализациях [BaseTwoWayPaginationView].
 *
 * @author am.boldinov
 */
object PresenterViewDelegates {

    /** @SelfDocumented */
    fun fragmentError(fragment: Fragment) = object : DisplayErrorDelegate {
        override fun showLoadingError(errorTextResId: Int) {
            fragment.popupErrorDelegate().showLoadingError(errorTextResId)
        }

        override fun showLoadingError(errorText: String) {
            fragment.popupErrorDelegate().showLoadingError(errorText)
        }
    }

    /** @SelfDocumented */
    fun <DM> adapterDispatcher(adapter: BaseTwoWayPaginationAdapter<DM>) = object : TwoWayAdapterDispatcher<DM> {
        override fun updateDataList(dataList: MutableList<DM>?, offset: Int) {
            adapter.setData(dataList, offset);
        }

        override fun updateDataListWithoutNotification(dataList: MutableList<DM>?, offset: Int) {
            adapter.setDataWithoutNotify(dataList, offset)
        }

        override fun notifyItemsInserted(position: Int, count: Int) {
            adapter.notifyItemRangeInserted(position, count)
        }

        override fun notifyItemsChanged(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun notifyItemsChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun notifyItemsRemoved(position: Int, count: Int) {
            adapter.notifyItemRangeRemoved(position, count)
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun notifyDataSetChanged() {
            adapter.notifyDataSetChanged()
        }
    }
}