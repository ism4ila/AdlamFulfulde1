package com.bekisma.adlamfulfulde.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ProManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProUpgradeScreen(
    onNavigateBack: () -> Unit,
    onPurchaseSuccess: () -> Unit,
    proManager: ProManager
) {
    val proStatus by proManager.proStatus.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var selectedPlan by remember { mutableStateOf(SubscriptionPlan.YEARLY) }
    var showTrialDialog by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ProUpgradeTopBar(onNavigateBack = onNavigateBack)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(createProBackgroundGradient()),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ProHeaderCard(
                    proStatus = proStatus,
                    onStartTrial = { showTrialDialog = true }
                )
            }

            item {
                ProFeaturesSection(
                    features = proManager.getProFeatures()
                )
            }

            item {
                SubscriptionPlansSection(
                    selectedPlan = selectedPlan,
                    onPlanSelected = { selectedPlan = it },
                    proStatus = proStatus
                )
            }

            item {
                PurchaseButton(
                    selectedPlan = selectedPlan,
                    isProcessing = isProcessing,
                    proStatus = proStatus,
                    onPurchase = {
                        isProcessing = true
                        coroutineScope.launch {
                            delay(2000) // Simulation d'achat
                            proManager.activateSubscription(selectedPlan)
                            isProcessing = false
                            onPurchaseSuccess()
                        }
                    }
                )
            }

            item {
                ProLegalSection()
            }
        }
    }

    if (showTrialDialog) {
        TrialDialog(
            onDismiss = { showTrialDialog = false },
            onConfirm = {
                proManager.activateTrial()
                showTrialDialog = false
                onPurchaseSuccess()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProUpgradeTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.upgrade_to_pro),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun ProHeaderCard(
    proStatus: ProStatus,
    onStartTrial: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_crown),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.unlock_full_potential),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.pro_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                if (!proStatus.isPro && !proStatus.isTrialActive) {
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedButton(
                        onClick = onStartTrial,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = BorderStroke(2.dp, Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.start_free_trial),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProFeaturesSection(features: List<ProFeature>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.pro_features),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            features.forEach { feature ->
                ProFeatureItem(feature = feature)
                if (feature != features.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ProFeatureItem(feature: ProFeature) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = feature.iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = feature.titleRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(id = feature.descriptionRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Icon(
            Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SubscriptionPlansSection(
    selectedPlan: SubscriptionPlan,
    onPlanSelected: (SubscriptionPlan) -> Unit,
    proStatus: ProStatus
) {
    if (proStatus.isPro) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.choose_your_plan),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            SubscriptionPlan.values().forEach { plan ->
                SubscriptionPlanCard(
                    plan = plan,
                    isSelected = selectedPlan == plan,
                    onSelected = { onPlanSelected(plan) }
                )
                if (plan != SubscriptionPlan.values().last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun SubscriptionPlanCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelected,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = plan.nameRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    plan.savings?.let { savings ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.save_percentage, savings),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "${plan.price}/${plan.billingPeriod}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun PurchaseButton(
    selectedPlan: SubscriptionPlan,
    isProcessing: Boolean,
    proStatus: ProStatus,
    onPurchase: () -> Unit
) {
    if (proStatus.isPro) return

    Button(
        onClick = onPurchase,
        enabled = !isProcessing,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        if (isProcessing) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White
            )
        } else {
            Text(
                text = stringResource(R.string.upgrade_now, selectedPlan.price),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ProLegalSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.pro_legal_text),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = { /* Ouvrir terms */ }) {
                Text(
                    text = stringResource(R.string.terms_of_service),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            TextButton(onClick = { /* Ouvrir privacy */ }) {
                Text(
                    text = stringResource(R.string.privacy_policy),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun TrialDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.start_free_trial),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.trial_dialog_text),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(R.string.start_trial))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun createProBackgroundGradient(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surface
        )
    )
}
