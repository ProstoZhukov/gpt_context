package ru.tensor.sbis.communicator.communicator_files.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.communicator.generated.AttachmentOrigin
import ru.tensor.sbis.communicator.communicator_files.R
import ru.tensor.sbis.communicator.communicator_files.mapper.CommunicatorFilesItem
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.list.view.adapter.SbisAdapterApi
import ru.tensor.sbis.list.view.decorator.ItemDecoration
import ru.tensor.sbis.design.R as RDesign

/**
 * Декоратор для отображения текстовых заголовков в RecyclerView на основе типа AttachmentOrigin.
 * Заголовки группируют элементы списка по их происхождению, например "Прикреплён к сообщению", "Закреплённый", "Прикреплён к диалогу".
 *
 * @param context Контекст для доступа к ресурсам.
 *
 * @author da.zhukov
 */
internal class ConversationFileOriginDecoration(
    private val context: Context
) : ItemDecoration {

    private val textPaint = Paint().apply {
        color = context.getThemeColorInt(RDesign.attr.labelTextColor)
        textSize = context.getDimen(RDesign.attr.fontSize_xs_scaleOn)
        isAntiAlias = true
    }

    // Фиксированные отступы для заголовков
    private val leftPadding = context.getDimenPx(RDesign.attr.offset_m)
    private val topAndBottomPadding = context.getDimenPx(RDesign.attr.offset_xs)

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State, view: View) {
        val left = parent.paddingLeft + leftPadding
        val right = parent.width - parent.paddingRight

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val attachmentOrigin = getAttachmentOrigin(child)

            val previousAttachmentOrigin = getPreviousAttachmentOrigin(
                parent,
                parent.getChildAdapterPosition(child) - 1
            )

            if (attachmentOrigin != null && (i == 0 || previousAttachmentOrigin != attachmentOrigin)) {
                drawHeader(c, attachmentOrigin, left, right, child, params)
            }
        }
    }

    /**
     * Отрисовка заголовка для определенного типа вложения.
     */
    private fun drawHeader(
        canvas: Canvas, attachmentOrigin: AttachmentOrigin, left: Int, right: Int, child: View, params: RecyclerView.LayoutParams
    ) {
        // Определяем текст заголовка в зависимости от типа вложения
        val title = when (attachmentOrigin) {
            AttachmentOrigin.FROM_MESSAGE -> context.getString(R.string.conversation_files_item_header_from_message)
            AttachmentOrigin.PINNED -> context.getString(R.string.conversation_files_item_header_pinned)
            AttachmentOrigin.FROM_THEME -> context.getString(R.string.conversation_files_item_header_from_theme)
        }

        val textHeight = textPaint.descent() - textPaint.ascent()
        val textOffset = (textHeight / 2) - textPaint.descent()

        // Рассчитываем положение заголовка, чтобы текст был по центру
        val top = child.top + params.topMargin - textHeight - topAndBottomPadding / 2
        canvas.drawText(title, left.toFloat(), top + textOffset + topAndBottomPadding, textPaint)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        // Сбрасываем отступы для всех элементов
        outRect.set(0, 0, 0, 0)

        val attachmentOrigin = getAttachmentOrigin(view)

        // Проверяем текущий элемент и предыдущий элемент по позиции
        if (position == 0 || getPreviousAttachmentOrigin(parent, position - 1) != attachmentOrigin) {
            // Высота текста заголовка
            val textHeight = textPaint.descent() - textPaint.ascent()

            // Полный отступ для заголовка, включая текст и дополнительные отступы
            val totalOffset = (textHeight + topAndBottomPadding * 2).toInt()

            // Устанавливаем равные отступы сверху и снизу, чтобы текст был по центру
            outRect.top = totalOffset
        }
    }

    /**
     * Извлекает тип `AttachmentOrigin` из переданного `View`.
     *
     * Этот метод использует тег, установленный на `View`, чтобы определить тип `AttachmentOrigin`,
     * связанный с этим элементом. Тег должен быть установлен ранее, например, в процессе биндинга.
     *
     * @param view `View`, из которого необходимо извлечь тег `AttachmentOrigin`.
     * @return Возвращает `AttachmentOrigin`, связанный с `View`, или `null`, если тег не установлен.
     */
    private fun getAttachmentOrigin(view: View): AttachmentOrigin? {
        return view.getTag(R.id.attachment_origin_tag) as? AttachmentOrigin
    }

    /**
     * Извлекает тип `AttachmentOrigin` предыдущего элемента по позиции.
     *
     * Этот метод использует адаптер для получения элемента на предыдущей позиции и извлекает
     * тип `AttachmentOrigin` из его данных.
     *
     * @param parent `RecyclerView`, содержащий элементы.
     * @param position Позиция предыдущего элемента в адаптере.
     * @return Возвращает `AttachmentOrigin` предыдущего элемента или `null`, если элемент не найден.
     */
    private fun getPreviousAttachmentOrigin(parent: RecyclerView, position: Int): AttachmentOrigin? {
        if (position < 0) return null // Проверка на отрицательные индексы

        val adapterApi = parent.adapter as? SbisAdapterApi ?: return null
        val item = adapterApi.getItem(position) as? CommunicatorFilesItem ?: return null
        return item.data.actionData.first().attachmentOrigin
    }
}