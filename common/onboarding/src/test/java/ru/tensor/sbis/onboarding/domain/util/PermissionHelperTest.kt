package ru.tensor.sbis.onboarding.domain.util

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ANSWER_PHONE_CALLS
import android.content.pm.PackageManager
import org.mockito.kotlin.*
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.onboarding.contract.providers.content.*
import ru.tensor.sbis.onboarding.domain.OnboardingRepository
import ru.tensor.sbis.onboarding.ui.utils.RequestPermissionDelegate

class PermissionHelperTest {

    private val testFeaturePageUuid = "uuid"
    private val mockRepo = mock<OnboardingRepository> {
        on { findPageSafely(testFeaturePageUuid) } doAnswer { testFeaturePage }
    }
    private val mockRequestPermissionDelegate = mock<RequestPermissionDelegate> {
        on { requestPermissions(anyOrNull(), anyOrNull()) } doAnswer { invocationOnMock ->
            testRequestCode = invocationOnMock.arguments.last() as Int
            Unit
        }
    }

    private lateinit var helper: PermissionHelper

    private lateinit var testFeaturePage: FeaturePage
    private lateinit var testPermissions: SystemPermissions
    private var testRequestCode: Int = 0
    private lateinit var testCustomActionExecute: (postExecute: (Boolean) -> Unit) -> Unit
    private lateinit var testOnResultAction: (Boolean) -> Unit

    @Before
    fun setUp() {
        helper = PermissionHelper(
            repository = mockRepo,
            permissionDelegate = mockRequestPermissionDelegate
        )
        testPermissions = SystemPermissions(
            listOf(ACCESS_FINE_LOCATION, ANSWER_PHONE_CALLS)
        )
        testCustomActionExecute = { postAction ->
            postAction(true)
        }
        testOnResultAction = spy {}
        testFeaturePage = FeaturePage(
            description = Description.EMPTY,
            image = Image.EMPTY,
            button = Button.EMPTY,
            style = Style.EMPTY,
            permissions = testPermissions,
            action = CustomAction(execute = spy(testCustomActionExecute))
        )
    }

    @Test
    fun `On check unresolved permissions inspect permissions of certain page`() {
        helper.hasUnresolvedPermissions(testFeaturePageUuid)

        verify(mockRepo, atLeastOnce()).findPageSafely(testFeaturePageUuid)
    }

    @Test
    fun `Empty system permissions processed by default`() {
        assertTrue(SystemPermissions.EMPTY.isProcessed)
    }

    @Test
    fun `Informs about resolved permissions when there are not required system and custom permissions`() {
        testFeaturePage.permissions = SystemPermissions.EMPTY
        testFeaturePage.action = CustomAction.EMPTY

        assertFalse(helper.hasUnresolvedPermissions(testFeaturePageUuid))
    }

    @Test
    fun `Informs about unresolved permissions when there are required system permissions at least`() {
        testFeaturePage.action = CustomAction.EMPTY

        assertTrue(helper.hasUnresolvedPermissions(testFeaturePageUuid))
    }

    @Test
    fun `Informs about unresolved permissions when there is required custom action at least`() {
        testFeaturePage.action = CustomAction.EMPTY

        assertTrue(helper.hasUnresolvedPermissions(testFeaturePageUuid))
    }

    @Test
    fun `Informs about resolved permissions only when system and custom ones have marked processed`() {
        assertTrue(helper.hasUnresolvedPermissions(testFeaturePageUuid))

        testFeaturePage.permissions.isProcessed = true
        assertTrue(helper.hasUnresolvedPermissions(testFeaturePageUuid))

        testFeaturePage.action.processed = true
        assertFalse(helper.hasUnresolvedPermissions(testFeaturePageUuid))
    }

    @Test
    fun `On request permission use custom action and mark it as used`() {
        helper.askPermissionsAndAction(testFeaturePageUuid) {}

        assertTrue(testFeaturePage.action.processed)
        verify(testFeaturePage.action.execute).invoke(anyOrNull())
    }

    @Test
    fun `On requested system permissions, ask them directly through delegate`() {
        testFeaturePage.action.processed = true
        helper.askPermissionsAndAction(testFeaturePageUuid) {}

        verify(mockRequestPermissionDelegate).requestPermissions(
            eq(testPermissions.values),
            anyOrNull()
        )
    }

    @Test
    fun `When requested system and custom permissions, ask systems after custom`() {
        helper.askPermissionsAndAction(testFeaturePageUuid) {}

        val inOrder = inOrder(mockRequestPermissionDelegate, testFeaturePage.action.execute)
        inOrder.verify(testFeaturePage.action.execute).invoke(anyOrNull())
        inOrder.verify(mockRequestPermissionDelegate).requestPermissions(
            eq(testPermissions.values),
            anyOrNull()
        )
    }

    @Test
    fun `On have processed system permissions call result action and mark their as processed`() {
        testFeaturePage.action.processed = true
        helper.askPermissionsAndAction(testFeaturePageUuid, testOnResultAction)

        helper.onRequestPermissionsResult(
            testRequestCode,
            intArrayOf(PackageManager.PERMISSION_GRANTED)
        )

        assertTrue(testFeaturePage.permissions.isProcessed)
        verify(testOnResultAction).invoke(true)
    }

    @Test
    fun `On have not processed system permissions do not call result action but mark their as processed`() {
        testFeaturePage.action.processed = true
        helper.askPermissionsAndAction(testFeaturePageUuid, testOnResultAction)

        helper.onRequestPermissionsResult(
            testRequestCode,
            intArrayOf(PackageManager.PERMISSION_DENIED)
        )

        assertTrue(testFeaturePage.permissions.isProcessed)
        verify(testOnResultAction).invoke(false)
    }

    @Test
    fun `Perform result action tied to specific permission in set`() {
        testFeaturePage.action = CustomAction.EMPTY

        helper.askPermissionsAndAction(testFeaturePageUuid, testOnResultAction)
        val firstResultCode = testRequestCode

        testFeaturePage = FeaturePage(
            description = Description.EMPTY,
            image = Image.EMPTY,
            button = Button.EMPTY,
            style = Style.EMPTY,
            permissions = SystemPermissions(listOf(ACCESS_FINE_LOCATION))
        )
        val nextOnResultAction = spy<(Boolean) -> Unit> {}
        helper.askPermissionsAndAction(testFeaturePageUuid, nextOnResultAction)

        helper.onRequestPermissionsResult(
            testRequestCode,
            intArrayOf(PackageManager.PERMISSION_GRANTED)
        )
        verify(nextOnResultAction).invoke(true)
        verify(testOnResultAction, never()).invoke(any())

        helper.onRequestPermissionsResult(
            firstResultCode,
            intArrayOf(PackageManager.PERMISSION_DENIED)
        )
        verify(nextOnResultAction, only()).invoke(any())
        verify(testOnResultAction).invoke(false)
    }

    @Test
    fun `When there are not required permissions at all it calls result action with false`() {
        testFeaturePage.action.processed = true
        testFeaturePage.permissions.isProcessed = true

        helper.askPermissionsAndAction(testFeaturePageUuid, testOnResultAction)

        verify(testOnResultAction).invoke(false)
    }

    @Test
    fun `Does not double check system permissions`() {
        helper.askPermissionsAndAction(testFeaturePageUuid) {}
        helper.askPermissionsAndAction(testFeaturePageUuid) {}
        helper.askPermissionsAndAction(testFeaturePageUuid) {}

        verify(mockRequestPermissionDelegate, only()).requestPermissions(
            anyOrNull(),
            anyOrNull()
        )
    }
}