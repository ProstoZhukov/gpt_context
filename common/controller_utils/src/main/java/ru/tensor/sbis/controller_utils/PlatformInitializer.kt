package ru.tensor.sbis.controller_utils

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.WorkerThread
import androidx.tracing.trace
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.tracing.FirebaseTracerDelegate
import ru.tensor.sbis.common.util.SESSION_ID
import ru.tensor.sbis.network_native.httpclient.Server
import ru.tensor.sbis.platform.Initializer
import ru.tensor.sbis.platform.generated.AppTypeEnum
import ru.tensor.sbis.platform.generated.CoreInitializerImpl
import ru.tensor.sbis.platform.generated.UserAgentHelper
import ru.tensor.sbis.platform.generated.UserError
import ru.tensor.sbis.toolbox_decl.navigation.AvailableAppNavigationFilterInitializer
import timber.log.Timber

/**
 * Класс для инициализации платформы
 *
 * @author am.boldinov
 */
object PlatformInitializer {
    private const val KEY_INSTALLER = "InstallerSource"

    @JvmOverloads
    @WorkerThread
    fun init(
        application: Application,
        type: AppTypeEnum = AppTypeEnum.GENERIC_APPLICATION,
        scope: CoroutineScope = GlobalScope,
        tracerDelegate: FirebaseTracerDelegate = { block -> block() },
        availableNavigationFilterInitializer: AvailableAppNavigationFilterInitializer? = null
    ): Unit = trace("PlatformInitializer#init with $type") {
        /*
        Указание идентификаторов доступных пунктов навигации в приложении необходимо выполнять до построения навигации,
        которое выполняется при инициализации контроллера.
        */
        availableNavigationFilterInitializer?.init()
        init(context = application, type, tracerDelegate)
        scope.launch {
            withContext(Dispatchers.IO) {
                try {
                    CoreInitializerImpl.initAfterLoadApplication()
                } catch (e: Throwable) {
                    Timber.e(e, "Error during initAfterLoadApplication")
                }
            }
        }
        Unit
    }

    @WorkerThread
    private fun init(
        context: Context,
        type: AppTypeEnum = AppTypeEnum.GENERIC_APPLICATION,
        tracerDelegate: FirebaseTracerDelegate = { block -> block() }
    ) = trace("PlatformInitializer#init with $type") {
        tracerDelegate.invoke {
            Initializer.setAppVersionInfo(
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE.toString(),
                BuildConfig.BUILD_DATE
            )

            try {
                Initializer.init(BuildConfig.PLATFORM_CORE_LIBRARY, context, type, SESSION_ID, true)
            } catch (ex: UserError) {
                Timber.e(ex)
                throw ex
            } finally {
                logInstaller(context)
            }
            Server.setUserAgent(UserAgentHelper.getUserAgent())
        }
    }

    private fun logInstaller(context: Context) {
        runCatching {
            val installerApp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.packageManager.getInstallSourceInfo(context.packageName).initiatingPackageName
            } else {
                context.packageManager.getInstallerPackageName(context.packageName)
            }
            FirebaseCrashlytics.getInstance().setCustomKey(KEY_INSTALLER, installerApp ?: "Unknown")
        }
    }
}