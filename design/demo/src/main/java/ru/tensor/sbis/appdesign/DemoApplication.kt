package ru.tensor.sbis.appdesign

import android.app.Application
import android.content.Context
import android.widget.Toast
import ru.tensor.sbis.design.profile.person.PersonClickListener
import java.util.*

/**
 * Реализация [Application] для имитации работы основного приложения с предоставлением зависимостей
 *
 * @author ma.kolpakov
 */
internal class DemoApplication : Application(), PersonClickListener {

    override fun onPersonClicked(context: Context, personUuid: UUID) {
        Toast.makeText(context, "Person $personUuid clicked", Toast.LENGTH_SHORT).show()
    }
}