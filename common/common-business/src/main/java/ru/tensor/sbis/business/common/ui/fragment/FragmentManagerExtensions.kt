package ru.tensor.sbis.business.common.ui.fragment

import android.os.Handler
import android.os.Looper
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle.State
import ru.tensor.sbis.business.common.R
import ru.tensor.sbis.common.util.hasFragmentOrPendingTransaction
import ru.tensor.sbis.mvvm.utils.retain.isCreated
import ru.tensor.sbis.design.R as RDesign

/** Текущий видимый [Fragment] на переднем плане и вершине стека или null если например childFragmentManager пуст */
val FragmentManager.frontFragment: Fragment?
    get() {
        if (backStackEntryCount <= 0) return null
        val name = getBackStackEntryAt(backStackEntryCount - 1).name
        return findFragmentByTag(name)
    }

/** Идентификатор контейнера для фрагментов по умолчанию */
@IdRes
private val defaultContainerId: Int = R.id.container

/**
 * Проверить содержит ли текущий [FragmentManager] требуемый [Fragment] с типом [T]
 * @return true если фрагмент доступен
 */
inline fun <reified T> FragmentManager?.hasFragment(): Boolean =
    if (this == null) {
        false
    } else {
        fragments.filterIsInstance(T::class.java).firstOrNull() != null
    }

/**
 * Выполняет транзакцию добавления заданного [Fragment] в backstack, если он не был добавлен ранее,
 * задавая анимацию постепенного появления и постепенного исчезновения
 * @see FragmentManager.showNewScreen
 */
fun FragmentManager.showNewScreenByFadeIn(
    tag: String,
    currentToPause: Boolean = true,
    @IdRes containerId: Int = defaultContainerId,
    getInstanceAction: () -> Fragment,
) = showNewScreen(
    tag = tag,
    currentToPause = currentToPause,
    containerId = containerId,
    transactionCallback = { transaction ->
        transaction.setCustomAnimations(
            R.anim.fade_fast_in,
            0,
            R.anim.fade_fast_in,
            0
        )
    },
    getInstanceAction = getInstanceAction
)

/**
 * Выполняет транзакцию добавления заданного [Fragment] в backstack, если он не был добавлен ранее,
 * задавая анимацию входа справа налево и выхода слева направо
 * @see FragmentManager.showNewScreen
 */
fun FragmentManager.showNewScreenFromRightToLeft(
    tag: String,
    currentToPause: Boolean = true,
    @IdRes containerId: Int = defaultContainerId,
    getInstanceAction: () -> Fragment,
) = showNewScreen(
    tag = tag,
    currentToPause = currentToPause,
    containerId = containerId,
    transactionCallback = { transaction ->
        transaction.setCustomAnimations(
            RDesign.anim.right_in,
            RDesign.anim.right_out,
            RDesign.anim.right_in,
            RDesign.anim.right_out
        )
    },
    getInstanceAction = getInstanceAction
)

/**
 * Выполняет транзакцию добавления заданного [Fragment] в backstack, если он не был добавлен ранее
 *
 * @param tag опциональный ключ экрана, используемый в качестве тега, вместо имени класса
 * @param addToBackStack true, если добавляем заданный [Fragment] в backstack
 * @param currentToPause true, если ограничиваем состояние жизненного цикла текущего фрагмента снизу.
 * В этом случае будет получено событие onPause при открытии нового экрана [getInstanceAction] и onResume при возврате назад
 * @param containerId идентификатор контейнера для фрагмента
 * @param transactionCallback лямбда надстройки транзакции, например для применения анимации
 * @param getInstanceAction лямбда создания нового экземпляра. Используется только если фрагмент
 * ранее не был создан, т.е. отсутствует в [FragmentManager] по заданному [tag]
 *
 * @return true если фрагмент будет добавлен
 */
fun FragmentManager.showNewScreen(
    tag: String,
    addToBackStack: Boolean = true,
    currentToPause: Boolean = true,
    @IdRes containerId: Int = defaultContainerId,
    transactionCallback: ((FragmentTransaction) -> FragmentTransaction)? = null,
    getInstanceAction: () -> Fragment,
): Boolean {
    if (currentToPause) {
        executePendingTransactions()
    }
    val hasFragment = hasFragmentOrPendingTransaction(tag)
    val isNotRemoving = findFragmentByTag(tag)?.isRemoving?.not() ?: true
    val isExisting = hasFragment && isNotRemoving
    if (isDestroyed || isExisting) {
        return false
    }
    beginTransaction().apply {
        if (transactionCallback != null) {
            transactionCallback(this)
        } else {
            setCustomAnimations(0, 0, 0, 0)
        }
        add(
            containerId,
            getInstanceAction(),
            tag
        )
        if (currentToPause) {
            frontFragment?.takeIf { it.isCreated }?.let { setMaxLifecycle(it, State.STARTED) }
        }
        if (addToBackStack) {
            addToBackStack(tag)
        }
    }.commit()
    return true
}

/**
 * Выполняет транзакцию добавления фрагмента в контейнер с заменой
 * @param fragment добавляемый фрагмент
 * @param containerId идентификатор контейнера для фрагмента
 * @param tag тег фрагмента
 */
fun FragmentManager.showWithReplacement(
    fragment: Fragment,
    tag: String = fragment.javaClass.canonicalName.orEmpty(),
    @IdRes containerId: Int = defaultContainerId,
    addToBackStack: Boolean = true,
) {
    beginTransaction()
        .replace(containerId, fragment, tag)
        .apply { if (addToBackStack) addToBackStack(tag) }
        .commitAllowingStateLoss()
}

/* region [DialogFragment] */
/**
 * Отображает диалоговый фрагмент, если он ещё не показан
 *
 * @param tag тег фрагмента
 * @param getInstanceAction лямбда создания нового экземпляра диалога
 */
fun FragmentManager.showDialog(
    tag: String,
    immediately: Boolean = false,
    getInstanceAction: () -> DialogFragment,
) {
    if (hasFragmentOrPendingTransaction(tag)) {
        return
    }
    if (immediately) {
        !isDestroyed && Handler(Looper.getMainLooper()).post {
            if (!isStateSaved) {
                getInstanceAction().show(this, tag)
            }
        }
    } else {
        if (isDestroyed || isStateSaved) return
        try {
            executePendingTransactions()
        } catch (ex: IllegalStateException) {
            // ignore already executing
        }
        getInstanceAction().show(this, tag)
    }
}

/**
 * Скрыть диалоговый фрагмент, если он существует
 *
 * @param tag тег фрагмента
 */
fun FragmentManager.dismissDialog(
    tag: String,
    stateLoss: Boolean = false,
) {
    val dialog = findFragmentByTag(tag)
    if (dialog == null || dialog !is DialogFragment) {
        return
    }
    if (dialog.isStateSaved && stateLoss.not()) {
        return
    }
    if (stateLoss) {
        dialog.dismissAllowingStateLoss()
    } else {
        Handler().post { dialog.dismiss() }
    }
}
/* endregion [DialogFragment] */

// region BackStack
/**
 * Проверяет возможность безопасного вытаскивания состояния из стека
 * @param minBackStackEntryCount минимально допустимый размер backStack'а для обработки извлечения.
 * По-умолчанию 1, условие что стек не пустой
 *
 * @return true, если можем вытащить верхнее состояние стека, иначе false
 */
fun FragmentManager?.allowPopBackStack(minBackStackEntryCount: Int = 1): Boolean {
    if (this == null) {
        return false
    }
    return !isStateSaved && backStackEntryCount >= minBackStackEntryCount
}

/**
 * Проверяет имеется ли в стеке состояние с именем [stateName]
 *
 * @return true, если имеется
 */
fun FragmentManager?.hasNamedStack(stateName: String): Boolean {
    if (this == null) {
        return false
    }
    var count = backStackEntryCount
    while (count > 0) {
        if (getBackStackEntryAt(count - 1).name == stateName) {
            return true
        }
        --count
    }
    return false
}

/**
 * Вытаскиваем заданное состояние из стека "асинхронно", если это возможно
 * @param stateName имя предыдущего состояния для поиска в стэке
 * @param itself true если выталкивается само именованное состояние также
 *
 * @return true если для состояния [stateName] была вызвана операция удаления из стека
 */
fun FragmentManager.popNamedBackStack(stateName: String, itself: Boolean = true): Boolean =
    if (allowPopBackStack() && hasNamedStack(stateName)) {
        val flag = if (itself) {
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        } else 0
        popBackStack(stateName, flag)
        true
    } else {
        false
    }

/**
 * Очищаем все вхождения в стеке состояний
 *
 * @return true, если BackStack очищен, иначе false
 */
fun FragmentManager.clearStackSafely(postAction: () -> Unit) {
    if (allowPopBackStack()) {
        val tag = getBackStackEntryAt(0).name
        if (tag.isNullOrBlank()) {
            Handler().post {
                for (i in 0 until backStackEntryCount) {
                    popBackStackImmediate()
                }
                postAction()
            }
        } else {
            popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            postAction()
        }
    }
}

/**
 * Пытается безопасно выполнить извлечение верхнего состояния из стека,
 * операция выполняется сразу внутри вызова
 *
 * @param minBackStackCountCondition минимально допустимый размер backStack'а для обработки извлечения
 * @return true если операция безопасно выполнена, false если выполнение операции не допускается
 */
fun FragmentManager.popLastBackStackState(minBackStackCountCondition: Int = 1): Boolean {
    if (allowPopBackStack(minBackStackCountCondition)) {
        // позволяет избежать IllegalStateException, когда во FragmentManager'e выполняется другая операция
        Handler(Looper.getMainLooper()).post {
            !isStateSaved && popBackStackImmediate()
        }
        return true
    }
    return false
}
// endregion BackStack