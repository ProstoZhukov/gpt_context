package ru.tensor.sbis.design.profile_decl.person

import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.profile_decl.R
import ru.tensor.sbis.person_decl.profile.model.InitialsStubData as InitialsStubDataDecl

/**
 * Данные для отображения заглушки с инициалами
 *
 * @author us.bessonov
 */
@Suppress("KDocUnresolvedReference")
@Parcelize
data class InitialsStubData internal constructor(
    override val initials: String,
    @ColorInt override val initialsBackgroundColor: Int,
    @ColorRes override val initialsBackgroundColorRes: Int
) : InitialsStubDataDecl, Parcelable {

    private constructor(initials: String) : this(initials, UNDEFINED_COLOR, getBackgroundColor(initials))

    /**
     * Данные получаются из микросервиса профилей.
     * Параметр [initials] не должен быть пустым, это ответственность микросервиса профилей
     *
     * @see EmployeeProfileControllerWrapper.getPersonInitialsStubData
     */
    constructor(initials: String, colorHex: String) :
        this(initials, Color.parseColor("#$colorHex"))

    /**
     * Данные получаются из микросервиса профилей.
     * Параметр [initials] не должен быть пустым, это ответственность микросервиса профилей
     *
     * @see EmployeeProfileControllerWrapper.getPersonInitialsStubData
     */
    constructor(initials: String, @ColorInt initialsBackgroundColor: Int) :
        this(initials, initialsBackgroundColor, ID_NULL)

    /**
     * Цвет фона, установленный при инициализации
     */
    @ColorInt
    fun getInitialsBackgroundColor(context: Context): Int =
        initialsBackgroundColor.takeIf { it != UNDEFINED_COLOR }
            ?: ContextCompat.getColor(context, initialsBackgroundColorRes)

    companion object InitialsHelper {

        private const val UNDEFINED_COLOR = 0

        private val BACKGROUND_COLORS = intArrayOf(
            R.color.design_profile_decl_person_view_initials_background_color_1,
            R.color.design_profile_decl_person_view_initials_background_color_2,
            R.color.design_profile_decl_person_view_initials_background_color_3,
            R.color.design_profile_decl_person_view_initials_background_color_4,
            R.color.design_profile_decl_person_view_initials_background_color_5
        )

        @ColorRes
        private fun getBackgroundColor(initials: String): Int {
            require(initials.length < 3) { "Unexpected initials length '$initials'" }
            return initials.sumOf(Char::toInt).let { sum ->
                BACKGROUND_COLORS[sum % BACKGROUND_COLORS.size]
            }
        }

        /**
         * Создать [InitialsStubData] по фамилии [lastName] и имени [firstName]
         *
         * Вспомогательный метод на случай, если сервис профилей по каким-то причинам недоступен.
         * TODO: 6/30/2021 [Будет удалён после перехода всех микросервисов на модели профилей](https://online.sbis.ru/doc/72738531-773d-4f08-b29f-3dc5dccdba29)
         * Следует использовать основной публичный конструктор [InitialsStubData]
         */
        fun createByNameParts(lastName: String, firstName: String): InitialsStubData? {
            val trimmedLastName: String = lastName.trimStart()
            val trimmedFirstName: String = firstName.trimStart()
            return if (trimmedLastName.isEmpty()) {
                if (trimmedFirstName.isEmpty()) {
                    null
                } else {
                    InitialsStubData(getSingleWordInitials(trimmedFirstName))
                }
            } else {
                if (trimmedFirstName.isEmpty()) {
                    InitialsStubData(getSingleWordInitials(trimmedLastName))
                } else {
                    InitialsStubData(
                        "${trimmedLastName.first().uppercaseChar()}${trimmedFirstName.first().uppercaseChar()}"
                    )
                }
            }
        }

        /**
         * Создать [InitialsStubData] по полному имени (ФИО) [fullName]
         *
         * Вспомогательный метод на случай, если сервис профилей по каким-то причинам недоступен.
         * TODO: 6/30/2021 [Будет удалён после перехода всех микросервисов на модели профилей](https://online.sbis.ru/doc/72738531-773d-4f08-b29f-3dc5dccdba29)
         * Следует использовать основной публичный конструктор [InitialsStubData]
         */
        fun createByFullName(fullName: CharSequence): InitialsStubData? =
            fullName
                .split(' ')
                .filter(CharSequence::isNotEmpty)
                .let { createByNameParts(it.getOrNull(0) ?: "", it.getOrNull(1) ?: "") }

        private fun getSingleWordInitials(firstOrLastName: String): String {
            return "${firstOrLastName.first().uppercaseChar()}" +
                firstOrLastName.getOrNull(1)?.lowercaseChar()?.toString().orEmpty()
        }
    }
}

/** @SelfDocumented */
fun InitialsStubDataDecl.createData() = InitialsStubData(
    initials,
    initialsBackgroundColor,
    initialsBackgroundColorRes
)