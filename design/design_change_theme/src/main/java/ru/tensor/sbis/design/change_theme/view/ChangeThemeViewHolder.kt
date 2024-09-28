package ru.tensor.sbis.design.change_theme.view

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.change_theme.databinding.ChangeThemeRecyclerItemBinding
import ru.tensor.sbis.design.change_theme.util.Theme

/**
 * ViewHolder списка тем приложения.
 *
 * @author da.zolotarev
 */
internal class ChangeThemeViewHolder(private val binding: ChangeThemeRecyclerItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bindData(itemTheme: Theme, currentTheme: Theme?, onClick: (Theme) -> Unit) {
        binding.changeThemePreview.setImageResource(itemTheme.previewRes)
        binding.root.setOnClickListener { onClick(itemTheme) }

        val isCurrentTheme = itemTheme.globalTheme == currentTheme?.globalTheme
        binding.root.isClickable = !isCurrentTheme
        binding.changeThemeCurrentTheme.isVisible = isCurrentTheme
    }
}