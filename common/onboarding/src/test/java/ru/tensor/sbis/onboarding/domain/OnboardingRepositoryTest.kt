package ru.tensor.sbis.onboarding.domain

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import io.reactivex.internal.schedulers.TrampolineScheduler
import io.reactivex.observers.TestObserver
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.verification_decl.permission.PermissionScope
import ru.tensor.sbis.onboarding.contract.providers.OnboardingProvider
import ru.tensor.sbis.onboarding.contract.providers.content.CustomAction
import ru.tensor.sbis.onboarding.contract.providers.content.FeaturePage
import ru.tensor.sbis.onboarding.contract.providers.content.Onboarding
import ru.tensor.sbis.onboarding.domain.TestPermissionScope.TEST_1
import ru.tensor.sbis.onboarding.domain.TestPermissionScope.TEST_2
import ru.tensor.sbis.onboarding.ui.utils.OnboardingProviderMediator

class OnboardingRepositoryTest {

    private val testScheduler = TrampolineScheduler.instance()

    private var provider = object : OnboardingProvider {
        override fun getOnboardingContent() = TestOnboarding.onboarding
    }
    private val mediator: OnboardingProviderMediator = mock {
        on { getActiveProvider() } doReturn provider
    }
    private var repository = OnboardingRepository(mediator, testScheduler, testScheduler)
    private lateinit var testSubscriber: TestObserver<Onboarding>

    @Before
    fun setUp() {
        testSubscriber = repository
            .observe()
            .test()
    }

    @Test
    fun `correctly subscribed onboarding repository on observe`() {
        testSubscriber.assertNoErrors()
        testSubscriber.assertSubscribed()
    }

    @Test
    fun `on observe onboarding repository emits content once`() {
        testSubscriber.assertValueCount(1)
        testSubscriber.assertNotComplete()
    }

    @Test
    fun `onboarding repository has cached value after on observe`() {
        testSubscriber.assertValue(repository.getCachedContent())
    }

    @Test
    fun `each page of onboarding repository has identity`() {
        repository.getCachedContent().pages.apply {
            assertTrue(all { it.uuid.isNotEmpty() })
            assertEquals(distinctBy { it.uuid }.size, size)
        }
    }

    @Test
    fun `correct header info`() {
        repository.getCachedContent().header
            .apply {
                assertEquals(textResId, TestOnboarding.headerTextId)
                assertEquals(imageResId, TestOnboarding.headerLogoId)
            }
    }

    @Test
    fun `Correct count page`() {
        assertEquals(repository.getCachedContent().pages.size, 4)
    }

    @Test
    fun `Correct result on search of feature page`() {
        val page = TestOnboarding.getFinalPage()
        assertEquals(repository.findDeclaredPage(page.uuid), page)
    }

    @Test
    fun `Correct info on first page`() {
        val page = repository.getCachedContent().pages.first() as FeaturePage
        assertEquals(page.image.imageResId, TestOnboarding.firstPageImageId)
        assertEquals(page.description.textResId, TestOnboarding.firstPageDescId)
    }

    @Test
    fun `Returned final page is correct type`() {
        val uuid = TestOnboarding.getFinalPage().uuid
        val finalPage = repository.findDeclaredPage(uuid)
        assertTrue(finalPage is FeaturePage)
    }

    @Test
    fun `Returned final page has correct custom action`() {
        val uuid = TestOnboarding.getFinalPage().uuid
        val finalPage = repository.findDeclaredPage(uuid) as FeaturePage
        finalPage.action.apply {
            assertNotEquals(this, CustomAction.EMPTY)
            assertEquals(execute, TestOnboarding.customAction)
        }
    }

    @Test
    fun `Returned final page has correct system permissions`() {
        val uuid = TestOnboarding.getFinalPage().uuid
        val finalPage = repository.findDeclaredPage(uuid) as FeaturePage
        assertTrue(finalPage.permissions.values.size == 2)
        assertTrue(
            finalPage.permissions.values.containsAll(
                listOf(
                    TestOnboarding.systemPermissionsName1,
                    TestOnboarding.systemPermissionsName2
                )
            )
        )
    }

    @Test
    fun `Repository has permission pages`() {
        assertTrue(repository.hasStubPages())
    }

    @Test
    fun `Repository has correct count of permission pages`() {
        assertEquals(repository.getStubPages().size, 1)
    }

    @Test
    fun `Repository returns corrected permission page`() {
        TestOnboarding.getStubPage()
            .apply {
                assertEquals(image.imageResId, TestOnboarding.noPermissionPageImageId)
                assertEquals(description.textResId, TestOnboarding.noPermissionPageDescId)
                assertThat(permissionScopes, equalTo(listOf<PermissionScope>(TEST_1, TEST_2)))
            }
    }

    @Test
    fun `Consistent result when searching for a stub page`() {
        val page = TestOnboarding.getStubPage()
        assertEquals(repository.findPageSafely(page.uuid), page)
    }
}