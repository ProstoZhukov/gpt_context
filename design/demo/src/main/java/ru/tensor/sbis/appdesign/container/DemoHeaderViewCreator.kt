package ru.tensor.sbis.appdesign.container

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.container.*

/**
 * Фобрика для создания шапки контейнера.
 *
 * @author ma.kolpakov
 */
@Parcelize
class DemoHeaderViewCreator : ContentCreator<DemoHeaderViewContent>, Parcelable {
    override fun createContent(): DemoHeaderViewContent = DemoHeaderViewContent()
}