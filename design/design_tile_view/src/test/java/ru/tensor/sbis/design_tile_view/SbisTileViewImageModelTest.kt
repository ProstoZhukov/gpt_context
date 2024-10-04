package ru.tensor.sbis.design_tile_view

import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SbisTileViewImageModelTest {

    private val image: TileViewImage = mockk()

    private val alignment: SbisTileViewImageAlignment = mockk()

    private val shape: SbisTileViewImageShape = mockk()

    private lateinit var imageModel: SbisTileViewImageModel

    @Test
    fun `When no image passed to constructor, then images should be empty`() {
        imageModel = SbisTileViewImageModel(null, alignment, shape)

        assertEquals(emptyList<TileViewImage>(), imageModel.images)
    }

    @Test
    fun `When sole image passed to constructor, then it should be in images list`() {
        imageModel = SbisTileViewImageModel(image, alignment, shape)

        assertEquals(listOf(image), imageModel.images)
    }

    @Test
    fun `When multiple images passed to constructor, then all of them should be in images list`() {
        // фильтрации и ограничения по количеству нет
        val images = listOf(image, image, image, image, image)
        imageModel = SbisTileViewImageModel(images, alignment, shape)

        assertEquals(images, imageModel.images)
    }
}