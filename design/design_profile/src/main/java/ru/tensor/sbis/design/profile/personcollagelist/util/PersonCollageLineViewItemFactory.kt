package ru.tensor.sbis.design.profile.personcollagelist.util

import android.content.Context
import androidx.annotation.Px
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.design.profile.imageview.PersonImageView
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile.personcollagelist.PersonCollageLineView
import ru.tensor.sbis.design.profile_decl.person.Shape

/**
 * Выполняет создание [PersonView], используемых в [PersonCollageLineView].
 *
 * @author us.bessonov
 */
internal class PersonCollageLineViewItemFactory(private val context: Context) : () -> PersonImageView {

    @Px
    private val padding =
        context.resources.getDimensionPixelSize(R.dimen.design_profile_person_collage_line_view_item_outline_size)

    /** @SelfDocumented */
    var shape = Shape.SUPER_ELLIPSE

    override fun invoke() = PersonImageView(context).apply {
        setShape(shape)
        setPadding(padding)
        setBackground(
            when (shape) {
                Shape.SUPER_ELLIPSE -> PersonViewDrawableProvider.getSuperEllipseShape(context)
                Shape.CIRCLE -> PersonViewDrawableProvider.getCircleShape(context)
                Shape.SQUARE -> PersonViewDrawableProvider.getSquareShape(context)
            }
        )
    }
}