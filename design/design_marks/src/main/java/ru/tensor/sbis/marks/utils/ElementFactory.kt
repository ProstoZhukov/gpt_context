/**
 * Функции создания компонентов для пометок.
 *
 * @author ra.geraskin
 */
package ru.tensor.sbis.marks.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout.LayoutParams
import androidx.core.view.isVisible
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisLinkButton
import ru.tensor.sbis.design.buttons.base.models.style.LabelButtonStyle
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.header.BaseHeader
import ru.tensor.sbis.design.header.createHeader
import ru.tensor.sbis.design.header.data.HeaderAcceptSettings
import ru.tensor.sbis.design.header.data.HeaderTitleSettings
import ru.tensor.sbis.design.header.data.LeftCustomContent
import ru.tensor.sbis.design.header.data.RightCustomContent
import ru.tensor.sbis.marks.model.SbisMarksCheckboxStatus
import ru.tensor.sbis.marks.model.item.SbisMarksElement
import ru.tensor.sbis.marks.model.item.SbisMarksIconElement
import ru.tensor.sbis.marks.R
import ru.tensor.sbis.marks.model.title.SbisMarksTitle
import ru.tensor.sbis.marks.style.SbisMarksStyleHolder

/**
 * Создать готовую модель для платформенной пометки "Важно".
 */
fun createImportant(checkboxStatus: SbisMarksCheckboxStatus): SbisMarksElement =
    SbisMarksIconElement(
        "important_id",
        SbisMarksTitle.Res(R.string.design_marks_title_important),
        checkboxStatus,
        SbisMobileIcon.Icon.smi_flag
    )

/**
 * Создать готовую модель для платформенной пометки "Плюс".
 */
fun createPlus(checkboxStatus: SbisMarksCheckboxStatus): SbisMarksElement =
    SbisMarksIconElement(
        "plus_id",
        SbisMarksTitle.Res(R.string.design_marks_title_plus),
        checkboxStatus,
        SbisMobileIcon.Icon.smi_Add
    )

/**
 * Создать view-компонент [BaseHeader] с требуемыми параметрами.
 *
 * @param context           темизированный контекст для покраски заголовка шапки в чёрный цвет
 * @param cancelButton      view элемент для кнопки "Сбросить"
 * @param isCheckboxVisible отображаются ли чекбоксы
 * @param onAccept          слушатель нажатия кнопки подтверждения (зелёная круглая кнопка с галкой)
 *
 */
internal fun createHeaderView(
    context: Context,
    cancelButton: View,
    isCheckboxVisible: Boolean,
    onAccept: () -> Unit
): View = createHeader(
    context = SbisMarksStyleHolder.getThemedContext(context, isCheckboxVisible),
    titleSettings = HeaderTitleSettings.withText(context.getString(R.string.design_marks_top_title)),
    acceptSettings = if (isCheckboxVisible) HeaderAcceptSettings.IconAccept else HeaderAcceptSettings.NoneAccept,
    hasClose = false,
    rightCustomContent = RightCustomContent.Content(
        contentIsResponsibleForEndPadding = true,
        creator = { cancelButton }
    ),
    // - В режиме с чекбоксами используем пустой контент, что приводит к появлению шапочного отступа, к которому
    // прибавляется отступ заголовка. Это позволяет сделать отступ заголовка по спецификации = ширине поля чекбокса.
    // - В режиме без чекбоксов используем заглушку. Она предотвращает появление шапочного отступа перед заголовком.
    // Это позволяет использовать для заголовка конкретное значение отступа из ресурсов в соответствии со спецификацией.
    leftCustomContent = if (isCheckboxVisible) {
        LeftCustomContent.NoneContent
    } else {
        LeftCustomContent.Content { View(context).apply { visibility = View.GONE } }
    },
    onAccept = onAccept
)

/**
 * Создать кнопку "Сбросить" для последующей вставки в шапку.
 *
 * @param context          контекст
 * @param styleHolder      класс с параметрами стилизации компонента
 * @param isVisibleOnStart флаг отображающий видимость кнопки при начальной отрисовке
 * @param onCancel         слушатель нажатия этой кнопки
 *
 */
internal fun createCancelHeaderButton(
    context: Context,
    styleHolder: SbisMarksStyleHolder,
    isVisibleOnStart: Boolean,
    onCancel: () -> Unit
) =
    SbisLinkButton(context).apply {
        setOnClickListener { onCancel() }
        setTitle(context.getString(R.string.design_marks_title_clear_label))
        style = LabelButtonStyle
        size = SbisButtonSize.XS
        isVisible = isVisibleOnStart
        minimumHeight = styleHolder.headerItemsHeight
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).also { params ->
            params.setMargins(0, 0, styleHolder.headerCancelButtonEndPadding, 0)
            params.gravity = Gravity.CENTER_VERTICAL
        }
    }
