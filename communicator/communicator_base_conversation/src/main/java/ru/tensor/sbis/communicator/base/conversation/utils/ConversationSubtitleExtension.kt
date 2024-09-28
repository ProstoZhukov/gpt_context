package ru.tensor.sbis.communicator.base.conversation.utils

import android.graphics.drawable.Drawable
import android.view.View.MeasureSpec
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.base.conversation.R
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.TypingDotsDrawable
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView.UsersTypingData
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView.UsersTypingData.UsersType
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.sbis_text_view.SbisTextView.MeasureResult

/**
 * Расширение подзаголовка экрана переписки.
 *
 * @see text
 * @see typingData
 *
 * @author vv.chekurda
 */
class ConversationSubtitleExtension : SbisTextView.Extension() {

    private val typingDotsDrawable by lazy {
        TypingDotsDrawable().apply {
            params = TypingDotsDrawable.DotsParams(
                size = (view.textSize * ACTIVE_POINTS_SIZE_PERCENT).toInt()
            )
            textColor = view.textColor
        }
    }

    private val oneTypingText by lazy {
        resources.getString(R.string.communicator_one_typing)
    }
    private val fewPrintingText by lazy {
        resources.getString(R.string.communicator_few_typing)
    }

    /**
     * Установить текст подзаголовка.
     *
     * Текст не будет отображаться, пока есть данные о печатающих пользователях [typingData].
     */
    var text: String? = null
        set(value) {
            field = value
            if (typingData?.hasData != true) {
                view.text = value
            }
        }

    /**
     * Установить данные о печатающих пользователях.
     *
     * После сброса данных будет установлен обычный текст подзаголовка [text].
     */
    var typingData: UsersTypingData? = null
        set(value) {
            val oldData = field
            field = value
            onTypingDataChanged(oldData, value)
        }

    override fun attach(view: SbisTextView, textLayout: TextLayout) {
        super.attach(view, textLayout)
        view.maxLines = 1
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int, measureResult: MeasureResult) {
        val typingData = typingData
        if (typingData == null || !typingData.hasData) return

        val availableWidth = (MeasureSpec.getSize(widthMeasureSpec) - view.compoundPaddingStart - view.compoundPaddingEnd)
            .coerceAtLeast(0)
        configureTypingUsers(typingData, availableWidth)
    }

    private fun onTypingDataChanged(oldData: UsersTypingData?, newData: UsersTypingData?) {
        when {
            oldData == newData -> return
            newData?.hasData != true -> {
                view.setCompoundDrawables(end = null as Drawable?)
                view.text = text
            }
            oldData?.hasData != true -> {
                updateTypingDotsBounds()
                view.setCompoundDrawables(end = typingDotsDrawable)
                view.isWrappedCompoundDrawables = true
            }
            else -> view.safeRequestLayout()
        }
    }

    private fun updateTypingDotsBounds() {
        val textBaseline = view.paddingTop + textLayout.baseline
        val viewHeight = view.measuredHeight.takeIf { it > 0 }
            ?: view.let {
                it.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
                it.measuredHeight
            }
        val availableVerticalSpace = viewHeight - view.paddingTop - view.paddingBottom
        val compoundDrawablePos = view.paddingTop + (availableVerticalSpace - typingDotsDrawable.intrinsicHeight) / 2
        val dotsTop = textBaseline - compoundDrawablePos - typingDotsDrawable.intrinsicHeight
        typingDotsDrawable.setBounds(
            0,
            dotsTop,
            typingDotsDrawable.intrinsicWidth,
            dotsTop + typingDotsDrawable.intrinsicHeight
        )
    }

    private fun configureTypingUsers(typingData: UsersTypingData, availableWidth: Int) {
        when (typingData.getVisibilityMode()) {
            VisibilityMode.PRIVATE_MODE -> configurePrivateMode()
            VisibilityMode.NAMES_MODE -> configureNamesMode(availableWidth)
            else -> configureParticipantsMode(availableWidth)
        }
    }

    private fun UsersTypingData.getVisibilityMode(): VisibilityMode =
        when {
            usersType == UsersType.SINGLE_USER -> VisibilityMode.PRIVATE_MODE
            typingUsers.size <= TWO_USERS -> VisibilityMode.NAMES_MODE
            else -> VisibilityMode.PARTICIPANTS_MODE
        }

    private fun configurePrivateMode() {
        textLayout.configure { text = oneTypingText }
    }

    private fun configureNamesMode(availableWidth: Int) {
        val userNameList = typingData!!.typingUsers
            .filter { it.isNotBlank() }
            .take(TWO_USERS)

        val resultText = StringBuilder(userNameList.joinToString { it })
            .append(StringUtils.SPACE)
            .append(if (userNameList.size == 1) oneTypingText else fewPrintingText)

        if (textLayout.getDesiredWidth(resultText) <= availableWidth) {
            textLayout.configure { text = resultText }
        } else {
            configureParticipantsMode(availableWidth)
        }
    }

    private fun configureParticipantsMode(availableWidth: Int) {
        val count = typingData!!.typingUsers.size
        val typingText = StringUtils.SPACE + view.resources.getQuantityString(R.plurals.communicator_typing, count)
        val resultText = view.resources.getQuantityString(R.plurals.communicator_participants_count, count, count) + typingText

        if (textLayout.getDesiredWidth(resultText) <= availableWidth) {
            textLayout.configure { text = resultText }
        } else {
            count.toString() + typingText
        }
    }

    /**
     * Моды отображения печатающих пользователей.
     */
    private enum class VisibilityMode {

        /**
         * Мод отображения личной переписки: отображаем только "печатает".
         */
        PRIVATE_MODE,

        /**
         * Мод отображения имён участников: отображаем "Фамилия, Фамилия печатают".
         */
        NAMES_MODE,

        /**
         * Мод отображения участников: отображаем "N участников печатает".
         */
        PARTICIPANTS_MODE
    }
}

/**
 * Процент размера анимируемых точек относительно размера текста.
 */
private const val ACTIVE_POINTS_SIZE_PERCENT = 0.15
private const val TWO_USERS = 2