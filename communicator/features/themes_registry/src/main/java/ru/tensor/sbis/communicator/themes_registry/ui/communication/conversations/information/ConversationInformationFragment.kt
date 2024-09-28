package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import androidx.fragment.app.Fragment
import androidx.tracing.Trace
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.util.AdjustResizeHelper.KeyboardEventListener
import ru.tensor.sbis.common.util.CommonUtils
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.result_mediator.MessageUuidMediator
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.databinding.CommunicatorFragmentConversationInformationBinding
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.di.DaggerConversationInformationComponent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.ConversationInformationController
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.ConversationInformationView
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.ConversationInformationViewImpl
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.folderspanel.PickNameDialogFragment
import ru.tensor.sbis.persons.ContactVM
import java.util.UUID

/**
 * Экран информации о диалоге/канале.
 *
 * @author dv.baranov
 */
internal class ConversationInformationFragment :
    BaseFragment(),
    KeyboardEventListener,
    PickNameDialogFragment.FolderPickNameDialogListener {

    companion object {
        const val CONVERSATION_UUID_KEY = "CONVERSATION_UUID_KEY"
        const val CONVERSATION_IS_NEW_KEY = "CONVERSATION_IS_NEW_KEY"
        const val CONVERSATION_IS_CHAT_KEY = "CONVERSATION_IS_CHAT_KEY"
        const val CONVERSATION_CHAT_PERMISSION_KEY = "CONVERSATION_CHAT_PERMISSION_KEY"
        const val CONVERSATION_TITLE_KEY = "CONVERSATION_TITLE_KEY"
        const val CONVERSATION_PARTICIPANTS_SUBTITLE = "CONVERSATION_PARTICIPANTS_SUBTITLE"
        const val CONVERSATION_PHOTO_DATA_KEY = "CONVERSATION_PHOTO_DATA_KEY"
        const val CONVERSATION_IS_GROUP_KEY = "CONVERSATION_IS_GROUP_KEY"
        const val CONVERSATION_SINGLE_PARTICIPANT = "CONVERSATION_SINGLE_PARTICIPANT"
        internal const val KEY_NEW_INFORMATION_CONVERSATION_SCREEN = "KEY_NEW_INFORMATION_CONVERSATION_SCREEN"

        /**
         * Создать новый инстанс экрана информации о диалоге/канале.
         *
         * @param args переданные аргументы в виде Bundle.
         */
        fun newInstance(args: Bundle?): Fragment = ConversationInformationFragment().apply { arguments = args }
    }

    private var view: ConversationInformationView? = null
    private lateinit var controller: ConversationInformationController

    private val fragmentLayout: Int
        get() = R.layout.communicator_fragment_conversation_information

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val conversationInformationData = getDataFromArguments()

        DaggerConversationInformationComponent.factory().create(
            CommunicatorCommonComponent.getInstance(requireContext()),
            {
                ConversationInformationViewImpl(
                    CommunicatorFragmentConversationInformationBinding.bind(requireView()),
                    conversationInformationData
                ).also {
                    this.view = it
                    this.view?.onViewStateRestored(savedInstanceState)
                }
            },
            conversationInformationData,
            this
        ).also {
            controller = it.injector().inject(this)
        }

        if (savedInstanceState == null) {
            // Для плавности анимации входа
            controller.changeTransactionsAvailability(isAvailable = false)
            postponeEnterTransition()
            Looper.getMainLooper().queue.addIdleHandler {
                startPostponedEnterTransition()
                false
            }
        }

        MessageUuidMediator().apply {
            setResultListener(this@ConversationInformationFragment) {
                this.provideResult(this@ConversationInformationFragment, it)
            }
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? =
        if (enter) {
            FragmentSoftOpenAnimation(requireContext()).apply {
                setAnimationListener(object : CommonUtils.SimpleAnimationListener() {
                    override fun onAnimationEnd(animation: Animation?) {
                        controller.changeTransactionsAvailability(isAvailable = true)
                    }
                })
            }
        } else {
            null
        }

    private fun getDataFromArguments(): ConversationInformationData {
        val conversationUuid = arguments?.getSerializable(CONVERSATION_UUID_KEY)?.castTo<UUID>() ?: UUIDUtils.NIL_UUID
        val subtitle = arguments?.getString(CONVERSATION_PARTICIPANTS_SUBTITLE) ?: StringUtils.EMPTY
        val title = arguments?.getString(CONVERSATION_TITLE_KEY, StringUtils.EMPTY) ?: StringUtils.EMPTY
        val isChat = arguments?.getBoolean(CONVERSATION_IS_CHAT_KEY, false) ?: false
        val chatPermissions = arguments?.getParcelable(CONVERSATION_CHAT_PERMISSION_KEY) ?: Permissions()
        val isNewConversation = arguments?.getBoolean(CONVERSATION_IS_NEW_KEY) ?: false
        val photoData = arguments?.getParcelableArrayList<PhotoData>(CONVERSATION_PHOTO_DATA_KEY) ?: emptyList()
        val isGroupConversation = arguments?.getBoolean(CONVERSATION_IS_GROUP_KEY) ?: false
        val singleParticipant = arguments?.getParcelableUniversally<ContactVM>(CONVERSATION_SINGLE_PARTICIPANT)
        return ConversationInformationData(
            conversationUuid,
            title,
            subtitle,
            photoData,
            isChat,
            chatPermissions,
            isNewConversation,
            isGroupConversation,
            singleParticipant
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, fragmentLayout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeBackLayout?.layoutParams?.apply {
            width = MATCH_PARENT
            height = MATCH_PARENT
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(view?.onSaveInstanceState(outState) ?: outState)
    }

    override fun onViewGoneBySwipe() {
        controller.closeOnSwipeBack()
    }

    override fun onBackPressed(): Boolean =
        controller.onBackPressed()

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        return false
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        return false
    }

    override fun swipeBackEnabled(): Boolean = !isTablet

    override fun onNameAccepted(name: String?) {
        controller.onNameAccepted(name)
    }

    override fun onDialogClose() {
        controller.onDialogClose()
    }
}

internal class FragmentSoftOpenAnimation(context: Context) : Animation() {

    /**
     * Последнее системное время, с которым был запрос [getTransformation]
     */
    private var lastSystemCurrentTime = EMPTY_LAST_TIME

    /**
     * Текущее время анимации, в процессе может отличаться от системного [lastSystemCurrentTime],
     * если были пропуски фреймов.
     */
    private var animationCurrentTime = EMPTY_LAST_TIME

    /**
     * Признак процесса анимации.
     */
    private var isAnimationRunning = false

    private val closedXDelta: Float =
        CLOSED_X_DELTA_PERCENT * context.resources.displayMetrics.widthPixels

    init {
        interpolator = DecelerateInterpolator()
        duration = ANIMATION_TIME
    }

    override fun getTransformation(currentTime: Long, outTransformation: Transformation?): Boolean {
        // Запоминаем время начала анимации
        if (animationCurrentTime == EMPTY_LAST_TIME) {
            Trace.beginAsyncSection("FragmentSoftOpenAnimation.running", 0)
            animationCurrentTime = currentTime
            lastSystemCurrentTime = currentTime
        }
        // Считаем diff предыдущего и нового времени
        val lastFrameDiff = currentTime - lastSystemCurrentTime
        lastSystemCurrentTime = currentTime
        // Если анимация уже запущена или если не было пропусков фреймов с момента предыдущего запроса трансформации ->
        // изменяем текущее время анимации, иначе оставляем прежним (анимация не начнется).
        // Это необходимо, чтобы не показывать анимацию с фризами из-за загруженности главного потока на момент старта анимации,
        // что часто бывает на средних девайсах при первичной установки списка.
        if (lastFrameDiff in 0..(ONE_FRAME_INTERVAL + 1) || isAnimationRunning) {
            isAnimationRunning = true
            animationCurrentTime += ONE_FRAME_INTERVAL
        }
        return super.getTransformation(animationCurrentTime, outTransformation)
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        t.alpha = getInterpolatedValue(MIN_ALPHA, MAX_ALPHA, interpolatedTime)
        t.matrix.setTranslate(
            getInterpolatedValue(closedXDelta, OPENED_X_DELTA, interpolatedTime),
            0f
        )
    }

    override fun restrictDuration(durationMillis: Long) = Unit

    private fun getInterpolatedValue(fromValue: Float, toValue: Float, interpolatedTime: Float) =
        fromValue + (toValue - fromValue) * interpolatedTime
}

private const val ANIMATION_TIME = 140L
private const val EMPTY_LAST_TIME = -1L
private const val ONE_FRAME_INTERVAL = 16L
private const val MIN_ALPHA = 0f
private const val MAX_ALPHA = 1f
private const val CLOSED_X_DELTA_PERCENT = 0.2f
private const val OPENED_X_DELTA = 0f