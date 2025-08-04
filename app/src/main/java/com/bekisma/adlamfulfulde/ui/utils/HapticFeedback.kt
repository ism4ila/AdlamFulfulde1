package com.bekisma.adlamfulfulde.ui.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role

enum class HapticType {
    LIGHT_IMPACT,
    MEDIUM_IMPACT,
    HEAVY_IMPACT,
    SUCCESS,
    WARNING,
    ERROR,
    SELECTION
}

object HapticFeedbackUtil {
    
    fun performHapticFeedback(context: Context, type: HapticType) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = when (type) {
                HapticType.LIGHT_IMPACT -> VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
                HapticType.MEDIUM_IMPACT -> VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
                HapticType.HEAVY_IMPACT -> VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE)
                HapticType.SUCCESS -> VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 50), -1)
                HapticType.WARNING -> VibrationEffect.createWaveform(longArrayOf(0, 30, 30, 30, 30, 30), -1)
                HapticType.ERROR -> VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100), -1)
                HapticType.SELECTION -> VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE)
            }
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            val duration = when (type) {
                HapticType.LIGHT_IMPACT -> 10L
                HapticType.MEDIUM_IMPACT -> 20L
                HapticType.HEAVY_IMPACT -> 40L
                HapticType.SUCCESS -> 100L
                HapticType.WARNING -> 150L
                HapticType.ERROR -> 200L
                HapticType.SELECTION -> 5L
            }
            vibrator.vibrate(duration)
        }
    }
}

// Enhanced clickable modifier with haptic feedback
@Composable
fun Modifier.clickableWithHaptic(
    hapticType: HapticType = HapticType.LIGHT_IMPACT,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    rippleColor: Color = Color.Unspecified,
    onClick: () -> Unit
): Modifier {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    
    return this.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        if (enabled) {
            // Use system haptic feedback for standard types
            when (hapticType) {
                HapticType.SELECTION -> hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                HapticType.LIGHT_IMPACT -> hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                else -> {
                    // Use custom haptic feedback for more complex patterns
                    HapticFeedbackUtil.performHapticFeedback(context, hapticType)
                }
            }
            onClick()
        }
    }
}

// Enhanced selectable modifier with haptic feedback
@Composable
fun Modifier.selectableWithHaptic(
    selected: Boolean,
    hapticType: HapticType = HapticType.SELECTION,
    enabled: Boolean = true,
    role: Role? = null,
    onClick: () -> Unit
): Modifier {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    
    return this.selectable(
        selected = selected,
        enabled = enabled,
        role = role,
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        if (enabled) {
            when (hapticType) {
                HapticType.SELECTION -> hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                HapticType.LIGHT_IMPACT -> hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                else -> {
                    HapticFeedbackUtil.performHapticFeedback(context, hapticType)
                }
            }
            onClick()
        }
    }
}