package com.novafiles

import java.io.File

object StorageAnalyzer {

    data class StorageNode(
        val file: File,
        val sizeBytes: Long,
        val isDirectory: Boolean,
        val children: List<StorageNode> = emptyList()
    )

    data class FileTypeSummary(
        val extension: String,
        val count: Int,
        val sizeBytes: Long
    )

    data class AnalysisResult(
        val root: File,
        val totalBytes: Long,
        val fileCount: Long,
        val directoryCount: Long,
        val largestFiles: List<File>,
        val fileTypes: List<FileTypeSummary>
    )

    /**
     * Belirtilen klasörü analiz eder.
     */
    fun analyze(
        root: File,
        maxDepth: Int = 20
    ): AnalysisResult {

        if (!root.exists() || !root.isDirectory) {
            return AnalysisResult(
                root = root,
                totalBytes = 0L,
                fileCount = 0L,
                directoryCount = 0L,
                largestFiles = emptyList(),
                fileTypes = emptyList()
            )
        }

        val files = mutableListOf<File>()
        val typeMap = mutableMapOf<String, Pair<Int, Long>>()

        var totalBytes = 0L
        var fileCount = 0L
        var directoryCount = 0L

        fun scan(
            directory: File,
            depth: Int
        ) {

            if (depth > maxDepth) {
                return
            }

            val children = try {
                directory.listFiles()
            } catch (_: Exception) {
                null
            } ?: return

            for (child in children) {

                try {

                    if (child.isDirectory) {

                        directoryCount++

                        scan(
                            child,
                            depth + 1
                        )

                    } else if (child.isFile) {

                        val size = child.length()

                        totalBytes += size
                        fileCount++

                        files.add(child)

                        val extension =
                            child.extension
                                .lowercase()
                                .ifBlank {
                                    "[uzantısız]"
                                }

                        val current =
                            typeMap[extension]
                                ?: (0 to 0L)

                        typeMap[extension] =
                            (current.first + 1) to
                                (current.second + size)
                    }

                } catch (_: Exception) {
                    // Erişilemeyen dosyalar atlanır.
                }
            }
        }

        scan(root, 0)

        val largestFiles =
            files
                .sortedByDescending {
                    try {
                        it.length()
                    } catch (_: Exception) {
                        0L
                    }
                }
                .take(20)

        val fileTypes =
            typeMap
                .map { entry ->

                    FileTypeSummary(
                        extension = entry.key,
                        count = entry.value.first,
                        sizeBytes = entry.value.second
                    )
                }
                .sortedByDescending {
                    it.sizeBytes
                }

        return AnalysisResult(
            root = root,
            totalBytes = totalBytes,
            fileCount = fileCount,
            directoryCount = directoryCount,
            largestFiles = largestFiles,
            fileTypes = fileTypes
        )
    }

    /**
     * Belirli klasörlerin boyutlarını hesaplar.
     */
    fun getDirectorySizes(
        directory: File
    ): List<StorageNode> {

        if (!directory.exists() || !directory.isDirectory) {
            return emptyList()
        }

        return try {

            directory
                .listFiles()
                ?.mapNotNull { child ->

                    try {

                        StorageNode(
                            file = child,
                            sizeBytes = calculateSize(child),
                            isDirectory = child.isDirectory
                        )

                    } catch (_: Exception) {

                        null
                    }
                }
                ?.sortedByDescending {
                    it.sizeBytes
                }
                ?: emptyList()

        } catch (_: Exception) {

            emptyList()
        }
    }

    /**
     * Dosya veya klasörün toplam boyutunu hesaplar.
     */
    fun calculateSize(
        file: File
    ): Long {

        if (!file.exists()) {
            return 0L
        }

        if (file.isFile) {
            return try {
                file.length()
            } catch (_: Exception) {
                0L
            }
        }

        return try {

            file.listFiles()
                ?.sumOf {
                    calculateSize(it)
                }
                ?: 0L

        } catch (_: Exception) {

            0L
        }
    }

    /**
     * Boyutu okunabilir biçime çevirir.
     */
    fun formatSize(
        bytes: Long
    ): String {
        return FileManager.formatSize(bytes)
    }

    /**
     * Boyut yüzdesini hesaplar.
     */
    fun calculatePercentage(
        value: Long,
        total: Long
    ): Float {

        if (total <= 0L) {
            return 0f
        }

        return (
            value.toDouble() /
                total.toDouble()
            )
            .toFloat()
            .coerceIn(0f, 1f)
    }
}
