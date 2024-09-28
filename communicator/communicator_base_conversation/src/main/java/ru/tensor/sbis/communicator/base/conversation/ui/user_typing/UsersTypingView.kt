package ru.tensor.sbis.communicator.base.conversation.ui.user_typing

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import org.apache.commons.lang3.StringUtils.SPACE
import ru.tensor.sbis.communicator.base.conversation.R
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.TypingDotsDrawable.DotsParams
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView.UsersTypingData.UsersType.SINGLE_USER
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView.UsersTypingData.UsersType.TWO_USERS
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView.VisibilityMode.EMPTY_DATA_MODE
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView.VisibilityMode.NAMES_MODE
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView.VisibilityMode.PARTICIPANTS_MODE
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView.VisibilityMode.PRIVATE_MODE
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.custom_view_tools.utils.textHeight

/**
 * View для отображения печатающих пользователей: "участник/и печатают...".
 * @see UsersTypingData
 *
 * @author vv.chekurda
 */
class UsersTypingView(context: Context) : View(context) {

    /**
     * Моды отображения печатающих пользователей.
     */
    private enum class VisibilityMode {

        /**
         * Мод пустых данных, никто не печатает.
         */
        EMPTY_DATA_MODE,

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

    /**
     * Данные о печатающих пользователях.
     *
     * @property typingUsers список имен печатающих пользователей.
     * @property usersType тип пользователей в переписке.
     */
    data class UsersTypingData(
        val typingUsers: List<String> = emptyList(),
        val usersType: UsersType = SINGLE_USER
    ) {
        val hasData: Boolean
            get() = typingUsers.isNotEmpty()

        enum class UsersType {

            /**
             * Переписка 1 на 1.
             */
            SINGLE_USER,

            /**
             * В переписке всего 2 участника помимо текущего пользователя.
             */
            TWO_USERS,

            /**
             * Любой тип, отображение никак не завязано на количество участников.
             */
            ANY
        }
    }

    /**
     * Установить/получить данные о печатающих пользователях.
     */
    var data: UsersTypingData = UsersTypingData()
        set(value) {
            val isChanged = data != value
            field = value
            if (isChanged) onDataSetChanged()
        }

    /**
     * Установить/получить размер текста.
     */
    @Px
    var textSize: Float = dp(DEFAULT_TEXT_SIZE_SP).toFloat()
        set(value) {
            val usersChanged = usersLayout.configure { paint.textSize = value }
            val typingChanged = typingLayout.configure { paint.textSize = value }
            typingDotsDrawable.params = DotsParams(size = (value * ACTIVE_POINTS_SIZE_PERCENT).toInt())

            field = value
            if (usersChanged || typingChanged) safeRequestLayout()
        }

    /**
     * Установить/получить цвет текста.
     */
    @ColorInt
    var textColor: Int = Color.GRAY
        set(value) {
            field = value
            usersLayout.textPaint.color = value
            typingLayout.textPaint.color = value
            typingDotsDrawable.textColor = value
        }

    private val oneTypingText = resources.getString(R.string.communicator_one_typing)
    private val fewPrintingText = resources.getString(R.string.communicator_few_typing)

    /**
     * Разметка для отображения пользователей.
     */
    private val usersLayout = TextLayout {
        paint.textSize = textSize
        paint.color = textColor
        includeFontPad = false
    }

    /**
     * Разметка для отображения "печатает(ют)".
     */
    private val typingLayout = TextLayout {
        paint.textSize = textSize
        paint.color = textColor
        includeFontPad = false
    }

    /**
     * Анимируемые точки.
     */
    private val typingDotsDrawable = TypingDotsDrawable().apply {
        params = DotsParams(size = (textSize * ACTIVE_POINTS_SIZE_PERCENT).toInt())
        callback = this@UsersTypingView
    }

    /**
     * Мод отображения печатающих пользователей.
     * @see VisibilityMode
     */
    private var visibilityMode = EMPTY_DATA_MODE

    init {
        setWillNotDraw(false)
    }

    /**
     * Произошло изменение данных для отображения печатающих пользователей.
     */
    private fun onDataSetChanged() {
        visibilityMode = getVisibilityMode()
        updateView()
    }

    /**
     * Получить мод отображения [VisibilityMode] для текущий данных [data].
     */
    private fun getVisibilityMode(): VisibilityMode =
        when {
            !data.hasData -> EMPTY_DATA_MODE
            data.usersType == SINGLE_USER -> PRIVATE_MODE
            data.typingUsers.size <= 2 -> NAMES_MODE
            else -> PARTICIPANTS_MODE
        }

    /**
     * Обновить view согласно текущему состоянию.
     */
    private fun updateView(withRequest: Boolean = true) {
        val isChanged = when (visibilityMode) {
            EMPTY_DATA_MODE -> configureEmptyDataMode()
            PRIVATE_MODE      -> configurePrivateMode()
            NAMES_MODE        -> configureTwoUsersMode()
            PARTICIPANTS_MODE -> configureParticipantsMode()
        }
        if (withRequest && isChanged) {
            safeRequestLayout()
        }
    }

    /**
     * Настроить view для [EMPTY_DATA_MODE] мода отобаржения.
     *
     * @return true, если необходимо вызвать [safeRequestLayout].
     */
    private fun configureEmptyDataMode(): Boolean {
        visibility = INVISIBLE
        return false
    }

    /**
     * Настроить view для [PRIVATE_MODE] мода отобаржения.
     *
     * @return true, если необходимо вызвать [safeRequestLayout].
     */
    private fun configurePrivateMode(): Boolean {
        visibility = VISIBLE
        usersLayout.configure { isVisible = false }
        return typingLayout.configure { text = oneTypingText }
    }

    /**
     * Настроить view для [NAMES_MODE] мода отобаржения.
     *
     * @return true, если необходимо вызвать [safeRequestLayout].
     */
    private fun configureTwoUsersMode(): Boolean {
        val userNameList = data.typingUsers
            .filter { it.isNotBlank() }
            .take(2)

        return if (userNameList.isNotEmpty()) {
            visibility = VISIBLE

            val isUsersChanged = usersLayout.configure {
                text = userNameList.joinToString { it }
                needHighWidthAccuracy = userNameList.size > 1
                isVisible = true
            }
            val isTypingChanged = typingLayout.configure {
                text = SPACE + if (userNameList.size == 1) oneTypingText else fewPrintingText
            }

            isUsersChanged || isTypingChanged
        } else {
            visibility = INVISIBLE
            false
        }
    }

    /**
     * Настроить view для [PARTICIPANTS_MODE] мода отобаржения.
     *
     * @return true, если необходимо вызвать [safeRequestLayout].
     */
    private fun configureParticipantsMode(): Boolean {
        visibility = VISIBLE

        val count = data.typingUsers.size
        val isTypingChanged = typingLayout.configure {
            text = SPACE + resources.getQuantityString(R.plurals.communicator_typing, count)
        }
        val isUsersChanged = usersLayout.configure {
            text = resources.getQuantityString(R.plurals.communicator_participants_count, count, count)
            needHighWidthAccuracy = false
            isVisible = true
        }

        return isTypingChanged || isUsersChanged
    }

    /**
     * Настроить сокращение для различных модов [VisibilityMode].
     */
    private fun configureEllipsizeForModes() {
        if (visibilityMode != NAMES_MODE && visibilityMode != PARTICIPANTS_MODE) return

        val usersAvailableWidth = measuredWidth - paddingStart - typingLayout.width - typingDotsDrawable.intrinsicWidth - paddingEnd
        val usersWidth = usersLayout.getDesiredWidth(usersLayout.text)
        if (usersAvailableWidth >= usersWidth) return

        var modeIsChanged = false
        // Если участников больше 2ух и фамилии печатающих не влезают -> меняем мод на участников.
        if (visibilityMode == NAMES_MODE && data.usersType != TWO_USERS) {
            visibilityMode = PARTICIPANTS_MODE
            updateView(withRequest = false)
            modeIsChanged = true
        }

        // Если слово "участников" не помещается -> отображаем только число.
        if (visibilityMode == PARTICIPANTS_MODE) {
            val participantsWidth = if (modeIsChanged) usersLayout.getDesiredWidth(usersLayout.text) else usersWidth
            if (participantsWidth > usersAvailableWidth) {
                usersLayout.configure { text = data.typingUsers.size.toString() }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            measureDirection(widthMeasureSpec) { suggestedMinimumWidth },
            measureDirection(heightMeasureSpec) { suggestedMinimumHeight }
        )
    }

    @Px
    private fun measureDirection(measureSpec: Int, getMinSize: () -> Int): Int =
        when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(measureSpec)
            MeasureSpec.AT_MOST -> minOf(getMinSize(), MeasureSpec.getSize(measureSpec))
            else                -> getMinSize()
        }

    override fun getSuggestedMinimumWidth(): Int {
        val minContentWidth = listOf(
            paddingStart,
            if (usersLayout.isVisible) usersLayout.getDesiredWidth(usersLayout.text) else 0,
            typingLayout.getDesiredWidth(typingLayout.text),
            typingDotsDrawable.intrinsicWidth,
            paddingEnd
        ).sumOf { it }
        return maxOf(super.getSuggestedMinimumWidth(), minContentWidth)
    }

    override fun getSuggestedMinimumHeight(): Int =
        maxOf(super.getSuggestedMinimumHeight(), paddingTop + typingLayout.textPaint.textHeight + paddingBottom)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val usersMaxWidth = w - paddingStart - typingLayout.width - typingDotsDrawable.intrinsicWidth - paddingEnd
        usersLayout.configure { maxWidth = usersMaxWidth }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        configureEllipsizeForModes()

        usersLayout.layout(paddingStart, paddingTop)
        typingLayout.layout(usersLayout.right, usersLayout.top)
        val dotsTop = typingLayout.top + typingLayout.baseline - typingDotsDrawable.intrinsicHeight
        typingDotsDrawable.setBounds(
            typingLayout.right,
            dotsTop,
            typingLayout.right + typingDotsDrawable.intrinsicWidth,
            dotsTop + typingDotsDrawable.intrinsicHeight
        )
    }

    override fun onDraw(canvas: Canvas) {
        usersLayout.draw(canvas)
        typingLayout.draw(canvas)
        typingDotsDrawable.draw(canvas)
    }

    override fun verifyDrawable(who: Drawable): Boolean =
        who == typingDotsDrawable || super.verifyDrawable(who)

    override fun hasOverlappingRendering(): Boolean = false
}

/**
 * Процент размера анимируемых точек относительно размера текста.
 */
private const val ACTIVE_POINTS_SIZE_PERCENT = 0.15

/**
 * Стандартный размер текста в sp.
 */
private const val DEFAULT_TEXT_SIZE_SP = 14
