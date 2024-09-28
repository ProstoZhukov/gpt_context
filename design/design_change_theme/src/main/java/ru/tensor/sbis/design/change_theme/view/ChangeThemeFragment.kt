package ru.tensor.sbis.design.change_theme.view

import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import ru.tensor.sbis.android_ext_decl.getParcelableArrayListUniversally
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.change_theme.R
import ru.tensor.sbis.design.change_theme.databinding.ChangeThemeFragmentBinding
import ru.tensor.sbis.design.change_theme.util.SystemThemeState.*
import ru.tensor.sbis.design.change_theme.util.SystemThemes
import ru.tensor.sbis.design.change_theme.util.Theme
import ru.tensor.sbis.design.change_theme.util.changeTheme
import ru.tensor.sbis.design.change_theme.util.getSystemThemeEnabledFlag
import ru.tensor.sbis.design.change_theme.util.getThemeFromPreferences
import ru.tensor.sbis.design.change_theme.util.getSystemThemeMode
import ru.tensor.sbis.design.change_theme.util.setSystemThemeEnabledFlag
import ru.tensor.sbis.design.confirmation_dialog.BaseContentProvider
import ru.tensor.sbis.design.confirmation_dialog.ButtonModel
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonHandler
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonId
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialog
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent.SmallTitle
import ru.tensor.sbis.design.design_confirmation.R as RC

/**
 * Экран смены темы.
 *
 * @author da.zolotarev
 */
internal class ChangeThemeFragment : Fragment(), ConfirmationButtonHandler {

    private var chosenTheme: Theme? = null
    private var chosenSwitchState: Boolean? = null
    private var switchCallbackNeedTag: Boolean = true

    private var changeSwitchWithCallback: () -> Unit = {}
    private var changeSwitchWithoutCallback: () -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ChangeThemeFragmentBinding.inflate(inflater).apply {
            setupToolbar(this)
            setupAdapter(this)
            if (changeThemeSwitchEnabled()) {
                setupSwitchCallbacks(this)
                setupSwitch(this)
            }
        }.root

    override fun onButtonClick(tag: String?, id: String, sbisContainer: SbisContainerImpl) {
        when {
            id == ConfirmationButtonId.YES.toString() && tag == DIALOG_TAG_CHANGED_BY_SWITCH -> {
                setSystemThemeEnabledFlag(requireContext(), chosenSwitchState ?: false)
                changeTheme(sbisContainer.requireContext(), null)
            }

            id == ConfirmationButtonId.YES.toString() -> {
                if (chosenTheme == null) {
                    sbisContainer.dismiss()
                    return
                }
                setSystemThemeEnabledFlag(requireContext(), false)
                changeTheme(sbisContainer.requireContext(), chosenTheme)
            }

            id == ConfirmationButtonId.NO.toString() -> sbisContainer.dismiss()

        }
    }

    private fun setupToolbar(binding: ChangeThemeFragmentBinding) = with(binding.changeThemeToolbar) {
        content = SmallTitle(PlatformSbisString.Res(R.string.change_theme_item_title))
        if (requireArguments().getBoolean(ARG_BACK_ARROW_KEY)) {
            showBackButton = true
            backBtn?.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        } else {
            showBackButton = false
        }
    }

    private fun setupAdapter(binding: ChangeThemeFragmentBinding) {
        val themes: List<Theme> = arguments?.getParcelableArrayListUniversally(ARG_THEMES) ?: return
        val defaultTheme: Theme = arguments?.getParcelableUniversally(ARG_DEFAULT_THEME) ?: themes.first()
        val currTheme = getThemeFromPreferences(requireContext(), defaultTheme)
        val dialogTitle = resources.getString(R.string.change_theme_dialog_title)
        val themeSwitch = binding.changeThemeSwitchLayout.changeThemeDefaultSwitchView
        binding.changeThemeRecycler.adapter = ChangeThemeAdapter(themes, currTheme) {
            chosenTheme = it
            val dialogTag = if (themeSwitch.isChecked) {
                changeSwitchWithoutCallback()
                DIALOG_TAG_CHANGED_BY_LIST_ITEM_SWITCH_WAS_CHECKED
            } else {
                DIALOG_TAG_CHANGED_BY_LIST_ITEM_SWITCH_WAS_UNCHECKED
            }
            createConfirmationDialog(dialogTitle, dialogTag).show(childFragmentManager)
        }

        setupSpansCount(binding)
    }

    /**
     * Определяем количество ячеек [GridLayoutManager] в зависимости от dpi устройства
     */
    private fun setupSpansCount(binding: ChangeThemeFragmentBinding) {
        val metrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(metrics)
        val widthDp = metrics.widthPixels / metrics.density
        val spansCount = if (
            widthDp > 600 && resources.configuration.orientation ==
            Configuration.ORIENTATION_LANDSCAPE
        ) {
            3
        } else {
            2
        }
        val layoutManager = GridLayoutManager(requireContext(), spansCount)
        binding.changeThemeRecycler.layoutManager = layoutManager
    }

    private fun createConfirmationDialog(dialogTitle: String, tag: String): ConfirmationDialog<ConfirmationButtonId> {
        val acceptButton = ButtonModel(
            ConfirmationButtonId.YES,
            R.string.change_theme_dialog_button_accept,
            PrimaryButtonStyle,
            true,
            RC.id.confirmation_dialog_button_yes
        )

        val cancelButton = ButtonModel(
            ConfirmationButtonId.NO,
            R.string.change_theme_dialog_button_cancel,
            viewId = RC.id.confirmation_dialog_button_no
        )

        val dialog = ConfirmationDialog(
            contentProvider = BaseContentProvider(null, dialogTitle, null),
            buttons = { listOf(cancelButton, acceptButton) },
            tag = tag
        )

        if (tag == DIALOG_TAG_CHANGED_BY_SWITCH ||
            (tag == DIALOG_TAG_CHANGED_BY_LIST_ITEM_SWITCH_WAS_CHECKED && chosenSwitchState == false)
        ) {
            dialog.setOnDismissListener(changeSwitchWithoutCallback)
        }

        return dialog
    }

    private fun setupSwitchCallbacks(binding: ChangeThemeFragmentBinding) {
        val themeSwitch = binding.changeThemeSwitchLayout.changeThemeDefaultSwitchView
        changeSwitchWithCallback = {
            switchCallbackNeedTag = true
            themeSwitch.isChecked = !themeSwitch.isChecked
        }
        changeSwitchWithoutCallback = {
            switchCallbackNeedTag = false
            themeSwitch.isChecked = !themeSwitch.isChecked
        }
    }

    private fun setupSwitch(binding: ChangeThemeFragmentBinding) = with(binding) {
        changeThemeSwitchLayout.root.visibility = View.VISIBLE
        changeThemeSwitchLayout.root.setOnClickListener { changeSwitchWithCallback() }
        val themeSwitch = changeThemeSwitchLayout.changeThemeDefaultSwitchView
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            chosenSwitchState = isChecked
            if (!switchCallbackNeedTag) return@setOnCheckedChangeListener
            if (getSystemThemeEnabledFlag(requireContext()) == isChecked) return@setOnCheckedChangeListener
            val dialogTitle = resources.getString(R.string.change_theme_dialog_title)
            createConfirmationDialog(dialogTitle, DIALOG_TAG_CHANGED_BY_SWITCH).show(childFragmentManager)
        }
        themeSwitch.isChecked = getSystemThemeEnabledFlag(requireContext())
    }

    private fun changeThemeSwitchEnabled() =
        getSystemThemeMode(requireContext()) != NOT_SUPPORTED &&
            arguments?.getParcelableUniversally(ARG_SYSTEM_THEMES, SystemThemes::class.java) != null

    companion object {

        /**
         * Фабричный метод
         *
         * @param backArrow включена ли навигация назад (стрелка в toolbar)
         * @param themes список тем отображаемых на экране
         * @param defaultTheme тема, выбранная по-умолчанию
         */
        fun create(
            backArrow: Boolean = true,
            themes: List<Theme>,
            defaultTheme: Theme,
            systemThemes: SystemThemes? = null
        ) = ChangeThemeFragment().apply {
            arguments = bundleOf(
                ARG_BACK_ARROW_KEY to backArrow,
                ARG_THEMES to themes.toMutableList(),
                ARG_DEFAULT_THEME to defaultTheme,
                ARG_SYSTEM_THEMES to systemThemes
            )
        }

        private const val ARG_BACK_ARROW_KEY = "ARG_BACK_ARROW"
        private const val ARG_THEMES = "ARG_THEMES"
        private const val ARG_DEFAULT_THEME = "ARG_DEFAULT_THEME"
        private const val ARG_SYSTEM_THEMES = "ARG_SYSTEM_THEMES"

        /** Тэг запуска диалога по нажатию на тему из списка, при ВЫКЛЮЧЕНОМ свиче */
        private const val DIALOG_TAG_CHANGED_BY_LIST_ITEM_SWITCH_WAS_UNCHECKED =
            "DIALOG_TAG_CHANGED_BY_LIST_ITEM_SWITCH_WAS_UNCHECKED"

        /** Тэг запуска диалога по нажатию на тему из списка, при ВКЛЮЧЁНОМ свиче */
        private const val DIALOG_TAG_CHANGED_BY_LIST_ITEM_SWITCH_WAS_CHECKED =
            "DIALOG_TAG_CHANGED_BY_LIST_ITEM_SWITCH_WAS_CHECKED"

        /** Тэг запуска диалога по нажатию на свич */
        private const val DIALOG_TAG_CHANGED_BY_SWITCH = "DIALOG_TAG_CHANGED_BY_SWITCH"
    }
}