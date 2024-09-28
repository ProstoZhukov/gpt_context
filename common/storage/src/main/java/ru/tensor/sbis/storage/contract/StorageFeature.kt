package ru.tensor.sbis.storage.contract

import android.content.Context
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.storage.external.SbisExternalStorage
import ru.tensor.sbis.storage.internal.SbisInternalStorage

/**
 * Стандартная имплементация [ExternalStorageProvider] и [InternalStorageProvider]
 *
 * @param context
 * @param loginInterfaceProvider поставщик интерфейса авторизации
 * 
 */
class StorageFeature(
    context: Context,
    loginInterfaceProvider: LoginInterface.Provider?
) : ExternalStorageProvider, InternalStorageProvider {

    override val externalStorage: SbisExternalStorage by lazy {
        SbisExternalStorage(context, loginInterfaceProvider)
    }

    override val internalStorage: SbisInternalStorage by lazy {
        SbisInternalStorage(context, loginInterfaceProvider)
    }

}