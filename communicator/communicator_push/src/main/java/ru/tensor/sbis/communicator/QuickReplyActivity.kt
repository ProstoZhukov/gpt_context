package ru.tensor.sbis.communicator

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.util.ContextReplacer.replace
import ru.tensor.sbis.communicator.push.R
import ru.tensor.sbis.communicator.ui.quickreply.QuickReplyFragment
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard

/** @SelfDocumented
 */
class QuickReplyActivity : AppCompatActivity(), EntryPointGuard.LegacyEntryPoint {
    override fun attachBaseContext(newBase: Context) {
        EntryPointGuard.activityAssistant.interceptAttachBaseContextLegacy(this, replace(newBase)) {
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.communicator_activity_quick_reply)
        if (savedInstanceState == null) {
            val fragment: Fragment = QuickReplyFragment.newInstance(intent.extras)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.communicator_fragment_container, fragment)
                .commit()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}