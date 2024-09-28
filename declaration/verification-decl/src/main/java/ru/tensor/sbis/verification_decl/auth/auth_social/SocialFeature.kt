package ru.tensor.sbis.verification_decl.auth.auth_social

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature


/**
 * Интерфейс фичи модуля работы с социальными сетями.
 *
 * @author ar.leschev
 */
interface SocialFeature : Feature {

    /**
     * Создать менеджера социальных сетей.
     * Отрисует список соц.сетей из настроек AuthSocialPlugin, произведет аутентификацию в соц.сети и вернет результат в [callbacks].
     * Если передан параметр [takeOnly] и его значение меньше, чем соц.сетей в настройках плагина,
     * то будет отрисована дополнительная кнопка "Более", список будет сокращен.
     * Поднимет последнюю использованную соц.сеть в начало списка.
     *
     * После инфлейта вызвать метод [SocnetManager.create] с передачей контейнера для встраивания.
     */
    fun createManager(
        fragment: Fragment,
        takeOnly: Int = Int.MAX_VALUE,
        callbacks: SocnetManager.Callbacks
    ): SocnetManager

    /**
     * Предоставляет ссылку на фичу.
     */
    interface Provider : Feature {
        val socialFeature: SocialFeature
    }
}