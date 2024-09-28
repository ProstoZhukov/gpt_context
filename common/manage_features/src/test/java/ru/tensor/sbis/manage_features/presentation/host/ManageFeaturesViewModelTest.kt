package ru.tensor.sbis.manage_features.presentation.host

import org.mockito.kotlin.*
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.manage_features.R
import ru.tensor.sbis.manage_features.data.DataSource
import ru.tensor.sbis.manage_features.data.GetValueInteractor
import ru.tensor.sbis.manage_features.presentation.ManageFeaturesViewModel

@RunWith(MockitoJUnitRunner::class)
class ManageFeaturesViewModelTest {

    private lateinit var viewModel: ManageFeaturesViewModel
    private val interactor = mock<GetValueInteractor> {
        on { getValueWithCheck(DataSource.DEFAULT_FEATURE_NAME, USER_ID, CLIENT_ID) } doReturn Single.just("abc")
    }

    @Before
    fun setup() {
        viewModel = ManageFeaturesViewModel(USER_ID, CLIENT_ID, NAME_OF_USER, NAME_OF_CLIENT, interactor)
        clearInvocations(interactor)
    }

    @Test
    fun `Given created viewModel without calls of any methods, then created, when properties must has values equals this test`() {
        //prepare
        val testObserver = viewModel.errors.test()
        //verify
        assertEquals(getIntField("userID"), USER_ID)
        assertEquals(getIntField("clientID"), CLIENT_ID)
        assertEquals(viewModel.userName.get(), NAME_OF_USER)
        assertEquals(viewModel.clientName.get(), NAME_OF_CLIENT)
        assertEquals(viewModel.featureName.get(), DataSource.DEFAULT_FEATURE_NAME)
        assert(viewModel.value.get()?.isEmpty() ?: true)
        assert(viewModel.state.get() == 0)
        testObserver.assertEmpty().dispose()
    }

    @Test
    fun `Given viewModel, then checkClickListener called, when interactor#getValueWithCheck called`() {
        //act
        viewModel.checkClickListener.onClick(mock())
        //verify
        verify(interactor).getValueWithCheck(DataSource.DEFAULT_FEATURE_NAME, USER_ID, CLIENT_ID)
    }

    @Test
    fun `Given viewModel, then checkClickListener called, when error thrown`() {
        //prepare
        viewModel.featureName.set(FEATURE_NAME)
        whenever(interactor.getValueWithCheck(FEATURE_NAME, USER_ID, CLIENT_ID)).thenReturn(
            Single.error(
                IllegalAccessException("abc")
            )
        )
        //act
        viewModel.checkClickListener.onClick(mock())
        //verify
        assertEquals(viewModel.value.get(), "")
        assertEquals(viewModel.state.get(), R.string.manage_features_state_disabled)
    }

    @Test
    fun `Given viewModel, then checkClickListener called, when 777 value returned`() {
        //prepare
        val value = "777"
        viewModel.featureName.set(FEATURE_NAME)
        whenever(interactor.getValueWithCheck(FEATURE_NAME, USER_ID, CLIENT_ID)).thenReturn(Single.just(value))
        //act
        viewModel.checkClickListener.onClick(mock())
        //verify
        assertEquals(viewModel.value.get(), value)
        assertEquals(viewModel.state.get(), R.string.manage_features_state_enabled)
    }

    /**
     * Получение данных через Kotlin Reflection API
     */
    private fun getIntField(fieldName: String): Int {
        val field = viewModel::class.java.getDeclaredField(fieldName)
        field.isAccessible = true
        return field.get(viewModel) as Int
    }

    companion object {
        const val USER_ID = 0
        const val CLIENT_ID = 0
        const val NAME_OF_USER = "123"
        const val NAME_OF_CLIENT = "456"
        const val FEATURE_NAME = "abc"
    }
}