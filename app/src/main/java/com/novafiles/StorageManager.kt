package com.novafiles

import android.os.StatFs
import java.io.File
import java.util.Locale

object StorageManager {

    data class StorageInfo(
        val totalBytes: Long,
        val usedBytes: Long,
        val availableBytes: Long
    ) {
        val usagePercent: Float
            get() {
                if (totalBytes <= 0L) {
                    return 0f
                }

                return (
                    usedBytes.toDouble() /
                        totalBytes.toDouble()
                    ).toFloat()
                        .coerceIn(0f, 1f)
            }
    }

    /**
     * Dahili ortak depolamanın gerçek kapasitesini okur.
     */
    fun getInternalStorageInfo(): StorageInfo {

        val directory = FileManager.getInternalStorage()

        return try {

            val statFs = StatFs(
                directory.absolutePath
            )

            val blockSize = statFs.blockSizeLong
            val totalBlocks = statFs.blockCountLong
            val availableBlocks = statFs.availableBlocksLong

            val totalBytes =
                totalBlocks * blockSize

            val availableBytes =
                availableBlocks * blockSize

            val usedBytes =
                (totalBytes - availableBytes)
                    .coerceAtLeast(0L)

            StorageInfo(
                totalBytes = totalBytes,
                usedBytes = usedBytes,
                availableBytes = availableBytes
            )

        } catch (_: Exception) {

            StorageInfo(
                totalBytes = 0L,
                usedBytes = 0L,
                availableBytes = 0L
            )
        }
    }

    /**
     * Byte değerini okunabilir biçime dönüştürür.
     */
    fun formatBytes(bytes: Long): String {

        if (bytes < 0L) {
            return "0 B"
        }

        if (bytes < 1024L) {
            return "$bytes B"
        }

        val kb = bytes / 1024.0

        if (kb < 1024.0) {
            return String.format(
                Locale.US,
                "%.1f KB",
                kb
            )
        }

        val mb = kb / 1024.0

        if (mb < 1024.0) {
            return String.format(
                Locale.US,
                "%.1f MB",
                mb
            )
        }

        val gb = mb / 1024.0

        if (gb < 1024.0) {
            return String.format(
                Locale.US,
                "%.1f GB",
                gb
            )
        }

        val tb = gb / 1024.0

        return String.format(
            Locale.US,
            "%.1f TB",
            tb
        )
    }

    /**
     * Toplam alanı okunabilir biçimde verir.
     */
    fun getTotalFormatted(): String {
        return formatBytes(
            getInternalStorageInfo().totalBytes
        )
    }

    /**
     * Kullanılan alanı okunabilir biçimde verir.
     */
    fun getUsedFormatted(): String {
        return formatBytes(
            getInternalStorageInfo().usedBytes
        )
    }

    /**
     * Boş alanı okunabilir biçimde verir.
     */
    fun getAvailableFormatted(): String {
        return formatBytes(
            getInternalStorageInfo().availableBytes
        )
    }

    /**
     * Kullanım oranını 0..1 aralığında döndürür.
     */
    fun getUsagePercent(): Float {
        return getInternalStorageInfo().usagePercent
    }
}
