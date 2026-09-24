package echo.music.iad1tya.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.music.innertube.YouTube
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.AccountChannelHandleKey
import echo.music.iad1tya.constants.AccountEmailKey
import echo.music.iad1tya.constants.AccountNameKey
import echo.music.iad1tya.constants.DataSyncIdKey
import echo.music.iad1tya.constants.InnerTubeCookieKey
import echo.music.iad1tya.constants.SavedAccountsKey
import echo.music.iad1tya.constants.VisitorDataKey
import echo.music.iad1tya.models.AccountData
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.utils.backToMain
import echo.music.iad1tya.utils.rememberPreference
import echo.music.iad1tya.utils.reportException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun LoginScreen(
  navController: NavController,
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var visitorData by rememberPreference(VisitorDataKey, "")
  var dataSyncId by rememberPreference(DataSyncIdKey, "")
  var innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
  var accountName by rememberPreference(AccountNameKey, "")
  var accountEmail by rememberPreference(AccountEmailKey, "")
  var accountChannelHandle by rememberPreference(AccountChannelHandleKey, "")
  var savedAccountsJson by rememberPreference(SavedAccountsKey, "[]")
  var hasCompletedLogin by remember { mutableStateOf(false) }

  var webView: WebView? = null

  AndroidView(
    modifier = Modifier.windowInsetsPadding(LocalPlayerAwareWindowInsets.current).fillMaxSize(),
    factory = { webViewContext ->
      WebView(webViewContext).apply {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(this, true)

        settings.apply {
          javaScriptEnabled = true
          domStorageEnabled = true
          databaseEnabled = true
          setSupportZoom(true)
          builtInZoomControls = true
          displayZoomControls = false
          // Modern Mobile Chrome User-Agent to bypass Google's "disallowed_useragent" / insecure browser block
          userAgentString =
            "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"
        }

        webViewClient =
          object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
              if (url?.contains("youtube.com") == true) {
                loadUrl("javascript:if(window.yt&&window.yt.config_){Android.onRetrieveVisitorData(window.yt.config_.VISITOR_DATA);Android.onRetrieveDataSyncId(window.yt.config_.DATASYNC_ID);}")
              }

              if (url != null && (url.contains("music.youtube.com") || url.contains("youtube.com")) && !hasCompletedLogin) {
                val currentCookies = cookieManager.getCookie("https://music.youtube.com")
                  ?: cookieManager.getCookie("https://youtube.com")
                  ?: cookieManager.getCookie(url)
                  ?: ""

                if (currentCookies.contains("SAPISID") || currentCookies.contains("__Secure-3PAPISID") || currentCookies.contains("LOGIN_INFO")) {
                  hasCompletedLogin = true
                  innerTubeCookie = currentCookies

                  coroutineScope.launch {
                    delay(600)

                    YouTube.cookie = currentCookies
                    YouTube.dataSyncId = dataSyncId
                    YouTube.visitorData = visitorData

                    Timber.d("Login: Validated Google cookies captured, resolving account details...")

                    var resolvedName = "YouTube User"
                    var resolvedEmail = ""
                    var resolvedHandle = ""
                    var resolvedAvatar = ""

                    try {
                      val info = YouTube.accountInfo().getOrNull()
                      if (info != null) {
                        resolvedName = info.name
                        resolvedEmail = info.email.orEmpty()
                        resolvedHandle = info.channelHandle.orEmpty()
                        resolvedAvatar = info.thumbnailUrl.orEmpty()
                      }
                    } catch (e: Exception) {
                      Timber.w(e, "Login: accountInfo metadata query failed, proceeding with authenticated cookie session")
                    }

                    accountName = resolvedName
                    accountEmail = resolvedEmail
                    accountChannelHandle = resolvedHandle

                    val newAccount =
                      AccountData(
                        name = resolvedName,
                        email = resolvedEmail,
                        channelHandle = resolvedHandle,
                        cookie = currentCookies,
                        visitorData = visitorData,
                        dataSyncId = dataSyncId,
                        avatarUrl = resolvedAvatar
                      )
                    val accounts =
                      try {
                          Json.decodeFromString<List<AccountData>>(savedAccountsJson)
                        } catch (e: Exception) {
                          emptyList()
                        }
                        .toMutableList()
                    accounts.removeAll { acc -> acc.cookie == currentCookies || acc.name == newAccount.name }
                    accounts.add(newAccount)
                    savedAccountsJson = Json.encodeToString(accounts)

                    Timber.d("Login: Successfully authenticated as $resolvedName, restarting app...")
                    Toast.makeText(context, "Logged in as $resolvedName", Toast.LENGTH_SHORT).show()

                    webView?.apply {
                      stopLoading()
                      clearHistory()
                      clearCache(true)
                      clearFormData()
                    }

                    val intent =
                      context.packageManager.getLaunchIntentForPackage(context.packageName)
                    intent?.addFlags(
                      Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                    context.startActivity(intent)
                    delay(500)
                    Runtime.getRuntime().exit(0)
                  }
                }
              }
            }
          }

        addJavascriptInterface(
          object {
            @JavascriptInterface
            fun onRetrieveVisitorData(newVisitorData: String?) {
              if (!newVisitorData.isNullOrBlank()) {
                visitorData = newVisitorData
              }
            }

            @JavascriptInterface
            fun onRetrieveDataSyncId(newDataSyncId: String?) {
              if (!newDataSyncId.isNullOrBlank()) {
                dataSyncId = newDataSyncId.substringBefore("||")
              }
            }
          },
          "Android"
        )
        webView = this

        cookieManager.removeAllCookies(null)
        cookieManager.flush()
        loadUrl("https://accounts.google.com/ServiceLogin?continue=https%3A%2F%2Fmusic.youtube.com")
      }
    }
  )

  TopAppBar(
    title = { Text(stringResource(R.string.login)) },
    navigationIcon = {
      IconButton(onClick = navController::navigateUp, onLongClick = navController::backToMain) {
        Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
      }
    }
  )

  BackHandler(enabled = webView?.canGoBack() == true) { webView?.goBack() }
}
