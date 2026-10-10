package echo.music.iad1tya.ui.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.BuildConfig
import echo.music.iad1tya.R

private const val SUPPORT_EMAIL = "bennyrahul1005@gmail.com"

enum class FeedbackCategory(val title: String) {
  BUG_REPORT("Bug Report"),
  FEATURE_REQUEST("Feature Request"),
  FEEDBACK("General Feedback")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackDialog(
  onDismiss: () -> Unit,
) {
  val context = LocalContext.current
  var selectedCategory by rememberSaveable { mutableStateOf(FeedbackCategory.BUG_REPORT) }
  var subject by rememberSaveable { mutableStateOf("") }
  var description by rememberSaveable { mutableStateOf("") }
  var includeDiagnostics by rememberSaveable { mutableStateOf(true) }

  DefaultDialog(
    onDismiss = onDismiss,
    icon = {
      Icon(
        painter = painterResource(R.drawable.support_echo),
        contentDescription = null,
        modifier = Modifier.size(32.dp),
        tint = MaterialTheme.colorScheme.primary
      )
    },
    title = {
      Text(
        text = "Feedback & Support",
        fontWeight = FontWeight.Bold,
      )
    },
    buttons = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
      Spacer(Modifier.width(8.dp))
      Button(
        onClick = {
          if (description.isBlank()) {
            Toast.makeText(context, "Please enter details for your feedback", Toast.LENGTH_SHORT).show()
            return@Button
          }
          sendFeedbackEmail(
            context = context,
            category = selectedCategory.title,
            subject = subject.trim(),
            description = description.trim(),
            includeDiagnostics = includeDiagnostics
          )
          onDismiss()
        }
      ) {
        Text("Send Email")
      }
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text(
        text = "Category",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FeedbackCategory.values().forEach { category ->
          FilterChip(
            selected = selectedCategory == category,
            onClick = { selectedCategory = category },
            label = { Text(category.title, style = MaterialTheme.typography.bodySmall) }
          )
        }
      }

      OutlinedTextField(
        value = subject,
        onValueChange = { subject = it },
        label = { Text("Subject / Title") },
        placeholder = {
          Text(
            when (selectedCategory) {
              FeedbackCategory.BUG_REPORT -> "e.g. Audio glitch on Bluetooth"
              FeedbackCategory.FEATURE_REQUEST -> "e.g. Add Sleep Timer quick tile"
              FeedbackCategory.FEEDBACK -> "e.g. Loving the new fluid playback!"
            }
          )
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )

      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Details & Description") },
        placeholder = { Text("Describe the bug or feedback in detail...") },
        minLines = 3,
        maxLines = 6,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp)
      ) {
        Checkbox(
          checked = includeDiagnostics,
          onCheckedChange = { includeDiagnostics = it }
        )
        Spacer(Modifier.width(4.dp))
        Column {
          Text(
            text = "Include device diagnostics",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "Device model, OS version, and app build",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

private fun sendFeedbackEmail(
  context: Context,
  category: String,
  subject: String,
  description: String,
  includeDiagnostics: Boolean
) {
  val emailSubject = "[FAIRY MUSIC $category] ${subject.ifEmpty { "User Feedback" }}"
  val emailBody = buildString {
    appendLine("--- FEEDBACK & SUPPORT REPORT ---")
    appendLine("Category: $category")
    if (subject.isNotEmpty()) {
      appendLine("Subject: $subject")
    }
    appendLine()
    appendLine("Description:")
    appendLine(description)
    appendLine()
    if (includeDiagnostics) {
      appendLine("--- DEVICE & APP DIAGNOSTICS ---")
      appendLine("App Version: ${BuildConfig.VERSION_NAME} (code ${BuildConfig.VERSION_CODE})")
      appendLine("Device Model: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.PRODUCT})")
      appendLine("Android Version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
      appendLine("Report Date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())}")
    }
  }

  val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:$SUPPORT_EMAIL")
    putExtra(Intent.EXTRA_EMAIL, arrayOf(SUPPORT_EMAIL))
    putExtra(Intent.EXTRA_SUBJECT, emailSubject)
    putExtra(Intent.EXTRA_TEXT, emailBody)
  }

  try {
    val chooser = Intent.createChooser(emailIntent, "Send Feedback via Email")
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
  } catch (e: Exception) {
    Toast.makeText(context, "No email app found to send feedback", Toast.LENGTH_LONG).show()
  }
}
