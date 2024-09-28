package ru.tensor.sbis.hallscheme.v2.presentation.model.places

import android.graphics.BitmapShader
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.HallSchemeV2
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.places.Place
import ru.tensor.sbis.hallscheme.v2.presentation.model.HallSchemeItemUi
import ru.tensor.sbis.hallscheme.v2.util.evaluateLayoutParams
import ru.tensor.sbis.hallscheme.v2.util.rotateItem
import java.lang.ref.WeakReference

/**
 * Класс для отображения отдельно стоящего места.
 * @author aa.gulevskiy
 */
internal abstract class PlaceUi(private val place: Place) : HallSchemeItemUi(place) {

    override fun draw(
        viewGroup: ViewGroup,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {
        val view = getView(viewGroup)
        viewReference = WeakReference(view)
        setElementZ(view)
        viewGroup.addView(view)
        view.setOnClickListener { }
    }

    override fun draw3D(
        viewGroup: ViewGroup,
        pressedShader: BitmapShader,
        unpressedShader: BitmapShader,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {
        draw(viewGroup, onItemClickListener)
    }

    override fun getView(viewGroup: ViewGroup): View {
        val rootView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.hall_scheme_item_place, viewGroup, false)

        rootView.evaluateLayoutParams(place)
        rootView.rotateItem(place)

        val vectorDrawable = getImage()

        val imageView = rootView.findViewById<ImageView>(R.id.placeImageView)
        imageView.adjustViewBounds = true
        imageView.setImageDrawable(vectorDrawable)

        setText(rootView)

        val padding = rootView.context.resources.getDimensionPixelSize(R.dimen.hall_scheme_spacing_normal)
        rootView.setPadding(padding, padding, padding, padding)

        return rootView
    }

    private fun setText(rootView: View) {
        val textView = rootView.findViewById<SbisTextView>(R.id.placeTextView)
        textView.text = place.name
        textView.rotation = -place.itemRotation.toFloat()

        val padding = rootView.context.resources.getDimensionPixelSize(R.dimen.hall_scheme_spacing_large)
        when (place.itemRotation) {
            0 -> textView.setPadding(0, 0, 0, padding)
            90 -> textView.setPadding(padding, 0, 0, 0)
            180 -> textView.setPadding(0, padding, 0, 0)
            270 -> textView.setPadding(0, 0, padding, 0)
        }
    }

    /**
     * Возвращает [Drawable] места.
     */
    abstract fun getImage(): Drawable?

    override fun get3dView(viewGroup: ViewGroup): View {
        return getView(viewGroup)
    }
}
