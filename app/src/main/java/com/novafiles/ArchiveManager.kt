package com.novafiles

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object ArchiveManager {

    enum class ArchiveType {
        ZIP,
        RAR,
        SEVEN_ZIP,
        TAR,
        GZIP,
        UNKNOWN
    }

    data class ArchiveInfo(
        val file: File,
        val type: ArchiveType,
        val sizeBytes: Long
    )

    fun getArchiveType(file: File): ArchiveType {
        if (!file.isFile) {
            return ArchiveType.UNKNOWN
        }

        return when (file.extension.lowercase()) {
            "zip" -> ArchiveType.ZIP
            "rar" -> ArchiveType.RAR
            "7z" -> ArchiveType.SEVEN_ZIP
            "tar" -> ArchiveType.TAR
            "gz", "gzip" -> ArchiveType.GZIP
            else -> ArchiveType.UNKNOWN
        }
    }

    fun isArchive(file: File): Boolean {
        return getArchiveType(file) != ArchiveType.UNKNOWN
    }

    fun getArchiveInfo(file: File): ArchiveInfo? {
        val type = getArchiveType(file)

        if (type == ArchiveType.UNKNOWN) {
            return null
        }

        return ArchiveInfo(
            file = file,
            type = type,
            sizeBytes = file.length()
        )
    }

    /**
     * ZIP dosyasının içerisindeki dosya ve klasörleri listeler.
     */
    fun listZipEntries(
        archive: File
    ): List<String> {

        if (!archive.exists() || !archive.isFile) {
            return emptyList()
        }

        if (getArchiveType(archive) != ArchiveType.ZIP) {
            return emptyList()
        }

        val result = mutableListOf<String>()

        return try {

            ZipInputStream(
                archive.inputStream().buffered()
            ).use { zipInputStream ->

                var entry: ZipEntry?

                while (true) {

                    entry = zipInputStream.nextEntry

                    if (entry == null) {
                        break
                    }

                    result.add(entry!!.name)

                    zipInputStream.closeEntry()
                }
            }

            result

        } catch (_: Exception) {

            emptyList()
        }
    }

    /**
     * ZIP arşivini belirtilen klasöre çıkarır.
     *
     * Path Traversal koruması uygulanır.
     */
    fun extractZip(
        archive: File,
        destination: File
    ): Boolean {

        if (!archive.exists() || !archive.isFile) {
            return false
        }

        if (getArchiveType(archive) != ArchiveType.ZIP) {
            return false
        }

        return try {

            if (!destination.exists()) {
                destination.mkdirs()
            }

            val canonicalDestination =
                destination.canonicalFile

            ZipInputStream(
                archive.inputStream().buffered()
            ).use { zipInputStream ->

                var entry: ZipEntry?

                while (true) {

                    entry = zipInputStream.nextEntry

                    if (entry == null) {
                        break
                    }

                    val outputFile =
                        File(
                            canonicalDestination,
                            entry!!.name
                        )

                    val canonicalOutput =
                        outputFile.canonicalFile

                    if (!canonicalOutput.path.startsWith(
                            canonicalDestination.path +
                                File.separator
                        ) &&
                        canonicalOutput != canonicalDestination
                    ) {
                        return false
                    }

                    if (entry!!.isDirectory) {

                        canonicalOutput.mkdirs()

                    } else {

                        canonicalOutput.parentFile?.mkdirs()

                        canonicalOutput.outputStream()
                            .buffered()
                            .use { output ->

                                zipInputStream.copyTo(
                                    output
                                )
                            }
                    }

                    zipInputStream.closeEntry()
                }
            }

            true

        } catch (_: Exception) {

            false
        }
    }

    /**
     * Dosya veya klasörleri ZIP haline getirir.
     */
    fun createZip(
        sources: List<File>,
        destination: File
    ): Boolean {

        if (sources.isEmpty()) {
            return false
        }

        return try {

            destination.parentFile?.mkdirs()

            ZipOutputStream(
                destination.outputStream().buffered()
            ).use { zipOutputStream ->

                sources.forEach { source ->

                    if (source.exists()) {

                        addToZip(
                            source = source,
                            zipOutputStream = zipOutputStream,
                            basePath = source.name
                        )
                    }
                }
            }

            destination.exists() &&
                destination.length() > 0L

        } catch (_: Exception) {

            false
        }
    }

    private fun addToZip(
        source: File,
        zipOutputStream: ZipOutputStream,
        basePath: String
    ) {

        if (source.isDirectory) {

            val children =
                source.listFiles()

            if (children.isNullOrEmpty()) {

                zipOutputStream.putNextEntry(
                    ZipEntry(
                        "$basePath/"
                    )
                )

                zipOutputStream.closeEntry()

                return
            }

            children.forEach { child ->

                addToZip(
                    source = child,
                    zipOutputStream = zipOutputStream,
                    basePath = "$basePath/${child.name}"
                )
            }

        } else {

            zipOutputStream.putNextEntry(
                ZipEntry(basePath)
            )

            source.inputStream()
                .buffered()
                .use { input ->

                    input.copyTo(
                        zipOutputStream
                    )
                }

            zipOutputStream.closeEntry()
        }
    }

    fun formatSize(
        bytes: Long
    ): String {
        return FileManager.formatSize(bytes)
    }
}
