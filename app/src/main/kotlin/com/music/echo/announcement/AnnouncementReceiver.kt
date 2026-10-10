package echo.music.iad1tya.announcement

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class AnnouncementReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    val action = intent?.action ?: return
    android.util.Log.d(TAG, "Received broadcast action: $action")

    when (action) {
      ACTION_CHECK_ANNOUNCEMENT -> {
        AnnouncementWorker.triggerImmediateCheck(context)
      }
      ACTION_TEST_ANNOUNCEMENT -> {
        val title = intent.getStringExtra("title") ?: "🎵 Fairy Music Test"
        val message = intent.getStringExtra("message") ?: "This is a test notification from Fairy Music."
        val id = intent.getStringExtra("id") ?: "test_${System.currentTimeMillis()}"
        AnnouncementWorker.showNotification(context, id, title, message, null)
      }
      ACTION_RESET_ANNOUNCEMENT -> {
        AnnouncementWorker.resetLastSeenId(context)
        android.util.Log.d(TAG, "Announcement last seen ID reset.")
      }
      Intent.ACTION_BOOT_COMPLETED,
      Intent.ACTION_MY_PACKAGE_REPLACED -> {
        AnnouncementWorker.schedulePeriodicCheck(context)
      }
    }
  }

  companion object {
    private const val TAG = "AnnouncementReceiver"
    const val ACTION_CHECK_ANNOUNCEMENT = "echo.music.action.CHECK_ANNOUNCEMENT"
    const val ACTION_TEST_ANNOUNCEMENT = "echo.music.action.TEST_ANNOUNCEMENT"
    const val ACTION_RESET_ANNOUNCEMENT = "echo.music.action.RESET_ANNOUNCEMENT"
  }
}
