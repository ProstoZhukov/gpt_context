package ru.tensor.sbis.appdesign.folders

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.appdesign.databinding.ActivityFoldersBinding
import ru.tensor.sbis.appdesign.extensions.showToast
import ru.tensor.sbis.appdesign.folders.data.FoldersData
import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.data.model.AdditionalCommandType
import ru.tensor.sbis.design.folders.data.model.FolderActionType

/**
 * @author ma.kolpakov
 */
class FoldersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityFoldersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionHandler = object : FolderActionHandler {
            override fun handleAction(actionType: FolderActionType, folderId: String) {
                showToast("$folderId ${actionType.name}")
            }
        }

        binding.apply {
            folders1.setFolders(FoldersData.threeOrLess)
            folders1.setActionHandler(actionHandler)

            folders2.setFolders(FoldersData.threeOrLessWithSubfolders)
            folders2.setActionHandler(actionHandler)

            folders3.setFolders(FoldersData.moreThanThree)
            folders3.setActionHandler(actionHandler)

            folders4.setFolders(FoldersData.threeOrLessWithSubfolders)
            folders4.setActionHandler(actionHandler)
            folders4.setAdditionalCommand(AdditionalCommand("Simple Command", AdditionalCommandType.DEFAULT))

            folders5.setFolders(FoldersData.threeOrLessWithSubfolders)
            folders5.setActionHandler(actionHandler)
            folders5.setAdditionalCommand(AdditionalCommand("Share Command", AdditionalCommandType.SHARE))

            folders6.setFolders(FoldersData.threeOrLessWithSubfolders)
            folders6.setActionHandler(actionHandler)
            folders6.setAdditionalCommand(
                AdditionalCommand(
                    "Cancel Sharing Command",
                    AdditionalCommandType.CANCEL_SHARING
                )
            )

            folders7.setFolders(FoldersData.manyFolder)
            folders7.setActionHandler(actionHandler)

            folders8.setFolders(FoldersData.moreThanTwenty)
            folders8.setActionHandler(actionHandler)
            folders8.onMoreClicked {
                showToast("More Clicked")
            }

            folders9.setActionHandler(actionHandler)
            folders9.setAdditionalCommand(AdditionalCommand("Only One Command", AdditionalCommandType.SHARE))
        }
    }
}
