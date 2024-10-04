package ru.tensor.sbis.design.profile_decl.person

import android.content.Context
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Обработчик нажатий по UI-элементу профиля
 *
 * @author kv.martyshenko
 */
interface PersonClickListener : Feature {

    /**
     * Метод-обработчик нажатия
     *
     * @param context контекст
     * @param personUuid идентификатор пользователя
     */
    fun onPersonClicked(context: Context, personUuid: UUID)

}