package ru.tensor.sbis.design.whats_new.domain

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.whats_new.SbisWhatsNewPlugin
import ru.tensor.sbis.design.whats_new.model.SbisWhatsNewDisplayBehavior
import ru.tensor.sbis.verification_decl.login.LoginInterface

/** Реализация [ShowConditionManager]. */
internal class ShowConditionManagerImpl(
    versionNumber: String,
    context: Context,
    private val loginInterface: LoginInterface? = null,
    private val prefs: SharedPreferences =
        context.getSharedPreferences(WHATS_NEW_SHARED_PREFERENCES, Context.MODE_PRIVATE)
) : ShowConditionManager {

    private val generalVersion = getGeneralPartVersion(versionNumber)
    private val whatsNewText = context.getString(SbisWhatsNewPlugin.customizationOptions.whatsNewRes)
    private val isShowPerUser
        get() = SbisWhatsNewPlugin.customizationOptions.displayBehavior == SbisWhatsNewDisplayBehavior.PER_USER

    override fun saveShowing() {
        saveState(getEntrancePersonId())
    }

    override fun checkShowing(): Boolean {
        val personId = getEntrancePersonId()
        val oldVersion = restoreVersion(personId).takeIf { it.isNotEmpty() || !isShowPerUser }
            ?: restoreVersion(DEFAULT_USER)
        return verifyVersion(oldVersion, personId)
    }

    private fun restoreVersion(key: String): String =
        prefs.getString(key, "").orEmpty()

    private fun getEntrancePersonId(): String =
        if (isShowPerUser) {
            loginInterface?.currentPersonId ?: DEFAULT_USER
        } else {
            DEFAULT_USER
        }

    private fun saveState(key: String) {
        prefs.edit {
            putString(key, generalVersion)
            if (key != DEFAULT_USER) {
                putString(DEFAULT_USER, generalVersion)
            }
        }
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
            } else {
                false
            }
        } else {
            true
        }
    }

    private fun getGeneralPartVersion(version: String): String {
        val separator = "."
        return version.split(separator).joinToString(
            separator = separator,
            limit = 2,
            truncated = StringUtils.EMPTY
        ).trimEnd { it == separator[0] }
    }

    companion object {
        const val DEFAULT_USER = "default_user"
    }

}

private const val WHATS_NEW_SHARED_PREFERENCES = "WHATSNEW_SHARED_PREFERENCES"