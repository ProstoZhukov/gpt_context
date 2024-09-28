package ru.tensor.sbis.whats_new

import android.content.SharedPreferences
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.onboarding.ui.utils.OnboardingPreferenceManager
import ru.tensor.sbis.verification_decl.login.LoginInterface

/**
 * Реализация [OnboardingPreferenceManager] с правилами работы "Что нового".
 *
 * @author ps.smirnyh
 */
internal class WhatsNewOnboardingPreferenceManagerImpl(
    private val preferences: SharedPreferences,
    versionNumber: String,
    private val whatsNewText: String,
    private val loginInterface: LoginInterface? = null
) : OnboardingPreferenceManager {

    private val generalVersion = getGeneralPartVersion(versionNumber)
    private val userAware
        get() = WhatsNewPlugin.customizationOptions.userAware

    override fun restoreEntrance(): Boolean {
        val personId = getEntrancePersonId()
        val oldVersion = restoreVersion(personId).takeIf { it.isNotEmpty() }
        // Сделано для проверки версии при пустом ключе по ошибке
        // https://dev.sbis.ru/opendoc.html?guid=1ebd88ab-2ef8-4ea2-b655-b6b8faeaf8e6&client=3
            ?: restoreVersion(StringUtils.EMPTY)
        return if (userAware) {
            verifyVersion(oldVersion, personId)
        } else {
            verifyVersion(oldVersion, personId) || restoreAnyEntrance()
        }
    }

    override fun restoreAnyEntrance(): Boolean =
        preferences.all.values.any { it == generalVersion }

    override fun saveEntrance() = Unit

    override fun restoreProcessed(): Boolean = restoreEntrance()

    override fun saveProcessed() = saveState(getEntrancePersonId())

    private fun saveState(key: String) {
        preferences.edit()
            .putString(key, generalVersion)
            .also {
                if (key != DEFAULT_USER) {
                    it.putString(DEFAULT_USER, generalVersion)
                }
            }
            .apply()
    }

    private fun restoreVersion(key: String): String =
        preferences.getString(key, StringUtils.EMPTY).orEmpty()

    private fun getEntrancePersonId(): String =
        if (userAware) {
            loginInterface?.currentPersonId ?: DEFAULT_USER
        } else {
            DEFAULT_USER
        }

    private fun verifyVersion(version: String, personId: String): Boolean {
        if (version.isBlank()) {
            saveState(personId)
            return true
        }

        // Если версии отличаются и текст не пустой, то показываем.
        // Если версии отличаются, но текст пуст, то запоминаем версию и не показываем
        // Иначе не показываем, не запоминаем версию
        return if (version != generalVersion) {
            if (whatsNewText.isBlank()) {
                saveState(personId)
                true
            } else false
        } else true
    }

    private fun getGeneralPartVersion(version: String): String {
        var generalVersion = version
        if (version.count { it == '.' } > 1) {
            val deleteIndex = version.indexOfLast { it == '.' }
            generalVersion = version.dropLast(version.length - deleteIndex)
        }
        return generalVersion
    }

    companion object {
        const val DEFAULT_USER = "default_user"
    }
}

const val WHATSNEW_SHARED_PREFERENCES = "WHATSNEW_SHARED_PREFERENCES"