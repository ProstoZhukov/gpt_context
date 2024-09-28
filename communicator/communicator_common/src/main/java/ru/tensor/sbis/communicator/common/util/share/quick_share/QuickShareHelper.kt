package ru.tensor.sbis.communicator.common.util.share.quick_share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.core.app.Person
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPerson
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.common.R
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.toolbox_decl.share.QUICK_SHARE_SHORTCUT_CATEGORY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.ACTION_CONVERSATION_ACTIVITY
import ru.tensor.sbis.person_decl.profile.model.PersonName
import java.util.UUID

/**
 * Интерфейс вспомогательного класса для обновления shortcuts приложения релевантными получателями.
 *
 * @author vv.chekurda
 */
interface QuickShareHelper {

    /**
     * Отправить новых получателей для функции быстрого шаринга.
     */
    fun pushContactQuickShareTargets(contactsList: List<ContactVM>)

    /**
     * Отправить новых получателей для функции быстрого шаринга.
     */
    fun pushContactQuickShare(contactsList: List<RecipientPerson>)

    /**
     * Отправить новые каналы для функции быстрого шаринга.
     */
    fun pushChannelQuickShareTargets(uuid: UUID, title: String, photoUrl: String?)

    /**
     * Удалить всех рекомендуемых для быстрого шаринга для данного приложения.
     */
    fun removeAllAppQuickShareTargets()
}

/**
 * Вспомогательного класса для обновления shortcuts приложения релевантными получателями.
 * @see [QuickShareHelper]
 */
class QuickShareHelperImpl(val context: Context) : QuickShareHelper {

    override fun pushContactQuickShareTargets(contactsList: List<ContactVM>) {
        contactsList.forEach { contact ->
            val preparedPhoto = contact.getPreparedPhoto(DIRECT_SHARE_PHOTO_SIZE, DIRECT_SHARE_PHOTO_SIZE)

            var icon: IconCompat
            val id = NavxId.DIALOGS.name.lowercase() + contact.uuid.toString()
            val shortLabel = contact.name.getShortLabelForShortcut()
            val args = Bundle().apply { putString(SENDER_UUID_KEY, contact.uuid.toString()) }

            loadIcon(
                context,
                preparedPhoto,
                R.drawable.contact_shortcut_icon
            ) {
                icon = IconCompat.createWithBitmap(it)

                buildShortcut(context, id, icon, shortLabel, true, args).pushDynamicShortcut(context)
            }
        }
    }

    override fun pushContactQuickShare(contactsList: List<RecipientPerson>) {
        contactsList.forEach { contact ->
            val preparedPhoto = contact.photoUrl.correctUrl()

            var icon: IconCompat
            val id = NavxId.DIALOGS.name.lowercase() + contact.uuid.toString()
            val shortLabel = contact.name.getShortLabelForShortcut()
            val args = Bundle().apply { putString(SENDER_UUID_KEY, contact.uuid.toString()) }

            loadIcon(
                context,
                preparedPhoto,
                R.drawable.contact_shortcut_icon
            ) {
                icon = IconCompat.createWithBitmap(it)

                buildShortcut(context, id, icon, shortLabel, true, args).pushDynamicShortcut(context)
            }
        }
    }

    override fun pushChannelQuickShareTargets(uuid: UUID, title: String, photoUrl: String?) {
        val id = NavxId.CHATS.name.lowercase() + uuid.toString()
        val args = Bundle().apply { putString(DIALOG_UUID_KEY, uuid.toString()) }

        loadIcon(
            context,
            photoUrl?.correctUrl(),
            R.drawable.channel_shortcut_icon
        ) {
            val icon = IconCompat.createWithBitmap(it)

            buildShortcut(context, id, icon, title, false, args).pushDynamicShortcut(context)
        }
    }

    override fun removeAllAppQuickShareTargets() {
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)
    }

    private fun buildShortcut(
        context: Context,
        id: String,
        icon: IconCompat,
        shortLabel: String,
        isContact: Boolean,
        intentArgs: Bundle
    ): ShortcutInfoCompat {
        // Category that our sharing shortcuts will be assigned to
        val categories = setOf(QUICK_SHARE_SHORTCUT_CATEGORY)
        val shortcut =  ShortcutInfoCompat.Builder(context, id)
            .setShortLabel(shortLabel)
            .setIcon(icon)
            .setIntent(
                Intent(ACTION_CONVERSATION_ACTIVITY).apply {
                    intentArgs.also(::putExtras)
                }
            )
            .setLongLived(true)
            .setCategories(categories)
            .setRank(HIGH_IMPORTANCE)
            .doIf(isContact) {
                setPerson(
                    Person.Builder()
                        .setIcon(icon)
                        .setName(shortLabel)
                        .build()
                )
            }
            .build()
        return shortcut
    }

    private fun loadIcon(context: Context, url: String?, @DrawableRes stub: Int, callback: (bitmap: Bitmap) -> Unit) {
        FrescoLoader.INSTANCE.setDataSubscriber(
            context,
            url,
            stub,
            DIRECT_SHARE_PHOTO_SIZE,
            DIRECT_SHARE_PHOTO_SIZE
        ) {
            callback(it)
        }
    }

    private fun ShortcutInfoCompat.pushDynamicShortcut(context: Context) {
        ShortcutManagerCompat.pushDynamicShortcut(context, this)
    }

    private fun String?.correctUrl(): String =
        when {
            this.isNullOrEmpty() -> StringUtils.EMPTY
            this.contains("%d") -> this.replace(UNSPECIFIED_PHOTO_SIZE, "$DIRECT_SHARE_PHOTO_SIZE")
            else -> this
        }

    private fun PersonName.getShortLabelForShortcut() = "$lastName $firstName".trim()
}

/** Размер фотографии контакта для shortcut */
private const val DIRECT_SHARE_PHOTO_SIZE = 124
private const val HIGH_IMPORTANCE = 0
/** @SelfDocumented */
private const val UNSPECIFIED_PHOTO_SIZE = "%d"
private const val DIALOG_UUID_KEY = "dialog_uuid"
private const val SENDER_UUID_KEY = "sender_Uuid"