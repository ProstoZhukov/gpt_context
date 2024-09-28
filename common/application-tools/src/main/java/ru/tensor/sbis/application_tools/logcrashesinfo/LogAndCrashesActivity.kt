package ru.tensor.sbis.application_tools.logcrashesinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.application_tools.R
import ru.tensor.sbis.application_tools.databinding.ApplicationToolsActivityLogAndCrashesInfoSettingsBinding
import ru.tensor.sbis.application_tools.logcrashesinfo.crashes.CrashInfoFragment
import ru.tensor.sbis.entrypoint_guard.activity.EntryPointActivity

/**
 * @author du.bykov
 *
 * Экран отображения данных о логах и крашах.
 */
class LogAndCrashesActivity : EntryPointActivity() {

    override fun onCreate(activity: AppCompatActivity, parent: FrameLayout, savedInstanceState: Bundle?) {
        LayoutInflater.from(activity).inflate(R.layout.application_tools_activity_log_and_crashes_info_settings, parent)
        val crashFileName = intent.getStringExtra(CRASH_ID)
        if (crashFileName != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.container_frame,
                    CrashInfoFragment.newInstance(crashFileName),
                    CrashInfoFragment::class.java.canonicalName
                )
                .commit()
        } else if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.container_frame,
                    LogAndCrashesFragment(),
                    LogAndCrashesFragment::class.java.canonicalName
                )
                .commit()
        }
    }

    companion object {
        const val CRASH_ID = "CRASH_ID"
    }
}