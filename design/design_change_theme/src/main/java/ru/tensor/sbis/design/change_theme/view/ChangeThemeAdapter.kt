package ru.tensor.sbis.design.change_theme.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.change_theme.databinding.ChangeThemeRecyclerItemBinding
import ru.tensor.sbis.design.change_theme.util.Theme

/**
 * Адаптер списка тем приложения.
 *
 * @author da.zolotarev
 */
internal class ChangeThemeAdapter(
    private val themes: List<Theme>,
    private val currentTheme: Theme?,
    private val onClick: (Theme) -> Unit
) : RecyclerView.Adapter<ChangeThemeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangeThemeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChangeThemeRecyclerItemBinding.inflate(inflater)
        return ChangeThemeViewHolder(binding)
    }

    override fun getItemCount() = themes.size

    override fun onBindViewHolder(holder: ChangeThemeViewHolder, position: Int) {
        holder.bindData(themes[position], currentTheme, onClick)
    }

}