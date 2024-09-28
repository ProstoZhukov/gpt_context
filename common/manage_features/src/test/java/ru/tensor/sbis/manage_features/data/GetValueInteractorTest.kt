package ru.tensor.sbis.manage_features.data

import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule

@RunWith(MockitoJUnitRunner::class)
class GetValueInteractorTest {

    private lateinit var interactor: GetValueInteractor
    private val dataSource = mock<DataSource> {
        on { getManageFeature(any(), any(), any()) } doReturn DEFAULT_FEATURE_VALUE
    }

    @get:Rule
    val rule = TrampolineSchedulerRule()

    @Before
    fun setup() {
        interactor = GetValueInteractor(dataSource)
    }

    @Test
    fun `Given isManageFeaturesEnabled = false, then getValueWithCheck called, when thrown IllegalAccessException`() {
        val featureName = ""
        val userID = 0
        val clientID = 0
        //prepare
        whenever(dataSource.isManageFeaturesEnabled(featureName, userID, clientID)).thenReturn(false)
        //act
        val testObserver = interactor.getValueWithCheck(featureName, userID, clientID).test()
        //verify
        testObserver.assertError(IllegalAccessException::class.java).dispose()
    }

    @Test
    fun `Given isManageFeaturesEnabled = true, then getValueWithCheck called, when returned 777 code`() {
        val featureName = ""
        val userID = 0
        val clientID = 0
        //prepare
        whenever(dataSource.isManageFeaturesEnabled(featureName, userID, clientID)).thenReturn(true)
        //act
        val testObserver = interactor.getValueWithCheck(featureName, userID, clientID).test()
        //verify
        testObserver.assertValue(DEFAULT_FEATURE_VALUE).dispose()
    }

    @Test
    fun `Given isManageFeaturesEnabled = true, then getValueWithCheck called, when returned empty string code and thrown IllegalAccessException`() {
        val featureName = ""
        val userID = 0
        val clientID = 0
        //prepare
        whenever(dataSource.isManageFeaturesEnabled(featureName, userID, clientID)).thenReturn(true)
        whenever(dataSource.getManageFeature(featureName, userID, clientID)).thenReturn("")
        //act
        val testObserver = interactor.getValueWithCheck(featureName, userID, clientID).test()
        //verify
        testObserver.assertError(IllegalAccessException::class.java).dispose()
    }

    companion object {
        const val DEFAULT_FEATURE_VALUE = "777"
    }
}