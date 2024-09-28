package ru.tensor.sbis.verification_decl.red_button

import io.reactivex.Single
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Конктракт предоставляющий возможность проверки активирована ли "Красная кнопка"
 */
interface RedButtonActivatedProvider : Feature {

    /**
     * Проверка не нажата ли "Красная кнопка"
     * @return [Single] излучающий true, если кнопка нажата, иначе false
     */
    fun isRedButtonActivated(): Single<Boolean> = Single.just(false)
}