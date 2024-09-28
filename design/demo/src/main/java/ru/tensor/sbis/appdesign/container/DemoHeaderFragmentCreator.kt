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
class DemoHeaderFragmentCreator(val content: DemoHeaderFragmentContent) :
    ContentCreator<DemoHeaderFragmentContent>, Parcelable {
    override fun createContent(): DemoHeaderFragmentContent = content
}