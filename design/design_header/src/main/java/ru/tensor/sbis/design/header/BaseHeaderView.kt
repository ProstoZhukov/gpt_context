package ru.tensor.sbis.design.header

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.header.data.HeaderAcceptSettings
import ru.tensor.sbis.design.header.data.HeaderTitleSettings
import ru.tensor.sbis.design.header.data.LeftCustomContent
import ru.tensor.sbis.design.header.data.RightCustomContent
import ru.tensor.sbis.design.header.databinding.HeaderViewBinding
import ru.tensor.sbis.design.toolbar.ToolbarTabLayout
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Вью шапок для контейнера
 * @author ma.kolpakov
 */
internal class BaseHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.sbisHeaderViewTheme
) : BaseHeader,
    FrameLayout(
        ThemeContextBuilder(context, defStyleAttr, R.style.SbisHeader).build(),
        attrs,
        defStyleAttr
    ) {
    private val binding: HeaderViewBinding = HeaderViewBinding.inflate(LayoutInflater.from(getContext()), this, true)
    private val acceptListeners = mutableSetOf<(() -> Unit)>()
    private val closeListeners = mutableSetOf<(() -> Unit)>()
    private val tabChangedListeners = mutableSetOf<((Int) -> Unit)>()
    override var isDividerVisible: Boolean = false
        set(value) {
            field = value
            binding.viewDivider.isVisible = isDividerVisible
        }

    override fun addAcceptListener(onAccept: (() -> Unit)) {
        acceptListeners.add(onAccept)
    }

    override fun addCloseListener(onClose: (() -> Unit)) {
        closeListeners.add(onClose)
    }

    override fun removeAcceptListener(onAccept: (() -> Unit)) {
        acceptListeners.remove(onAccept)
    }

    override fun removeCloseListener(onClose: (() -> Unit)) {
        closeListeners.add(onClose)
    }

    override fun setAcceptButtonEnabled(isEnabled: Boolean) {
        binding.confirmIconButton.isEnabled = isEnabled
        binding.confirmTextButton.isEnabled = isEnabled
    }

    fun addTabChangedListener(onTabChanged: ((Int) -> Unit)) {
        tabChangedListeners.add(onTabChanged)
    }

    fun removeTabChangedListener(onTabChanged: ((Int) -> Unit)) {
        tabChangedListeners.add(onTabChanged)
    }

    fun setHeaderContent(
        titleSettings: HeaderTitleSettings = HeaderTitleSettings.NoneTitle,
        acceptSettings: HeaderAcceptSettings = HeaderAcceptSettings.NoneAccept,
        hasClose: Boolean = false,
        leftCustomContent: LeftCustomContent = LeftCustomContent.NoneContent,
        rightCustomContent: RightCustomContent = RightCustomContent.NoneContent,
    ) {
        // Включаем заголовок или табы
        when (titleSettings) {
            is HeaderTitleSettings.TextTitle -> {
                binding.headerTitle.visibility = VISIBLE
                binding.headerTitle.text = titleSettings.text
            }

            is HeaderTitleSettings.TextResTitle -> {
                binding.headerTitle.visibility = VISIBLE
                binding.headerTitle.setText(titleSettings.textRes)
            }

            is HeaderTitleSettings.TabsTitle -> {
                with(binding.headerTabs) {
                    visibility = VISIBLE
                    setOnTabClickListener(object : ToolbarTabLayout.OnTabClickListener {
                        override fun onTabClicked(tabId: Int) {
                            tabChangedListeners.forEach { it.invoke(tabId) }
                        }
                    })
                    setTabs(titleSettings.tabs, titleSettings.selectedTab)
                }
            }

            is HeaderTitleSettings.NoneTitle -> {
                binding.headerTabs.visibility = GONE
                binding.headerTitle.visibility = INVISIBLE
            }
        }

        // Включаем выключаем кнопку подтверждения
        binding.confirmButtonContainer.isVisible = acceptSettings != HeaderAcceptSettings.NoneAccept
        if (acceptSettings is HeaderAcceptSettings.IconAccept) {
            binding.confirmIconButton.visibility = VISIBLE
            binding.confirmTextButton.visibility = GONE
            binding.confirmIconButton.setOnClickListener {
                acceptListeners.forEach { it.invoke() }
            }
        }

        if (acceptSettings is HeaderAcceptSettings.TextAccept) {
            binding.confirmTextButton.visibility = VISIBLE
            binding.confirmIconButton.visibility = GONE
            binding.confirmTextButton.setTitleRes(acceptSettings.textRes)
            binding.confirmTextButton.setOnClickListener {
                acceptListeners.forEach { it.invoke() }
            }
        }

        // Включаем выключаем кнопку закрыть
        if (hasClose) {
            val color = context.getThemeColorInt(RDesign.attr.unaccentedIconColor)
            binding.closeButton.apply {
                style = SbisButtonCustomStyle(
                    backgroundColor = Color.TRANSPARENT,
                    iconStyle = SbisButtonIconStyle(
                        ColorStateList.valueOf(color)
                    )
                )
                visibility = VISIBLE
                setOnClickListener {
                    closeListeners.forEach { it.invoke() }
                }
            }
        } else {
            binding.closeButton.visibility = GONE
            binding.closeButton.setOnClickListener(null)
        }

        // Кастомный контент
        when (leftCustomContent) {
            is LeftCustomContent.BackArrowContent -> {
                binding.leftCustomContentContainer.addView(leftCustomContent.getView(context))
                binding.leftCustomContentContainer.isVisible = true
            }

            is LeftCustomContent.Content -> {
                binding.leftCustomContentContainer.addView(leftCustomContent.getView(context))
                binding.leftCustomContentContainer.isVisible = true
            }

            is LeftCustomContent.NoneContent -> {}
        }

        when (rightCustomContent) {
            is RightCustomContent.Content -> {
                binding.rightCustomContentContainer.apply {
                    if (rightCustomContent.contentIsResponsibleForEndPadding) {
                        setPaddingRelative(paddingStart, paddingTop, 0, paddingBottom)
                    }
                    rightCustomContent.getView(context).also { addView(it) }
                }
            }

            is RightCustomContent.ContentWithLayoutParams -> {
                binding.rightCustomContentContainer.apply {
                    if (rightCustomContent.contentIsResponsibleForEndPadding) {
                        setPaddingRelative(paddingStart, paddingTop, 0, paddingBottom)
                    }
                    rightCustomContent.getView(context).also { addView(it, rightCustomContent.layoutParams) }
                }
            }

            is RightCustomContent.NoneContent -> {}
        }
    }
}