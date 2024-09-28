package ru.tensor.sbis.folderspanel

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.junit.Assert.*
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

class UtilsTest {

    //Создаёт список папок, каждая следующая папка ссылается на предыдущую, первая имеет битую ссылку.
    private fun createFoldersList(): List<Folder> {
        val allFolders = ArrayList<Folder>()
        for (i in 0..9) {
            allFolders.add(mock {
                on { getStringUuid() } doReturn "uuid_$i"
                on { getParentStringUuid() } doReturn "uuid_" + (i - 1)
                on { toFolderViewModel(anyString()) } doReturn FolderViewModel(
                    "uuid_$i",
                    0,
                    "title",
                    0,
                    0,
                    false,
                    0,
                    showTotalCount = false,
                    swipeEnabled = false
                )
            })
        }
        return allFolders
    }

    @Test
    fun substractFolders() {
        val allFolders = createFoldersList()
        val sublistFolders = allFolders.subList(2, 4)
        //act
        val result = Utils.substractFolders(allFolders, sublistFolders)
        //verify
        assertEquals(8, result.size)
        val subFoldersUuids = sublistFolders.map { it.getStringUuid() }
        result.forEach { assertTrue(!subFoldersUuids.contains(it.getStringUuid())) }
    }

    @Test
    fun removeParentFolder() {
        val allFolders = createFoldersList()
        val removeFolderIndex = 5
        // Делаем -1 т.к. createFoldersList создаёт список папок в котором папки ссылаются на предыдущие.
        val checkFolderIndex = removeFolderIndex - 1
        //act
        val result = Utils.removeParentFolder(allFolders, allFolders[removeFolderIndex].getParentStringUuid())
        //verify
        assertEquals(allFolders.size - 1, result.size)
        assertNotEquals(allFolders[checkFolderIndex].getStringUuid(), result[checkFolderIndex].getStringUuid())
    }

    @Test
    fun toFolderViewModelList() {
        val allFolders = createFoldersList()
        //act
        val list = Utils.toFolderViewModelList(allFolders)
        //verify
        assertEquals(list.size, allFolders.size)
        for (i in allFolders.indices) assertEquals(allFolders[i].toFolderViewModel(""), list[i])
    }
}