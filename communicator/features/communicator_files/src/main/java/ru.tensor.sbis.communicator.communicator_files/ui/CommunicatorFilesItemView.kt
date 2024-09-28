package ru.tensor.sbis.communicator.communicator_files.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import ru.tensor.sbis.attachments.ui.viewmodel.base.AttachmentVM
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileActionData
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileData
import ru.tensor.sbis.communicator.communicator_files.utils.CommunicatorFileClickListener
import ru.tensor.sbis.communicator.communicator_files.utils.CommunicatorFilesAttachmentViewPool
import ru.tensor.sbis.communicator.communicator_files.utils.calculateQuantityOfViews
import ru.tensor.sbis.communicator.communicator_files.utils.calculateViewWidthForFullScreen
import ru.tensor.sbis.design.custom_view_tools.utils.dp

/**
 * Кастомное представление для отображения списка вложений в горизонтальном каскадном макете.
 *
 * Этот класс управляет отображением нескольких вложений, располагая их горизонтально
 * и динамически подстраивая их размеры для соответствия ширине экрана. Вложения предоставляются
 * в виде списка объектов [AttachmentVM], и каждое вложение отображается с использованием
 * пользовательского представления, полученного из [CommunicatorFilesAttachmentViewPool].
 *
 * @property context Контекст, используемый для доступа к ресурсам и получения метрик дисплея.
 */
internal class CommunicatorFilesItemView(
    private val context: Context,
) : LinearLayout(context) {

    /**
     * Пул view вложений
     */
    private lateinit var viewPool: CommunicatorFilesAttachmentViewPool

    // Вычисление количества представлений, которые могут поместиться по ширине и их размера
    private val viewCount = context.applicationContext.calculateQuantityOfViews()
    private val viewSize = context.applicationContext.calculateViewWidthForFullScreen(viewCount)

    /**
     * Обработчик нажатий на вложение.
     */
    lateinit var communicatorFileClickListener: CommunicatorFileClickListener

    init {
        orientation = HORIZONTAL
        setPadding(
            /* left = */ dp(COMMUNICATOR_FILES_VIEW_RIGHT_AND_LEFT_PARENT_MARGIN),
            /* top = */ 0,
            /* right = */ dp(COMMUNICATOR_FILES_VIEW_RIGHT_AND_LEFT_PARENT_MARGIN),
            /* bottom = */ 0
        )
    }

    /**
    * Устанавливает данные вложений для отображения в представлении.
    *
    * Этот метод очищает все текущие представления и проходит по предоставленному списку вложений [data.attachments].
    * Для каждого вложения создается новое представление с использованием [CommunicatorFilesAttachmentViewPool],
    * после чего оно добавляется в контейнер.
    *
    * @param data Список объектов [CommunicatorFileData], содержащий вложения для отображения.
    */
    fun setData(data: CommunicatorFileData) {
        removeAllViews() // Удаление всех текущих представлений перед добавлением новых
        data.attachments.forEachIndexed { index, attachment ->
            val actionData = data.actionData[index]
            val view = createAttachmentView(attachment, actionData)
            addView(view)
        }
    }

    /**
     * Создает представление для отображения одного вложения.
     *
     * Этот метод получает представление из [CommunicatorFilesAttachmentViewPool] для указанного [attachment],
     * устанавливает отступ и параметры разметки для корректного отображения размера.
     *
     * @param attachment Объект [AttachmentVM] для отображения.
     * @return Настроенное представление, представляющее вложение.
     */
    private fun createAttachmentView(attachment: AttachmentVM, data: CommunicatorFileActionData): View {
        val view = viewPool.getAttachmentView(attachment).apply {
            setCollageData(listOf(attachment))

            /**
             * Расширение функции для ViewGroup, устанавливающее обработчика нажатия только на корневую ViewGroup.
             * Дочерние элементы не будут обрабатывать клики.
             *
             * @param isClickable Флаг, указывающий, должен ли корневой ViewGroup быть кликабельным.
             */
            fun ViewGroup.setClickListeners(isClickable: Boolean = true) {
                if (isClickable) {
                    this.setOnLongClickListener {
                        communicatorFileClickListener.onLongClick(
                            view = it,
                            actionData = data
                        )
                    }
                    this.setOnClickListener {
                        communicatorFileClickListener.onClick(
                            actionData = data
                        )
                    }
                }

                this.children.forEach { childView ->
                    childView.isClickable = false // Отключаем обработку кликов у дочерних элементов
                    if (childView is ViewGroup) {
                        childView.setClickListeners(false) // Рекурсивно устанавливаем для всех дочерних ViewGroup
                    }
                }
            }

            // Вызов функции для установки обработчиков на корневую ViewGroup
            this.setClickListeners()

            setPadding(
                /* left = */ 0,
                /* top = */ 0,
                /* right = */ dp(COMMUNICATOR_FILES_VIEW_RIGHT_AND_BOTTOM_PADDING_DP),
                /* bottom = */ dp(COMMUNICATOR_FILES_VIEW_RIGHT_AND_BOTTOM_PADDING_DP)
            )
            layoutParams = LayoutParams(viewSize, viewSize)
        }
        return view
    }

    /**
     * Устанавливает пул представлений для получения представлений вложений.
     *
     * @param attachmentViewPool Экземпляр [CommunicatorFilesAttachmentViewPool], используемый для получения представлений.
     */
    fun setViewPool(attachmentViewPool: CommunicatorFilesAttachmentViewPool) {
        viewPool = attachmentViewPool
    }
}

private const val COMMUNICATOR_FILES_VIEW_RIGHT_AND_BOTTOM_PADDING_DP = 4f
private const val COMMUNICATOR_FILES_VIEW_RIGHT_AND_LEFT_PARENT_MARGIN = 6f