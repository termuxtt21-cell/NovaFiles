package com.novafiles

import android.os.Environment
import java.io.File

/**
 * NovaFiles - Gerçek dosya sistemi yöneticisi.
 *
 * Bu sınıf UI'dan bağımsız tutulur.
 * Daha sonra Shizuku ve Root backend'leri
 * buraya eklenebilir.
 */
object FileManager {

    /**
     * Dahili ortak depolamanın kök dizini.
     *
     * Örnek:
     * /storage/emulated/0
     */
    fun getInternalStorage(): File {
        return Environment.getExternalStorageDirectory()
    }

    /**
     * Verilen klasördeki dosya ve klasörleri getirir.
     *
     * Klasörler önce, dosyalar sonra gelir.
     * İsim sıralaması alfabetiktir.
     */
    fun listFiles(directory: File): List<File> {

        if (!directory.exists()) {
            return emptyList()
        }

        if (!directory.isDirectory) {
            return emptyList()
        }

        return try {
            directory
                .listFiles()
                ?.filter { it.exists() }
                ?.sortedWith(
                    compareBy<File> {
                        !it.isDirectory
                    }.thenBy {
                        it.name.lowercase()
                    }
                )
                ?: emptyList()
        } catch (_: SecurityException) {
            emptyList()
        }
    }

    /**
     * Klasör oluşturur.
     */
    fun createDirectory(
        parent: File,
        name: String
    ): File? {

        val folderName = name.trim()

        if (folderName.isEmpty()) {
            return null
        }

        val directory = File(parent, folderName)

        return try {
            if (directory.exists()) {
                null
            } else if (directory.mkdirs()) {
                directory
            } else {
                null
            }
        } catch (_: SecurityException) {
            null
        }
    }

    /**
     * Dosya oluşturur.
     */
    fun createFile(
        parent: File,
        name: String
    ): File? {

        val fileName = name.trim()

        if (fileName.isEmpty()) {
            return null
        }

        val file = File(parent, fileName)

        return try {
            if (file.exists()) {
                null
            } else if (file.createNewFile()) {
                file
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Dosya veya klasörü yeniden adlandırır.
     */
    fun rename(
        file: File,
        newName: String
    ): File? {

        val cleanName = newName.trim()

        if (cleanName.isEmpty()) {
            return null
        }

        val parent = file.parentFile ?: return null
        val destination = File(parent, cleanName)

        if (destination.exists()) {
            return null
        }

        return try {
            if (file.renameTo(destination)) {
                destination
            } else {
                null
            }
        } catch (_: SecurityException) {
            null
        }
    }

    /**
     * Dosya veya klasörü siler.
     *
     * Klasörse içerisindeki tüm dosyalar
     * ve alt klasörler de silinir.
     */
    fun delete(file: File): Boolean {

        if (!file.exists()) {
            return false
        }

        return try {

            if (file.isDirectory) {

                file.listFiles()?.forEach { child ->
                    delete(child)
                }
            }

            file.delete()

        } catch (_: SecurityException) {
            false
        }
    }

    /**
     * Dosya boyutunu byte olarak döndürür.
     */
    fun getSize(file: File): Long {

        if (!file.exists()) {
            return 0L
        }

        return try {

            if (file.isFile) {
                file.length()
            } else {
                calculateDirectorySize(file)
            }

        } catch (_: Exception) {
            0L
        }
    }

    /**
     * Klasör boyutunu hesaplar.
     */
    private fun calculateDirectorySize(
        directory: File
    ): Long {

        var total = 0L

        try {

            directory.listFiles()?.forEach { file ->

                total += if (file.isDirectory) {
                    calculateDirectorySize(file)
                } else {
                    file.length()
                }
            }

        } catch (_: Exception) {
            return total
        }

        return total
    }

    /**
     * Kullanılabilir alan.
     */
    fun getAvailableSpace(): Long {
        return try {
            getInternalStorage().usableSpace
        } catch (_: Exception) {
            0L
        }
    }

    /**
     * Toplam alan.
     */
    fun getTotalSpace(): Long {
        return try {
            getInternalStorage().totalSpace
        } catch (_: Exception) {
            0L
        }
    }

    /**
     * Kullanılan alan.
     */
    fun getUsedSpace(): Long {

        val total = getTotalSpace()
        val available = getAvailableSpace()

        return if (total > 0L) {
            total - available
        } else {
            0L
        }
    }

    /**
     * Dosya/klasörün gizli olup olmadığını döndürür.
     */
    fun isHidden(file: File): Boolean {
        return file.isHidden
    }

    /**
     * Dosya mı?
     */
    fun isFile(file: File): Boolean {
        return file.isFile
    }

    /**
     * Klasör mü?
     */
    fun isDirectory(file: File): Boolean {
        return file.isDirectory
    }

    /**
     * Dosyanın uzantısını getirir.
     *
     * Örnek:
     * example.apk -> apk
     */
    fun getExtension(file: File): String {

        if (!file.isFile) {
            return ""
        }

        val name = file.name
        val dotIndex = name.lastIndexOf('.')

        return if (
            dotIndex > 0 &&
            dotIndex < name.length - 1
        ) {
            name.substring(dotIndex + 1)
                .lowercase()
        } else {
            ""
        }
    }

    /**
     * İnsan tarafından okunabilir dosya boyutu.
     */
    fun formatSize(bytes: Long): String {

        if (bytes < 1024) {
            return "$bytes B"
        }

        val kb = bytes / 1024.0

        if (kb < 1024) {
            return String.format(
                "%.1f KB",
                kb
            )
        }

        val mb = kb / 1024.0

        if (mb < 1024) {
            return String.format(
                "%.1f MB",
                mb
            )
        }

        val gb = mb / 1024.0

        if (gb < 1024) {
            return String.format(
                "%.1f GB",
                gb
            )
        }

        val tb = gb / 1024.0

        return String.format(
            "%.1f TB",
            tb
        )
    }
}
