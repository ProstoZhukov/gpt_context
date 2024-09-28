package ru.tensor.sbis.design.navigation.view.view.util

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.navigation.databinding.NavigationMenuFooterItemBinding
import ru.tensor.sbis.design.navigation.view.initAndObserve
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.getThemeColorInt

/**
 * Реализация [NavigationFooterBindingDelegate] при установке [R.layout.navigation_menu_footer_item] в качестве атрибута
 * [R.styleable.NavView_footer], для отображения в подвале обычного пункта меню.
 *
 * @author us.bessonov
 */
class MenuItemNavigationFooterBindingDelegate(val item: NavigationItem) :
    NavigationFooterBindingDelegate<NavigationMenuFooterItemBinding> {

    override fun getViewBinding(view: View) = NavigationMenuFooterItemBinding.bind(view)

    override fun setClickSelectionListener(binding: NavigationMenuFooterItemBinding, listener: () -> Unit) {
        binding.root.setOnClickListener { listener() }
    }

    override fun setSelectionLiveData(
        binding: NavigationMenuFooterItemBinding,
        lifecycleOwner: LifecycleOwner,
        liveData: LiveData<Boolean>
    ) {
        initAndObserve(liveData, lifecycleOwner) {
            binding.root.isSelected = it ?: false
            binding.marker.visibility = if (it == true) View.VISIBLE else View.INVISIBLE
        }
    }

    @Suppress("DEPRECATION")
    override fun bind(binding: NavigationMenuFooterItemBinding) {
        val icon = item.iconObservable.blockingFirst().default
        // TODO костыль для поддержания иконок со старого шрифта https://dev.sbis.ru/opendoc.html?guid=c9a72eb0-17de-47d1-a3a7-8e87248f0ac2&client=3
        if (binding.icon.text.toString().isBlank()) {
            binding.icon.setText(icon)
        }

        setIconColor(binding.icon, binding.root.context)

        val label = item.labelObservable.blockingFirst().default
        binding.title.text = label.getString(binding.root.context)
    }

    override fun setItemLabelSubscription(
        binding: NavigationMenuFooterItemBinding,
        lifecycleOwner: LifecycleOwner,
        liveData: LiveData<NavigationItemLabel>,
    ) {
        initAndObserve(liveData, lifecycleOwner) { label ->
            label?.default?.getString(binding.root.context)?.let {
                binding.title.text = it
            }
        }
    }

    override fun getNavigationItem() = item

    private fun setIconColor(icon: SbisTextView, context: Context) {
        val colors = intArrayOf(
            context.getThemeColorInt(ru.tensor.sbis.design.R.attr.navigationIconColor),
            context.getThemeColorInt(ru.tensor.sbis.design.R.attr.navigationActiveIconColor)
        )
        icon.setTextColor(ColorStateList(STATES, colors))
    }

    companion object {
        internal val STATES = arrayOf(
            intArrayOf(-android.R.attr.state_selected),
            intArrayOf(android.R.attr.state_selected)
        )
    }
}