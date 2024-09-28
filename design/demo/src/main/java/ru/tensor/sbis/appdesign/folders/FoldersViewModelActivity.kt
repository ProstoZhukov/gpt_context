package ru.tensor.sbis.appdesign.folders

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.appdesign.databinding.ActivityFoldersViewModelBinding
import ru.tensor.sbis.appdesign.extensions.showToast
import ru.tensor.sbis.appdesign.folders.data.DemoFoldersProvider
import ru.tensor.sbis.design.folders.support.FoldersViewModel
import ru.tensor.sbis.design.folders.support.extensions.attach
import ru.tensor.sbis.design.folders.support.listeners.FolderActionListener

/**
 * @author ma.kolpakov
 */
class FoldersViewModelActivity : AppCompatActivity() {

    private val viewModel: FoldersViewModel by viewModels {

        object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM =
                FoldersViewModel(DemoFoldersProvider()) as VM
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityFoldersViewModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionsListener = object : FolderActionListener {
            override fun opened(id: String) = showToast("Folder $id open")
            override fun closed() = showToast("Folder closed")
            override fun selected(id: String) = showToast("Folder $id selected")
            override fun additionalCommandClicked() = showToast("Additional command clicked")
        }

        viewModel.attach(
            this,
            foldersView = binding.folders,
            actionsListener = actionsListener,
        )

        binding.moveToFolderButton.setOnClickListener {
            viewModel.onFolderSelectionClicked()
        }
    }
}
