package ru.tensor.sbis.red_button.ui.settings_item

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.red_button.RedButtonPlugin
import ru.tensor.sbis.red_button.databinding.RedButtonItemBinding
import ru.tensor.sbis.red_button.ui.settings_item.di.DaggerRedButtonItemComponent
import ru.tensor.sbis.red_button.utils.DIALOG_CODE_RED_BUTTON_DIALOG
import ru.tensor.sbis.red_button.utils.RedButtonOpenHelper
import ru.tensor.sbis.settings_screen_decl.CustomBackgroundItem
import ru.tensor.sbis.settings_screen_decl.Delegate
import ru.tensor.sbis.settings_screen_decl.Item
import ru.tensor.sbis.settings_screen_decl.RequestCodeWithAction
import ru.tensor.sbis.settings_screen_decl.vm.ItemVM

/**
 * Пункт КК на экране настроек.
 * @param context используется для получения компонентов DI из Application.
 *
 * @author du.bykov
 */
class RedButtonItem(context: Context) : Item, CustomBackgroundItem {

    private val openHelper = RedButtonOpenHelper()

    private val viewModel = DaggerRedButtonItemComponent.builder()
        .commonSingletonComponent(RedButtonPlugin.commonSingletonComponentProvider.get())
        .build()!!.viewModel

    private val viewModelSubscriber = ViewModelSubscriber(viewModel, openHelper)

    override fun getViewModel(): ItemVM = viewModel

    override fun getView(inflater: LayoutInflater, resources: Resources, delegate: Delegate) =
        RedButtonItemBinding.inflate(inflater).root.apply {
            val colorStateList = ContextCompat.getColorStateList(
                context, ru.tensor.sbis.design.R.color.list_item_background_color_states
            )
            background = GradientDrawable().apply { color = colorStateList }
        }

    override fun initialize(lifecycleOwner: LifecycleOwner, resources: Resources, delegate: Delegate) {
        viewModelSubscriber.bindDelegate(delegate)
        lifecycleOwner.lifecycle.addObserver(viewModelSubscriber)
    }

    override fun onClick(resources: Resources, delegate: Delegate) {
        viewModel.onPreferenceClick()
    }

    override fun getRequestCodeWithAction() =
        RequestCodeWithAction(DIALOG_CODE_RED_BUTTON_DIALOG, null, OpenFragmentAction(openHelper))
}