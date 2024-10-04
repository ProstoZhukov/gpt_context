package ru.tensor.sbis.design.folders.support

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.ROOT_FOLDER_ID
import ru.tensor.sbis.design.folders.support.extensions.getFolder

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class FoldersViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val foldersSubject = PublishSubject.create<List<Folder>>()

    private val additionalCommandSubject = PublishSubject.create<AdditionalCommand>()

    @Mock
    private lateinit var foldersProvider: FoldersProvider

    private lateinit var vm: FoldersViewModel

    @Before
    fun setUp() {
        whenever(foldersProvider.getFolders()).thenReturn(foldersSubject)
        whenever(foldersProvider.getAdditionalCommand()).thenReturn(additionalCommandSubject)

        vm = FoldersViewModel(foldersProvider)
    }

    @Test
    fun `When view model created, then folders view compact by default`() {
        assertTrue(vm.isCompact.value!!)
    }

    @Test
    fun `When view model initialised, then folder list should be empty`() {
        val observer: Observer<List<Folder>> = mock()

        vm.collapsingFolders.observeForever(observer)

        verify(observer, only()).onChanged(emptyList())
    }

    @Test
    fun `When folder list contains root folder, then it should be filtered out for compact view`() {
        val rootFolder: Folder = mock { on { id } doReturn ROOT_FOLDER_ID }
        val folder: Folder = mock()
        val observer: Observer<List<Folder>> = mock()
        val captor = argumentCaptor<List<Folder>>()

        vm.collapsingFolders.observeForever(observer)
        foldersSubject.onNext(listOf(rootFolder, folder))

        verify(observer, times(2)).onChanged(captor.capture())
        assertEquals(listOf(folder), captor.lastValue)
    }

    @Test
    fun `When folder list contains root folder, then it should be visible in all folders view`() {
        val rootFolder: Folder = mock(lenient = true) {
            on { id } doReturn ROOT_FOLDER_ID
            on { canMove } doReturn true
        }
        val folder: Folder = mock {
            on { canMove } doReturn true
        }
        val observer: Observer<List<Folder>> = mock()
        val captor = argumentCaptor<List<Folder>>()

        vm.selectionFolders.observeForever(observer)
        foldersSubject.onNext(listOf(rootFolder, folder))

        verify(observer, times(2)).onChanged(captor.capture())
        // проверим только количество, моки не переживают копирования
        assertEquals(2, captor.lastValue.size)
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=2f5983cc-c4b9-4bc3-855c-ec96d407be17
    @Test
    fun `When folder list contains only root folder, then folder panel should be invisible`() {
        val rootFolder: Folder = mock { on { id } doReturn ROOT_FOLDER_ID }
        val observer: Observer<Boolean> = mock()

        vm.isVisible.observeForever(observer)
        foldersSubject.onNext(listOf(rootFolder))

        verify(observer, only()).onChanged(false)
    }

    @Test
    fun `When folders list contains only root folder, then data update listener should receive isEmpty event`() {
        val rootFolder: Folder = mock { on { id } doReturn ROOT_FOLDER_ID }
        val observer: Observer<Boolean> = mock()

        vm.dataUpdated.observeForever(observer)
        foldersSubject.onNext(listOf(rootFolder))

        verify(observer, atLeast(1)).onChanged(true)
    }

    @Test
    fun `When view model initialised, then data update listener should receive isEmpty event`() {
        val observer: Observer<Boolean> = mock()

        vm.dataUpdated.observeForever(observer)

        verify(observer, atLeast(1)).onChanged(true)
    }

    @Test
    fun `When all folders removed from list, then data update listener should receive isEmpty event`() {
        val rootFolder: Folder = mock { on { id } doReturn ROOT_FOLDER_ID }
        val folder: Folder = mock()
        val observer: Observer<Boolean> = mock()
        val captor = argumentCaptor<Boolean>()

        vm.dataUpdated.observeForever(observer)
        foldersSubject.onNext(listOf(rootFolder, folder))
        foldersSubject.onNext(listOf(rootFolder))

        verify(observer, atLeast(1)).onChanged(captor.capture())
        assertEquals(listOf(true, true, false, true), captor.allValues)
    }
    //endregion

    /**
     * Проверяет что при попытке вызова метода [getFolder], в который передается [Folder] с
     *  id [ROOT_FOLDER_ID], вернется нужная папка
     *
     * Fix https://online.sbis.ru/opendoc.html?guid=2c98657c-0b55-403f-8d68-4cb545fdc31f
     */
    @Test
    fun `Given view model, when call getFolder with root folder id, then we get this folder`() {
        val rootFolder: Folder = mock { on { id } doReturn ROOT_FOLDER_ID }
        vm.folders.observeForever(mock())
        // act
        foldersSubject.onNext(listOf(rootFolder))
        val verifyingFolder = vm.getFolder(ROOT_FOLDER_ID)
        // verify
        assertEquals(rootFolder, verifyingFolder)
    }
}