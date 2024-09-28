package ru.tensor.sbis.base_components.activity.behaviour

import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.base_components.util.disableAutofillServiceApi29
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityBehaviour

/**
 * Реализация поведения, выключающего автозаполнение на Android 10.
 *
 * @author kv.martyshenko
 */
class DisableAutofillBehaviour : ActivityBehaviour<AppCompatActivity> {

    override fun onCreate(activity: AppCompatActivity) {
        activity.disableAutofillServiceApi29()
    }
}