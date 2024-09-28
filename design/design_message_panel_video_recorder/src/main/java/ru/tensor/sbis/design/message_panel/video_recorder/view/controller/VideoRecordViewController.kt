package ru.tensor.sbis.design.message_panel.video_recorder.view.controller

import android.animation.ValueAnimator
import android.net.Uri
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import androidx.core.animation.doOnEnd
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.MutableStateFlow
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource
import ru.tensor.sbis.communication_decl.communicator.media.data.SourceData.UriData
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.communication_decl.communicator.media.data.State
import ru.tensor.sbis.design.message_panel.decl.record.MediaFileFactory
import ru.tensor.sbis.design.message_panel.decl.record.RecorderDecorData
import ru.tensor.sbis.design.message_panel.recorder_common.R
import ru.tensor.sbis.design.message_panel.recorder_common.contact.RecordQuoteActionListener
import ru.tensor.sbis.design.message_panel.recorder_common.contact.RecordRecipientsActionListener
import ru.tensor.sbis.design.message_panel.recorder_common.contact.RecordResultListener
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.RecordControlView
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract.RecordControlEvent
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract.RecordControlQuoteActionListener
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract.RecordControlRecipientsActionListener
import ru.tensor.sbis.design.message_panel.recorder_common.utils.DefaultMediaFileFactory
import ru.tensor.sbis.design.message_panel.recorder_common.utils.RecordingDeviceHelper
import ru.tensor.sbis.design.message_panel.video_recorder.MessagePanelVideoRecorderPlugin
import ru.tensor.sbis.design.message_panel.video_recorder.view.contract.VideoRecordResultData
import ru.tensor.sbis.design.message_panel.video_recorder.view.contract.VideoRecordViewApi
import ru.tensor.sbis.design.message_panel.video_recorder.view.contract.VideoRecordViewState
import ru.tensor.sbis.design.message_panel.video_recorder.view.VideoRecordView
import ru.tensor.sbis.design.message_panel.video_recorder.view.layout.VideoRecordViewLayout
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.RoundCameraView
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.contract.RoundCameraListener
import ru.tensor.sbis.design.video_message_view.player.VideoPlayerView
import ru.tensor.sbis.design.video_message_view.player.children.StateListener
import java.lang.Exception

/**
 * Реализует логику компонента [VideoRecordView].
 * @see VideoRecordViewApi
 *
 * @author vv.chekurda
 */
internal class VideoRecordViewController : VideoRecordViewApi {

    private lateinit var rootView: VideoRecordView
    private lateinit var cameraView: RoundCameraView
    private lateinit var controlView: RecordControlView
    private lateinit var videoPlayerView: VideoPlayerView
    private lateinit var switchCameraButton: SbisRoundButton
    private lateinit var backgroundView: View
    private var switchRotateAnimation: Animation? = null

    private var resultData: VideoRecordResultData? = null

    private var mediaPlayer: MediaPlayer? = null
    private val videoPlayerViewDataFactory = MessagePanelVideoRecorderPlugin.videoPlayerViewDataFactory

    private val isRecording: Boolean
        get() = state.value.isRecording

    private var isLocked: Boolean = false
    private var animateHiding: Boolean = true

    private var controlFadeOutAnimator: ValueAnimator? = null
    private var cameraShowingAnimator: ValueAnimator? = null
    private var backgroundAnimator: ValueAnimator? = null
    private var showingInterpolator = DecelerateInterpolator()

    private var deviceHelper: RecordingDeviceHelper? = null

    override val state: MutableStateFlow<VideoRecordViewState> = MutableStateFlow(VideoRecordViewState())

    override var decorData: RecorderDecorData
        get() = controlView.decorData
        set(value) { controlView.decorData = value }

    override var resultListener: RecordResultListener<VideoRecordResultData>? = null

    /**
     * Прикрепить разметку.
     */
    fun attachLayout(layout: VideoRecordViewLayout) {
        rootView = layout.view
        cameraView = layout.cameraView
        controlView = layout.controlView
        videoPlayerView = layout.videoPlayerView
        switchCameraButton = layout.switchCameraButton.apply {
            setOnClickListener {
                animateRolling()
                cameraView.switchCamera()
            }
        }
        backgroundView = layout.backgroundView
        subscribeOnControlEvents()
    }

    override fun init(
        fragment: Fragment,
        fileFactory: MediaFileFactory?,
        customPlayer: MediaPlayer?
    ) {
        val player = customPlayer ?: MessagePanelVideoRecorderPlugin.mediaPlayerFeature.getMediaPlayer()
        attachPlayer(player)
        deviceHelper = RecordingDeviceHelper(fragment.requireActivity())

        val mediaFileFactory = fileFactory
            ?: DefaultMediaFileFactory(
                fragment.getString(R.string.design_message_panel_recorder_video_file_name_template),
                fragment.requireContext().cacheDir.path
            )
        cameraView.initController(
            fragment,
            mediaFileFactory,
            object : RoundCameraListener {
                override fun onRecordStarted() {
                    this@VideoRecordViewController.onRecordStarted()
                }

                override fun onRecordCanceled() {
                    this@VideoRecordViewController.onRecordCanceled()
                }

                override fun onRecordCompleted(resultData: VideoRecordResultData, byTimeOut: Boolean) {
                    this@VideoRecordViewController.onRecordCompleted(resultData, byTimeOut)
                }

                override fun onRecordError(error: Exception) {
                    this@VideoRecordViewController.onRecordError(error)
                }

                override fun onVolumeAmplitudeChanged(amplitude: Float) {
                    if (isRecording) controlView.setAmplitude(amplitude)
                }
            }
        )
    }

    override fun startRecording(
        lockRecord: Boolean,
        animateHiding: Boolean
    ) {
        if (state.value.isVisible) return
        this.animateHiding = animateHiding
        resultData = null
        cameraView.withPermissions {
            deviceHelper?.requestLockOrientation(request = true)
            rootView.isVisible = true
            resultListener?.onRecordStarted()
            updateState(isRecording = true)
            cameraView.startRecording()
            cameraView.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
            animateShowing(withLock = lockRecord)
        }
    }

    override fun stopRecording() {
        stopRecordingInternal(isLocked = controlView.isLocked)
    }

    private fun stopRecordingInternal(isLocked: Boolean) {
        if (!isRecording) return
        this.isLocked = isLocked
        if (controlView.recordDuration >= 1) {
            controlView.stopRecordAnimation()
            cameraView.stopRecording()
        } else {
            cancelRecording()
        }
    }

    override fun cancelRecording() {
        when {
            state.value.isSendPreparing -> onRecordCanceled()
            state.value.isRecording -> cameraView.cancelRecording()
        }
    }

    override fun release() {
        resultData = null
        resultListener = null
        cameraView.release()
        rootView.isVisible = false
        updateState(isRecording = false, isSendPreparing = false)
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        rootView.updatePadding(bottom = keyboardHeight)
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        rootView.updatePadding(bottom = 0)
        return true
    }

    override fun setRecipientsActionListener(listener: RecordRecipientsActionListener?) {
        controlView.recipientsActionListener = listener?.let {
            object : RecordControlRecipientsActionListener {
                override fun onRecipientsClicked() {
                    it.onRecipientsClicked()
                }

                override fun onClearButtonClicked() {
                    it.onClearButtonClicked()
                }
            }
        }
    }

    override fun setQuoteActionListener(listener: RecordQuoteActionListener?) {
        controlView.quoteActionListener = listener?.let {
            object : RecordControlQuoteActionListener {
                override fun onClearButtonClicked() {
                    it.onClearButtonClicked()
                }
            }
        }
    }

    private fun showVideoPreview(videoData: VideoRecordResultData) {
        resultData = videoData
        videoPlayerView.apply {
            isVisible = true
            val videoSource = MediaSource.VideoSource(data = UriData(Uri.fromFile(videoData.videoFile)))
            val videoPlayerData = videoPlayerViewDataFactory.createVideoPlayerViewData(videoSource, videoData.duration)
            val stateListener = object : StateListener {
                override fun onFirstVideoFrameRendered(state: State) {
                    if (videoPlayerView.isVisible) onPreviewReady()
                }
            }
            setStateListener(stateListener)
            data = videoPlayerData
            prepareFirstFrame()
        }
        updateState(isRecording = false, isSendPreparing = true)
    }

    private fun onPreviewReady() {
        videoPlayerView.showPreview(false)
        cameraView.isVisible = false
        cameraView.clearState()
        switchCameraButton.isVisible = false
    }

    private fun subscribeOnControlEvents() {
        controlView.eventsHandler = { event: RecordControlEvent ->
            when (event) {
                RecordControlEvent.OnRecordStopped  -> stopRecording()
                RecordControlEvent.OnRecordCanceled -> cancelRecording()
                RecordControlEvent.OnSendClicked -> onSendClicked()
                RecordControlEvent.OnHidingEnd -> animateControlFadeOut()
                else -> Unit
            }
        }
    }

    private fun attachPlayer(mediaPlayer: MediaPlayer) {
        this.mediaPlayer = mediaPlayer
        videoPlayerView.setMediaPlayer(mediaPlayer)
    }

    private fun onRecordStarted() {
        mediaPlayer?.isEnabled = false
        controlView.startRecordAnimation()
    }

    private fun onRecordCanceled() {
        mediaPlayer?.isEnabled = true
        if (!state.value.isVisible) return
        resultListener?.onRecordCanceled()
        animateHiding()
    }

    private fun onRecordCompleted(resultData: VideoRecordResultData, byTimeOut: Boolean) {
        mediaPlayer?.isEnabled = true
        if (!state.value.isVisible) return
        if (byTimeOut) {
            isLocked = controlView.isLocked
            controlView.stopRecordAnimation()
        }
        when {
            resultData.duration < 1 -> onRecordCanceled()
            isLocked -> showVideoPreview(resultData)
            else -> complete(resultData)
        }
    }

    private fun onRecordError(error: Exception) {
        mediaPlayer?.isEnabled = true
        if (!state.value.isVisible) return
        resultListener?.onRecordError(error)
        animateHiding()
    }

    private fun onSendClicked() {
        if (isRecording) {
            stopRecordingInternal(isLocked = false)
        } else {
            resultData?.also(::complete)
                ?: onRecordCanceled()
        }
    }

    private fun complete(resultData: VideoRecordResultData) {
        resultListener?.onRecordCompleted(resultData)
        animateHiding()
    }

    private fun animateShowing(withLock: Boolean) {
        animateCamera(show = true)
        animateBackgroundFading(show = true)
        controlView.animateShowing(withLock = withLock)
    }

    private fun animateHiding() {
        if (animateHiding) {
            animateCamera(show = false)
            animateBackgroundFading(show = false)
            controlView.animateHiding()
        } else {
            clearState()
        }
    }

    private fun animateCamera(show: Boolean) {
        if (show) {
            animateCameraShowing()
        } else {
            animateCameraHiding()
        }
    }

    private fun animateCameraShowing() {
        cameraShowingAnimator?.cancel()
        val startAlpha = 0.5f
        var startTranslation = 0f

        cameraView.isVisible = true
        cameraView.alpha = 0f
        cameraShowingAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = CAMERA_SHOWING_ANIMATION_DURATION_MS
            addUpdateListener {
                val interpolation = showingInterpolator.getInterpolation(it.animatedFraction)
                cameraView.apply {
                    alpha = startAlpha + (1f - startAlpha) * interpolation
                    scaleX = interpolation
                    scaleY = interpolation
                    translationY = startTranslation * (1f - interpolation)
                }
            }
            start()
            pause()
            cameraView.doOnPreDraw {
                startTranslation = cameraView.bottom.toFloat()
                resume()
            }
        }
    }

    private fun animateCameraHiding() {
        cameraShowingAnimator?.cancel()
        val endAlpha = 0.5f
        val endTranslation = cameraView.bottom.toFloat()
        cameraShowingAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = CAMERA_SHOWING_ANIMATION_DURATION_MS
            if (state.value.isSendPreparing) {
                addUpdateListener {
                    val interpolation = showingInterpolator.getInterpolation(1f - it.animatedFraction)
                    videoPlayerView.apply {
                        alpha = endAlpha + (1f - endAlpha) * interpolation
                        scaleX = interpolation
                        scaleY = interpolation
                        translationY = endTranslation * (1f - interpolation)
                    }
                }
            } else {
                addUpdateListener {
                    val interpolation = showingInterpolator.getInterpolation(1f - it.animatedFraction)
                    cameraView.apply {
                        alpha = endAlpha + (1f - endAlpha) * interpolation
                        scaleX = interpolation
                        scaleY = interpolation
                        translationY = endTranslation * (1f - interpolation)
                    }
                }
            }
            start()
            pause()
            cameraView.doOnPreDraw { resume() }
        }
    }

    private fun animateBackgroundFading(show: Boolean) {
        backgroundAnimator?.cancel()
        backgroundView.alpha = if (show) 0f else 1f
        backgroundAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = BACKGROUND_SHOWING_ANIMATION_DURATION_MS
            if (show) {
                addUpdateListener {
                    val interpolation = showingInterpolator.getInterpolation(it.animatedFraction)
                    switchCameraButton.alpha = interpolation
                    backgroundView.alpha = interpolation
                }
            } else {
                addUpdateListener {
                    val interpolation = showingInterpolator.getInterpolation(1f - it.animatedFraction)
                    switchCameraButton.alpha = interpolation
                    backgroundView.alpha = interpolation
                }
            }
            start()
            pause()
            backgroundView.doOnPreDraw { resume() }
        }
    }

    private fun animateControlFadeOut() {
        controlFadeOutAnimator?.cancel()
        controlFadeOutAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            controlFadeOutAnimator = this
            duration = FADE_OUT_ANIMATION_DURATION_MS
            addUpdateListener { controlView.alpha = 1f - it.animatedFraction }
            doOnEnd { clearState() }
            start()
            pause()
            rootView.invalidate()
            rootView.doOnPreDraw { resume() }
        }
    }

    private fun View.animateRolling() {
        switchRotateAnimation?.cancel()
        switchRotateAnimation = RotateAnimation(
            0f,
            SWITCH_ROTATE_DEGREES,
            width / 2f,
            height / 2f
        ).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = SWITCH_ROTATE_ANIMATION_DURATION_MS
            fillAfter = false
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) = Unit
                override fun onAnimationRepeat(animation: Animation?) = Unit
                override fun onAnimationEnd(animation: Animation?) {
                    switchRotateAnimation = null
                }
            })
            doOnPreDraw { startAnimation(this) }
        }
    }

    private fun clearState() {
        cancelAllAnimations()
        resultData = null
        cameraView.apply {
            clearState()
            translationY = 0f
            isVisible = false
            scaleX = 1f
            scaleY = 1f
        }
        videoPlayerView.apply {
            clearState()
            alpha = 1f
            translationY = 0f
            isVisible = false
            scaleX = 1f
            scaleY = 1f
        }
        switchCameraButton.isVisible = true
        rootView.isVisible = false
        updateState(isRecording = false, isSendPreparing = false)
        isLocked = false
        deviceHelper?.requestLockOrientation(request = false)
        mediaPlayer?.isEnabled = true
    }

    private fun cancelAllAnimations() {
        switchRotateAnimation?.cancel()
        switchRotateAnimation = null
        controlFadeOutAnimator?.cancel()
        controlFadeOutAnimator = null
        cameraShowingAnimator?.cancel()
        cameraShowingAnimator = null
        backgroundAnimator?.cancel()
        backgroundAnimator = null
        controlView.clearRecordAnimation()
    }

    private fun updateState(
        isRecording: Boolean = state.value.isRecording,
        isSendPreparing: Boolean = state.value.isSendPreparing
    ) {
        state.tryEmit(
            state.value.copy(
                isRecording = isRecording,
                isSendPreparing = isSendPreparing
            )
        )
    }
}

private const val SWITCH_ROTATE_ANIMATION_DURATION_MS = 500L
private const val SWITCH_ROTATE_DEGREES = 180f

/**
 * Продолжительность анимации скрытия view контрола.
 */
private const val FADE_OUT_ANIMATION_DURATION_MS = 100L
private const val CAMERA_SHOWING_ANIMATION_DURATION_MS = 180L
private const val BACKGROUND_SHOWING_ANIMATION_DURATION_MS = 180L