package com.bekisma.adlamfulfulde

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.bekisma.adlamfulfulde.screens.ProFeature
import com.bekisma.adlamfulfulde.screens.SubscriptionPlan
import com.bekisma.adlamfulfulde.screens.ProStatus
import com.bekisma.adlamfulfulde.R


class ProManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("pro_prefs", Context.MODE_PRIVATE)
    private val _proStatus = MutableStateFlow(loadProStatus())
    val proStatus: StateFlow<ProStatus> = _proStatus.asStateFlow()
    
    val isProUser: StateFlow<Boolean> = MutableStateFlow(true)

    private fun loadProStatus(): ProStatus {
        return ProStatus(
            isPro = true,
            subscriptionPlan = SubscriptionPlan.LIFETIME,
            expirationDate = null,
            isTrialActive = false,
            trialDaysRemaining = 0
        )
    }

    fun activateTrial() {
        // No-op since everything is free
    }

    fun activateSubscription(plan: SubscriptionPlan) {
        // No-op since everything is free
    }

    fun checkSubscriptionStatus(): Boolean {
        return true
    }

    fun getProFeatures(): List<ProFeature> {
        return listOf(
            ProFeature(
                id = "unlimited_lessons",
                titleRes = R.string.unlimited_lessons,
                descriptionRes = R.string.unlimited_lessons_desc,
                iconRes = com.bekisma.adlamfulfulde.R.drawable.ic_infinity
            ),
            ProFeature(
                id = "offline_mode",
                titleRes = R.string.offline_mode,
                descriptionRes = R.string.offline_mode_desc,
                iconRes = R.drawable.ic_offline
            ),
            ProFeature(
                id = "advanced_quiz",
                titleRes = R.string.advanced_quiz,
                descriptionRes = R.string.advanced_quiz_desc,
                iconRes = R.drawable.ic_quiz_advanced
            ),
            ProFeature(
                id = "progress_analytics",
                titleRes = R.string.progress_analytics,
                descriptionRes = R.string.progress_analytics_desc,
                iconRes = R.drawable.ic_analytics
            ),
            ProFeature(
                id = "custom_themes",
                titleRes = R.string.custom_themes,
                descriptionRes = R.string.custom_themes_desc,
                iconRes = R.drawable.ic_palette
            ),
            ProFeature(
                id = "ad_free",
                titleRes = R.string.ad_free,
                descriptionRes = R.string.ad_free_desc,
                iconRes = R.drawable.ic_no_ads
            ),
            ProFeature(
                id = "priority_support",
                titleRes = R.string.priority_support,
                descriptionRes = R.string.priority_support_desc,
                iconRes = R.drawable.ic_support
            ),
            ProFeature(
                id = "export_progress",
                titleRes = R.string.export_progress,
                descriptionRes = R.string.export_progress_desc,
                iconRes = R.drawable.ic_export
            )
        )
    }
}