package ru.tensor.sbis.design.profile

import android.content.Context
import androidx.annotation.Px
import org.mockito.kotlin.KStubbing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.theme.global_variables.ImageSize

/** @SelfDocumented */
internal fun mockPhotoSize(
    context: Context,
    @Px size: Int,
    mockAlso: KStubbing<PhotoSize>.() -> Unit = { }
): PhotoSize {
    val imageSize = mock<ImageSize> {
        on { getDimenPx(context) } doReturn size
    }
    return mock {
        on { photoImageSize } doReturn imageSize
        mockAlso()
    }
}