package ru.tensor.sbis.manage_features.contract

import ru.tensor.sbis.verification_decl.login.CurrentAccount
import ru.tensor.sbis.verification_decl.login.CurrentPersonalAccount

/**
 * Зависимости модуля менеджера фичей
 */
interface ManageFeaturesDependency : CurrentAccount, CurrentPersonalAccount