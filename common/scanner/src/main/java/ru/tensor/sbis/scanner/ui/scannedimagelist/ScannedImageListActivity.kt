package ru.tensor.sbis.scanner.ui.scannedimagelist

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.base_components.AloneFragmentContainerActivity
import ru.tensor.sbis.scanner.ui.DocumentScannerContract
import ru.tensor.sbis.scanner.ui.scannedimagelist.ScannedImageListFragment.Companion.newInstance

/**
 * @author sa.nikitin
 */
internal class ScannedImageListActivity : AloneFragmentContainerActivity() {
    override fun createFragment(): Fragment {
        return newInstance()
    }

    companion object {
        @JvmStatic
        fun getActivityIntent(parent: Activity, requestCode: String) =
            Intent(parent, ScannedImageListActivity::class.java).apply {
                putExtra(DocumentScannerContract.EXTRA_REQUEST_CODE_KEY, requestCode)
            }
    }
}