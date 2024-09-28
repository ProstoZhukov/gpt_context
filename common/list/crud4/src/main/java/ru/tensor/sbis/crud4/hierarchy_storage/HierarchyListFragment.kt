package ru.tensor.sbis.crud4.hierarchy_storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.crud4.R
import ru.tensor.sbis.service.PathProtocol

/**
 * Фрагмент для управления проваливанием в папку для иерархических списков
 *
 * @author ma.kolpakov
 */
class HierarchyListFragment<COLLECTION, PATH_MODEL : PathProtocol<IDENT>, IDENT, FILTER> : Fragment() {

    private var hierarchyFragmentManager: HierarchyFragmentManager<COLLECTION, PATH_MODEL, IDENT, FILTER>? = null

    companion object {

        /**
         * Создать [HierarchyListFragment], использующий [provider] для создания экранов конкретных уровней иерархии.
         */
        fun <COLLECTION, PATH_MODEL : PathProtocol<IDENT>, IDENT, FILTER> newInstance(
            provider: ListComponentProvider<PATH_MODEL, COLLECTION, IDENT, FILTER>,
            bundle: Bundle?
        ): Fragment = HierarchyListFragment<COLLECTION, PATH_MODEL, IDENT, FILTER>().apply {
            arguments = Bundle().apply {
                putBundle(KEY_BUNDLE, bundle)
                putParcelable(KEY_PROVIDER, provider)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val firstContainerId = R.id.crud4_hierarchy_list_first_container_id
        val secondContainerId = R.id.crud4_hierarchy_list_second_container_id
        return FrameLayout(requireContext()).apply {
            FrameLayout(requireContext()).let {
                it.id = firstContainerId
                addView(it)
            }
            FrameLayout(requireContext()).let {
                it.id = secondContainerId
                addView(it)
            }
        }.also {
            hierarchyFragmentManager = HierarchyFragmentManager(
                this,
                childFragmentManager,
                it,
                firstContainerId,
                secondContainerId,
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
                if (f is ListComponentFragment<*, *, *, *>) {
                    subscribeListFragment(f as ListComponentFragment<COLLECTION, FILTER, PATH_MODEL, IDENT>)
                }
            }
        }, false)
    }

    override fun onResume() {
        super.onResume()
        updateFragmentPriorities()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hierarchyFragmentManager = null
    }

    /**
     * Открыть папку
     */
    private fun openFolder(view: IDENT, folder: IDENT?) {
        hierarchyFragmentManager?.goNext(folder!!, view)
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
            hierarchyFragmentManager?.goNext(null, null)
        }
    }

    private fun subscribeListFragment(fragment: ListComponentFragment<COLLECTION, FILTER, PATH_MODEL, IDENT>) {
        fragment.swipeBackEvent.observe(fragment.viewLifecycleOwner) { event ->
            hierarchyFragmentManager?.deferredBack(event)
        }

        with(fragment.getViewModel()) {
            onOpenFolder.observe(fragment.viewLifecycleOwner) {
                it?.let { path -> this@HierarchyListFragment.openFolder(path.first, path.second) }
            }

            onMove.observe(fragment.viewLifecycleOwner) {
                it?.let { path -> hierarchyFragmentManager?.move(path) }
            }

            onGoBackFolder.observe(fragment.viewLifecycleOwner) {
                this@HierarchyListFragment.goBackFolder()
            }

            onChangeFilter.observe(fragment.viewLifecycleOwner) {
                hierarchyFragmentManager!!.changeFilter(it!!)
            }
        }
    }

    private fun updateFragmentPriorities() {
        hierarchyFragmentManager?.apply {
            frontFragment?.let {
                it.setPriority(true)
                backFragment?.setPriority(false)
            } ?: backFragment?.setPriority(true)

            restoreFragmentOrder()
        }
    }

    private fun getProvider() =
        requireArguments().getParcelableUniversally<ListComponentProvider<PATH_MODEL, COLLECTION, IDENT, FILTER>>(
            KEY_PROVIDER
        )!!

    private fun getBundle() = requireArguments().getBundle(KEY_BUNDLE)

}

private const val KEY_BUNDLE = "KEY_BUNDLE"
private const val KEY_PROVIDER = "KEY_PROVIDER"