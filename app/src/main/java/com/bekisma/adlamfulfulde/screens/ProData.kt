package com.bekisma.adlamfulfulde.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class ProFeature(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val iconRes: Int
)

enum class SubscriptionPlan(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val price: String,
    val billingPeriod: String,
    val savePercentage: Int = 0
) {
    MONTHLY("monthly", 0, 0, "$4.99", "Monthly", 0),
    YEARLY("yearly", 0, 0, "$39.99", "Yearly", 17),
    LIFETIME("lifetime", 0, 0, "$99.99", "Lifetime", 0)
}

data class ProStatus(
    val isPro: Boolean = false,
    val subscriptionPlan: SubscriptionPlan? = null,
    val expirationDate: Long? = null,
    val isTrialActive: Boolean = false,
    val trialDaysRemaining: Int = 0
)