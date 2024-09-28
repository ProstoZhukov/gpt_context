package ru.tensor.sbis.crud4.hierarchy_component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.crud4.R

/**
 * Фрагмент для управления проваливанием в папку для иерархических списков
 *
 * @author ma.kolpakov
 */
class HierarchyListFragment<PATH_MODEL> : Fragment() {

    private var hierarchyFragmentManager: HierarchyFragmentManager<PATH_MODEL>? = null

    companion object {

        /**
         * Создать [HierarchyListFragment], использующий [provider] для создания экранов конкретных уровней иерархии.
         */
        fun <PATH_MODEL> newInstance(
            provider: ListComponentProvider<PATH_MODEL>,
            bundle: Bundle?
        ): Fragment = HierarchyListFragment<PATH_MODEL>().apply {
            arguments = Bundle().apply {
                putBundle(KEY_BUNDLE, bundle)
                putParcelable(KEY_PROVIDER, provider)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val areContainersReordered = savedInstanceState?.getBoolean(KEY_ARE_CONTAINERS_REORDERED) ?: false
        val firstContainerId = R.id.crud4_hierarchy_list_first_container_id
        val secondContainerId = R.id.crud4_hierarchy_list_second_container_id
        return FrameLayout(requireContext()).apply {
            FrameLayout(requireContext()).let {
                it.id = if (areContainersReordered) secondContainerId else firstContainerId
                addView(it)
            }
            FrameLayout(requireContext()).let {
                it.id = if (areContainersReordered) firstContainerId else secondContainerId
                addView(it)
            }
        }.also {
            hierarchyFragmentManager = HierarchyFragmentManager(
                childFragmentManager,
                it,
                firstContainerId,
                secondContainerId,
                areContainersReordered,
                { getProvider() }
            ) { getBundle() }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showRootIfNeeded()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {

            @Suppress("UNCHECKED_CAST")
            override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                if (f is ListComponentFragment<*>) {
                    subscribeListFragment(f as ListComponentFragment<PATH_MODEL>)
                }
            }
        }, false)
    }

    override fun onResume() {
        super.onResume()
        updateFragmentPriorities()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_ARE_CONTAINERS_REORDERED, hierarchyFragmentManager?.areContainersReordered ?: false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hierarchyFragmentManager = null
    }

    /**
     * Открыть папку
     */
    private fun openFolder(folder: PATH_MODEL?) {
        hierarchyFragmentManager?.goNext(folder)
    }

    /**
     * Вернуться в предыдущую папку.
     *
     * TODO: сделали публичным, что бы можно было обращаться извне, продумать лучшее решение.
     * TODO: https://online.sbis.ru/opendoc.html?guid=7eddbb5d-f76c-472c-a7a6-fc773fe5e1cc&client=3
     */
    fun goBackFolder() {
        hierarchyFragmentManager?.goBack()
    }

    private fun showRootIfNeeded() {
        if (hierarchyFragmentManager?.frontFragment == null && hierarchyFragmentManager?.backFragment == null) {
            openFolder(null)
        }
    }

    private fun subscribeListFragment(fragment: ListComponentFragment<PATH_MODEL>) {
        with(fragment.getViewModel()) {
            onMove.observe(viewLifecycleOwner) {
                it?.let { path -> this@HierarchyListFragment.openFolder(path) }
            }

            onGoBackFolder.observe(viewLifecycleOwner) {
                this@HierarchyListFragment.goBackFolder()
            }
        }
    }

    private fun updateFragmentPriorities() {
        hierarchyFragmentManager?.apply {
            frontFragment?.let {
                it.setPriority(true)
                backFragment?.setPriority(false)
            } ?: backFragment?.setPriority(true)
        }
    }

    private fun getProvider() =
        requireArguments().getParcelableUniversally<ListComponentProvider<PATH_MODEL>>(KEY_PROVIDER)!!

    private fun getBundle() = requireArguments().getBundle(KEY_BUNDLE)

}

private const val KEY_BUNDLE = "KEY_BUNDLE"
private const val KEY_PROVIDER = "KEY_PROVIDER"
private const val KEY_ARE_CONTAINERS_REORDERED = "KEY_ARE_CONTAINERS_REORDERED"