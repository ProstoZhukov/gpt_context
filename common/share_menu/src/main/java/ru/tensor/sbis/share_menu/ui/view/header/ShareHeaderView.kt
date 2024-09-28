package ru.tensor.sbis.share_menu.ui.view.header

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.share_menu.R
import ru.tensor.sbis.share_menu.databinding.ShareMenuHeaderViewBinding

/**
 * Шапка меню шаринга.
 * @see ShareHeaderViewAPI
 *
 * @author dv.baranov
 */
internal class ShareHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.shareMenuHeaderViewTheme,
    @StyleRes defStyleRes: Int = R.style.ShareMenuHeaderViewDefaultStyle,
    private val controller: ShareHeaderViewController = ShareHeaderViewController(context)
) : ConstraintLayout(
    ThemeContextBuilder(
        context,
        defStyleAttr = defStyleAttr,
        defaultStyle = defStyleRes
    ).build(),
    attrs,
    defStyleAttr,
    defStyleRes
),
    ShareHeaderViewAPI by controller {

    private val binding = ShareMenuHeaderViewBinding.inflate(LayoutInflater.from(getContext()), this)

    init {
        controller.initBinding(binding)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(controller.onRestoreInstanceState(state))
    }

    override fun onSaveInstanceState(): Parcelable {
        return controller.onSaveInstanceState(super.onSaveInstanceState())
    }
}
