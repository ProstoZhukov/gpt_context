package ru.tensor.sbis.design.folders.view.common

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.params
import ru.tensor.sbis.design.folders.data.model.AdditionalCommandType.CANCEL_SHARING
import ru.tensor.sbis.design.folders.data.model.AdditionalCommandType.DEFAULT
import ru.tensor.sbis.design.folders.data.model.FolderButton
import ru.tensor.sbis.design.folders.data.model.FolderItem
import ru.tensor.sbis.design.folders.data.model.FolderType.SHARED
import ru.tensor.sbis.design.folders.data.model.MoreButton
import ru.tensor.sbis.design.folders.test_utils.command
import ru.tensor.sbis.design.folders.test_utils.folder
import ru.tensor.sbis.design.folders.data.model.FolderType.DEFAULT as DEFAULT_FOLDER

/**
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class FolderCompactDiffCallbackTest {

    private val diffCallback = FolderCompactDiffCallback()

    @Test
    @Parameters(method = "paramsForAreItemsTheSame")
    fun `areItemsTheSame() test`(old: FolderItem, new: FolderItem, expected: Boolean) {
        assertEquals(expected, diffCallback.areItemsTheSame(old, new))
    }

    @Suppress("unused")
    private fun paramsForAreItemsTheSame() = params {
        add(FolderButton, FolderButton, true)
        add(MoreButton, MoreButton, true)

        add(command(), command(), true)
        add(command(title = "1"), command(title = "2"), true)

        add(folder(), folder(), true)
        add(folder(id = "1"), folder(id = "1"), true)
        add(folder(id = "1", title = "t"), folder(id = "1", "ttt"), true)
        add(folder(id = "1"), folder(id = "2"), false)
        add(folder(id = "333", depthLevel = 12), folder(id = "712", depthLevel = 12), false)

        add(MoreButton, FolderButton, false)
        add(folder(), command(), false)
        add(MoreButton, command(), false)
        add(FolderButton, folder(), false)
    }

    @Test
    @Parameters(method = "paramsForAreContentsTheSame")
    fun `areContentsTheSame() test`(old: FolderItem, new: FolderItem, expected: Boolean) {
        assertEquals(expected, diffCallback.areContentsTheSame(old, new))
    }

    @Suppress("unused")
    private fun paramsForAreContentsTheSame() = params {
        add(FolderButton, FolderButton, true)
        add(MoreButton, MoreButton, true)

        add(command(), command(), true)
        add(command("t", DEFAULT), command("t", DEFAULT), true)
        add(command("omg", DEFAULT), command("no", DEFAULT), false)
        add(command("t", DEFAULT), command("t", CANCEL_SHARING), false)

        add(folder(), folder(), true)
        add(folder(id = "12"), folder(id = "44"), true)
        add(
            folder(
                id = "77",
                title = "title1",
                type = DEFAULT_FOLDER,
                depthLevel = 12,
                totalContentCount = 66,
                unreadContentCount = 6,
            ),
            folder(
                id = "778",
                title = "title1",
                type = DEFAULT_FOLDER,
                depthLevel = 12,
                totalContentCount = 66,
                unreadContentCount = 6,
            ),
            true
        )
        add(
            folder(
                id = "77",
                title = "title 18",
                type = DEFAULT_FOLDER,
                depthLevel = 12,
                totalContentCount = 66,
                unreadContentCount = 6,
            ),
            folder(
                id = "778",
                title = "title1",
                type = DEFAULT_FOLDER,
                depthLevel = 12,
                totalContentCount = 66,
                unreadContentCount = 6,
            ),
            false
        )
        add(
            folder(
                id = "77",
                title = "title1",
                type = DEFAULT_FOLDER,
                depthLevel = 12,
                totalContentCount = 66,
                unreadContentCount = 6,
            ),
            folder(
                id = "778",
                title = "title1",
                type = SHARED,
                depthLevel = 12,
                totalContentCount = 66,
                unreadContentCount = 6,
            ),
            false
        )
        add(
            folder(
                id = "77",
                title = "title1",
                type = DEFAULT_FOLDER,
                depthLevel = 12,
                totalContentCount = 66,
                unreadContentCount = 6,
            ),
            folder(
                id = "778",
                title = "title1",
                type = DEFAULT_FOLDER,
                depthLevel = 1,
                totalContentCount = 66,
                unreadContentCount = 6,
            ),
            false
        )
        add(
            folder(
                id = "77",
                title = "title1",
                type = DEFAULT_FOLDER,
                depthLevel = 12,
                totalContentCount = 66,
                unreadContentCount = 6,
            ),
            folder(
                id = "778",
                title = "title1",
                type = DEFAULT_FOLDER,
                depthLevel = 12,
                totalContentCount = 7778,
                unreadContentCount = 6,
            ),
            false
        )
        add(
            folder(
                id = "77",
                title = "title1",
                type = DEFAULT_FOLDER,
                depthLevel = 12,
                totalContentCount = 66,
                unreadContentCount = 8888,
            ),
            folder(
                id = "778",
                title = "title1",
                type = DEFAULT_FOLDER,
                depthLevel = 12,
                totalContentCount = 66,
                unreadContentCount = 6,
            ),
            false
        )

        add(MoreButton, FolderButton, false)
        add(folder(), command(), false)
        add(MoreButton, command(), false)
        add(FolderButton, folder(), false)
    }
}
