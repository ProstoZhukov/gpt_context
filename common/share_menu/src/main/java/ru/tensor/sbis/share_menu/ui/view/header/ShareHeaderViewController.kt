package ru.tensor.sbis.share_menu.ui.view.header

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.view.isVisible
import ru.tensor.sbis.share_menu.databinding.ShareMenuHeaderViewBinding

/**
 * Контроллер для управления состоянием и внешним видом шапки шаринга [ShareHeaderView].
 *
 * @author dv.baranov
 */
internal class ShareHeaderViewController(private val context: Context) : ShareHeaderViewAPI {

    private lateinit var headerViewLayoutBinding: ShareMenuHeaderViewBinding
    private var headerViewState = ShareHeaderViewState.DEFAULT
    private var countOfFiles = 0

    /**
     * Инициализировать биндинг.
     */
    internal fun initBinding(headerViewLayoutBinding: ShareMenuHeaderViewBinding) {
        this.headerViewLayoutBinding = headerViewLayoutBinding
    }

    override fun onStateChanged(state: ShareHeaderViewState, countOfFiles: Int) {
        this.countOfFiles = countOfFiles
        updateState(state)
    }

    /**
     * Сохраненить состояние.
     */
    fun onSaveInstanceState(superState: Parcelable?): Parcelable =
        Bundle().apply {
            putParcelable(SUPER_STATE_KEY, superState)
            putSerializable(HEADER_VIEW_STATE_KEY, headerViewState)
            putInt(COUNT_FILES_KEY, countOfFiles)
            putBoolean(BACK_BUTTON_VISIBILITY, headerViewLayoutBinding.shareMenuHeaderViewBackButton.isVisible)
            putBoolean(CLOSE_BUTTON_VISIBILITY, headerViewLayoutBinding.shareMenuHeaderViewCloseButton.isVisible)
        }

    /**
     * Восстановить сохраненное состояние.
     */
    fun onRestoreInstanceState(state: Parcelable): Parcelable? =
        if (state is Bundle) {
            with(state) {
                countOfFiles = getInt(COUNT_FILES_KEY)
                updateState(
                    getSerializable(HEADER_VIEW_STATE_KEY) as? ShareHeaderViewState ?: ShareHeaderViewState.DEFAULT
                )
                setBackButtonVisibility(getBoolean(BACK_BUTTON_VISIBILITY))
                setCloseButtonVisibility(getBoolean(CLOSE_BUTTON_VISIBILITY))
                getParcelable(SUPER_STATE_KEY)
            }
        } else null

    override fun setOnCloseListener(listener: View.OnClickListener) {
        headerViewLayoutBinding.shareMenuHeaderViewCloseButton.setOnClickListener(listener)
    }

    override fun setCloseButtonVisibility(isVisible: Boolean) {
        if (isVisible != headerViewLayoutBinding.shareMenuHeaderViewCloseButton.isVisible) {
            headerViewLayoutBinding.shareMenuHeaderViewCloseButton.isVisible = isVisible
        }
    }

    override fun setOnBackListener(listener: View.OnClickListener) {
        headerViewLayoutBinding.shareMenuHeaderViewBackButton.setOnClickListener(listener)
    }

    override fun setBackButtonVisibility(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        if (visibility != headerViewLayoutBinding.shareMenuHeaderViewBackButton.visibility) {
            headerViewLayoutBinding.shareMenuHeaderViewBackButton.visibility = visibility
        }
    }

    private fun updateState(newState: ShareHeaderViewState) {
        headerViewState = newState
        changeViewsVisibilityWhenStatusChanged()
        val titleText = getTitleText()
        headerViewLayoutBinding.shareMenuHeaderViewTitle.text = titleText
    }

    private fun getTitleText(): String =
        if (countOfFiles <= 0 || headerViewState == ShareHeaderViewState.COMPLETED) {
            context.getString(headerViewState.textResId, "")
        } else {
            context.getString(headerViewState.textResId, " ($countOfFiles)")
        }

    private fun changeViewsVisibilityWhenStatusChanged() {
        headerViewLayoutBinding.shareMenuHeaderViewLoadingIndicator.isVisible =
            headerViewState == ShareHeaderViewState.SENDING
        headerViewLayoutBinding.shareMenuHeaderViewSuccessIcon.isVisible =
            headerViewState == ShareHeaderViewState.COMPLETED
    }
}

private const val SUPER_STATE_KEY = "super_state"
private const val COUNT_FILES_KEY = "count_files_key"
private const val HEADER_VIEW_STATE_KEY = "header_view_state_key"
private const val CLOSE_BUTTON_VISIBILITY = "close_button_visibility"
private const val BACK_BUTTON_VISIBILITY = "back_button_visibility"