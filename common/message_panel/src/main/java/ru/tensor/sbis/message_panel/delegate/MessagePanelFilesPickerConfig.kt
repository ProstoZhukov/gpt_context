package ru.tensor.sbis.message_panel.delegate

data class MessagePanelFilesPickerConfig(
    val galleryEnabled: Boolean = true,
    val isOnlyImagesFromGallery: Boolean = false,
    val recentEnabled: Boolean = true,
    val favoritesEnabled: Boolean = true,
    val myDiskEnabled: Boolean = true,
    val companyDiskEnabled: Boolean = true,
    val bufferEnabled: Boolean = true,
    val scannerEnabled: Boolean = true,
    val tasksEnabled: Boolean = true
)