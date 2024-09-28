package ru.tensor.sbis.crud4.hierarchy_storage

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import ru.tensor.sbis.crm.generated.PathModelOfHClientListViewModelMapOfStringString
import ru.tensor.sbis.design.R
import ru.tensor.sbis.service.CollectionStorageProtocol
import ru.tensor.sbis.service.PathProtocol
import java.util.UUID

private const val KEY_ARE_CONTAINERS_REORDERED = "KEY_ARE_CONTAINERS_REORDERED"

/**
 * Инструмент для навигации по иерархии, с использованием двух контейнеров для размещения фрагментов переднего и
 * заднего плана.
 *
 * @param areContainersReordered `true`, если на переднем плане находится [firstContainerId], `false` если на переднем
 * плане [secondContainerId].
 *
 * @author us.bessonov
 */
internal class HierarchyFragmentManager<COLLECTION, PATH_MODEL : PathProtocol<IDENT>, IDENT, FILTER>(
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val childFragmentManager: FragmentManager,
    private val root: ViewGroup,
    @IdRes
    private val firstContainerId: Int,
    @IdRes
    private val secondContainerId: Int,
    private val getProvider: () -> ListComponentProvider<PATH_MODEL, COLLECTION, IDENT, FILTER>,
    private val getBundle: () -> Bundle?,
) {

    /**
     * TODO Изучить решение. https://online.sbis.ru/opendoc.html?guid=27fe6fd5-a3bd-4370-90bf-838590774613&client=3
     */
    @Suppress("UNCHECKED_CAST")
    private val collectionStorageHolder: HierarchyStorageViewModel<COLLECTION, PATH_MODEL, IDENT, FILTER> =
        ViewModelProvider(viewModelStoreOwner, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return HierarchyStorageViewModel(getProvider().createStorage(), extras.createSavedStateHandle()) as T
            }
        })[HierarchyStorageViewModel::class.java] as HierarchyStorageViewModel<COLLECTION, PATH_MODEL, IDENT, FILTER>

    private val collectionStorage: CollectionStorageProtocol<COLLECTION, PATH_MODEL, IDENT, FILTER> =
        collectionStorageHolder.storage

    private var areContainersReordered: Boolean
        get() = collectionStorageHolder.savedStateHandle[KEY_ARE_CONTAINERS_REORDERED] ?: false
        set(value) = collectionStorageHolder.savedStateHandle.set(KEY_ARE_CONTAINERS_REORDERED, value)

    /** @SelfDocumented */
    val backFragment: ListComponentFragment<COLLECTION, FILTER, PATH_MODEL, IDENT>?
        get() = getListFragment(getBackAndFrontContainers().first)

    /** @SelfDocumented */
    val frontFragment: ListComponentFragment<COLLECTION, FILTER, PATH_MODEL, IDENT>?
        get() = getListFragment(getBackAndFrontContainers().second)

    /**
     * Провалиться в следующую папку.
     * В контейнер на заднем плане помещается новый фрагмент, после чего он выводится на передний план.
     *
     * @param folder следующая папка (`null` - корень).
     */
    fun goNext(folder: IDENT?, view: IDENT?) {
        val collection = createCollection(folder, view)

        openFolder(collection)
    }

    fun move(pathModel: PATH_MODEL) {
        val collection = collectionStorage.move(pathModel)
        openFolder(collection)
    }

    private fun openFolder(collection: COLLECTION) {
        val (back, front) = getBackAndFrontContainers()
        val newListFragmentFront = createListFragment()
        runTransaction({
            replace(front, newListFragmentFront)
        }, onCommitAction = {
            println("fHClientListViewM  NEXT : $collection")
            newListFragmentFront.getViewModel().setCollection(collection)
            setPriority(front, true)
        })

        if (getListFragment(back) == null) {
            childFragmentManager.beginTransaction()
                .add(back, createListFragment())
                .commit()
            setPriority(back, true)
        }
    }

    /**
     * Вернуться назад.
     * Фрагмент на переднем плане анимированно удаляется, его контейнер уходит на задний план, после чего в этот
     * контейнер добавляется фрагмент с содержимым, предшествующим экрану, на который произошёл возврат.
     */
    fun goBack() {
        val (back, front) = getBackAndFrontContainers()
        val frontFragment = getListFragment(front)
            ?: return

        val backFragment = getListFragment(back)
            ?: return
        backFragment.getViewModel()
            .setCollection(collectionStorage.createPrev(frontFragment.getViewModel().onPath.value?.getParentPathModel()?.ident))
        collectionStorage.commitPrev()

        runTransaction({
            setCustomAnimations(ResourcesCompat.ID_NULL, R.anim.right_out)
            remove(frontFragment)
        }, onCommitAction = {
            setPriority(back, true)
            runTransaction({
                add(front, createListFragment())
            }, {
                setPriority(front, false)
                rearrangeContainers()
            })

        })
    }

    fun deferredBack(event: SwipeBackEvent) {
        when (event) {
            SwipeBackEvent.START -> {
                val (back, front) = getBackAndFrontContainers()
                val frontFragment = getListFragment(front)
                    ?: return

                val backFragment = getListFragment(back)
                    ?: return
                val folder = frontFragment.getViewModel().onPath.value?.getParentPathModel()?.ident
                val collection =
                    collectionStorage.createPrev(folder)

                backFragment.getViewModel().setCollection(collection)
            }

            SwipeBackEvent.END -> {
                collectionStorage.rollbackPrev()
            }

            SwipeBackEvent.END_BACK -> {
                collectionStorage.commitPrev()

                val (back, front) = getBackAndFrontContainers()
                val frontFragment = getListFragment(front)
                    ?: return

                runTransaction({
                    setCustomAnimations(ResourcesCompat.ID_NULL, R.anim.right_out)
                    remove(frontFragment)
                }, onCommitAction = {
                    setPriority(back, true)
                    runTransaction({
                        add(front, createListFragment())
                    }, {
                        setPriority(front, false)
                        rearrangeContainers()
                    })

                })
            }

            SwipeBackEvent.IDLE -> {}
        }
    }

    internal fun restoreFragmentOrder() {
        if (areContainersReordered) {
            root.findViewById<View>(firstContainerId).bringToFront()
        }
    }

    private fun getBackAndFrontContainers() = if (areContainersReordered) {
        secondContainerId to firstContainerId
    } else {
        firstContainerId to secondContainerId
    }

    private fun rearrangeContainers() {
        if (areContainersReordered) {
            root.findViewById<View>(secondContainerId)
        } else {
            root.findViewById<View>(firstContainerId)
        }.bringToFront()

        areContainersReordered = !areContainersReordered
    }

    private fun setPriority(@IdRes id: Int, isForeground: Boolean) {
        (childFragmentManager.findFragmentById(id) as? ListComponentFragment<*, *, *, *>)?.setPriority(isForeground)
    }

    fun goNext(folder: PATH_MODEL) {
        val (back, front) = getBackAndFrontContainers()
        when {
            childFragmentManager.findFragmentById(back) == null -> {
                runTransaction({
                    add(back, createListFragment(folder))
                }, onCommitAction = {
                    setPriority(back, true)
                })
            }

            childFragmentManager.findFragmentById(front) == null -> {
                runTransaction({
                    add(front, createListFragment(folder))
                }, onCommitAction = {
                    setPriority(front, true)
                    setPriority(back, false)
                })
            }

            else -> {
                runTransaction({
                    replace(back, createListFragment(folder))
                }, onCommitAction = {
                    rearrangeContainers()
                    setPriority(back, true)
                    setPriority(front, false)
                })
            }
        }
    }

    internal fun changeFilter(filter: FILTER) {
        val (_, front) = getBackAndFrontContainers()
        getListFragment(front)!!.getViewModel().setCollection(
            collectionStorage.changeFilter(filter)
        )
    }

    private fun createListFragment() =
        getProvider().create(getBundle(), null)

    private fun createListFragment(folder: PATH_MODEL?) =
        getProvider().create(getBundle(), folder)

    private fun createCollection(folder: IDENT?, view: IDENT?): COLLECTION {
        return if (folder == null) {
            collectionStorage.get()
        } else {
            collectionStorage.next(view ?: folder, folder)
        }
    }

    private fun getFolder(folder: PATH_MODEL?): UUID? {
        return (folder as? PathModelOfHClientListViewModelMapOfStringString)?.ident?.field1
    }

    private fun getPath(@IdRes id: Int) = getListFragment(id)?.getViewModel()?.onPath?.value

    private fun runTransaction(action: FragmentTransaction.() -> Unit, onCommitAction: () -> Unit) {
        childFragmentManager.beginTransaction()
            .apply(action)
            .runOnCommit(onCommitAction)
            .commit()
    }

    private fun List<PATH_MODEL>.getParentPathModel() = getOrNull(lastIndex - 1)

    @Suppress("UNCHECKED_CAST")
    private fun getListFragment(containerId: Int) =
        childFragmentManager.findFragmentById(containerId) as ListComponentFragment<COLLECTION, FILTER, PATH_MODEL, IDENT>?

}
