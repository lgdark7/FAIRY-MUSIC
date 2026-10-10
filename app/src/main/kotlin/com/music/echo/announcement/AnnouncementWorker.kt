package echo.music.iad1tya.announcement

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import echo.music.iad1tya.MainActivity
import echo.music.iad1tya.R
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber

class AnnouncementWorker(
  private val context: Context,
  workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

  override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
    try {
      android.util.Log.d(TAG, "Checking for remote announcements...")
      var jsonText: String? = fetchUrlText(ANNOUNCEMENT_URL)

      var id = ""
      var title = ""
      var message = ""
      var imageUrl = ""

      if (!jsonText.isNullOrBlank()) {
        try {
          val json = JSONObject(jsonText)
          id = json.optString("id", "").trim()
          title = json.optString("title", "").trim()
          message = json.optString("message", "").trim()
          imageUrl = json.optString("imageUrl", "").trim()
        } catch (e: Exception) {
          android.util.Log.w(TAG, "Failed to parse announcements.json", e)
        }
      }

      // Fallback: Check if version.json has an embedded "announcement" object
      if (id.isEmpty() || title.isEmpty() || message.isEmpty()) {
        val versionJsonText = fetchUrlText(VERSION_URL)
        if (!versionJsonText.isNullOrBlank()) {
          try {
            val vJson = JSONObject(versionJsonText)
            val annObj = vJson.optJSONObject("announcement")
            if (annObj != null) {
              id = annObj.optString("id", "").trim()
              title = annObj.optString("title", "").trim()
              message = annObj.optString("message", "").trim()
              imageUrl = annObj.optString("imageUrl", "").trim()
            }
          } catch (e: Exception) {
            android.util.Log.w(TAG, "Failed to parse announcement from version.json", e)
          }
        }
      }

      if (id.isEmpty() || title.isEmpty() || message.isEmpty()) {
        android.util.Log.d(TAG, "No valid announcement found to display.")
        return@withContext Result.success()
      }

      val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      val lastSeenId = prefs.getString(KEY_LAST_SEEN_ID, "") ?: ""

      if (id == lastSeenId) {
        android.util.Log.d(TAG, "Announcement id '$id' was already shown. Skipping.")
        return@withContext Result.success()
      }

      var largeBitmap: Bitmap? = null
      if (imageUrl.isNotEmpty()) {
        largeBitmap = downloadBitmap(imageUrl)
      }

      showNotification(context, id, title, message, largeBitmap)
      prefs.edit().putString(KEY_LAST_SEEN_ID, id).apply()
      android.util.Log.d(TAG, "Announcement id '$id' displayed and saved.")

      Result.success()
    } catch (e: Exception) {
      android.util.Log.e(TAG, "Error executing AnnouncementWorker", e)
      Result.retry()
    }
  }

  private fun fetchUrlText(urlString: String): String? {
    return try {
      val url = URL(urlString)
      val conn = (url.openConnection() as HttpURLConnection).apply {
        connectTimeout = 8_000
        readTimeout = 8_000
        requestMethod = "GET"
      }
      if (conn.responseCode == HttpURLConnection.HTTP_OK) {
        conn.inputStream.bufferedReader().use { it.readText() }
      } else {
        conn.disconnect()
        null
      }
    } catch (e: Exception) {
      null
    }
  }

  private fun downloadBitmap(urlStr: String): Bitmap? {
    return try {
      val url = URL(urlStr)
      val conn = (url.openConnection() as HttpURLConnection).apply {
        connectTimeout = 8_000
        readTimeout = 8_000
        doInput = true
      }
      conn.connect()
      val input: InputStream = conn.inputStream
      val bitmap = BitmapFactory.decodeStream(input)
      conn.disconnect()
      bitmap
    } catch (e: Throwable) {
      android.util.Log.w(TAG, "Failed to download announcement bitmap", e)
      null
    }
  }

  companion object {
    const val TAG = "AnnouncementWorker"
    const val ANNOUNCEMENT_URL =
      "https://raw.githubusercontent.com/lgdark7/FAIRY-MUSIC/main/announcements.json"
    const val VERSION_URL =
      "https://raw.githubusercontent.com/lgdark7/FAIRY-MUSIC/main/version.json"
    private const val PREFS_NAME = "fairy_announcements"
    const val KEY_LAST_SEEN_ID = "last_seen_announcement_id"
    private const val CHANNEL_ID = "fairy_announcements_channel"
    private const val NOTIFICATION_ID = 2002
    private const val UNIQUE_PERIODIC_WORK = "FairyAnnouncementPeriodicWorker"
    private const val UNIQUE_ONE_TIME_WORK = "FairyAnnouncementOneTimeWorker"

    fun showNotification(
      context: Context,
      id: String,
      title: String,
      message: String,
      largeBitmap: Bitmap? = null
    ) {
      val nm = context.getSystemService(NotificationManager::class.java)

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
          CHANNEL_ID,
          "Announcements & News",
          NotificationManager.IMPORTANCE_HIGH
        ).apply {
          description = "Notifications for news, announcements, and playlist updates"
          enableLights(true)
          enableVibration(true)
        }
        nm?.createNotificationChannel(channel)
      }

      val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra("announcement_id", id)
      }

      val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      val pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, flags)

      val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_nobg)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

      if (largeBitmap != null) {
        builder.setLargeIcon(largeBitmap)
        builder.setStyle(
          NotificationCompat.BigPictureStyle()
            .bigPicture(largeBitmap)
            .setBigContentTitle(title)
            .setSummaryText(message)
        )
      } else {
        builder.setStyle(
          NotificationCompat.BigTextStyle().bigText(message)
        )
      }

      val notificationId = try {
        if (id.isNotEmpty()) id.hashCode() else NOTIFICATION_ID
      } catch (e: Exception) {
        NOTIFICATION_ID
      }

      NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    fun schedulePeriodicCheck(context: Context) {
      val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

      val periodicRequest = PeriodicWorkRequestBuilder<AnnouncementWorker>(
        15, TimeUnit.MINUTES
      )
        .setConstraints(constraints)
        .build()

      WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        UNIQUE_PERIODIC_WORK,
        ExistingPeriodicWorkPolicy.KEEP,
        periodicRequest
      )
    }

    fun triggerImmediateCheck(context: Context) {
      val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

      val oneTimeRequest = OneTimeWorkRequestBuilder<AnnouncementWorker>()
        .setConstraints(constraints)
        .build()

      WorkManager.getInstance(context).enqueueUniqueWork(
        UNIQUE_ONE_TIME_WORK,
        ExistingWorkPolicy.REPLACE,
        oneTimeRequest
      )
    }

    fun resetLastSeenId(context: Context) {
      val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      prefs.edit().remove(KEY_LAST_SEEN_ID).apply()
    }
  }
}
