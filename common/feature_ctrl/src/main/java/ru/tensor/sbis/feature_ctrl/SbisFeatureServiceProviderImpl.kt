package ru.tensor.sbis.feature_ctrl

import ru.tensor.sbis.verification_decl.login.LoginInterface

/**
 * Имплементация провайдера [SbisFeatureServiceImpl]
 */
class SbisFeatureServiceProviderImpl(loginInterface: LoginInterface) : SbisFeatureServiceProvider {
    override val sbisFeatureService: SbisFeatureService by lazy {
        SbisFeatureServiceImpl(loginInterface)
    }
}