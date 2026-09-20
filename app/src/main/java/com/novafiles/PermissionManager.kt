package com.novafiles

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings

object PermissionManager {

    /**
     * NovaFiles'ın geniş depolama erişimine sahip olup olmadığını
     * kontrol eder.
     */
    fun hasStorageAccess(): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true
        }
    }

    /**
     * Android Ayarlarındaki "Tüm dosyalara erişim" ekranını açar.
     */
    fun openStorageAccessSettings(
        context: Context
    ) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return
        }

        try {

            val intent = Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            ).apply {
                data = Uri.parse(
                    "package:${context.packageName}"
                )
            }

            context.startActivity(intent)

        } catch (_: Exception) {

            // Bazı üretici ROM'larında uygulamaya özel
            // ekran açılamazsa genel depolama ayarını aç.
            try {

                val fallbackIntent = Intent(
                    Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                )

                context.startActivity(
                    fallbackIntent
                )

            } catch (_: Exception) {
                // Ayarlar uygulaması açılamazsa sessizce devam et.
            }
        }
    }

    /**
     * Depolama erişimi gerekiyorsa Ayarlar ekranını açar.
     *
     * true  -> zaten izin var
     * false -> Ayarlar açıldı
     */
    fun requestStorageAccess(
        context: Context
    ): Boolean {

        if (hasStorageAccess()) {
            return true
        }

        openStorageAccessSettings(context)

        return false
    }

    /**
     * Uygulamanın depolama erişimi durumunu
     * kullanıcıya gösterilecek metne dönüştürür.
     */
    fun getStorageAccessStatus(): String {

        return if (hasStorageAccess()) {
            "Erişim açık"
        } else {
            "Erişim gerekli"
        }
    }

    /**
     * İzin durumuna göre kısa açıklama.
     */
    fun getStorageAccessDescription(): String {

        return if (hasStorageAccess()) {
            "NovaFiles depolama alanına erişebiliyor."
        } else {
            "Dosyaları yönetmek için depolama erişimini aç."
        }
    }
}
