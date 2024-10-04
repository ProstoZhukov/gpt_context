package ru.tensor.sbis.design

import org.junit.Test
import ru.tensor.sbis.design.documentlink.models.DocumentLinkModel

class DocumentLinkModelTest {
    @Test(expected = IllegalArgumentException::class)
    fun `Checks that model with invalid parameters can't be create`() {
        DocumentLinkModel("", "")
    }
}