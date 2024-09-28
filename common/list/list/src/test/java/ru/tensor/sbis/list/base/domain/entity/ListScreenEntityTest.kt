package ru.tensor.sbis.list.base.domain.entity

import org.junit.Assert.assertFalse
import org.junit.Test
import ru.tensor.sbis.list.view.utils.ListData

class ListScreenEntityTest {

    @Test
    fun default() {
        val entity = object : ListScreenEntity {
            override fun toListData(): ListData {
                null!!
            }

            override fun cleanPagesData() {
            }

            override fun increasePage() {

            }

            override fun decreasePage() {
            }
        }

        assertFalse(entity.isStub())
        assertFalse(entity.hasPrevious())
        assertFalse(entity.hasNext())
    }
}