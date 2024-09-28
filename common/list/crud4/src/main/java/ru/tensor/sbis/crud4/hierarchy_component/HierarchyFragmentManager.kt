package ru.tensor.sbis.crud4.hierarchy_component

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import ru.tensor.sbis.design.R

/**
 * Инструмент для навигации по иерархии, с использованием двух контейнеров для размещения фрагментов переднего и
 * заднего плана.
 *
 * @param areContainersReordered `true`, если на переднем плане находится [firstContainerId], `false` если на переднем
 * плане [secondContainerId].
 *
 * @author us.bessonov
 */
internal class HierarchyFragmentManager<PATH_MODEL>(
    private val childFragmentManager: FragmentManager,
    private val root: ViewGroup,
    @IdRes
    private val firstContainerId: Int,
    @IdRes
    private val secondContainerId: Int,
    var areContainersReordered: Boolean,
    private val getProvider: () -> ListComponentProvider<PATH_MODEL>,
    private val getBundle: () -> Bundle?
) {
    private val delay = root.resources.getInteger(R.integer.animation_activity_horizontal_translate_duration).toLong()

    /** @SelfDocumented */
    val backFragment: ListComponentFragment<PATH_MODEL>?
        get() = getListFragment(getBackAndFrontContainers().first)

    /** @SelfDocumented */
    val frontFragment: ListComponentFragment<PATH_MODEL>?
        get() = getListFragment(getBackAndFrontContainers().second)

    /**
     * Провалиться в следующую папку.
     * В контейнер на заднем плане помещается новый фрагмент, после чего он выводится на передний план.
     *
     * @param folder следующая папка (`null` - корень).
     */
    fun goNext(folder: PATH_MODEL?) {
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

    /**
     * Вернуться назад.
     * Фрагмент на переднем плане анимированно удаляется, его контейнер уходит на задний план, после чего в этот
     * контейнер добавляется фрагмент с содержимым, предшествующим экрану, на который произошёл возврат.
     */
    fun goBack() {
        val (back, front) = getBackAndFrontContainers()
        val frontFragment = childFragmentManager.findFragmentById(front)
            ?: return
        runTransaction({
            setCustomAnimations(ResourcesCompat.ID_NULL, R.anim.right_out)
            remove(frontFragment)
        }, onCommitAction = {
            setPriority(back, true)
            /*
            Фрагмент, который мы закрыли, должен уйти на задний план не сразу, иначе не успеем увидеть анимацию
            закрытия.
            */
            root.postDelayed({
                addPreviousFragmentToBackIfNeeded(back, front)
            }, delay)
        })
    }

    private fun addPreviousFragmentToBackIfNeeded(@IdRes back: Int, @IdRes front: Int) {
        getPath(back)?.takeUnless(List<PATH_MODEL>::isEmpty)?.let { path ->
            rearrangeContainers()
            childFragmentManager.beginTransaction()
                .add(front, createListFragment(path.getParentPathModel()))
                .runOnCommit { setPriority(front, false) }
                .commit()
        }
    }

    private fun getBackAndFrontContainers() = if (areContainersReordered) {
        secondContainerId to firstContainerId
    } else {
        firstContainerId to secondContainerId
    }

    private fun rearrangeContainers() {
        val firstIndex = root.indexOfChild(root.findViewById(firstContainerId))
        val secondView = root.findViewById<View>(secondContainerId)
        root.removeViewInLayout(secondView)
        root.addView(secondView, firstIndex)
        areContainersReordered = !areContainersReordered
    }

    private fun setPriority(@IdRes id: Int, isForeground: Boolean) {
        (childFragmentManager.findFragmentById(id) as ListComponentFragment<*>).setPriority(isForeground)
    }

    private fun createListFragment(folder: PATH_MODEL?) = getProvider().create(getBundle(), folder)

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
        childFragmentManager.findFragmentById(containerId) as ListComponentFragment<PATH_MODEL>?

}