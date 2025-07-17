package com.bekisma.adlamfulfulde.screens

import com.bekisma.adlamfulfulde.R

data class ProFeature(
    val id: String,
    val titleRes: Int,
    val descriptionRes: Int,
    val iconRes: Int,
    val isAvailable: Boolean = true
)

enum class SubscriptionPlan(
    val id: String,
    val nameRes: Int,
    val price: String,
    val billingPeriod: String,
    val savings: String? = null
) {
    MONTHLY("monthly", R.string.monthly_plan, "$4.99", "month"),
    YEARLY("yearly", R.string.yearly_plan, "$39.99", "year", "33%"),
    LIFETIME("lifetime", R.string.lifetime_plan, "$99.99", "one-time", "83%")
}

data class ProStatus(
    val isPro: Boolean = false,
    val subscriptionPlan: SubscriptionPlan? = null,
    val expirationDate: Long? = null,
    val isTrialActive: Boolean = false,
    val trialDaysRemaining: Int = 0
)
