package ru.tensor.sbis.mediaplayer.zoom

import android.graphics.Matrix
import android.graphics.PointF
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.TextureView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import ru.tensor.sbis.mediaplayer.zoom.ZoomState.DRAG
import ru.tensor.sbis.mediaplayer.zoom.ZoomState.NONE
import ru.tensor.sbis.mediaplayer.zoom.ZoomState.ZOOM

@UnstableApi
/**
 * Обработчик жестов для [TextureView] созданной в [PlayerView]
 * 1. Зума Pinch-Spread-жестами, т.е. щипок (сведение) двумя пальцами
 * 2. Drag-and-Drop – перемещения по приближенному отображению плеера
 *
 * @author as.chadov
 *
 * @param playerView вью плеера
 * @param maxScale максимальный масштаб приближения
 *
 * @property scaleGestureDetector детектор жестов масштабирования
 * @property zoomListener слушатель событий зума
 * @property zoomView масштабируемая вью плеера
 * @property mode текущее состояние зумирования
 * @property matrix матрица трансформации координат
 * @property transformCoordinates списочное представление матрицы [matrix]
 * @property startPoint координаты начала [MotionEvent]
 * @property lastPoint последние координаты продолженного [MotionEvent]
 * @property saveScale текущий масштаб контента
 */
class ZoomMoveGestureProcessor(
    private val playerView: PlayerView,
    private val maxScale: Float = MAX_SCALE,
) : ScaleGestureDetector.SimpleOnScaleGestureListener() {

    private val scaleGestureDetector = ScaleGestureDetector(playerView.context, this)
    private var zoomListener: ZoomPlayerStateListener? = null
    private val zoomView = playerView.videoSurfaceView as? TextureView
    private var mode = NONE
    private val matrix = Matrix()
    private val transformCoordinates = FloatArray(MATRIX_SIZE)
    private val startPoint = PointF()
    private val lastPoint = PointF()
    private var saveScale = MIN_SCALE
    private var right = 0f
    private var bottom = 0f
    @Suppress("unused")
    private var resizeMode = playerView.resizeMode

    /**
     * Анализируем данное событие движения [motionEvent] и если применимо обрабатываем соответствующим образом на [zoomView]
     */
    fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        motionEvent ?: return false
        zoomView ?: return false
        // детектируем жест масштабирования
        scaleGestureDetector.onTouchEvent(motionEvent)
        when (motionEvent.actionMasked) {
            //прерываем зумирование
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> mode = NONE
            //начинаем перемещение
            MotionEvent.ACTION_DOWN -> {
                lastPoint.set(motionEvent.x, motionEvent.y)
                startPoint.set(lastPoint)
                mode = DRAG
            }
            //начинаем масштабирование
            MotionEvent.ACTION_POINTER_DOWN -> {
                lastPoint.set(motionEvent.x, motionEvent.y)
                startPoint.set(lastPoint)
                mode = ZOOM
            }
            // обрабатываем перемещение Drag-and-Drop
            MotionEvent.ACTION_MOVE -> if (mode == ZOOM || mode == DRAG && saveScale > MIN_SCALE) {
                val currentPoint = PointF(motionEvent.x, motionEvent.y)
                val transPoint = translationPoint()
                // дельта движения
                var deltaX: Float = currentPoint.x - lastPoint.x
                var deltaY: Float = currentPoint.y - lastPoint.y
                if (transPoint.y + deltaY > 0) {
                    deltaY = -transPoint.y
                } else if (transPoint.y + deltaY < -bottom) {
                    deltaY = -(transPoint.y + bottom)
                }
                if (transPoint.x + deltaX > 0) {
                    deltaX = -transPoint.x
                } else if (transPoint.x + deltaX < -right) {
                    deltaX = -(transPoint.x + right)
                }
                matrix.postTranslate(deltaX, deltaY)
                lastPoint.set(currentPoint.x, currentPoint.y)
            }
        }
        transform()
        return true
    }

    private fun transform() {
        // трансформируем контент [TextureView]
        zoomView?.run {
            setTransform(this@ZoomMoveGestureProcessor.matrix)
            invalidate()
        }
    }

    /** Установить слушателя событий зума */
    fun setListener(listener: ZoomPlayerStateListener?) {
        if (listener == null) {
            zoomListener?.onScale(false, MIN_SCALE)
        }
        zoomListener = listener
    }

    /** Координаты перемещения по X и Y */
    private fun translationPoint(): PointF {
        matrix.getValues(transformCoordinates)
        return PointF(
            transformCoordinates[Matrix.MTRANS_X],
            transformCoordinates[Matrix.MTRANS_Y]
        )
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        mode = ZOOM
        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val scaleFactor = detector.scaleFactor
        return performScale(scaleFactor, detector.focusX, detector.focusY)
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        zoomListener?.onScale(saveScale > MIN_SCALE, saveScale)
    }

    private fun performScale(factor: Float, focusX: Float, focusY: Float): Boolean {
        zoomView ?: return false
        var scaleFactor = factor
        val originScale = saveScale
        saveScale *= scaleFactor
        if (saveScale > maxScale) {
            saveScale = maxScale
            scaleFactor = maxScale / originScale
        } else if (saveScale < MIN_SCALE) {
            saveScale = MIN_SCALE
            scaleFactor = MIN_SCALE / originScale
        }
        right = zoomView.width * saveScale - zoomView.width
        bottom = zoomView.height * saveScale - zoomView.height
        if (zoomView.width >= 0 || zoomView.height >= 0) {
            matrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
            if (scaleFactor < MIN_SCALE) {
                val transPoint = translationPoint()
                matrix.getValues(transformCoordinates)
                if (scaleFactor < MIN_SCALE) {
                    if (zoomView.width > 0) {
                        if (transPoint.y < -bottom) {
                            matrix.postTranslate(0f, -(transPoint.y + bottom))
                        } else if (transPoint.y > 0) {
                            matrix.postTranslate(0f, -transPoint.y)
                        }
                    } else {
                        if (transPoint.x < -right) {
                            matrix.postTranslate(-(transPoint.x + right), 0f)
                        } else if (transPoint.x > 0) {
                            matrix.postTranslate(-transPoint.x, 0f)
                        }
                    }
                }
            }
        } else {
            matrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
            val transPoint = translationPoint()
            if (scaleFactor < MIN_SCALE) {
                if (transPoint.x < -right) {
                    matrix.postTranslate(-(transPoint.x + right), 0f)
                } else if (transPoint.x > 0) {
                    matrix.postTranslate(-transPoint.x, 0f)
                }
                if (transPoint.y < -bottom) {
                    matrix.postTranslate(0f, -(transPoint.y + bottom))
                } else if (transPoint.y > 0) {
                    matrix.postTranslate(0f, -transPoint.y)
                }
            }
        }
        return true
    }

    /** Зум к определенной точке */
    @Suppress("unused")
    fun zoomTo(scaleFactor: Float, focusX: Float, focusY: Float) {
        performScale(scaleFactor, focusX, focusY)
        transform()
    }

    /** Сбросить зумирование к дефолтному состоянию */
    fun reset() {
        saveScale = MIN_SCALE
        matrix.reset()
        transform()
    }

    /** Проверка на то, что view еще не приближалась. */
    @Suppress("unused")
    fun isNotScaling() = saveScale == MIN_SCALE

    companion object {
        /** Минимальное масштабирование */
        const val MIN_SCALE = 1f

        /** Максимальное масштабирование */
        const val MAX_SCALE = 8f

        /** Размер матрицы 3х3 */
        private const val MATRIX_SIZE = 9
    }
}