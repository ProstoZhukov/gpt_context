package ru.tensor.sbis.mvvm.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity

import ru.tensor.sbis.mvvm.utils.BaseCommandRunner

/**
 * Исполнитель [ru.tensor.sbis.mvvm.utils.BaseCommand], использующий [Activity]
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
open class CommandRunner : BaseCommandRunner<AppCompatActivity>()