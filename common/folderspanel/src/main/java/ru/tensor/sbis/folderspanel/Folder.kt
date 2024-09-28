package ru.tensor.sbis.folderspanel

/**
 * Интерфейс для элементов списка папок (ViewModel-ей)
 */
interface Folder {

    /**@SelfDocumented*/
    fun getStringUuid(): String

    /**@SelfDocumented*/
    fun getTotalCount(): Int

    /**@SelfDocumented*/
    fun getTitle(): String

    /**@SelfDocumented*/
    fun isParentRoot(): Boolean

    /**@SelfDocumented*/
    fun getParentStringUuid(): String

    /**@SelfDocumented*/
    fun getFolderLevel(): Int

    /**@SelfDocumented*/
    fun isNotEmpty(): Boolean

    /**@SelfDocumented*/
    fun toFolderViewModel(longestTotalCount: String): FolderViewModel

}