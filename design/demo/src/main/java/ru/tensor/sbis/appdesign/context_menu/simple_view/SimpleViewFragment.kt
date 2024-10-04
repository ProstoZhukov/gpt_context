package ru.tensor.sbis.appdesign.context_menu.simple_view

import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import android.widget.Toast
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.context_menu.ContextMenuPagerFragment
import ru.tensor.sbis.appdesign.context_menu.MenuSingleItemSelector
import ru.tensor.sbis.appdesign.databinding.FragmentContextMenuSimpleViewBinding
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenu

/**
 * Fragment для демонстрации работы контестного меню на обычных view
 *
 * @author ma.kolpakov
 */
class SimpleViewFragment : ContextMenuPagerFragment(R.layout.fragment_context_menu_simple_view, "Простые View") {

    private val menuItemSelector = MenuSingleItemSelector()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = FragmentContextMenuSimpleViewBinding.bind(view)

        registerForContextMenu(viewBinding.center)
        registerForContextMenu(viewBinding.topLeft)
        registerForContextMenu(viewBinding.topRight)
        registerForContextMenu(viewBinding.bottomLeft)
        registerForContextMenu(viewBinding.bottomRight)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        val subSubChildren = listOf(
            MenuItem("Под Под пункт 1"),
            MenuItem("Под Под пункт 2"),
            MenuItem("Под Под пункт 3"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 4"),
            MenuItem("Под Под пункт 5")
        )
        val subSubMenu = SbisMenu("Под под меню", children = subSubChildren)
        val subChildren = listOf(
            MenuItem(
                title = "Подпункт 1",
                discoverabilityTitle = "Коментарий",
            ) { Toast.makeText(this.requireContext(), "Подпункт 1", Toast.LENGTH_SHORT).show() },
            subSubMenu
        )

        val submenu = SbisMenu("Еще", children = subChildren, image = SbisMobileIcon.Icon.smi_navBarMore)
        menuItemSelector.apply(submenu)
        val children = listOf(
            MenuItem("Добавить участника", image = SbisMobileIcon.Icon.smi_addParticipant),
            MenuItem("Настройки чата", image = SbisMobileIcon.Icon.smi_settings),
            MenuItem("Уведомлять только о личных сообщениях", image = SbisMobileIcon.Icon.smi_NotificationOff),
            MenuItem("Скрыть чат", image = SbisMobileIcon.Icon.smi_delete, destructive = true),
            MenuItem("Выйти из чата", image = SbisMobileIcon.Icon.smi_exit),
            submenu
        )
        val sbisMenu = SbisMenu(children = children)
        sbisMenu.addCloseListener {
            Toast.makeText(requireContext(), "Меню закрылось", Toast.LENGTH_SHORT).show()
        }
        sbisMenu.showMenu(childFragmentManager, v)
    }

}