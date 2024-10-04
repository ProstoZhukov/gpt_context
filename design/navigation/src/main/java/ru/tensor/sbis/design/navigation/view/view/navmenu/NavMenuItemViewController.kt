package ru.tensor.sbis.design.navigation.view.view.navmenu

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import io.reactivex.disposables.Disposable
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.design.navigation.view.model.content.NavigationItemContent

/**
 * Контроллер представления элемента аккордеона.
 *
 * @author ma.kolpakov
 */
internal class NavMenuItemViewController(private val alignmentHolder: ItemTitleRightAlignmentHolder) :
    NavViewItemViewApi {
    private lateinit var view: NavMenuItemView

    override var ordinal: Int = 0
    override var content: NavigationItemContent? = null
        set(value) {
            field = value
            contentVisible = content != null
            if (!contentVisible)
                contentExpanded = false
            view.safeRequestLayout()
        }

    override var contentExpanded: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                view.itemBaseLayout.contentExpanded = value
                content?.let {
                    if (value) {
                        view.showContent(it)
                    } else {
                        view.hideContent()
                    }
                    view.safeRequestLayout()
                }
            }
        }

    override var iconButtonClickListener: (() -> Unit)? = null

    override var expandIconButtonClickListener: (() -> Unit)? = null

    override var counterSecondary: String = ""
        set(value) {
            field = value
            view.counterLayout.counterRightView.configure {
                text = value
            }
            view.safeRequestLayout()
        }

    override var contentVisible: Boolean = false
        set(value) {
            field = value
            view.itemBaseLayout.contentVisibility = value
        }

    override var buttonIconRes: Int = ID_NULL
        set(value) {
            field = value
            view.iconButtonLayout.setIcon(getIcon(value, view.context))
            view.iconButtonLayout.iconView.setOnClickListener { _, _ ->
                iconButtonClickListener?.invoke()
            }
            view.safeRequestLayout()
        }

    override var counter: String = ""
        set(value) {
            field = value
            view.counterLayout.counterLeftView.configure {
                text = value
            }
            view.safeRequestLayout()
        }

    /**
     * Связать [NavMenuItemViewController] с [NavMenuItemView].
     */
    fun attach(navMenuItemView: NavMenuItemView) {
        view = navMenuItemView
        view.id = R.id.navigation_nav_item_id
        view.itemBaseLayout.expandContentIcon.setOnClickListener { _, _ ->
            expandIconButtonClickListener?.invoke()
        }
    }

    /**
     * Установить правое выравнивание у [NavMenuItemView].
     *
     * @param value правая позиция разметки текста (координата конца по оси X).
     */
    fun publishRightAlignment(value: Int) {
        alignmentHolder.setItemRight(ordinal, value)
    }

    override fun setIconRes(iconRes: Int) {
        view.itemBaseLayout.iconView.configure {
            text = getIcon(iconRes, view.context)
        }
        view.safeRequestLayout()
    }

    override fun setIconCalendarDay(calendarDay: Int?) {
        view.itemBaseLayout.calendarDrawable.dayNumber = calendarDay
        view.safeRequestLayout()
    }

    override fun setLabel(label: NavigationItemLabel?) {
        view.itemBaseLayout.titleView.configure {
            text = getLabelText(label)
        }
        view.safeRequestLayout()
    }

    override fun setRightAlignment(parentOrdinal: Int): Disposable? {
        return alignmentHolder.getSubject(parentOrdinal).subscribe {
            view.itemBaseLayout.rightAlignment = it
        }
    }

    private fun getIcon(@StringRes icon: Int, context: Context) =
        icon.takeUnless { it == ID_NULL }
            ?.let(context::getString)
            ?: StringUtils.EMPTY

    private fun getLabelText(label: NavigationItemLabel?) =
        label?.default?.getString(view.context) ?: StringUtils.EMPTY
}
