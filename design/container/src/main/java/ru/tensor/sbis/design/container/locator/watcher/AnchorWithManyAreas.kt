package ru.tensor.sbis.design.container.locator.watcher

import android.graphics.Rect

interface AnchorWithManyAreas {
    fun getAreas(): List<Area>
}

data class Area(val rect: Rect, val cornerRadius: Float)