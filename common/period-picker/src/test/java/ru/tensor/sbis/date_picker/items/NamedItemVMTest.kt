package ru.tensor.sbis.date_picker.items

import android.graphics.Typeface
import org.junit.Test

import org.junit.Assert.*

class NamedItemVMTest {

    private val textNamedItemVM = TextNamedItemVM()

    @Test
    fun initially() {
       assertEquals(Typeface.NORMAL, textNamedItemVM.fontStyle.get())
    }

    @Test
    fun setSelected() {
        textNamedItemVM.setSelected()

        assertEquals(Typeface.BOLD, textNamedItemVM.fontStyle.get())
    }

    @Test
    fun setNoSelected() {
        textNamedItemVM.setSelected()

        textNamedItemVM.setNoSelected()

        assertEquals(Typeface.NORMAL, textNamedItemVM.fontStyle.get())
    }

    @Test
    fun getLabel() {
        assertEquals("label", TextNamedItemVM().label)
    }
}

private const val label = "label"

private class TextNamedItemVM: NamedItemVM(label)