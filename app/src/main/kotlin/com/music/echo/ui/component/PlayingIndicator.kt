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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
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

  Row(
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.Bottom,
    modifier = modifier,
  ) {
    heights.take(bars.coerceAtMost(4)).forEach { heightFraction ->
      Canvas(
        modifier = Modifier.fillMaxHeight().width(barWidth),
      ) {
        val h = heightFraction * size.height
        drawRoundRect(
          color = color,
          topLeft = Offset(x = 0f, y = size.height - h),
          size = size.copy(height = h),
          cornerRadius = CornerRadius(cornerRadius.toPx()),
        )
      }
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
