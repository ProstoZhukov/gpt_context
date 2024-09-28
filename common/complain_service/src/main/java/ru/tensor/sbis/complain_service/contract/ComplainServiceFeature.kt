package ru.tensor.sbis.complain_service.contract

import ru.tensor.sbis.communication_decl.complain.ComplainDialogFragmentFeature
import ru.tensor.sbis.communication_decl.complain.ComplainService

/**
 * Фичи модуля сервиса "Пожаловаться".
 *
 * @author da.zhukov
 */
internal interface ComplainServiceFeature : ComplainService.Provider,
    ComplainDialogFragmentFeature