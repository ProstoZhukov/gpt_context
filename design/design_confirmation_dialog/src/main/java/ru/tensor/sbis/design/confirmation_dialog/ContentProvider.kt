package ru.tensor.sbis.design.confirmation_dialog

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.design_confirmation.databinding.ConfirmationDialogBaseContentBinding
import ru.tensor.sbis.design.design_confirmation.databinding.ConfirmationProgressContentBinding
import ru.tensor.sbis.design.design_confirmation.databinding.ConfirmationTextInputContentBinding
import ru.tensor.sbis.design.progress.SbisLoadingIndicator
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.view.input.text.TextInputView

/**
 * Интерфейс предоставляющий контент для диалога подтверждения
 *
 * @author ma.kolpakov
 */

interface ContentProvider : Parcelable {
    fun getContent(context: Context, container: SbisContainerImpl): View
}

/**
 * Базовый контент диалога подтверждения.
 *
 * @param message заголовок
 * @param comment комментарий
 * @param provider прикладной контент
 */
@Parcelize
class BaseContentProvider(
    private val message: CharSequence?,
    private val comment: CharSequence?,
    private val provider: ((context: Context, container: SbisContainerImpl) -> View)? = null,
    private val initializer: ((message: TextView, comment: SbisTextView) -> Unit)? = null
) : ContentProvider {
    override fun getContent(context: Context, container: SbisContainerImpl): View {
        val binding = ConfirmationDialogBaseContentBinding.inflate(LayoutInflater.from(context))

        provider?.let {
            binding.designConfirmationCustomContent.addView(it(context, container))
            binding.designConfirmationCustomContent.visibility = View.VISIBLE
        }

        initializer?.invoke(binding.designConfirmationMessage, binding.designConfirmationComment)
        initMessageAndComment(binding.designConfirmationMessage, binding.designConfirmationComment, message, comment)

        return binding.root
    }
}

/**
 * Реализация [ContentProvider] с использованием [PlatformSbisString.Res] в качестве типов сообщения и комментария.
 */
@Parcelize
class ResourceStringContentProvider(
    private val message: PlatformSbisString.Res?,
    private val comment: PlatformSbisString.Res?,
    private val provider: ((context: Context, container: SbisContainerImpl) -> View)? = null,
    private val initializer: ((message: TextView, comment: SbisTextView) -> Unit)? = null
) : ContentProvider {

    override fun getContent(context: Context, container: SbisContainerImpl): View {
        val binding = ConfirmationDialogBaseContentBinding.inflate(LayoutInflater.from(context))

        provider?.let {
            binding.designConfirmationCustomContent.addView(it(context, container))
            binding.designConfirmationCustomContent.visibility = View.VISIBLE
        }

        initializer?.invoke(binding.designConfirmationMessage, binding.designConfirmationComment)

        initMessageAndComment(
            binding.designConfirmationMessage,
            binding.designConfirmationComment,
            message?.getString(context),
            comment?.getString(context)
        )

        return binding.root
    }
}

@Parcelize
class TextInputContentProvider(
    private val message: CharSequence?,
    private val comment: CharSequence?,
    private val initializer: ((inputView: TextInputView, container: SbisContainerImpl) -> Unit)
) : ContentProvider {
    override fun getContent(context: Context, container: SbisContainerImpl): View {
        val binding = ConfirmationTextInputContentBinding.inflate(LayoutInflater.from(context))

        initializer(binding.designConfirmationTextInput, container)

        initMessageAndComment(binding.designConfirmationMessage, binding.designConfirmationComment, message, comment)

        return binding.root
    }
}

@Parcelize
class ProgressContentProvider(
    private val message: CharSequence?,
    private val init: (
        (message: SbisTextView, progress: SbisLoadingIndicator, container: SbisContainerImpl) -> Unit
    )? = null
) : ContentProvider {
    override fun getContent(context: Context, container: SbisContainerImpl): View {
        val binding = ConfirmationProgressContentBinding.inflate(LayoutInflater.from(context))
        message?.let {
            binding.designConfirmationMessage.visibility = View.VISIBLE
            binding.designConfirmationMessage.text = it
        }
        init?.invoke(
            binding.designConfirmationMessage, binding.designConfirmationLoadingIndicator, container
        )
        return binding.root
    }
}

private fun initMessageAndComment(
    messageView: TextView,
    commentView: SbisTextView,
    message: CharSequence?,
    comment: CharSequence?
) {
    message?.let {
        messageView.visibility = View.VISIBLE
        messageView.text = it
    }
    comment?.let {
        commentView.visibility = View.VISIBLE
        commentView.text = it
    }
}