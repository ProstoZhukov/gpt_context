package ru.tensor.sbis.person_decl.status.ui

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик [Intent] на изменение статуса
 * @author us.bessonov
 */
interface StatusChooserIntentProvider : Feature {

    /**
     *  метод позволяет получить [Intent] для вызова [android.app.Activity.startActivityForResult] с выбором статуса
     *  @return [Intent] для старта активити
     */
    fun createStatusChooserIntent(context: Context): Intent
}