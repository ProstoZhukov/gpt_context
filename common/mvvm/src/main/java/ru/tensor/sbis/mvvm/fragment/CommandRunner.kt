package ru.tensor.sbis.mvvm.fragment

import androidx.fragment.app.Fragment

import ru.tensor.sbis.mvvm.utils.BaseCommandRunner

/**
 * Исполнитель [ru.tensor.sbis.mvvm.utils.BaseCommand], использующий [Fragment]
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
open class CommandRunner : BaseCommandRunner<Fragment>()