package ru.tensor.sbis.design.rating.api

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.ViewCompat
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutTouchManager
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.rating.SbisRatingView
import ru.tensor.sbis.design.rating.model.SbisRatingColorsMode
import ru.tensor.sbis.design.rating.model.SbisRatingFilledMode
import ru.tensor.sbis.design.rating.model.SbisRatingIconType
import ru.tensor.sbis.design.rating.model.SbisRatingPrecision
import ru.tensor.sbis.design.rating.type.RatingIconTypeController
import ru.tensor.sbis.design.rating.utils.RatingAccessibilityDelegate
import ru.tensor.sbis.design.rating.utils.RatingStyleHolder
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.utils.delegateNotEqual
import kotlin.properties.Delegates

/**
 * Класс управления основной логикой [SbisRatingView].
 *
 * @author ps.smirnyh
 */
internal class SbisRatingViewController(
    internal val styleHolder: RatingStyleHolder = RatingStyleHolder()
) : SbisRatingViewApi {

    private val context: Context
        get() = ratingView.context

    internal var currentIconTypeController: RatingIconTypeController by Delegates.notNull()

    internal var ratingView: SbisRatingView by Delegates.notNull()

    /** Список [TextLayout], отображаемых во [ratingView]. */
    internal val icons: MutableList<TextLayout>
        get() = currentIconTypeController.icons

    /** Helper для поддержки касаний по [TextLayout]. */
    internal var touchManager: TextLayoutTouchManager? = null

    /** @SelfDocumented */
    internal var accessibilityDelegate: RatingAccessibilityDelegate by Delegates.notNull()

    internal val iconsOffset: Int
        get() = currentIconTypeController.iconProvider.iconsOffset

    override var value: Double = SBIS_RATING_MIN_RATING
        set(value) {
            require(value in SBIS_RATING_MIN_RATING..SBIS_RATING_MAX_RATING) {
                "The rating value must be in the range from 0 to iconCount."
            }
            if (field == value) return
            field = value
            currentIconTypeController.value = value
            ratingView.safeRequestLayout()
        }

    override var maxValue: Int = SBIS_RATING_MAX_ICON_COUNT.toInt()
        set(value) {
            require(value in SBIS_RATING_MIN_ICON_COUNT..SBIS_RATING_MAX_ICON_COUNT) {
                "The icon count value must be in the range from " +
                    "$SBIS_RATING_MIN_ICON_COUNT to $SBIS_RATING_MAX_ICON_COUNT"
            }
            if (field == value) return
            field = value
            currentIconTypeController.maxValue = value
            accessibilityDelegate.layoutSet = icons.toSet()
            touchManager?.clear()
            touchManager?.addAll(icons)
            ratingView.safeRequestLayout()
        }

    override var iconSize: IconSize by delegateNotEqual(IconSize.X7L) { size ->
        currentIconTypeController.iconSize = size
        ratingView.safeRequestLayout()
    }

    override var iconType: SbisRatingIconType by delegateNotEqual(SbisRatingIconType.STARS) { type ->
        currentIconTypeController = type.getIconTypeController(this)
        initIconTypeController()
        accessibilityDelegate.layoutSet = icons.toSet()
        touchManager?.clear()
        touchManager?.addAll(icons)
        ratingView.safeRequestLayout()
    }

    override var colorsMode: SbisRatingColorsMode by delegateNotEqual(SbisRatingColorsMode.STATIC) { mode ->
        currentIconTypeController.colorsMode = mode
        ratingView.invalidate()
    }

    override var emptyIconFilledMode: SbisRatingFilledMode by delegateNotEqual(SbisRatingFilledMode.BORDERED) { mode ->
        currentIconTypeController.emptyIconFilledMode = mode
        ratingView.safeRequestLayout()
    }

    override var precision: SbisRatingPrecision by delegateNotEqual(
        SbisRatingPrecision.FULL
    ) { precision ->
        currentIconTypeController.precision = precision
        ratingView.safeRequestLayout()
    }

    override var readOnly: Boolean by delegateNotEqual(
        false
    ) { newMode ->
        ratingView.isEnabled = !newMode
    }

    override var allowUserToResetRating: Boolean = true

    override var onRatingSelected: ((Double) -> Unit)? = null

    /**
     * Первоначальная настройка параметров вью.
     */
    internal fun attach(
        ratingView: SbisRatingView,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
        accessibilityDelegate: RatingAccessibilityDelegate = RatingAccessibilityDelegate(ratingView)
    ) {
        this.ratingView = ratingView
        this.accessibilityDelegate = accessibilityDelegate
        touchManager = TextLayoutTouchManager(ratingView)
        ViewCompat.setAccessibilityDelegate(ratingView, accessibilityDelegate)
        styleHolder.load(context, attrs, defStyleAttr, defStyleRes)
        currentIconTypeController = iconType.getIconTypeController(this)
        accessibilityDelegate.layoutSet = icons.toSet()
        touchManager?.addAll(icons)
    }

    private fun initIconTypeController() {
        currentIconTypeController.apply {
            value = this@SbisRatingViewController.value
            maxValue = this@SbisRatingViewController.maxValue
            iconSize = this@SbisRatingViewController.iconSize
            colorsMode = this@SbisRatingViewController.colorsMode
            emptyIconFilledMode = this@SbisRatingViewController.emptyIconFilledMode
            precision = this@SbisRatingViewController.precision
        }
    }
}