package ru.tensor.sbis.onboarding.domain

import org.mockito.kotlin.*
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.verification_decl.permission.LifecyclePermissionChecker
import ru.tensor.sbis.verification_decl.permission.PermissionInfo
import ru.tensor.sbis.verification_decl.permission.PermissionLevel
import ru.tensor.sbis.onboarding.domain.holder.PermissionFeatureHolder
import ru.tensor.sbis.onboarding.domain.interactor.HostInteractor

class HostInteractorTest {

    @get:Rule
    @Suppress("RedundantVisibilityModifier")
    public var rule = TrampolineSchedulerRule()

    private lateinit var interactor: HostInteractor
    private var permissions = listOf<PermissionInfo>()
    private val grantedPermissions = listOf(PermissionInfo(TestPermissionScope.TEST_1, PermissionLevel.ADMIN))
    private val dinedPermissions = listOf(PermissionInfo(TestPermissionScope.TEST_1, PermissionLevel.NONE))
    private val featureCount = TestOnboarding.getFeatureCount()

    private val mockRepository = mock<OnboardingRepository> {
        on { observe() } doReturn TestOnboarding.getOnboardingObservable()
        on { hasStubPages() } doReturn true
        on { getStubPages() } doAnswer { listOf(TestOnboarding.getStubPage()) }
    }
    private val mockChecker = mock<LifecyclePermissionChecker> {
        on { checkPermissionsNow(any(), any()) } doAnswer { permissions }
    }
    private val countSubject = spy(PublishSubject.create<Int>())

    @Test
    fun `checking of stub pages after creating`() {
        createInteractor()
        verify(mockChecker, only()).checkPermissionsNow(any(), any())
    }

    @Test
    fun `show stub page for none granted permission scope`() {
        permissions = dinedPermissions
        createInteractor()
        assertEquals(interactor.getPageCount(), featureCount + 1)
    }

    @Test
    fun `not show stub page for granted permission scope`() {
        permissions = grantedPermissions
        createInteractor()
        assertEquals(interactor.getPageCount(), featureCount)
    }

    @Test
    fun `notify observers about new page count`() {
        permissions = dinedPermissions
        createInteractor()
        verify(countSubject).onNext(featureCount + 1)
    }

    private fun createInteractor() {
        interactor = HostInteractor(
            mockRepository,
            PermissionFeatureHolder(mock {
                on { permissionChecker } doReturn mockChecker
            }),
            mock(),
            countSubject,
            mock(),
            mock()
        )
    }
}