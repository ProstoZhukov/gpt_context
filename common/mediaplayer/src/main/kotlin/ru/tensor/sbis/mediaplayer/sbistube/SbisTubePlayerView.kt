package ru.tensor.sbis.mediaplayer.sbistube

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import androidx.annotation.AttrRes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerControlView
import androidx.media3.ui.PlayerView
import ru.tensor.sbis.design.utils.extentions.ViewPaddings
import ru.tensor.sbis.design.utils.extentions.applyWindowInsets
import ru.tensor.sbis.design.utils.extentions.doOnApplyWindowInsets
import ru.tensor.sbis.mediaplayer.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val CONTROLLER_SHOW_TIMEOUT_MS = 3500
private const val DEFAULT_REWIND_MS = 10000
private const val MIN_REWIND_MS = 2000

private const val FIRST_THIRD_FACTOR: Float = 1f / 3f
private const val LAST_THIRD_FACTOR: Float = 2f / 3f

@UnstableApi
/**
 * Вьюшка медиаплеера
 * Расширяет [PlayerView]
 * Дополнительно:
 *      Перемотка двойным тапом по краям видео
 *      Добавление WindowInsets к вьюшке контроллера
 *      Добавление анимаций изменения вьюшек
 *
 * @author sa.nikitin
 */
class SbisTubePlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : PlayerView(context, attrs, defStyleAttr),
    Player.Listener {

    private var fastForwardRewindMs: Int = DEFAULT_REWIND_MS
    private var fastBackRewindMs: Int = DEFAULT_REWIND_MS
    private var canDoubleTapSeek: Boolean = false

    init {
        initControlView()
        initFastRewindView()
        layoutTransition =
            LayoutTransition().apply {
                setDuration(LayoutTransition.CHANGE_DISAPPEARING, 0)
            }
        controllerShowTimeoutMs = CONTROLLER_SHOW_TIMEOUT_MS
    }

    private fun initControlView() {
        val controlView: PlayerControlView = findViewById(R.id.exo_controller)
        controlView.doOnApplyWindowInsets { _: View, windowInsets: WindowInsets, viewPaddings: ViewPaddings ->
            controlView.applyWindowInsets(windowInsets, viewPaddings)
        }
    }

    override fun performClick(): Boolean {
        if (player?.playerError == null || !isControllerFullyVisible) {
            super.performClick()
        }
        return true
    }

    private fun initFastRewindView() {
        val clickableAreaView: View? = findViewById(R.id.exo_clickable_area)
        if (clickableAreaView != null) {
            clickableAreaView.setWillNotDraw(true)
            val gestureDetector = GestureDetector(
                context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                        performClick()
                        return true
                    }

                    override fun onDoubleTap(event: MotionEvent): Boolean {
                        if (canDoubleTapSeek) {
                            if (isControllerFullyVisible) {
                                showController()
                            }
                            //player != null, canDoubleTapSeek == true гарантирует это, см. метод updateSeekParams()
                            if (event.x < clickableAreaView.width * FIRST_THIRD_FACTOR) {
                                rewind(player!!)
                            } else if (event.x > clickableAreaView.width * LAST_THIRD_FACTOR) {
                                fastForward(player!!)
                            }
                        }
                        return true
                    }

                    override fun onDown(event: MotionEvent): Boolean = true
                }
            )
            clickableAreaView.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        }
    }

    override fun setPlayer(player: Player?) {
        this.player?.removeListener(this)
        super.setPlayer(player)
        player?.addListener(this)
        updateSeekParams()
    }

    private fun setFastForwardIncrementMs(fastForwardMs: Int) {
        fastForwardRewindMs = fastForwardMs
    }

    private fun setRewindIncrementMs(rewindMs: Int) {
        fastBackRewindMs = rewindMs
    }

    private fun updateSeekParams() {
        var fastRewindMs = 0
        if (player == null || player!!.currentTimeline.isEmpty) {
            canDoubleTapSeek = false
        } else {
            val timelineWindow = player!!.currentTimeline.getWindow(player!!.currentMediaItemIndex, Timeline.Window())
            canDoubleTapSeek = timelineWindow.isSeekable
            fastRewindMs =
                if (timelineWindow.durationMs != C.TIME_UNSET) {
                    //Переводим в секунды, чтобы перемотка была целыми секундами
                    val durationSec: Float = timelineWindow.durationMs / 1000f
                    val tenPercentDurationSec: Int = (durationSec / 10f).roundToInt()
                    //Обратно в миллисекунды
                    val tenPercentDurationMs: Int = tenPercentDurationSec * 1000
                    max(MIN_REWIND_MS, min(tenPercentDurationMs, DEFAULT_REWIND_MS))
                } else {
                    DEFAULT_REWIND_MS
                }
        }
        setFastForwardIncrementMs(fastRewindMs)
        setRewindIncrementMs(fastRewindMs)
    }

    private fun fastForward(player: Player) {
        val durationMs = player.duration
        var seekPositionMs = player.currentPosition + fastForwardRewindMs
        if (durationMs != C.TIME_UNSET) {
            seekPositionMs = min(seekPositionMs, durationMs)
        }
        player.seekTo(seekPositionMs)
    }

    private fun rewind(player: Player) {
        player.seekTo(max(player.currentPosition - fastBackRewindMs, 0))
    }

    //region Player.EventListener
    override fun onPositionDiscontinuity(reason: Int) {
        updateSeekParams()
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        updateSeekParams()
    }
    //endregion
}