package ru.tensor.sbis.verification_decl.auth.auth_code

import android.app.Activity
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс для предоставления [Intent] отвечающего за показ экрана авторизации по коду.
 * Для большинста МП формой логина является LoginActivity. Но для Розницы и Экрана Повора свои активности.
 * Для SabyGet это не нужно т.к. там используется только фрагмент авторизации.
 *
 * @author da.pavlov1
 */
interface AuthCodeLoginActivityProvider : Feature {

    /**
     * Возвращает AuthCodeLoginActivity [Intent]
     *
     * @return [Intent] - AuthCodeActivity Intent
     */
    fun getAuthCodeLoginIntent(): Intent

    /**
     * Возвращает AuthCodeLoginActivity [Class]
     *
     * @return [Class] - AuthCodeActivity [Class]
     */
    fun getAuthCodeLoginClass(): Class<out Activity>
}