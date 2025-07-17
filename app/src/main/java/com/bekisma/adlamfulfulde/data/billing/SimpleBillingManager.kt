package com.bekisma.adlamfulfulde.data.billing

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay

data class SimpleSubscriptionProduct(
    val productId: String,
    val title: String,
    val description: String,
    val price: String,
    val billingPeriod: String
)

data class SimplePurchaseState(
    val isLoading: Boolean = false,
    val products: List<SimpleSubscriptionProduct> = emptyList(),
    val error: String? = null,
    val isConnected: Boolean = false
)

class SimpleBillingManager(private val context: Context) {
    
    private val _purchaseState = MutableStateFlow(SimplePurchaseState())
    val purchaseState: StateFlow<SimplePurchaseState> = _purchaseState.asStateFlow()
    
    private val _isPremiumUser = MutableStateFlow(false)
    val isPremiumUser: StateFlow<Boolean> = _isPremiumUser.asStateFlow()
    
    private val mockProducts = listOf(
        SimpleSubscriptionProduct(
            productId = "adlam_pro_monthly",
            title = "Monthly Pro",
            description = "Monthly subscription to Adlam Pro",
            price = "$4.99",
            billingPeriod = "Monthly"
        ),
        SimpleSubscriptionProduct(
            productId = "adlam_pro_yearly",
            title = "Yearly Pro",
            description = "Yearly subscription to Adlam Pro",
            price = "$39.99",
            billingPeriod = "Yearly"
        ),
        SimpleSubscriptionProduct(
            productId = "adlam_pro_lifetime",
            title = "Lifetime Pro",
            description = "Lifetime access to Adlam Pro",
            price = "$99.99",
            billingPeriod = "Lifetime"
        )
    )
    
    init {
        initialize()
    }
    
    private fun initialize() {
        _purchaseState.value = SimplePurchaseState(
            isConnected = true,
            products = mockProducts
        )
    }
    
    suspend fun purchaseProduct(productId: String): Boolean {
        _purchaseState.value = _purchaseState.value.copy(isLoading = true)
        
        // Simulate purchase process
        delay(2000)
        
        // For demo purposes, always succeed
        _isPremiumUser.value = true
        _purchaseState.value = _purchaseState.value.copy(isLoading = false)
        
        return true
    }
    
    fun restorePurchases() {
        // In real implementation, this would query actual purchases
        // For now, we'll just simulate
        _purchaseState.value = _purchaseState.value.copy(isLoading = true)
        
        // Simulate restore
        _purchaseState.value = _purchaseState.value.copy(isLoading = false)
    }
    
    fun getPremiumFeatures(): List<String> {
        return listOf(
            "Unlimited Lessons",
            "Offline Mode",
            "Advanced Quiz",
            "Progress Analytics",
            "Custom Themes",
            "Ad-Free Experience",
            "Priority Support",
            "Export Progress"
        )
    }
    
    fun isFeatureAvailable(feature: String): Boolean {
        return if (_isPremiumUser.value) {
            true
        } else {
            // Define which features are available for free users
            when (feature) {
                "alphabet" -> true
                "basic_writing" -> true
                else -> false
            }
        }
    }
    
    fun getSubscriptionStatus(productId: String): String {
        return if (_isPremiumUser.value) "Active" else "Not subscribed"
    }
}