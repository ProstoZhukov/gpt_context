package ru.tensor.sbis.master_detail.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.list.BuildConfig
import ru.tensor.sbis.list.R
import ru.tensor.sbis.list.view.SbisList
import ru.tensor.sbis.master_detail.MasterDetailFragment
import ru.tensor.sbis.master_detail.SelectionHelper
import timber.log.Timber

/**
 * Колбек [FragmentManager] который вызовет метод Мастер-фрагмента для включения режима подсветки нажатой строки.
 * Если фрагмент использует SbisList, то он будет найден по id и в нем выставится режим выделения строки напрямую.
 *
 * @author du.bykov
 */
internal class CallbacksForSelectionHighlighting : FragmentManager.FragmentLifecycleCallbacks() {

    /**
     * А если Master фрагмент использует SbisList, то будет вызван [SbisList.highlightSelection], иначе,
     * если Master фрагмент реализует интерфейс [SelectionHelper], будет вызван метод [SelectionHelper.shouldHighlightSelectedItems].
     * Если не произойдет ни того ни другого, будет выбранено исключения [IllegalStateException] в DEBUG сборке, а для
     * RELEASE сборки, это исключение будет залогировано.
     */
    override fun onFragmentResumed(fm: FragmentManager, fragment: Fragment) {
        if (fragment.id == MasterDetailFragment.masterContainer) {
            callHighlightSelectionOnMaster(fm)
        }
    }

    private fun callHighlightSelectionOnMaster(fragmentManager: FragmentManager) {
        val fragment = fragmentManager.findFragmentById(MasterDetailFragment.masterContainer)

        if (!tryHighlightSelectionOnSbisList(fragment))
            if (fragment is SelectionHelper) {
                fragment.shouldHighlightSelectedItems()
                return
            }
        val exception =
            IllegalStateException("Master-fragment is supposed to implement Master interface, but it doesn't")

        if (BuildConfig.DEBUG) Timber.e(exception)
        else Timber.d(exception)
    }

    private fun tryHighlightSelectionOnSbisList(fragment: Fragment?): Boolean {
        val sbisList = fragment?.requireView()?.findViewById<SbisList>(
            R.id.list_sbisList
        ) ?: return false
        sbisList.highlightSelection()
        return true
    }
}