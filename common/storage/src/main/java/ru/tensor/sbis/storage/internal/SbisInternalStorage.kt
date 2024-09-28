package ru.tensor.sbis.storage.internal

import android.content.Context
import androidx.annotation.WorkerThread
import ru.tensor.sbis.verification_decl.account.UserAccount
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.storage.SbisStorage
import java.io.File

/**
 * Внутреннее хранилище
 *
 * @param context
 * @param loginInterfaceProvider поставщик интерфейса авторизации
 *
 * @author sa.nikitin
 */
class SbisInternalStorage(
    context: Context,
    loginInterfaceProvider: LoginInterface.Provider?
) : SbisStorage(context, loginInterfaceProvider) {

    /** SelfDocumented */
    fun filesDir(): File = context.filesDir

    /** SelfDocumented */
    fun cacheDir(): File = context.cacheDir

    /** SelfDocumented */
    @WorkerThread
    fun mediaCacheDir(): File = createCacheSubDir("media")

    /** SelfDocumented */
    @WorkerThread
    fun snapshotsDir(): File = createCacheSubDir("snapshots")

    @WorkerThread
    private fun createFilesSubDir(subDirName: String): File = createSubDir(filesDir(), subDirName)

    @WorkerThread
    private fun createCacheSubDir(subDirName: String): File = createSubDir(cacheDir(), subDirName)

    @WorkerThread
    private fun createSubDir(parentDir: File, subDirName: String): File =
        File(userDirOrNull(parentDir) ?: parentDir, subDirName).apply {
            mkdirs()
        }

    override fun userDirName(currentUser: UserAccount): String = currentUser.uuid.toString()

}