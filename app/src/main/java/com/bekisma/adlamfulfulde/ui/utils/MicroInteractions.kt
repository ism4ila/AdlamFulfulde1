package com.bekisma.adlamfulfulde.ui.utils

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.LocalContentColor
import androidx.compose.foundation.layout.size
import androidx.compose.animation.AnimatedContentScope

// Bouncy press animation
@Composable
fun Modifier.bouncyPress(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    pressScale: Float = 0.95f
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "press_scale"
    )
    
    return this.scale(scale)
}

// Pulse animation for important elements
@Composable
fun Modifier.pulseAnimation(
    enabled: Boolean = true,
    minScale: Float = 1f,
    maxScale: Float = 1.05f,
    duration: Int = 1000
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    
    return if (enabled) {
        this.scale(scale)
    } else {
        this
    }
}

// Shake animation for errors
@Composable
fun Modifier.shakeAnimation(
    enabled: Boolean = false,
    strength: Float = 5f,
    duration: Int = 300
): Modifier {
    val density = LocalDensity.current
    val shake = remember { Animatable(0f) }
    
    LaunchedEffect(enabled) {
        if (enabled) {
            for (i in 0..3) {
                shake.animateTo(
                    targetValue = strength,
                    animationSpec = tween(duration / 8, easing = LinearEasing)
                )
                shake.animateTo(
                    targetValue = -strength,
                    animationSpec = tween(duration / 8, easing = LinearEasing)
                )
            }
            shake.animateTo(0f, animationSpec = tween(duration / 4))
        }
    }
    
    return this.graphicsLayer {
        translationX = with(density) { shake.value.dp.toPx() }
    }
}

// Floating action button with enhanced animations
@Composable
fun AnimatedFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    icon: @Composable () -> Unit,
    text: @Composable (() -> Unit)? = null,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val density = LocalDensity.current
    
    // Enhanced press animation
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fab_scale"
    )
    
    // Rotation animation on press
    val rotation by animateFloatAsState(
        targetValue = if (isPressed) 5f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "fab_rotation"
    )
    
    if (expanded && text != null) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            modifier = modifier
                .scale(scale)
                .graphicsLayer { rotationZ = rotation },
            icon = icon,
            text = text,
            containerColor = containerColor,
            contentColor = contentColor,
            interactionSource = interactionSource
        )
    } else {
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier
                .scale(scale)
                .graphicsLayer { rotationZ = rotation },
            containerColor = containerColor,
            contentColor = contentColor,
            interactionSource = interactionSource
        ) {
            icon()
        }
    }
}

// Slide in/out animations for dialogs and sheets
@Composable
fun SlideInFromBottom(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(
            animationSpec = tween(300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessHigh
            )
        ) + fadeOut(
            animationSpec = tween(200)
        ),
        content = content
    )
}

@Composable
fun SlideInFromRight(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(
            animationSpec = tween(300)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessHigh
            )
        ) + fadeOut(
            animationSpec = tween(200)
        ),
        content = content
    )
}

// Morphing button animation
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MorphingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    enabled: Boolean = true,
    content: @Composable (AnimatedContentScope.() -> Unit)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val buttonScale by animateFloatAsState(
        targetValue = when {
            !enabled -> 0.9f
            isPressed -> 0.95f
            loading -> 1.02f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "morphing_scale"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier.scale(buttonScale),
        enabled = enabled && !loading,
        interactionSource = interactionSource
    ) {
        AnimatedContent(
            targetState = loading,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) with
                fadeOut(animationSpec = tween(300))
            },
            label = "morphing_content"
        ) { isLoading ->
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = LocalContentColor.current
                )
            } else {
                content()
            }
        }
    }
}

// Attention-seeking animation for notifications
@Composable
fun Modifier.attentionSeeker(
    enabled: Boolean = true,
    type: AttentionType = AttentionType.BOUNCE
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "attention")
    
    return when (type) {
        AttentionType.BOUNCE -> {
            val translateY by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bounce_y"
            )
            
            if (enabled) {
                this.graphicsLayer { translationY = translateY }
            } else {
                this
            }
        }
        
        AttentionType.WIGGLE -> {
            val rotation by infiniteTransition.animateFloat(
                initialValue = -5f,
                targetValue = 5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(200, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wiggle_rotation"
            )
            
            if (enabled) {
                this.graphicsLayer { rotationZ = rotation }
            } else {
                this
            }
        }
        
        AttentionType.GLOW -> {
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.7f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "glow_alpha"
            )
            
            if (enabled) {
                this.graphicsLayer { this.alpha = alpha }
            } else {
                this
            }
        }
    }
}

enum class AttentionType {
    BOUNCE,
    WIGGLE,
    GLOW
}