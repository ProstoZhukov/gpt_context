package ru.tensor.sbis.verification_decl.verification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Структура для получения данных на вызывающем фрагменте через FragmentResult API.
 * @property requestKey - ключ, по которому наблюдаем за получением результата
 * @property bundleKey - ключ, по которому получаем значение выполненной или невыполненной верификации
 *
 * @author ra.temnikov
 */
@Parcelize
class VerificationResultKeys(val requestKey: String, val bundleKey: String) : Parcelable