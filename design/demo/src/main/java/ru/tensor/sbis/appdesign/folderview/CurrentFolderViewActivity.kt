package ru.tensor.sbis.appdesign.folderview

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.databinding.ActivityCurrentFolderViewBinding
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.model.BreadCrumb

private const val CURRENT_FOLDER_ID_KEY = "CURRENT_FOLDER_ID_KEY"
private const val CONFIG_KEY = "CONFIG_KEY"

/**
 * @author us.bessonov
 */
class CurrentFolderViewActivity : AppCompatActivity() {

    private lateinit var currentFolder: Folder
    private lateinit var config: Config

    private val adapter = DemoFolderListAdapter()

    private lateinit var viewBinding: ActivityCurrentFolderViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCurrentFolderViewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.list.layoutManager = LinearLayoutManager(this)
        viewBinding.list.adapter = adapter
        adapter.setOnClickListener(::setCurrentFolder)

        viewBinding.breadCrumbs.setHomeIconClickListener {
            setCurrentFolder(getDemoHierarchyRoot())
        }
        viewBinding.breadCrumbs.setItemClickListener {
            setCurrentFolder(getFolderById(it.id))
        }

        viewBinding.currentFolder.setOnClickListener { goBack() }

        viewBinding.folderPath.setHomeIconClickListener {
            setCurrentFolder(getDemoHierarchyRoot())
        }
        viewBinding.folderPath.setItemClickListener {
            setCurrentFolder(getFolderById(it.id))
        }
        viewBinding.folderPath.setOnClickListener { goBack() }

        viewBinding.btnDisplayMode.setOnClickListener {
            val areViewsSeparated = !config.areViewsSeparated
            config.areViewsSeparated = areViewsSeparated
            config.isHomeButtonControlVisible = areViewsSeparated
            updateConfig()
        }
        viewBinding.btnHomeEnabled.setOnClickListener {
            config.isHomeButtonVisible = !config.isHomeButtonVisible
            updateConfig()
        }
        viewBinding.btnRandomEnabled.setOnClickListener {
            config.areRandomItemsEnabled = !config.areRandomItemsEnabled
            updateConfig()
        }
        viewBinding.btnReduceSpace.setOnClickListener {
            viewBinding.header.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginEnd += 1
            }
        }
        viewBinding.btnIncreaseSpace.setOnClickListener {
            viewBinding.header.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginEnd = (marginEnd - 1).coerceAtLeast(0)
            }
        }

        currentFolder = savedInstanceState?.getString(CURRENT_FOLDER_ID_KEY)
            ?.let { getFolderById(it) }
            ?: getDemoHierarchyRoot()
        config = savedInstanceState?.getParcelable(CONFIG_KEY)
            ?: Config()

        updateConfig()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(CURRENT_FOLDER_ID_KEY, currentFolder.id)
        outState.putParcelable(CONFIG_KEY, config)
        super.onSaveInstanceState(outState)
    }

    private fun setCurrentFolder(folder: Folder) {
        currentFolder = folder
        if (folder.children.isEmpty()) {
            folder.addRandomChildren()
        }

        adapter.setItems(folder.children.filter { config.areRandomItemsEnabled || !it.isGenerated })

        val isFolderVisible = folder.parent != null
        viewBinding.currentFolder.setTitle(folder.title)
        viewBinding.currentFolder.isVisible = isFolderVisible && config.areViewsSeparated

        val breadCrumbs = createBreadCrumbs(folder)
        val isHomeIconVisible = folder.parent?.parent != null
        viewBinding.breadCrumbs.setItems(breadCrumbs)
        viewBinding.breadCrumbsContainer.isVisible = isHomeIconVisible && config.areViewsSeparated

        viewBinding.folderPath.setTitle(folder.title)
        viewBinding.folderPath.isVisible = isFolderVisible && !config.areViewsSeparated
        viewBinding.folderPath.setHomeIconVisible(isHomeIconVisible)
        viewBinding.folderPath.setItems(breadCrumbs)
    }

    private fun goBack() {
        currentFolder.parent?.let(::setCurrentFolder)
    }

    private fun createBreadCrumbs(folder: Folder): List<BreadCrumb> {
        val list = mutableListOf<BreadCrumb>()

        var p: Folder = folder

        while (true) {
            p.parent?.parent
                ?: return list.reversed()

            val parent = p.parent
                ?: return list.reversed()

            list.add(BreadCrumb(parent.title, parent.id))
            p = parent
        }
    }

    private fun updateConfig() = with(config) {
        viewBinding.breadCrumbs.setHomeIconVisible(isHomeButtonVisible)
        setCurrentFolder(currentFolder)

        viewBinding.btnHomeEnabled.isVisible = isHomeButtonControlVisible

        viewBinding.btnHomeEnabled.backgroundTintList = getButtonTint(config.isHomeButtonVisible)
        viewBinding.btnRandomEnabled.backgroundTintList = getButtonTint(config.areRandomItemsEnabled)

        viewBinding.btnDisplayMode.setText(if (areViewsSeparated) R.string.btn_combined_text else R.string.btn_separated_text)
    }

    private fun getButtonTint(isEnabled: Boolean): ColorStateList {
        return ColorStateList.valueOf(
            ContextCompat.getColor(
                this,
                if (isEnabled) R.color.palette_color_green1 else R.color.palette_color_gray4
            )
        )
    }
}

@Parcelize
private data class Config(
    var isHomeButtonVisible: Boolean = true,
    var isHomeButtonControlVisible: Boolean = true,
    var areRandomItemsEnabled: Boolean = false,
    var areViewsSeparated: Boolean = true
) : Parcelable
