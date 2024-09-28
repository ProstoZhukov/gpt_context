package ru.tensor.sbis.user_activity_track_watcher.reporter

import ru.tensor.sbis.activity_fixation.generated.ActivityFixationController

/**
 * Реализация [ActivityReporter]
 *
 * @author kv.martyshenko
 */
internal class ControllerActivityReporter : ActivityReporter {
    private val controller by lazy {
        ActivityFixationController.instance()
    }

    override fun report(metaInfo: String): Boolean {
        try {
            controller.sendActivity()
        } catch (e: Throwable) {
            e.printStackTrace()
            // ignore errors
        }
        return true
    }

}

/**
 * Позволяет получить дефолтную реализацию [ActivityReporter]
 */
fun ActivityReporter.Companion.default(): ActivityReporter = ControllerActivityReporter()