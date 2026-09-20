package com.novafiles

import java.io.File

object TextEditorManager {

    data class EditorDocument(
        val file: File,
        val content: String,
        val lineCount: Int,
        val sizeBytes: Long,
        val extension: String
    )

    /**
     * Metin dosyasını UTF-8 olarak açar.
     */
    fun open(file: File): EditorDocument? {

        if (!file.exists() || !file.isFile) {
            return null
        }

        return try {

            val content = file
                .readText(Charsets.UTF_8)

            EditorDocument(
                file = file,
                content = content,
                lineCount = countLines(content),
                sizeBytes = file.length(),
                extension = file.extension.lowercase()
            )

        } catch (_: Exception) {

            null
        }
    }

    /**
     * Metni UTF-8 olarak kaydeder.
     */
    fun save(
        file: File,
        content: String
    ): Boolean {

        return try {

            file.parentFile?.mkdirs()

            file.writeText(
                content,
                Charsets.UTF_8
            )

            true

        } catch (_: Exception) {

            false
        }
    }

    /**
     * Dosyanın NovaFiles Text Editor ile
     * açılabilecek bir metin dosyası olup olmadığını belirler.
     */
    fun isTextFile(file: File): Boolean {

        if (!file.isFile) {
            return false
        }

        return when (file.extension.lowercase()) {

            "txt",
            "xml",
            "json",
            "java",
            "kt",
            "kts",
            "js",
            "jsx",
            "ts",
            "tsx",
            "html",
            "htm",
            "css",
            "scss",
            "smali",
            "gradle",
            "properties",
            "pro",
            "md",
            "yaml",
            "yml",
            "ini",
            "cfg",
            "conf",
            "sh",
            "bat",
            "log",
            "sql",
            "c",
            "cpp",
            "h",
            "hpp",
            "rs",
            "py",
            "php",
            "go"
            -> true

            else -> false
        }
    }

    /**
     * Satır sayısını hesaplar.
     */
    fun countLines(
        content: String
    ): Int {

        if (content.isEmpty()) {
            return 1
        }

        return content.count {
            it == '\n'
        } + 1
    }

    /**
     * Belirli bir metni arar.
     *
     * Sonuçlar sıfır tabanlı karakter konumlarıdır.
     */
    fun find(
        content: String,
        query: String,
        ignoreCase: Boolean = true
    ): List<Int> {

        if (query.isEmpty()) {
            return emptyList()
        }

        val results = mutableListOf<Int>()

        var startIndex = 0

        while (startIndex < content.length) {

            val index =
                content.indexOf(
                    string = query,
                    startIndex = startIndex,
                    ignoreCase = ignoreCase
                )

            if (index < 0) {
                break
            }

            results.add(index)

            startIndex =
                index + query.length.coerceAtLeast(1)
        }

        return results
    }

    /**
     * Metin içinde değiştirme yapar.
     */
    fun replace(
        content: String,
        search: String,
        replacement: String,
        ignoreCase: Boolean = true
    ): String {

        if (search.isEmpty()) {
            return content
        }

        return if (ignoreCase) {

            Regex(
                Regex.escape(search),
                RegexOption.IGNORE_CASE
            ).replace(
                content,
                replacement
            )

        } else {

            content.replace(
                search,
                replacement
            )
        }
    }

    /**
     * Satır numarasını karakter konumundan bulur.
     */
    fun getLineNumber(
        content: String,
        characterIndex: Int
    ): Int {

        if (characterIndex <= 0) {
            return 1
        }

        val safeIndex =
            characterIndex.coerceAtMost(
                content.length
            )

        return content
            .take(safeIndex)
            .count { it == '\n' } + 1
    }

    /**
     * Bir satırı getirir.
     */
    fun getLine(
        content: String,
        lineNumber: Int
    ): String? {

        if (lineNumber < 1) {
            return null
        }

        return content
            .lines()
            .getOrNull(lineNumber - 1)
    }

    /**
     * Otomatik girinti için temel girinti seviyesini bulur.
     */
    fun detectIndent(
        line: String
    ): String {

        return line
            .takeWhile {
                it == ' ' || it == '\t'
            }
    }

    /**
     * Dosyanın sözdizimi türünü belirler.
     */
    fun getLanguage(
        file: File
    ): String {

        return when (file.extension.lowercase()) {

            "kt", "kts" -> "Kotlin"
            "java" -> "Java"
            "xml" -> "XML"
            "json" -> "JSON"
            "js", "jsx" -> "JavaScript"
            "ts", "tsx" -> "TypeScript"
            "html", "htm" -> "HTML"
            "css", "scss" -> "CSS"
            "smali" -> "Smali"
            "py" -> "Python"
            "c" -> "C"
            "cpp", "hpp" -> "C++"
            "rs" -> "Rust"
            "go" -> "Go"
            "php" -> "PHP"
            "sql" -> "SQL"
            "sh" -> "Shell"
            "md" -> "Markdown"
            "yaml", "yml" -> "YAML"
            "txt" -> "Plain Text"

            else -> "Plain Text"
        }
    }
}
