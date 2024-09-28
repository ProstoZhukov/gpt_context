package ru.tensor.sbis.base_components.fragment.selection.shadow

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import ru.tensor.sbis.base_components.R
import ru.tensor.sbis.base_components.fragment.selection.ViewDependencyProvider

/**
 * Created by aa.mironychev on 17.05.2018.
 */
/**
 * Реализация поведения для тени в SelectionWindow (тень должна показываться,
 * когда вложенный скролл-контейнер находится в проскролленом состоянии).
 */
class SelectionWindowShadowBehavior(context: Context?, attrs: AttributeSet?)
    : CoordinatorLayout.Behavior<View>(context, attrs) {

    /**
     * Идентификатор вью - зависимости (возвращает идентификатор скроллируемой вью для конкретной реализации фрагмента)
     */
    private var mDependencyProvider: ViewDependencyProvider? = null

    /**
     * Диспетчер видимости тени (возвращает значение видимости в зависимости от состояния сколл-вью).
     */
    private var mDispatcher: ShadowVisibilityDispatcher? = null

    /**
     * Задать диспетчер видимости тени.
     */
    fun setDispatcher(dispatcher: ShadowVisibilityDispatcher?) {
        if (mDispatcher != dispatcher) {
            mDispatcher = dispatcher
        }
    }

    fun setupDependency(dependencyProvider: ViewDependencyProvider) {
        mDependencyProvider = dependencyProvider
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        // Слушаем только вертикальный скролл
        return (axes and ViewCompat.SCROLL_AXIS_VERTICAL) == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        @Suppress("DEPRECATION")
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        //обновляем visibility тени в зависимости от скрола
        handleShadowVisibilityChanges(target, child)
    }

    //определяем зависимость тени от контейнера для контента
    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        //если вью - FrameLayout и id совпадает с идентификатором контейнера для контента - эта вью является зависимостью
        return dependency is FrameLayout && dependency.id == R.id.base_components_content_container
    }
    //будет вызван после onPreDraw()
    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        //Пытаемся найти скроллящуюся View внутри контейнера для контента. Если найти вью получилось - обрабатываем
        //показ тени в соответствии с состоянием скроллящейся View. Если найти вью не получилось - выбрасываем исключение
        mDependencyProvider?.let { dependencyIdentifierProvider ->
            if (dependency is FrameLayout) {
                child.let {childView ->
                    dependency.findViewById<View>(dependencyIdentifierProvider.getContentViewId())?.let { foundView ->
                        handleShadowVisibilityChanges(foundView, childView)
                    } ?: run {
                        throw IllegalStateException("Dependency view with id \"${mDependencyProvider?.getContentViewId()}\" was not found!" +
                            " Check out the attribute value of setDependencyView(int id) call!")
                    }
                }
                return true
            }
        } ?: run {
            if (!parent.isInEditMode) {
                throw NullPointerException("ViewDependencyProvider is null! setupDependency(ViewDependencyProvider _) method was not called when instantiate " + javaClass.simpleName + "?")
            }
        }
        return false
    }

    /**
     * Обновить состояние тени
     *
     * @param scrolledView вью, поддающаяся пролистыванию и являющаяся зависимостью для целевой вью
     * @param target целевая вью, которую нужно обновить
     */
    private fun handleShadowVisibilityChanges(scrolledView: View, target: View) {
        mDispatcher?.let {
            if (it.canDispatch(scrolledView)) {
                target.visibility = it.getVisibility(scrolledView, target)
            }
        }
    }


}
