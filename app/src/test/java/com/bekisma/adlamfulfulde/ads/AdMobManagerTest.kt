package com.bekisma.adlamfulfulde.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.InitializationStatus
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
class AdMobManagerTest {

    private lateinit var mockApplication: Application
    private lateinit var mockActivity: Activity
    private lateinit var mockInitializationStatus: InitializationStatus

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockApplication = mockk(relaxed = true)
        mockActivity = mockk(relaxed = true)
        mockInitializationStatus = mockk(relaxed = true)
        
        // Reset AdMobManager state before each test
        clearStaticMockk(AdMobManager::class)
        
        // Mock MobileAds static methods
        mockkStatic(MobileAds::class)
        every { MobileAds.initialize(any<Application>(), any()) } answers {
            val callback = secondArg<(InitializationStatus) -> Unit>()
            callback.invoke(mockInitializationStatus)
        }
        every { MobileAds.setRequestConfiguration(any()) } just Runs
        every { mockInitializationStatus.adapterStatusMap } returns emptyMap()
        
        // Mock ConsentManager
        mockkObject(ConsentManager)
        coEvery { ConsentManager.initialize(any()) } returns ConsentManager.ConsentResult.Success(false)
        every { ConsentManager.canRequestAds() } returns true
        
        // Mock SimpleInterstitialManager
        mockkObject(SimpleInterstitialManager)
        every { SimpleInterstitialManager.preloadAd(any()) } just Runs
    }

    @Test
    fun `initialize should return true when successful`() = runTest {
        val result = AdMobManager.initialize(mockApplication)
        
        assert(result)
        verify { MobileAds.initialize(mockApplication, any()) }
    }

    @Test
    fun `initialize should set isInitialized to true on success`() = runTest {
        AdMobManager.initialize(mockApplication)
        
        assert(AdMobManager.isInitialized())
    }

    @Test
    fun `initialize should return true if already initialized`() = runTest {
        // Initialize first time
        AdMobManager.initialize(mockApplication)
        
        // Initialize second time
        val result = AdMobManager.initialize(mockApplication)
        
        assert(result)
        // MobileAds.initialize should only be called once
        verify(exactly = 1) { MobileAds.initialize(any<Application>(), any()) }
    }

    @Test
    fun `initializeWithConsent should initialize consent and AdMob`() = runTest {
        every { mockActivity.application } returns mockApplication
        
        val result = AdMobManager.initializeWithConsent(mockActivity)
        
        assert(result)
        coVerify { ConsentManager.initialize(mockActivity) }
        verify { MobileAds.initialize(mockApplication, any()) }
    }

    @Test
    fun `initializeWithConsent should preload ads when successful`() = runTest {
        every { mockActivity.application } returns mockApplication
        
        AdMobManager.initializeWithConsent(mockActivity)
        
        verify { SimpleInterstitialManager.preloadAd(mockActivity) }
    }

    @Test
    fun `setTestConfiguration should configure test device IDs`() {
        val testDeviceIds = listOf("TEST_DEVICE_1", "TEST_DEVICE_2")
        
        AdMobManager.setTestConfiguration(testDeviceIds)
        
        verify { MobileAds.setRequestConfiguration(any()) }
    }

    @Test
    fun `setTestConfiguration should not configure when empty list`() {
        clearAllMocks()
        mockkStatic(MobileAds::class)
        
        AdMobManager.setTestConfiguration(emptyList())
        
        verify(exactly = 0) { MobileAds.setRequestConfiguration(any()) }
    }

    @Test
    fun `setChildDirectedTreatment should configure request properly`() {
        AdMobManager.setChildDirectedTreatment(true)
        
        verify { 
            MobileAds.setRequestConfiguration(
                match<RequestConfiguration> { config ->
                    config.tagForChildDirectedTreatment == RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
                }
            )
        }
    }

    @Test
    fun `canRequestAds should return false when not initialized`() {
        val result = AdMobManager.canRequestAds()
        
        assert(!result)
    }

    @Test
    fun `canRequestAds should return true when initialized with consent`() = runTest {
        every { mockActivity.application } returns mockApplication
        
        AdMobManager.initializeWithConsent(mockActivity)
        
        val result = AdMobManager.canRequestAds()
        
        assert(result)
    }

    @Test
    fun `canRequestAds should return false when consent denied`() = runTest {
        every { ConsentManager.canRequestAds() } returns false
        every { mockActivity.application } returns mockApplication
        
        AdMobManager.initializeWithConsent(mockActivity)
        
        val result = AdMobManager.canRequestAds()
        
        assert(!result)
    }

    @Test
    fun `preloadAllAds should not run when not initialized`() {
        val mockContext: Context = mockk()
        
        AdMobManager.preloadAllAds(mockContext)
        
        verify(exactly = 0) { SimpleInterstitialManager.preloadAd(any()) }
    }

    @Test
    fun `preloadAllAds should preload ads when initialized`() = runTest {
        AdMobManager.initialize(mockApplication)
        
        AdMobManager.preloadAllAds(mockApplication)
        
        // Give coroutine time to execute
        kotlinx.coroutines.delay(100)
        
        verify { SimpleInterstitialManager.preloadAd(mockApplication) }
    }

    @Test
    fun `hasConsentForAds should return correct consent status`() = runTest {
        every { mockActivity.application } returns mockApplication
        
        AdMobManager.initializeWithConsent(mockActivity)
        
        val hasConsent = AdMobManager.hasConsentForAds()
        
        // Should return true since we mocked consent as successful
        assert(hasConsent)
    }

    @Test
    fun `initialize should handle exceptions gracefully`() = runTest {
        every { MobileAds.initialize(any<Application>(), any()) } throws RuntimeException("Test exception")
        
        val result = AdMobManager.initialize(mockApplication)
        
        assert(!result)
        assert(!AdMobManager.isInitialized())
    }

    @Test
    fun `initializeWithConsent should handle consent errors gracefully`() = runTest {
        coEvery { ConsentManager.initialize(any()) } returns ConsentManager.ConsentResult.Error("Consent failed")
        every { mockActivity.application } returns mockApplication
        
        val result = AdMobManager.initializeWithConsent(mockActivity)
        
        // Should still succeed with AdMob initialization even if consent fails
        assert(result)
    }
}