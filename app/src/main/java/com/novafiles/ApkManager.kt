package com.novafiles

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import java.io.File

object ApkManager {

    data class ApkInfo(
        val packageName: String,
        val appName: String,
        val versionName: String,
        val versionCode: Long,
        val minSdk: Int,
        val targetSdk: Int,
        val sourcePath: String,
        val sizeBytes: Long,
        val isSystemApp: Boolean,
        val isUpdatedSystemApp: Boolean
    )

    /**
     * Verilen APK dosyasının bilgilerini okur.
     */
    fun getApkInfo(
        context: Context,
        apkFile: File
    ): ApkInfo? {

        if (!apkFile.exists() || !apkFile.isFile) {
            return null
        }

        if (!apkFile.name.endsWith(
                ".apk",
                ignoreCase = true
            )
        ) {
            return null
        }

        return try {

            val packageManager =
                context.packageManager

            val packageInfo =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    packageManager.getPackageArchiveInfo(
                        apkFile.absolutePath,
                        PackageManager.PackageInfoFlags.of(
                            PackageManager.GET_META_DATA.toLong()
                        )
                    )

                } else {

                    @Suppress("DEPRECATION")
                    packageManager.getPackageArchiveInfo(
                        apkFile.absolutePath,
                        PackageManager.GET_META_DATA
                    )
                }
                    ?: return null

            val applicationInfo =
                packageInfo.applicationInfo
                    ?: return null

            applicationInfo.sourceDir =
                apkFile.absolutePath

            applicationInfo.publicSourceDir =
                apkFile.absolutePath

            val appName =
                packageManager
                    .getApplicationLabel(applicationInfo)
                    .toString()

            ApkInfo(
                packageName =
                    packageInfo.packageName,

                appName =
                    appName,

                versionName =
                    packageInfo.versionName
                        ?: "Bilinmiyor",

                versionCode =
                    getVersionCode(packageInfo),

                minSdk =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        applicationInfo.minSdkVersion
                    } else {
                        0
                    },

                targetSdk =
                    applicationInfo.targetSdkVersion,

                sourcePath =
                    apkFile.absolutePath,

                sizeBytes =
                    apkFile.length(),

                isSystemApp =
                    applicationInfo.flags and
                        ApplicationInfo.FLAG_SYSTEM != 0,

                isUpdatedSystemApp =
                    applicationInfo.flags and
                        ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0
            )

        } catch (_: Exception) {

            null
        }
    }

    /**
     * Cihazdaki kurulu uygulamaları listeler.
     */
    fun getInstalledApps(
        context: Context
    ): List<PackageInfo> {

        val packageManager =
            context.packageManager

        return try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                packageManager.getInstalledPackages(
                    PackageManager.PackageInfoFlags.of(0)
                )

            } else {

                @Suppress("DEPRECATION")
                packageManager.getInstalledPackages(0)
            }

        } catch (_: Exception) {

            emptyList()
        }
    }

    /**
     * APK dosyası mı?
     */
    fun isApk(
        file: File
    ): Boolean {

        return file.isFile &&
            file.extension.equals(
                "apk",
                ignoreCase = true
            )
    }

    /**
     * APK'nın boyutunu okunabilir biçimde verir.
     */
    fun formatSize(
        bytes: Long
    ): String {
        return FileManager.formatSize(bytes)
    }

    /**
     * Paket adından cihazda kurulu olup olmadığını kontrol eder.
     */
    fun isInstalled(
        context: Context,
        packageName: String
    ): Boolean {

        if (packageName.isBlank()) {
            return false
        }

        return try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )

            } else {

                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    packageName,
                    0
                )
            }

            true

        } catch (_: PackageManager.NameNotFoundException) {

            false

        } catch (_: Exception) {

            false
        }
    }

    /**
     * Kurulu uygulamanın APK yolunu verir.
     */
    fun getInstalledApkPath(
        context: Context,
        packageName: String
    ): String? {

        return try {

            val packageInfo =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    context.packageManager.getPackageInfo(
                        packageName,
                        PackageManager.PackageInfoFlags.of(0)
                    )

                } else {

                    @Suppress("DEPRECATION")
                    context.packageManager.getPackageInfo(
                        packageName,
                        0
                    )
                }

            packageInfo.applicationInfo
                ?.sourceDir

        } catch (_: Exception) {

            null
        }
    }

    /**
     * Sürüm kodunu Android sürümünden bağımsız okur.
     */
    private fun getVersionCode(
        packageInfo: PackageInfo
    ): Long {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            packageInfo.longVersionCode

        } else {

            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
    }
}
