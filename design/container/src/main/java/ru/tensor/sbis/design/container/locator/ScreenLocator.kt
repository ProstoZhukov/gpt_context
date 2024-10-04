package ru.tensor.sbis.design.container.locator

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import io.reactivex.Completable
import io.reactivex.subjects.PublishSubject
import kotlinx.parcelize.IgnoredOnParcel
import ru.tensor.sbis.design.container.R
import ru.tensor.sbis.design.container.locator.calculator.PositionCalculator
import ru.tensor.sbis.design.container.locator.calculator.ScreenPositionCalculator

/**
 * Локатор позволяющий задать позиционирования относительно экрана или ограничивающей вью
 * @param alignment - Выравнивание контейнера
 * @param boundsViewId - Id ограничивающей вью
 * @author ma.kolpakov
 */
internal open class ScreenLocator(
    private val alignment: LocatorAlignment = LocatorAlignment.CENTER,
    @IdRes private val boundsViewId: Int = View.NO_ID
) : Locator {
    //region ignore parcel

    @IgnoredOnParcel
    var isVertical = false

    @IgnoredOnParcel
    override val offsetSubject = PublishSubject.create<LocatorCalculatedData>()

    @IgnoredOnParcel
    protected lateinit var parent: View

    @IgnoredOnParcel
    protected lateinit var root: View

    @IgnoredOnParcel
    protected var boundsView: View? = null

    @IgnoredOnParcel
    private var contentRect: Rect? = null

    @IgnoredOnParcel
    internal var boundsRect: Rect? = null

    @IgnoredOnParcel
    override var rules: ScreenLocatorRules = ScreenLocatorRules()

    //endregion
    open var positionCalculator: PositionCalculator = ScreenPositionCalculator(alignment)

    override fun apply(root: View, parent: View, contentRect: Rect) {
        boundsView = parent.findViewById(boundsViewId)
        this.parent = parent
        this.root = root
        this.contentRect = contentRect
        initLocator()
    }

    open fun initLocator() {
        // TODO: 27.04.2021 Разобраться с последовательностью вызовов, постараться  избавиться от post{} https://online.sbis.ru/opendoc.html?guid=6779e02d-86cf-459b-b716-90ac96b76982
        // Инициализируем локатор только после того как родитель отрисовался
        root.post {
            prepareLocator().andThen(publishNewPosition()).subscribe()
        }
    }

    private fun publishNewPosition(): Completable {
        return Completable.create {
            offsetSubject.onNext(positionCalculator.calculate(isVertical))
        }
    }

    /**
     * Инициализируем данные для расчетов
     */
    @SuppressLint("CheckResult")
    internal open fun prepareLocator(): Completable {
        return Completable.fromAction {
            val srcData = LocatorSrcData()
            boundsView?.let { bounds ->
                boundsRect = bounds.getRectDescendantParent(parent as ViewGroup)
            }
            val locationPrent = IntArray(2)
            val locationRoot = IntArray(2)
            // вычисляем смещение вызывающего окна относительно окна внутри которого рисуется(Необходимо потому что
            // контейнер может быть вызван из "диалог активити" или еще какой нибудь не полноэкранной формы и надо \
            // переводить ограничивающие области и другие элементы в систему координат контейнера).
            parent.getLocationOnScreen(locationPrent)
            root.getLocationOnScreen(locationRoot)
            val toRootX = locationPrent[0] - locationRoot[0]
            val toRootY = locationPrent[1] - locationRoot[1]
            // переводим ограничивающую область в систему координат контейнера.
            // Сейчас необходимости нет, но так же возможно потребуется передать это смещение "затенению с вырезом"
            // для корректного отображения выреза.
            boundsRect?.offset(toRootX, toRootY)

            val drawingRect = Rect()
            root.getDrawingRect(drawingRect)

            if (isVertical) {
                srcData.rootOffset = toRootY
                srcData.rootSize = drawingRect.height()

                srcData.boundsPos = boundsRect?.top ?: 0
                srcData.boundsSize = boundsRect?.height() ?: 0

                srcData.contentSize = contentRect?.height() ?: 0

                srcData.marginStart = if (rules.defaultMarginTop)
                    parent.resources.getDimensionPixelSize(R.dimen.container_margin_top)
                else 0
                srcData.marginEnd = if (rules.defaultMarginBottom)
                    parent.resources.getDimensionPixelSize(R.dimen.container_margin_bottom)
                else 0
            } else {
                srcData.rootOffset = toRootX

                srcData.rootSize = drawingRect.width()

                srcData.boundsPos = boundsRect?.left ?: 0
                srcData.boundsSize = boundsRect?.width() ?: 0

                srcData.contentSize = contentRect?.width() ?: 0

                srcData.marginStart = if (rules.defaultMarginStart)
                    parent.resources.getDimensionPixelSize(R.dimen.container_margin_left)
                else 0
                srcData.marginEnd = if (rules.defaultMarginEnd)
                    parent.resources.getDimensionPixelSize(R.dimen.container_margin_right)
                else 0
            }
            positionCalculator.srcData = srcData
        }
    }

    override fun dispose() = Unit
}