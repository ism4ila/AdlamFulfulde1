package com.bekisma.adlamfulfulde.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekisma.adlamfulfulde.R

@Composable
fun PremiumBadge(
    modifier: Modifier = Modifier,
    size: BadgeSize = BadgeSize.MEDIUM
) {
    val badgeColors = listOf(
        Color(0xFF4CAF50),
        Color(0xFF2E7D32),
        Color(0xFF4CAF50)
    )
    
    val iconSize = when (size) {
        BadgeSize.SMALL -> 16.dp
        BadgeSize.MEDIUM -> 20.dp
        BadgeSize.LARGE -> 24.dp
    }
    
    Box(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(badgeColors),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Free",
                modifier = Modifier.size(iconSize),
                tint = Color.White
            )
            if (size != BadgeSize.SMALL) {
                Text(
                    text = "FREE",
                    color = Color.White,
                    fontSize = when (size) {
                        BadgeSize.SMALL -> 10.sp
                        BadgeSize.MEDIUM -> 12.sp
                        BadgeSize.LARGE -> 14.sp
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PremiumFeatureLock(
    featureName: String,
    description: String,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Since everything is free now, this component won't show any locks
    // It can be used as a regular feature card or simply not displayed
}

@Composable
fun PremiumFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    isAvailable: Boolean = true,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isAvailable) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color(0xFF4CAF50),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAvailable) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isAvailable) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isAvailable) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (isAvailable) {
                    PremiumBadge(size = BadgeSize.SMALL)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAvailable) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                )
            ) {
                Text(
                    text = "Use Feature",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PremiumStatusIndicator(
    isPremium: Boolean = false,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = isPremium,
        modifier = modifier,
        transitionSpec = {
            fadeIn() with fadeOut()
        }
    ) { premium ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Free",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Free",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PremiumUpgradePrompt(
    title: String,
    benefits: List<String>,
    onUpgradeClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Since everything is free now, this component won't show upgrade prompts
}

@Composable
fun PremiumContentPlaceholder(
    contentType: String,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Since everything is free now, this component won't show content placeholders
}

@Composable
fun PremiumFeatureList(
    features: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        features.forEach { feature ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF4CAF50)
                )
                Text(
                    text = feature,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

enum class BadgeSize {
    SMALL, MEDIUM, LARGE
}