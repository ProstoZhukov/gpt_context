package ru.tensor.sbis.design.view.input.mask.date

import android.content.res.TypedArray
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import androidx.core.content.res.use
import androidx.core.text.isDigitsOnly
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.base.ValidationStatus
import ru.tensor.sbis.design.view.input.base.utils.UpdateState
import ru.tensor.sbis.design.view.input.mask.BaseMaskInputViewTextWatcher
import ru.tensor.sbis.design.view.input.mask.MaskInputViewTextWatcher
import ru.tensor.sbis.design.view.input.mask.WIDE_SPACE_PLACEHOLDER
import ru.tensor.sbis.design.view.input.mask.api.BaseMaskInputViewController
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewController
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

/**
 * Класс для управления состоянием и внутренними компонентами поля ввода даты и времени.
 *
 * @author ps.smirnyh
 */
internal class DateInputViewController(
    singleLineInputViewController: SingleLineInputViewController = SingleLineInputViewController()
) : BaseMaskInputViewController(singleLineInputViewController), DateInputViewApi {

    override var minDate: Date? = null
        set(value) {
            val maxDate = maxDate
            if (value == null || maxDate == null) {
                field = value
                return
            }

            field = value.coerceAtMost(maxDate)
        }

    override var maxDate: Date? = null
        set(value) {
            val minDate = minDate
            if (value == null || minDate == null) {
                field = value
                return
            }

            field = value.coerceAtLeast(minDate)
        }

    override fun attach(
        baseInputView: BaseInputView,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        super.attach(baseInputView, attrs, defStyleAttr, defStyleRes)
        actualKeyListener = DigitsKeyListener.getInstance("0123456789.: $WIDE_SPACE_PLACEHOLDER")

        updateFocusCallback = UpdateState {
            updateOnFocusChanged(inputView.isFocused)
        }
    }

    override fun checkMask(mask: String): Boolean {
        return when (mask) {
            DATE_MASK,
            TIME_MASK,
            DATE_FULL_YEAR_MASK,
            DATE_WITHOUT_YEAR_MASK,
            DATE_ONLY_YEAR_MASK,
            DATE_TIME_MASK -> true

            else -> false
        }
    }

    override fun createMaskTextWatcher(
        maskInputViewArray: TypedArray,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ): BaseMaskInputViewTextWatcher =
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DateInputView,
            defStyleAttr,
            defStyleRes
        ).use {
            MaskInputViewTextWatcher(
                inputView = inputView,
                valueChangedWatcher = valueChangedWatcher,
                mask = when (it.getInt(R.styleable.DateInputView_inputView_format, 2)) {
                    0 -> DATE_MASK
                    1 -> TIME_MASK
                    3 -> DATE_FULL_YEAR_MASK
                    4 -> DATE_WITHOUT_YEAR_MASK
                    5 -> DATE_ONLY_YEAR_MASK
                    else -> DATE_TIME_MASK
                },
                initialText = inputView.text
            )
        }

    override fun updateOnFocusChanged(isFocus: Boolean) {
        super.updateOnFocusChanged(isFocus)
        if (!isFocus) {
            validateDate()
        }
    }

    override fun updateValue(calendar: Calendar) {
        inputView.setText(calendar.getStringValue())
    }

    override fun getDate(): Calendar? {
        val inputDate = inputView.text
        return when (mask) {
            DATE_MASK, DATE_FULL_YEAR_MASK -> getCalendarFromDateMask(inputDate)
            TIME_MASK -> getCalendarFromTimeMask(inputDate)
            DATE_WITHOUT_YEAR_MASK -> getCalendarFromMaskWithoutYear(inputDate)
            DATE_ONLY_YEAR_MASK -> getCalendarFromOnlyYearMask(inputDate)
            DATE_TIME_MASK -> getCalendarFromDateTimeMask(inputDate)
            else -> null
        }
    }

    /** @SelfDocumented */
    private fun getCalendarFromDateMask(inputDate: Editable?): Calendar? {
        val date = inputDate?.split(".")
        val currentDate = Calendar.getInstance()
        return if (
            date != null && date.size == 3 &&
            date[0].isDigitsOnly() && date[1].isDigitsOnly() && date[2].isDigitsOnly()
        ) {
            val dayOfMonth = getDayOfMonth(date[0], currentDate)
            val month = getMonth(date[1], currentDate)
            val year = getYear(date[2], currentDate)
            getCalendar(year, month, dayOfMonth)
        } else {
            null
        }
    }

    /** @SelfDocumented */
    private fun getCalendarFromTimeMask(inputDate: Editable?): Calendar? {
        val time = inputDate?.split(":")
        val currentDate = Calendar.getInstance()
        return if (time != null && time.size == 2 && time[0].isDigitsOnly() && time[1].isDigitsOnly()) {
            val hours = getHours(time[0], currentDate)
            val minutes = getMinutes(time[1], currentDate)
            getCalendar(
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
            ).apply {
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, minutes)
            }
        } else {
            null
        }
    }

    /** @SelfDocumented */
    private fun getCalendarFromMaskWithoutYear(inputDate: Editable?): Calendar? {
        val date = inputDate?.split(".")
        val currentDate = Calendar.getInstance()
        return if (date != null && date.size == 2 && date[0].isDigitsOnly() && date[1].isDigitsOnly()) {
            val dayOfMonth = getDayOfMonth(date[0], currentDate)
            val month = getMonth(date[1], currentDate)
            getCalendar(currentDate.get(Calendar.YEAR), month, dayOfMonth)
        } else {
            null
        }
    }

    /** @SelfDocumented */
    private fun getCalendarFromOnlyYearMask(inputDate: Editable?): Calendar? {
        val year = inputDate?.toString()
        val currentDate = Calendar.getInstance()
        return if (year != null && year.isDigitsOnly()) {
            getCalendar(year.toInt(), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))
        } else {
            null
        }
    }

    /** @SelfDocumented */
    private fun getCalendarFromDateTimeMask(inputDate: Editable?): Calendar? {
        val date = inputDate?.split(".")
        val currentDate = Calendar.getInstance()
        return if (date != null && date.size == 3 && date[0].isDigitsOnly() && date[1].isDigitsOnly()) {
            val dayOfMonth = getDayOfMonth(date[0], currentDate)
            val month = getMonth(date[1], currentDate)
            var year = currentDate.get(Calendar.YEAR)
            var hours = currentDate.get(Calendar.HOUR_OF_DAY)
            var minutes = currentDate.get(Calendar.MINUTE)

            val mixed = date[2].split(" ")
            if (mixed.size == 2 && mixed[0].isDigitsOnly()) {
                year = getYear(mixed[0], currentDate)
                val time = mixed[1].split(":")
                if (time.size == 2 && time[0].isDigitsOnly() && time[1].isDigitsOnly()) {
                    hours = getHours(time[0], currentDate)
                    minutes = getMinutes(time[1], currentDate)
                }
            }

            getCalendar(year, month, dayOfMonth).apply {
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, minutes)
            }
        } else {
            null
        }
    }

    /** @SelfDocumented */
    private fun Calendar.getStringValue(): String {
        val day = this.get(Calendar.DAY_OF_MONTH)
        val dayStr = day.getStringFromQuantum()

        val month = this.get(Calendar.MONTH) + 1
        val monthStr = month.getStringFromQuantum()

        val year = this.get(Calendar.YEAR)
        val yearStr = year.toString()
        val shortYearStr = year.toString().substring(2, 4)

        val hour = this.get(Calendar.HOUR_OF_DAY)
        val hourStr = hour.getStringFromQuantum()

        val minute = this.get(Calendar.MINUTE)
        val minuteStr = minute.getStringFromQuantum()

        return when (mask) {
            DATE_MASK -> "$dayStr$monthStr$shortYearStr"
            TIME_MASK -> "$hourStr$minuteStr"
            DATE_FULL_YEAR_MASK -> "$dayStr$monthStr$yearStr"
            DATE_WITHOUT_YEAR_MASK -> "$dayStr$monthStr"
            DATE_ONLY_YEAR_MASK -> yearStr
            else -> "$dayStr$monthStr$shortYearStr$hourStr$minuteStr"
        }
    }

    /** @SelfDocumented */
    private fun Int.getStringFromQuantum() = if (this < 10) "0$this" else this.toString()

    /** @SelfDocumented */
    private fun getQuantum(value: String, defaultValue: Int, isMonth: Boolean = false): Int {
        return when {
            value.isEmpty() -> defaultValue
            isMonth -> value.toInt() - 1
            else -> value.toInt()
        }
    }

    /** @SelfDocumented */
    private fun getDayOfMonth(value: String, currentDate: Calendar) =
        getQuantum(value, currentDate.get(Calendar.DAY_OF_MONTH))

    /** @SelfDocumented */
    private fun getMonth(value: String, currentDate: Calendar) =
        getQuantum(value, currentDate.get(Calendar.MONTH), true)

    /** @SelfDocumented */
    private fun getYear(value: String, currentDate: Calendar) =
        getYear(value, currentDate.get(Calendar.YEAR))

    /** @SelfDocumented */
    private fun getYear(value: String, defaultValue: Int): Int {
        return when {
            value.isEmpty() -> defaultValue
            value.length == 2 -> getFullYearFromTwoDigits(value)
            else -> value.toInt()
        }
    }

    /** @SelfDocumented */
    private fun getHours(value: String, currentDate: Calendar) =
        getQuantum(value, currentDate.get(Calendar.HOUR_OF_DAY))

    /** @SelfDocumented */
    private fun getMinutes(value: String, currentDate: Calendar) =
        getQuantum(value, currentDate.get(Calendar.MINUTE))

    /** @SelfDocumented */
    private fun getCalendar(year: Int, month: Int, dayOfMonth: Int): Calendar {
        return GregorianCalendar(year, month, dayOfMonth)
    }

    /** @SelfDocumented */
    private fun validateDate() {
        validationStatus = ValidationStatus.Default("")
        val dateFormat = SimpleDateFormat(getDateFormat(true), Locale.getDefault())
        dateFormat.isLenient = false
        var inputViewText = inputView.text.toString()

        if (inputViewText.checkIfDateEmpty()) return

        val date = try {
            if (mask == DATE_MASK || mask == DATE_TIME_MASK) {
                val year = inputViewText.getDate(YEAR_RANGE)
                // заменяем на полный год
                inputViewText = inputViewText.replaceRange(
                    YEAR_RANGE,
                    if (year.isNotEmpty()) getFullYearFromTwoDigits(year).toString() else "    "
                )
            }
            dateFormat.parse(inputViewText)
        } catch (e: ParseException) {
            getAutoCompleteInputView(inputViewText)
        }

        val commentDateFormat = SimpleDateFormat(getDateFormat(), Locale.getDefault())
        val minDate = minDate
        val maxDate = maxDate

        when {
            minDate == null && maxDate != null && date > maxDate ->
                validationStatus = ValidationStatus.Error(
                    context.resources.getString(
                        R.string.date_input_view_validation_max_date,
                        commentDateFormat.format(maxDate)
                    )
                )

            maxDate == null && minDate != null && date < minDate ->
                validationStatus = ValidationStatus.Error(
                    context.resources.getString(
                        R.string.date_input_view_validation_min_date,
                        commentDateFormat.format(minDate)
                    )
                )

            maxDate != null && minDate != null && (date < minDate || date > maxDate) -> {
                validationStatus = ValidationStatus.Error(
                    context.resources.getString(
                        R.string.date_input_view_validation_min_max_date,
                        commentDateFormat.format(minDate),
                        commentDateFormat.format(maxDate)
                    )
                )
            }
        }

        updateInputViewText(date)
    }

    /** Проверить, не пустое ли поле ввода. */
    private fun String.checkIfDateEmpty(): Boolean {
        when (mask) {
            DATE_MASK, DATE_FULL_YEAR_MASK, DATE_WITHOUT_YEAR_MASK -> {
                val splitDate = this.split(".")
                return splitDate.all { it.isBlank() }
            }

            TIME_MASK -> {
                return this.getDate(DAY_OR_HOUR_RANGE).isBlank() &&
                    this.getDate(MONTH_OR_MINUTE_RANGE).isBlank()
            }

            DATE_TIME_MASK -> {
                return this.getDate(YEAR_RANGE).isBlank() &&
                    this.getDate(MONTH_OR_MINUTE_RANGE).isBlank() &&
                    this.getDate(DAY_OR_HOUR_RANGE).isBlank() &&
                    this.getDate(HOUR_RANGE).isBlank() &&
                    this.getDate(MINUTE_RANGE).isBlank()
            }

            DATE_ONLY_YEAR_MASK -> {
                return this.isBlank()
            }

            else -> return true
        }
    }

    /** @SelfDocumented */
    private fun getAutoCompleteInputView(inputViewText: String): Date {
        val currentDate = Calendar.getInstance()
        var year = currentDate.get(Calendar.YEAR)
        var month = currentDate.get(Calendar.MONTH)
        var day = currentDate.get(Calendar.DAY_OF_MONTH)
        var hour = currentDate.get(Calendar.HOUR_OF_DAY)
        var minute = currentDate.get(Calendar.MINUTE)

        when (mask) {
            DATE_MASK -> {
                year = getAutoCompleteYear(inputViewText, FULL_YEAR_RANGE)
                month = getAutoCompleteMonth(inputViewText, MONTH_OR_MINUTE_RANGE)
                day = getAutoCompleteDay(inputViewText, DAY_OR_HOUR_RANGE, year, month)
            }

            DATE_FULL_YEAR_MASK -> {
                year = getAutoCompleteYear(inputViewText, FULL_YEAR_RANGE)
                month = getAutoCompleteMonth(inputViewText, MONTH_OR_MINUTE_RANGE)
                day = getAutoCompleteDay(inputViewText, DAY_OR_HOUR_RANGE, year, month)
            }

            DATE_WITHOUT_YEAR_MASK -> {
                month = getAutoCompleteMonth(inputViewText, MONTH_OR_MINUTE_RANGE)
                day = getAutoCompleteDay(inputViewText, DAY_OR_HOUR_RANGE, year, month)
            }

            TIME_MASK -> {
                hour = getAutoCompleteHour(inputViewText, DAY_OR_HOUR_RANGE)
                minute = getAutoCompleteMinute(inputViewText, MONTH_OR_MINUTE_RANGE)
            }

            DATE_TIME_MASK -> {
                year = getAutoCompleteYear(inputViewText, FULL_YEAR_RANGE)
                month = getAutoCompleteMonth(inputViewText, MONTH_OR_MINUTE_RANGE)
                day = getAutoCompleteDay(inputViewText, DAY_OR_HOUR_RANGE, year, month)
                hour = getAutoCompleteHour(inputViewText, FULL_YEAR_HOUR_RANGE)
                minute = getAutoCompleteMinute(inputViewText, FULL_YEAR_MINUTE_RANGE)
            }

            DATE_ONLY_YEAR_MASK -> {
                year = getAutoCompleteYear(inputViewText, ONLY_YEAR_RANGE)
            }
        }

        currentDate.set(year, month, day, hour, minute)

        return currentDate.time
    }

    /** @SelfDocumented */
    private fun getAutoCompleteYear(inputViewText: String, range: IntRange): Int {
        val year = inputViewText.getDate(range).toIntOrNull()
        return year ?: Calendar.getInstance().get(Calendar.YEAR)
    }

    /** @SelfDocumented */
    private fun getAutoCompleteMonth(inputViewText: String, range: IntRange): Int {
        return when (val month = inputViewText.getDate(range).toIntOrNull()) {
            null, 0 -> Calendar.getInstance().get(Calendar.MONTH)
            in 1..12 -> month - 1
            else -> 11
        }
    }

    /** @SelfDocumented */
    private fun getAutoCompleteDay(
        inputViewText: String,
        range: IntRange,
        year: Int,
        month: Int
    ): Int {
        val day = inputViewText.getDate(range).toIntOrNull()
        val date = Calendar.getInstance()
        date.set(year, month, 1)
        return when {
            day == null -> if (Calendar.getInstance().get(Calendar.MONTH) == month) {
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            } else {
                1
            }

            day == 0 -> 1
            day > date.getActualMaximum(Calendar.DAY_OF_MONTH) -> date.getActualMaximum(Calendar.DAY_OF_MONTH)
            else -> day
        }
    }

    /** @SelfDocumented */
    private fun getAutoCompleteHour(inputViewText: String, range: IntRange) =
        when (val hour = inputViewText.getDate(range).toIntOrNull()) {
            null -> Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            in 0..23 -> hour
            else -> 23
        }

    /** @SelfDocumented */
    private fun getAutoCompleteMinute(inputViewText: String, range: IntRange) =
        when (val minute = inputViewText.getDate(range).toIntOrNull()) {
            null -> Calendar.getInstance().get(Calendar.MINUTE)
            in 0..59 -> minute
            else -> 59
        }

    /** Получить год, месяц, день, часы или минуты из строки поля ввода. */
    private fun String.getDate(range: IntRange) = this.substring(range).trim()

    /**
     * Получить четырехзначный год из двухзначного по правилу:
     * если указанный год попадает в промежуток [00 - "текущий год + 10 лет"],
     * то подставляется текущий век, в противном случае - прошлый век.
     * */
    private fun getFullYearFromTwoDigits(yearStr: String): Int {
        val year = yearStr.toInt()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentYearPlusTen: Int = currentYear % 100 + 10
        val currentCentury: Int = (currentYear / 1000) * 1000
        return if (year in 0..currentYearPlusTen) {
            currentCentury + year
        } else {
            (currentCentury - 100) + year
        }
    }

    /** Обновить значение в поле ввода после автокоррекции. */
    private fun updateInputViewText(date: Date) {
        val dateFormat = SimpleDateFormat(getDateFormat(), Locale.getDefault())
        val strDate: String = dateFormat.format(date)
        inputView.text = SpannableStringBuilder(strDate)
    }

    /**
     * Получить паттерн для форматирования текста.
     *
     * @param isFullYear - свойство, указывающее, нужен ли полный формат года.
     */
    private fun getDateFormat(isFullYear: Boolean = false) =
        when (mask) {
            DATE_MASK -> if (isFullYear) "dd.MM.yyyy" else "dd.MM.yy"
            DATE_FULL_YEAR_MASK -> "dd.MM.yyyy"
            TIME_MASK -> "HH:mm"
            DATE_WITHOUT_YEAR_MASK -> "dd.MM"
            DATE_ONLY_YEAR_MASK -> "yyyy"
            else -> if (isFullYear) "dd.MM.yyyy HH:mm" else "dd.MM.yy HH:mm"
        }

    private companion object {
        private const val DATE_MASK = "00.00.00"
        private const val DATE_FULL_YEAR_MASK = "00.00.0000"
        private const val DATE_WITHOUT_YEAR_MASK = "00.00"
        private const val DATE_ONLY_YEAR_MASK = "0000"
        private const val TIME_MASK = "00:00"
        private const val DATE_TIME_MASK = "$DATE_MASK $TIME_MASK"

        val DAY_OR_HOUR_RANGE = 0..1
        val MONTH_OR_MINUTE_RANGE = 3..4
        val YEAR_RANGE = 6..7
        val HOUR_RANGE = 9..10
        val MINUTE_RANGE = 12..13
        val ONLY_YEAR_RANGE = 0..3
        val FULL_YEAR_RANGE = 6..9
        val FULL_YEAR_HOUR_RANGE = 11..12
        val FULL_YEAR_MINUTE_RANGE = 14..15
    }
}