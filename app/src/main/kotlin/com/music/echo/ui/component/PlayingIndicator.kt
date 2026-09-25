package echo.music.iad1tya.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.ThumbnailCornerRadius

@Composable
fun PlayingIndicator(
  color: Color,
  modifier: Modifier = Modifier,
  bars: Int = 3,
  barWidth: Dp = 4.dp,
  cornerRadius: Dp = ThumbnailCornerRadius,
) {
  val transition = rememberInfiniteTransition(label = "equalizer")
  val bar1 by transition.animateFloat(
    initialValue = 0.2f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(420, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar1"
  )
  val bar2 by transition.animateFloat(
    initialValue = 0.45f,
    targetValue = 0.85f,
    animationSpec = infiniteRepeatable(
      animation = tween(310, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar2"
  )
  val bar3 by transition.animateFloat(
    initialValue = 0.15f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(530, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar3"
  )
  val bar4 by transition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.75f,
    animationSpec = infiniteRepeatable(
      animation = tween(380, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar4"
  )

  val heights = listOf(bar1, bar2, bar3, bar4)
  val activeBars = bars.coerceIn(1, 4)
  val spacing = 4.dp
  val totalWidth = (barWidth * activeBars) + (spacing * (activeBars - 1))

  Canvas(
    modifier = modifier.height(24.dp).width(totalWidth)
  ) {
    val barWidthPx = barWidth.toPx()
    val spacingPx = spacing.toPx()
    val cornerPx = cornerRadius.toPx()

    for (i in 0 until activeBars) {
      val fraction = heights.getOrElse(i) { 0.5f }
      val h = fraction * size.height
      val x = i * (barWidthPx + spacingPx)
      drawRoundRect(
        color = color,
        topLeft = Offset(x = x, y = size.height - h),
        size = Size(width = barWidthPx, height = h),
        cornerRadius = CornerRadius(cornerPx),
      )
    }
  }
}

@Composable
fun PlayingIndicatorBox(
  modifier: Modifier = Modifier,
  isActive: Boolean,
  playWhenReady: Boolean,
  color: Color = Color.White,
) {
  AnimatedVisibility(
    visible = isActive,
    enter = fadeIn(tween(300)),
    exit = fadeOut(tween(200)),
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = modifier,
    ) {
      if (playWhenReady) {
        PlayingIndicator(
          color = color,
          modifier = Modifier.height(24.dp),
        )
      } else {
        Icon(
          painter = painterResource(R.drawable.play),
          contentDescription = null,
          tint = color,
        )
      }
    }
  }
}
