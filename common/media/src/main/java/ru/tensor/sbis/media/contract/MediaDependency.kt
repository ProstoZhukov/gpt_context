package ru.tensor.sbis.media.contract

import ru.tensor.sbis.attachments.loading.decl.presentation.DownloadFragmentFactory
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.storage.contract.InternalStorageProvider

/**
 * @author sa.nikitin
 */
interface MediaDependency :
    LoginInterface.Provider,
    InternalStorageProvider,
    ApiService.Provider,
    DownloadFragmentFactory