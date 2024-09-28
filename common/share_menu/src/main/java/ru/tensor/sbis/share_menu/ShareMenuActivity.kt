package ru.tensor.sbis.share_menu

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.design.change_theme.contract.SelfThemedActivity
import ru.tensor.sbis.entrypoint_guard.activity.EntryPointActivity
import ru.tensor.sbis.share_menu.ui.ShareMenuFragment
import ru.tensor.sbis.share_menu.utils.ShareDataFactory
import ru.tensor.sbis.share_menu.utils.ShareDataFactory.quickShareKey
import ru.tensor.sbis.share_menu.utils.ShareMenuContainerFactory
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.toolbox_decl.share.ShareHandler
import ru.tensor.sbis.verification_decl.lockscreen.BlockOnTopActivity
import timber.log.Timber

/**
 * [Activity] для показа меню "поделиться" приложения,
 * которое открывается при попытке пользователя поделиться файлами или текстом.
 *
 * Для отображения и регистрации прикладного пункта меню необходимо реализовать
 * и зарегистрировать обработчик [ShareHandler].
 *
 * Для авторизованного пользователя отображается меню с доступными разделами, для неавторизованного -
 * перенаправление на экран авторизации.
 *
 * [Activity] меню открывается в отдельном от приложения процессе.
 * При отмене или по окончанию шаринга - процесс закрывается.
 *
 * @author vv.chekurda
 */
class ShareMenuActivity : EntryPointActivity(), BlockOnTopActivity, SelfThemedActivity {

    override fun getThemeRes(): Int = R.style.ShareMenuActivityTheme

    override fun onCreate(activity: AppCompatActivity, parent: FrameLayout, savedInstanceState: Bundle?) {
        layoutInflater.inflate(R.layout.share_menu_activity, parent, false)

        val shareData = ShareDataFactory.createShareData(intent)
        when {
            shareData == null && intent.quickShareKey == null -> {
                finishAndRemoveTask()
            }

            savedInstanceState == null -> {
                showShareMenu(shareData)
            }
        }
    }

    override fun onIntent(activity: AppCompatActivity, intent: Intent?) {
        setIntent(intent)
        val shareData = ShareDataFactory.createShareData(intent)
        showShareMenu(shareData)
    }

    private fun showShareMenu(shareData: ShareData?) {
        if (shareData == null) {
            finishAndRemoveTask()
        } else {
            closeShareMenu()
            val shareMenuFragment = ShareMenuContainerFactory.createContainer(
                context = this,
                contentCreator = ShareMenuFragment.Creator(
                    shareData = shareData,
                    quickShareKey = intent.quickShareKey,
                )
            )
            try {
                shareMenuFragment.showNow(supportFragmentManager, SHARE_MENU_FRAGMENT_TAG)
            } catch (ex: IllegalStateException) {
                Timber.e(ex)
                finishAndRemoveTask()
            }
        }
    }

    @SuppressLint("CommitTransaction")
    private fun closeShareMenu() {
        val fragment = supportFragmentManager.findFragmentByTag(SHARE_MENU_FRAGMENT_TAG) ?: return
        try {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commitNowAllowingStateLoss()
        } catch (ex: IllegalStateException) {
            Timber.e(ex)
            finishAndRemoveTask()
        }
    }
}

private const val SHARE_MENU_FRAGMENT_TAG = "SHARE_MENU_FRAGMENT_TAG"