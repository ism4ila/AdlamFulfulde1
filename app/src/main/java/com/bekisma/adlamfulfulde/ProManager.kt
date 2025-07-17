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
    
        val isProUser: StateFlow<Boolean> = MutableStateFlow(false)

    private fun loadProStatus(): ProStatus {
        val isPro = prefs.getBoolean("is_pro", false)
        val planId = prefs.getString("subscription_plan", null)
        val expirationDate = prefs.getLong("expiration_date", 0L)
        val isTrialActive = prefs.getBoolean("is_trial_active", false)
        val trialDaysRemaining = prefs.getInt("trial_days_remaining", 7)

        return ProStatus(
            isPro = isPro,
            subscriptionPlan = planId?.let { id -> SubscriptionPlan.values().find { it.id == id } },
            expirationDate = if (expirationDate > 0) expirationDate else null,
            isTrialActive = isTrialActive,
            trialDaysRemaining = trialDaysRemaining
        )
    }

    fun activateTrial() {
        prefs.edit()
            .putBoolean("is_trial_active", true)
            .putInt("trial_days_remaining", 7)
            .putLong("expiration_date", System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000))
            .apply()

        _proStatus.value = _proStatus.value.copy(
            isTrialActive = true,
            trialDaysRemaining = 7
        )
    }

    fun activateSubscription(plan: SubscriptionPlan) {
        val expirationTime = when (plan) {
            SubscriptionPlan.MONTHLY -> System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)
            SubscriptionPlan.YEARLY -> System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)
            SubscriptionPlan.LIFETIME -> Long.MAX_VALUE
        }

        prefs.edit()
            .putBoolean("is_pro", true)
            .putString("subscription_plan", plan.id)
            .putLong("expiration_date", expirationTime)
            .putBoolean("is_trial_active", false)
            .apply()

        _proStatus.value = ProStatus(
            isPro = true,
            subscriptionPlan = plan,
            expirationDate = if (plan != SubscriptionPlan.LIFETIME) expirationTime else null
        )
    }

        fun checkSubscriptionStatus(): Boolean {
        return false
    }

    fun getProFeatures(): List<ProFeature> {
        // NOTE: Les ressources R.drawable.ic_* et R.string.* doivent exister dans votre projet.
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
