package ru.tensor.sbis.storage.external

import android.content.Context
import android.os.Environment
import androidx.annotation.WorkerThread
import ru.tensor.sbis.verification_decl.account.UserAccount
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.storage.R
import ru.tensor.sbis.storage.SbisStorage
import java.io.File

private const val SPACE = " "

/**
 * Внешнее хранилище
 *
 * @param context
 * @param loginInterfaceProvider поставщик интерфейса авторизации
 *
 * @author sa.nikitin
 */
class SbisExternalStorage(
    context: Context,
    loginInterfaceProvider: LoginInterface.Provider?
) : SbisStorage(context, loginInterfaceProvider) {

    /** SelfDocumented */
    fun isExternalStorageExists(): Boolean =
        Environment.getExternalStorageState().startsWith(Environment.MEDIA_MOUNTED)

    /** SelfDocumented */
    fun isExternalStorageReadable(): Boolean =
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED_READ_ONLY

    /** SelfDocumented */
    fun isExternalStorageWritable(): Boolean =
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    /** SelfDocumented */
    fun getExternalStorageDir(): ExternalDir =
        createBaseExternalDir(Environment.getExternalStorageDirectory())

    /** SelfDocumented */
    fun getDownloadsDir(): ExternalDir =
        createBaseExternalDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))

    /** SelfDocumented */
    fun getPituresDir(): ExternalDir =
        createBaseExternalDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))

    private fun createBaseExternalDir(dir: File): ExternalDir {
        val writable = isExternalStorageWritable()
        return ExternalDir(
            dir,
            ExternalDirStatus(
                dir.exists(),
                writable || isExternalStorageReadable(),
                writable
            )
        )
    }

    /** SelfDocumented */
    @WorkerThread
    fun getSbisExternalDir(): ExternalDir = createSbisExternalDir(getExternalStorageDir())

    /** SelfDocumented */
    @WorkerThread
    fun getSbisImagesExternalDir(): ExternalDir = createSbisExternalDir(getPituresDir())

    @WorkerThread
    private fun createSbisExternalDir(parentExternalDir: ExternalDir): ExternalDir {
        val appName: String = context.applicationInfo.loadLabel(context.packageManager).toString()
        val sbisDirFile = File(parentExternalDir.dir, appName)
        val sbisDir = ExternalDir(
            sbisDirFile,
            externalDirStatus(sbisDirFile, parentExternalDir)
        )
        val userDir = userDirOrNull(sbisDir.dir)
        return if (userDir == null) {
            sbisDir
        } else {
            ExternalDir(
                userDir,
                externalDirStatus(userDir, sbisDir)
            )
        }
    }

    /** SelfDocumented */
    @WorkerThread
    fun getSbisDiskExternalDir(): ExternalDir =
        getSbisExternalSubDir(context.getString(R.string.storage_sbis_disk_external_dir_name))

    /** SelfDocumented */
    @WorkerThread
    fun getSbisScansExternalDir(): ExternalDir =
        getSbisImagesExternalSubDir(context.getString(R.string.storage_sbis_scans_external_dir_name))

    /** SelfDocumented */
    @WorkerThread
    fun getSbisExternalSubDir(subDirName: String): ExternalDir {
        val sbisExternalDir = getSbisExternalDir()
        val dir = File(sbisExternalDir.dir, subDirName)
        return ExternalDir(dir, externalDirStatus(dir, sbisExternalDir))
    }

    /** SelfDocumented */
    @WorkerThread
    fun getSbisImagesExternalSubDir(subDirName: String): ExternalDir {
        val sbisExternalDir = getSbisImagesExternalDir()
        val dir = File(sbisExternalDir.dir, subDirName)
        return ExternalDir(dir, externalDirStatus(dir, sbisExternalDir))
    }

    /** SelfDocumented */
    fun isExists(dir: File, parentDir: ExternalDir): Boolean =
        try {
            when {
                dir.exists() -> true
                parentDir.status.writable -> dir.mkdirs()
                else -> false
            }
        } catch (e: SecurityException) {
            false
        }

    private fun externalDirStatus(dir: File, parentDir: ExternalDir): ExternalDirStatus {
        val exists = isExists(dir, parentDir)
        val readable = try {
            exists && dir.canRead()
        } catch (e: SecurityException) {
            false
        }
        val writable = try {
            exists && dir.canWrite()
        } catch (e: SecurityException) {
            false
        }
        return ExternalDirStatus(exists, readable, writable)
    }

    override fun userDirName(currentUser: UserAccount): String =
        currentUser.userName + SPACE + currentUser.userSurname

}