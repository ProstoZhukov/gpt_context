package ru.tensor.sbis.appdesign.folders.data

import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.FolderType
import kotlin.random.Random

/**
 * @author ma.kolpakov
 */
object FoldersData {

    val threeOrLess = listOf(
        Folder(
            title = "Folder 1",
            id = "0",
            totalContentCount = 2,
            unreadContentCount = 0,
            depthLevel = 0,
            type = FolderType.DEFAULT,
        ),
        Folder(
            title = "Folder 2",
            id = "1",
            totalContentCount = 0,
            unreadContentCount = 7,
            depthLevel = 0,
            type = FolderType.EDITABLE,
        ),
        Folder(
            title = "Folder 3 with long name",
            id = "2",
            totalContentCount = 12,
            unreadContentCount = 8,
            depthLevel = 0,
            type = FolderType.EDITABLE,
        ),
    )

    val threeOrLessWithSubfolders = listOf(
        Folder(
            title = "Folder Test",
            id = "0",
            totalContentCount = 2,
            unreadContentCount = 6,
            depthLevel = 0,
            type = FolderType.DEFAULT,
        ),
        Folder(
            title = "Subfolder",
            id = "1",
            totalContentCount = 6,
            unreadContentCount = 7,
            depthLevel = 1,
            type = FolderType.SHARED,
        ),
        Folder(
            title = "Another Folder With Long Name",
            id = "2",
            totalContentCount = 6,
            unreadContentCount = 7,
            depthLevel = 0,
            type = FolderType.WITH_SETTINGS,
        ),
    )

    val moreThanThree = listOf(
        Folder(
            title = "Folder Test",
            id = "0",
            totalContentCount = 2,
            unreadContentCount = 6,
            depthLevel = 0,
            type = FolderType.DEFAULT,
        ),
        Folder(
            title = "Another Folder",
            id = "1",
            totalContentCount = 6,
            unreadContentCount = 7,
            depthLevel = 1,
            type = FolderType.DEFAULT,
        ),
        Folder(
            title = "One More Folder With Long Name",
            id = "2",
            totalContentCount = 1,
            unreadContentCount = 3,
            depthLevel = 2,
            type = FolderType.SHARED,
        ),
        Folder(
            title = "One More Folder With Long Name One More Folder With Long Name",
            id = "3",
            totalContentCount = 8,
            unreadContentCount = 4,
            depthLevel = 3,
            type = FolderType.WITH_SETTINGS,
        ),

        Folder(
            title = "One More Folder With Long Name One More Folder With Long Name",
            id = "4",
            totalContentCount = 33,
            unreadContentCount = 18,
            depthLevel = 0,
            type = FolderType.EDITABLE,
        ),
    )

    val manyFolder = listOf(
        Folder(
            title = "Big Folder",
            id = "0",
            totalContentCount = 12889,
            unreadContentCount = 790,
            depthLevel = 0,
            type = FolderType.WITH_SETTINGS,
        ),
        Folder(
            title = "Folder 2",
            id = "1",
            totalContentCount = 0,
            unreadContentCount = 7,
            depthLevel = 1,
            type = FolderType.SHARED,
        ),
        Folder(
            title = "Folder 3",
            id = "2",
            totalContentCount = 0,
            unreadContentCount = 8,
            depthLevel = 2,
            type = FolderType.DEFAULT,
        ),
        Folder(
            title = "Folder 4",
            id = "3",
            totalContentCount = 12,
            unreadContentCount = 0,
            depthLevel = 1,
            type = FolderType.DEFAULT,
        ),
        Folder(
            title = "Folder 3",
            id = "4",
            totalContentCount = 0,
            unreadContentCount = 0,
            depthLevel = 2,
            type = FolderType.DEFAULT,
        ),
        Folder(
            title = "Folder 3",
            id = "5",
            totalContentCount = 12,
            unreadContentCount = 8,
            depthLevel = 2,
            type = FolderType.DEFAULT,
        ),

        Folder(
            title = "Big Folder 2",
            id = "6",
            totalContentCount = 2,
            unreadContentCount = 0,
            depthLevel = 0,
            type = FolderType.WITH_SETTINGS,
        ),
        Folder(
            title = "Folder 2",
            id = "7",
            totalContentCount = 888,
            unreadContentCount = 1288,
            depthLevel = 1,
            type = FolderType.SHARED,
        ),
        Folder(
            title = "Folder 3",
            id = "8",
            totalContentCount = 888,
            unreadContentCount = 0,
            depthLevel = 2,
            type = FolderType.DEFAULT,
        ),
        Folder(
            title = "Folder 4",
            id = "9",
            totalContentCount = 999,
            unreadContentCount = 0,
            depthLevel = 3,
            type = FolderType.DEFAULT,
        ),
        Folder(
            title = "Folder 3",
            id = "10",
            totalContentCount = 655588,
            unreadContentCount = 8999,
            depthLevel = 4,
            type = FolderType.DEFAULT,
        ),
        Folder(
            title = "Folder 3",
            id = "11",
            totalContentCount = 12,
            unreadContentCount = 8,
            depthLevel = 2,
            type = FolderType.DEFAULT,
        ),
    )

    val moreThanTwenty = mutableListOf<Folder>().apply {
        val foldersCount = 30

        repeat(foldersCount) {

            val folderType = when (Random.nextInt(0, 4)) {
                0 -> FolderType.DEFAULT
                1 -> FolderType.WITH_SETTINGS
                2 -> FolderType.SHARED
                else -> FolderType.EDITABLE
            }

            val totalCount = Random.nextInt(2, 2_000)
            val unreadCount = totalCount / Random.nextInt(2, 5)
            val id = it + 1

            add(
                Folder(
                    title = "Folder $id",
                    id = id.toString(),
                    totalContentCount = totalCount,
                    unreadContentCount = unreadCount,
                    depthLevel = 0,
                    type = folderType,
                )
            )
        }
    }
}
