package com.novafiles

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

object UpdateManager {

    private const val UPDATE_JSON_URL =
        "https://raw.githubusercontent.com/termuxtt21-cell/NovaFiles/main/update.json"

    data class UpdateInfo(
        val versionCode: Int,
        val versionName: String,
        val downloadUrl: String,
        val sha256: String,
        val changelog: String
    )

    data class DownloadResult(
        val success: Boolean,
        val file: File?,
        val error: String? = null
    )

    fun checkForUpdate(): UpdateInfo? {

        return try {

            val connection =
                URL(UPDATE_JSON_URL)
                    .openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = 10_000
            connection.readTimeout = 15_000
            connection.instanceFollowRedirects = true

            if (connection.responseCode !in 200..299) {

                connection.disconnect()

                return null
            }

            val json =
                connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

            connection.disconnect()

            val objectJson =
                JSONObject(json)

            UpdateInfo(
                versionCode =
                    objectJson.getInt("versionCode"),

                versionName =
                    objectJson.getString("versionName"),

                downloadUrl =
                    objectJson.getString("downloadUrl"),

                sha256 =
                    objectJson.optString(
                        "sha256",
                        ""
                    ),

                changelog =
                    objectJson.optString(
                        "changelog",
                        ""
                    )
            )

        } catch (_: Exception) {

            null
        }
    }

    fun isNewerVersion(
        updateInfo: UpdateInfo
    ): Boolean {

        return updateInfo.versionCode >
            BuildConfig.VERSION_CODE
    }

    fun downloadApk(
        context: Context,
        updateInfo: UpdateInfo,
        onProgress: ((Int) -> Unit)? = null
    ): DownloadResult {

        return try {

            val connection =
                URL(updateInfo.downloadUrl)
                    .openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = 15_000
            connection.readTimeout = 30_000
            connection.instanceFollowRedirects = true

            if (connection.responseCode !in 200..299) {

                connection.disconnect()

                return DownloadResult(
                    success = false,
                    file = null,
                    error =
                        "APK indirilemedi. HTTP ${connection.responseCode}"
                )
            }

            val totalBytes =
                connection.contentLengthLong

            val updateDirectory =
                File(
                    context.cacheDir,
                    "updates"
                )

            if (!updateDirectory.exists()) {
                updateDirectory.mkdirs()
            }

            val apkFile =
                File(
                    updateDirectory,
                    "NovaFiles-${updateInfo.versionName}.apk"
                )

            connection.inputStream.use { input ->

                FileOutputStream(apkFile).use { output ->

                    val buffer =
                        ByteArray(16 * 1024)

                    var downloadedBytes = 0L

                    while (true) {

                        val count =
                            input.read(buffer)

                        if (count == -1) {
                            break
                        }

                        output.write(
                            buffer,
                            0,
                            count
                        )

                        downloadedBytes += count

                        if (totalBytes > 0) {

                            val progress =
                                (
                                    downloadedBytes * 100 /
                                        totalBytes
                                    )
                                    .toInt()
                                    .coerceIn(
                                        0,
                                        100
                                    )

                            onProgress?.invoke(
                                progress
                            )
                        }
                    }
                }
            }

            connection.disconnect()

            DownloadResult(
                success = true,
                file = apkFile
            )

        } catch (exception: Exception) {

            DownloadResult(
                success = false,
                file = null,
                error =
                    exception.message
                        ?: "APK indirilirken hata oluştu."
            )
        }
    }

    fun calculateSha256(
        file: File
    ): String {

        if (!file.exists() || !file.isFile) {
            return ""
        }

        return try {

            val digest =
                MessageDigest.getInstance(
                    "SHA-256"
                )

            file.inputStream().use { input ->

                val buffer =
                    ByteArray(16 * 1024)

                while (true) {

                    val count =
                        input.read(buffer)

                    if (count == -1) {
                        break
                    }

                    digest.update(
                        buffer,
                        0,
                        count
                    )
                }
            }

            digest.digest().joinToString("") {
                "%02x".format(it)
            }

        } catch (_: Exception) {

            ""
        }
    }

    fun verifySha256(
        file: File,
        expectedSha256: String
    ): Boolean {

        if (expectedSha256.isBlank()) {
            return false
        }

        val actual =
            calculateSha256(file)

        return actual.equals(
            expectedSha256.trim(),
            ignoreCase = true
        )
    }

    fun installApk(
        context: Context,
        apkFile: File
    ): Boolean {

        if (!apkFile.exists() || !apkFile.isFile) {
            return false
        }

        return try {

            val authority =
                "${context.packageName}.fileprovider"

            val apkUri =
                FileProvider.getUriForFile(
                    context,
                    authority,
                    apkFile
                )

            val intent =
                Intent(
                    Intent.ACTION_INSTALL_PACKAGE
                ).apply {

                    data = apkUri

                    addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                }

            context.startActivity(intent)

            true

        } catch (_: Exception) {

            false
        }
    }

    fun canInstallPackages(
        context: Context
    ): Boolean {

        return if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            context.packageManager
                .canRequestPackageInstalls()

        } else {

            true
        }
    }

    fun openInstallPermissionSettings(
        context: Context
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val intent =
                Intent(
                    android.provider.Settings
                        .ACTION_MANAGE_UNKNOWN_APP_SOURCES
                ).apply {

                    data =
                        Uri.parse(
                            "package:${context.packageName}"
                        )
                }

            context.startActivity(intent)
        }
    }
}
