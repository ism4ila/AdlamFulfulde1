package com.bekisma.adlamfulfulde.ads

import android.app.Activity
import com.bekisma.adlamfulfulde.BuildConfig
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@LooperMode(LooperMode.Mode.PAUSED)
class ConsentManagerTest {

    private lateinit var mockActivity: Activity
    private lateinit var mockConsentInformation: ConsentInformation
    private lateinit var mockConsentForm: ConsentForm
    private lateinit var mockFormError: FormError

    @Before
    fun setUp() {
        mockActivity = mockk(relaxed = true)
        mockConsentInformation = mockk(relaxed = true)
        mockConsentForm = mockk(relaxed = true)
        mockFormError = mockk(relaxed = true)
        
        // Mock UserMessagingPlatform static methods
        mockkStatic(UserMessagingPlatform::class)
        
        every { UserMessagingPlatform.getConsentInformation(any()) } returns mockConsentInformation
        every { mockConsentInformation.isConsentFormAvailable } returns true
        every { mockConsentInformation.consentStatus } returns ConsentInformation.ConsentStatus.OBTAINED
        every { mockConsentInformation.privacyOptionsRequirementStatus } returns ConsentInformation.PrivacyOptionsRequirementStatus.NOT_REQUIRED
        
        // Clear any previous state
        clearStaticMockk(ConsentManager::class)
    }

    @Test
    fun `initialize should return success when consent info update succeeds`() = runTest {
        every { 
            mockConsentInformation.requestConsentInfoUpdate(any(), any(), any(), any())
        } answers {
            val onSuccess = arg<() -> Unit>(2)
            onSuccess.invoke()
        }
        
        every { 
            UserMessagingPlatform.loadConsentForm(any(), any(), any())
        } answers {
            val onSuccess = arg<(ConsentForm) -> Unit>(1)
            onSuccess.invoke(mockConsentForm)
        }
        
        val result = ConsentManager.initialize(mockActivity)
        
        assert(result is ConsentManager.ConsentResult.Success)
    }

    @Test
    fun `initialize should return error when consent info update fails`() = runTest {
        val errorMessage = "Network error"
        every { mockFormError.message } returns errorMessage
        
        every { 
            mockConsentInformation.requestConsentInfoUpdate(any(), any(), any(), any())
        } answers {
            val onError = arg<(FormError) -> Unit>(3)
            onError.invoke(mockFormError)
        }
        
        val result = ConsentManager.initialize(mockActivity)
        
        assert(result is ConsentManager.ConsentResult.Error)
        assert((result as ConsentManager.ConsentResult.Error).message == errorMessage)
    }

    @Test
    fun `initialize should return success when no consent form available`() = runTest {
        every { mockConsentInformation.isConsentFormAvailable } returns false
        
        every { 
            mockConsentInformation.requestConsentInfoUpdate(any(), any(), any(), any())
        } answers {
            val onSuccess = arg<() -> Unit>(2)
            onSuccess.invoke()
        }
        
        val result = ConsentManager.initialize(mockActivity)
        
        assert(result is ConsentManager.ConsentResult.Success)
        assert(!(result as ConsentManager.ConsentResult.Success).consentShown)
    }

    @Test
    fun `initialize should show consent form when required`() = runTest {
        every { mockConsentInformation.consentStatus } returns ConsentInformation.ConsentStatus.REQUIRED
        
        every { 
            mockConsentInformation.requestConsentInfoUpdate(any(), any(), any(), any())
        } answers {
            val onSuccess = arg<() -> Unit>(2)
            onSuccess.invoke()
        }
        
        every { 
            UserMessagingPlatform.loadConsentForm(any(), any(), any())
        } answers {
            val onSuccess = arg<(ConsentForm) -> Unit>(1)
            onSuccess.invoke(mockConsentForm)
        }
        
        every { mockConsentForm.show(any(), any()) } answers {
            val onDismissed = arg<(FormError?) -> Unit>(1)
            onDismissed.invoke(null)
        }
        
        val result = ConsentManager.initialize(mockActivity)
        
        assert(result is ConsentManager.ConsentResult.Success)
        verify { mockConsentForm.show(mockActivity, any()) }
    }

    @Test
    fun `initialize should handle form load error`() = runTest {
        val errorMessage = "Form load error"
        every { mockFormError.message } returns errorMessage
        
        every { 
            mockConsentInformation.requestConsentInfoUpdate(any(), any(), any(), any())
        } answers {
            val onSuccess = arg<() -> Unit>(2)
            onSuccess.invoke()
        }
        
        every { 
            UserMessagingPlatform.loadConsentForm(any(), any(), any())
        } answers {
            val onError = arg<(FormError) -> Unit>(2)
            onError.invoke(mockFormError)
        }
        
        val result = ConsentManager.initialize(mockActivity)
        
        assert(result is ConsentManager.ConsentResult.Error)
        assert((result as ConsentManager.ConsentResult.Error).message == errorMessage)
    }

    @Test
    fun `initialize should handle form show error`() = runTest {
        val errorMessage = "Form show error"
        every { mockFormError.message } returns errorMessage
        every { mockConsentInformation.consentStatus } returns ConsentInformation.ConsentStatus.REQUIRED
        
        every { 
            mockConsentInformation.requestConsentInfoUpdate(any(), any(), any(), any())
        } answers {
            val onSuccess = arg<() -> Unit>(2)
            onSuccess.invoke()
        }
        
        every { 
            UserMessagingPlatform.loadConsentForm(any(), any(), any())
        } answers {
            val onSuccess = arg<(ConsentForm) -> Unit>(1)
            onSuccess.invoke(mockConsentForm)
        }
        
        every { mockConsentForm.show(any(), any()) } answers {
            val onDismissed = arg<(FormError?) -> Unit>(1)
            onDismissed.invoke(mockFormError)
        }
        
        val result = ConsentManager.initialize(mockActivity)
        
        assert(result is ConsentManager.ConsentResult.Error)
        assert((result as ConsentManager.ConsentResult.Error).message == errorMessage)
    }

    @Test
    fun `canRequestAds should return true when consent obtained`() {
        every { mockConsentInformation.consentStatus } returns ConsentInformation.ConsentStatus.OBTAINED
        
        // Initialize first to set up the consent information
        every { 
            mockConsentInformation.requestConsentInfoUpdate(any(), any(), any(), any())
        } answers {
            val onSuccess = arg<() -> Unit>(2)
            onSuccess.invoke()
        }
        
        every { 
            UserMessagingPlatform.loadConsentForm(any(), any(), any())
        } answers {
            val onSuccess = arg<(ConsentForm) -> Unit>(1)
            onSuccess.invoke(mockConsentForm)
        }
        
        runTest {
            ConsentManager.initialize(mockActivity)
        }
        
        val result = ConsentManager.canRequestAds()
        
        assert(result)
    }

    @Test
    fun `canRequestAds should return true when consent not required`() {
        every { mockConsentInformation.consentStatus } returns ConsentInformation.ConsentStatus.NOT_REQUIRED
        
        // Initialize first
        every { 
            mockConsentInformation.requestConsentInfoUpdate(any(), any(), any(), any())
        } answers {
            val onSuccess = arg<() -> Unit>(2)
            onSuccess.invoke()
        }
        
        every { 
            UserMessagingPlatform.loadConsentForm(any(), any(), any())
        } answers {
            val onSuccess = arg<(ConsentForm) -> Unit>(1)
            onSuccess.invoke(mockConsentForm)
        }
        
        runTest {
            ConsentManager.initialize(mockActivity)
        }
        
        val result = ConsentManager.canRequestAds()
        
        assert(result)
    }

    @Test
    fun `canRequestAds should return false when consent required but not obtained`() {
        every { mockConsentInformation.consentStatus } returns ConsentInformation.ConsentStatus.REQUIRED
        
        val result = ConsentManager.canRequestAds()
        
        assert(!result)
    }

    @Test
    fun `getConsentStatus should return current consent status`() {
        val status = ConsentInformation.ConsentStatus.OBTAINED
        every { mockConsentInformation.consentStatus } returns status
        
        // Initialize first
        every { 
            mockConsentInformation.requestConsentInfoUpdate(any(), any(), any(), any())
        } answers {
            val onSuccess = arg<() -> Unit>(2)
            onSuccess.invoke()
        }
        
        every { 
            UserMessagingPlatform.loadConsentForm(any(), any(), any())
        } answers {
            val onSuccess = arg<(ConsentForm) -> Unit>(1)
            onSuccess.invoke(mockConsentForm)
        }
        
        runTest {
            ConsentManager.initialize(mockActivity)
        }
        
        val result = ConsentManager.getConsentStatus()
        
        assert(result == status)
    }

    @Test
    fun `isPrivacyOptionsRequired should return correct status`() {
        every { mockConsentInformation.privacyOptionsRequirementStatus } returns 
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        
        // Initialize first
        every { 
            mockConsentInformation.requestConsentInfoUpdate(any(), any(), any(), any())
        } answers {
            val onSuccess = arg<() -> Unit>(2)
            onSuccess.invoke()
        }
        
        every { 
            UserMessagingPlatform.loadConsentForm(any(), any(), any())
        } answers {
            val onSuccess = arg<(ConsentForm) -> Unit>(1)
            onSuccess.invoke(mockConsentForm)
        }
        
        runTest {
            ConsentManager.initialize(mockActivity)
        }
        
        val result = ConsentManager.isPrivacyOptionsRequired()
        
        assert(result)
    }

    @Test
    fun `showPrivacyOptionsForm should show form successfully`() {
        every { 
            UserMessagingPlatform.showPrivacyOptionsForm(any(), any())
        } answers {
            val callback = arg<(FormError?) -> Unit>(1)
            callback.invoke(null)
        }
        
        var callbackResult: ConsentManager.ConsentResult? = null
        ConsentManager.showPrivacyOptionsForm(mockActivity) { result ->
            callbackResult = result
        }
        
        assert(callbackResult is ConsentManager.ConsentResult.Success)
        verify { UserMessagingPlatform.showPrivacyOptionsForm(mockActivity, any()) }
    }

    @Test
    fun `showPrivacyOptionsForm should handle error`() {
        val errorMessage = "Privacy form error"
        every { mockFormError.message } returns errorMessage
        
        every { 
            UserMessagingPlatform.showPrivacyOptionsForm(any(), any())
        } answers {
            val callback = arg<(FormError?) -> Unit>(1)
            callback.invoke(mockFormError)
        }
        
        var callbackResult: ConsentManager.ConsentResult? = null
        ConsentManager.showPrivacyOptionsForm(mockActivity) { result ->
            callbackResult = result
        }
        
        assert(callbackResult is ConsentManager.ConsentResult.Error)
        assert((callbackResult as ConsentManager.ConsentResult.Error).message == errorMessage)
    }

    @Test
    fun `initialize should handle exceptions gracefully`() = runTest {
        every { UserMessagingPlatform.getConsentInformation(any()) } throws RuntimeException("Test exception")
        
        val result = ConsentManager.initialize(mockActivity)
        
        assert(result is ConsentManager.ConsentResult.Error)
        assert((result as ConsentManager.ConsentResult.Error).message == "Test exception")
    }
}