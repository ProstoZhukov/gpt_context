/**
 * Инструменты для реализации сложной логики отображения поиска в [AppBarLayout]
 *
 * @author sr.golovkin on 18.06.2020
 */

@file:Suppress("KDocUnresolvedReference")

package ru.tensor.sbis.design.view.input.searchinput.util

import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import ru.tensor.sbis.design.toolbar.ToolbarTabLayout
import ru.tensor.sbis.design.view.input.searchinput.SearchInput

/**
 * Реализация репозитория состояний строки поиска в разных реестрах
 * Представляет собой синглтон-хранилище пар ключ-значение, где ключем выступает путь до строки поиска,
 * а значением - состояние из перечисления [SearchInputState]
 * @see [SearchInput.formPathOfUsage]
 */
internal object SearchStateRepository {

    private val store = hashMapOf<String, SearchInputState>()

    fun getSearchInputStateForScreen(screenId: String): SearchInputState {
        return store[screenId] ?: SearchInputState.HIDDEN
    }

    fun toggleInputState(screenId: String, newState: SearchInputState): SearchInputState {
        store[screenId] = newState
        return newState
    }

    /**
     * Сбрасывает состояния строк поиска во всех реестрах до скрытого
     */
    fun reset() {
        store.clear()
    }
}

/**
 * Состояние отображения поисковой строки
 */
internal enum class SearchInputState {

    /**
     * Строка поиска показана, appbar раскрыт полностью.
     */
    SHOWN,

    /**
     * Строка поиска спрятана, appBar раскрыт частично
     */
    HIDDEN
}

/**
 * Развернуть скрытую строку поиска под шапкой путем отключения игнорирования оффсета и его сбросом.
 * Стандартный [AppBarLayout.setExpanded] может не работать из-за использования
 * [AppBarLayoutWithDynamicElevationBehavior].
 */
fun expandSearchInput(appBarLayout: AppBarLayout) {
    val params = (appBarLayout.layoutParams as? CoordinatorLayout.LayoutParams)
    params?.let { layoutParams ->
        val behavior = layoutParams.behavior as? AppBarLayoutWithDynamicElevationBehavior
        behavior?.expandSearchInput()
    }
}

/**
 * Свернуть развёрнутую строку поиска под шапкой путем отключения игнорирования оффсета и его сбросом.
 * Стандартный [AppBarLayout.setExpanded] может не работать из-за использования
 * [AppBarLayoutWithDynamicElevationBehavior].
 */
fun collapseSearchInput(appBarLayout: AppBarLayout) {
    val params = (appBarLayout.layoutParams as? CoordinatorLayout.LayoutParams)
    params?.let { layoutParams ->
        val behavior = layoutParams.behavior as? AppBarLayoutWithDynamicElevationBehavior
        behavior?.collapseSearchInput()
    }
}

/**
 * Функция поиска компонентов [SearchInput] и [ToolbarTabLayout] в иерархии вью.
 * Первоначально ищет [SearchInput], поиском [ToolbarTabLayout] занимается только в том случае, если был найден [SearchInput]
 * и его требуется искать согласно параметру функции [needToFindTabs].
 * Поиск производит в двух направлениях, в 2 этапа:
 * Строку поиска ищет в рамках переданного родителя, так как ожидается, что [SearchInput] гарантированно находится в его рамках.
 * В случае, если требуется найти TabLayout - пробуем найти и его вместе со строкой поиска (SearchInput и TabLayout могут иметь общего родителя)
 * В случае, если строка поиска была найдена, а так же требуется найти TabLayout, который не был найден при проходе вглубь -
 * производится поиск в обратном направлении - наружу:
 * Проверяются соседи внутри текущего родителя, после чего проверка переводится на 1 уровень выше по дереву,
 * где проверяются все соседи вью-группы с прошлой итерации (кроме этой вью-группы) в направлении вглубь.
 * Операция повторяется до тех пор, пока мы не дойдем до вершины иерархии, родительской вью-группы с Visibility.GONE или не найдем [ToolbarTabLayout]
 */
internal fun findSearchInputAndTabs(parent: ViewGroup): Pair<SearchInput?, ToolbarTabLayout?> {
    // Локальная функция поиска ToolbarTabLayout в пределах указанной currentView
    fun tryFindTabLayoutTraversal(currentView: View): ToolbarTabLayout? {
        var found: ToolbarTabLayout? = null
        if (currentView.isVisible) {
            if (currentView is ToolbarTabLayout) {
                found = currentView
            } else if (currentView is ViewGroup) {
                for (i in 0 until currentView.childCount) {
                    val child = currentView.getChildAt(i)
                    found = tryFindTabLayoutTraversal(child)
                    if (found != null) {
                        break
                    }
                }
            }
        }

        return found
    }

    // Локальная функция для поиска ToolbarTabLayout ЗА пределами указанной вью ВЫШЕ по иерархии
    fun tryFindTabLayoutReverseTraversal(currentView: View): ToolbarTabLayout? {
        var foundTabLayout: ToolbarTabLayout? = null
        if (currentView is ToolbarTabLayout && currentView.isVisible) {
            foundTabLayout = currentView
        } else {
            val viewParent = currentView.parent
            if (viewParent is ViewGroup) {
                // По чайлдам итерируемся только тогда, когда текущий parent - видимый
                if (viewParent.isVisible) {
                    for (i in 0 until viewParent.childCount) {
                        val view = viewParent.getChildAt(i)
                        // Пропускаем шаг, тк проверка этой вью проводилась в tryToFindSearchInputAndTabLayoutInsideOneParent
                        // либо на прошлой итерации
                        if (view === currentView) {
                            continue
                        }
                        // Пробуем найти TabLayout внутри данной вью-группы
                        foundTabLayout = tryFindTabLayoutTraversal(view)
                        if (foundTabLayout != null) {
                            break
                        }
                    }
                }
                // Не нашли вью на текущем уровне вложенности и ниже, пробуем найти выше
                if (foundTabLayout == null) {
                    foundTabLayout = tryFindTabLayoutReverseTraversal(viewParent)
                }
            }
        }
        return foundTabLayout
    }

    // Локальная функция, ищущая компоненты в рамках одного родителя (наиболее частый случай - на телефоне, Toolbar и SearchInput лежат в рамках AppBar
    fun tryFindSearchInputAndTabLayoutInsideOneParent(parent: View): Pair<SearchInput?, ToolbarTabLayout?> {
        var foundSearchInput: SearchInput? = null
        var foundTabLayout: ToolbarTabLayout? = null
        if (parent is SearchInput) {
            foundSearchInput = parent
        } else if (parent is ToolbarTabLayout && parent.isVisible) {
            foundTabLayout = parent
        } else if (parent is ViewGroup && parent.isVisible) {
            for (i in 0 until parent.childCount) {
                val currentChild = parent.getChildAt(i)
                val pair = tryFindSearchInputAndTabLayoutInsideOneParent(currentChild)
                pair.first?.let { foundSearchInput = it }
                pair.second?.let { foundTabLayout = it }
                if (foundSearchInput != null && foundTabLayout != null) {
                    break
                }
            }
        }
        return Pair(foundSearchInput, foundTabLayout)
    }

    val primaryPair = tryFindSearchInputAndTabLayoutInsideOneParent(parent)
    val searchInput = primaryPair.first
    var toolbarTabLayout: ToolbarTabLayout? = null
    if (primaryPair.first != null) {
        // Если не нашли ToolbarTabLayout за проход внутрь - ищем, двигаясь наружу, просматривая соседей внутри
        toolbarTabLayout = primaryPair.second ?: tryFindTabLayoutReverseTraversal(parent)
    }

    return Pair(searchInput, toolbarTabLayout)
}