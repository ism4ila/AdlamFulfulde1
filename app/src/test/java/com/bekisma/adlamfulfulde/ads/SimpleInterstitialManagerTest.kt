package com.bekisma.adlamfulfulde.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@LooperMode(LooperMode.Mode.PAUSED)
class SimpleInterstitialManagerTest {

    private lateinit var mockContext: Context
    private lateinit var mockActivity: Activity
    private lateinit var mockInterstitialAd: InterstitialAd
    private lateinit var mockAdRequest: AdRequest
    private lateinit var mockLoadAdError: LoadAdError
    private lateinit var mockAdError: AdError

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        mockActivity = mockk(relaxed = true)
        mockInterstitialAd = mockk(relaxed = true)
        mockAdRequest = mockk(relaxed = true)
        mockLoadAdError = mockk(relaxed = true)
        mockAdError = mockk(relaxed = true)
        
        // Mock static methods
        mockkStatic(InterstitialAd::class)
        mockkConstructor(AdRequest.Builder::class)
        
        every { anyConstructed<AdRequest.Builder>().build() } returns mockAdRequest
        
        // Reset SimpleInterstitialManager state
        clearStaticMockk(SimpleInterstitialManager::class)
    }

    @Test
    fun `loadAd should load interstitial ad successfully`() {
        var loadCallback: InterstitialAdLoadCallback? = null
        
        every { 
            InterstitialAd.load(any(), any(), any(), any<InterstitialAdLoadCallback>())
        } answers {
            loadCallback = arg(3)
        }
        
        SimpleInterstitialManager.loadAd(mockContext)
        
        // Simulate successful ad load
        loadCallback?.onAdLoaded(mockInterstitialAd)
        
        verify { InterstitialAd.load(any(), any(), any(), any<InterstitialAdLoadCallback>()) }
    }

    @Test
    fun `loadAd should handle load failure gracefully`() {
        var loadCallback: InterstitialAdLoadCallback? = null
        
        every { 
            InterstitialAd.load(any(), any(), any(), any<InterstitialAdLoadCallback>())
        } answers {
            loadCallback = arg(3)
        }
        
        SimpleInterstitialManager.loadAd(mockContext)
        
        // Simulate ad load failure
        loadCallback?.onAdFailedToLoad(mockLoadAdError)
        
        verify { InterstitialAd.load(any(), any(), any(), any<InterstitialAdLoadCallback>()) }
        // Should not crash and handle error gracefully
    }

    @Test
    fun `showAd should show ad when available`() {
        var loadCallback: InterstitialAdLoadCallback? = null
        var showCallback: (() -> Unit)? = null
        var failedCallback: (() -> Unit)? = null
        
        every { 
            InterstitialAd.load(any(), any(), any(), any<InterstitialAdLoadCallback>())
        } answers {
            loadCallback = arg(3)
        }
        
        // Load ad first
        SimpleInterstitialManager.loadAd(mockContext)
        loadCallback?.onAdLoaded(mockInterstitialAd)
        
        // Show ad
        SimpleInterstitialManager.showAd(
            mockActivity,
            onAdDismissed = { showCallback?.invoke() },
            onAdFailed = { failedCallback?.invoke() }
        )
        
        verify { mockInterstitialAd.show(mockActivity) }
    }

    @Test
    fun `showAd should call onAdFailed when no ad available`() {
        var failedCalled = false
        
        SimpleInterstitialManager.showAd(
            mockActivity,
            onAdDismissed = { },
            onAdFailed = { failedCalled = true }
        )
        
        assert(failedCalled)
    }

    @Test
    fun `showAd should set fullScreenContentCallback`() {
        var loadCallback: InterstitialAdLoadCallback? = null
        var contentCallback: FullScreenContentCallback? = null
        
        every { 
            InterstitialAd.load(any(), any(), any(), any<InterstitialAdLoadCallback>())
        } answers {
            loadCallback = arg(3)
        }
        
        every { mockInterstitialAd.fullScreenContentCallback = any() } answers {
            contentCallback = arg(0)
        }
        
        // Load ad first
        SimpleInterstitialManager.loadAd(mockContext)
        loadCallback?.onAdLoaded(mockInterstitialAd)
        
        // Show ad
        SimpleInterstitialManager.showAd(mockActivity, {}, {})
        
        verify { mockInterstitialAd.fullScreenContentCallback = any() }
    }

    @Test
    fun `fullScreenContentCallback should handle onAdDismissedFullScreenContent`() {
        var loadCallback: InterstitialAdLoadCallback? = null
        var contentCallback: FullScreenContentCallback? = null
        var dismissedCalled = false
        
        every { 
            InterstitialAd.load(any(), any(), any(), any<InterstitialAdLoadCallback>())
        } answers {
            loadCallback = arg(3)
        }
        
        every { mockInterstitialAd.fullScreenContentCallback = any() } answers {
            contentCallback = arg(0)
        }
        
        // Load and show ad
        SimpleInterstitialManager.loadAd(mockContext)
        loadCallback?.onAdLoaded(mockInterstitialAd)
        
        SimpleInterstitialManager.showAd(
            mockActivity,
            onAdDismissed = { dismissedCalled = true },
            onAdFailed = { }
        )
        
        // Simulate ad dismissed
        contentCallback?.onAdDismissedFullScreenContent()
        
        assert(dismissedCalled)
    }

    @Test
    fun `fullScreenContentCallback should handle onAdFailedToShowFullScreenContent`() {
        var loadCallback: InterstitialAdLoadCallback? = null
        var contentCallback: FullScreenContentCallback? = null
        var failedCalled = false
        
        every { 
            InterstitialAd.load(any(), any(), any(), any<InterstitialAdLoadCallback>())
        } answers {
            loadCallback = arg(3)
        }
        
        every { mockInterstitialAd.fullScreenContentCallback = any() } answers {
            contentCallback = arg(0)
        }
        
        // Load and show ad
        SimpleInterstitialManager.loadAd(mockContext)
        loadCallback?.onAdLoaded(mockInterstitialAd)
        
        SimpleInterstitialManager.showAd(
            mockActivity,
            onAdDismissed = { },
            onAdFailed = { failedCalled = true }
        )
        
        // Simulate ad failed to show
        contentCallback?.onAdFailedToShowFullScreenContent(mockAdError)
        
        assert(failedCalled)
    }

    @Test
    fun `preloadAd should load ad`() {
        every { 
            InterstitialAd.load(any(), any(), any(), any<InterstitialAdLoadCallback>())
        } just Runs
        
        SimpleInterstitialManager.preloadAd(mockContext)
        
        verify { InterstitialAd.load(any(), any(), any(), any<InterstitialAdLoadCallback>()) }
    }

    @Test
    fun `isAdReady should return false initially`() {
        val result = SimpleInterstitialManager.isAdReady()
        
        assert(!result)
    }

    @Test
    fun `isAdReady should return true after successful load`() {
        var loadCallback: InterstitialAdLoadCallback? = null
        
        every { 
            InterstitialAd.load(any(), any(), any(), any<InterstitialAdLoadCallback>())
        } answers {
            loadCallback = arg(3)
        }
        
        SimpleInterstitialManager.loadAd(mockContext)
        loadCallback?.onAdLoaded(mockInterstitialAd)
        
        val result = SimpleInterstitialManager.isAdReady()
        
        assert(result)
    }

    @Test
    fun `ad should be null after being shown`() {
        var loadCallback: InterstitialAdLoadCallback? = null
        var contentCallback: FullScreenContentCallback? = null
        
        every { 
            InterstitialAd.load(any(), any(), any(), any<InterstitialAdLoadCallback>())
        } answers {
            loadCallback = arg(3)
        }
        
        every { mockInterstitialAd.fullScreenContentCallback = any() } answers {
            contentCallback = arg(0)
        }
        
        // Load and show ad
        SimpleInterstitialManager.loadAd(mockContext)
        loadCallback?.onAdLoaded(mockInterstitialAd)
        
        SimpleInterstitialManager.showAd(mockActivity, {}, {})
        
        // Simulate ad dismissed (which should set ad to null)
        contentCallback?.onAdDismissedFullScreenContent()
        
        val result = SimpleInterstitialManager.isAdReady()
        assert(!result)
    }
}