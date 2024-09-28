package ru.tensor.sbis.appdesign.container

import android.os.Parcelable
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.design.container.Content
import ru.tensor.sbis.design.container.ContentCreator
import ru.tensor.sbis.design.container.FragmentContent

// TODO: 14.04.2021 Добавить разные виды контента в демо приложение https://online.sbis.ru/opendoc.html?guid=f3600a86-19bf-4957-9a3b-d789fc6b9246
@Parcelize
class DemoFragmentCreator : ContentCreator<FragmentContent>, Parcelable {
    override fun createContent() =DemoFragmentContent()
}