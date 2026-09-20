package com.novafiles

import java.io.BufferedReader
import java.io.InputStreamReader

object RootManager {

    /**
     * Cihazda root erişimi mevcut mu?
     */
    fun isRootAvailable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(
                arrayOf("su", "-c", "id")
            )

            val output = process
                .inputStream
                .bufferedReader()
                .use { it.readText() }

            val error = process
                .errorStream
                .bufferedReader()
                .use { it.readText() }

            val exitCode = process.waitFor()

            exitCode == 0 &&
                output.contains("uid=0") &&
                error.isBlank()

        } catch (_: Exception) {
            false
        }
    }

    /**
     * Root durumunu metin olarak verir.
     */
    fun getStatus(): String {
        return if (isRootAvailable()) {
            "Root aktif"
        } else {
            "Root bulunamadı"
        }
    }

    /**
     * Root üzerinden tek komut çalıştırır.
     *
     * Önemli:
     * Komut doğrudan root shell'e gider.
     * UI katmanı bu fonksiyona kullanıcı girdisini
     * doğrudan vermemeli; ileride güvenli command
     * builder katmanı ekleyeceğiz.
     */
    fun executeCommand(
        command: String
    ): CommandResult {

        if (command.isBlank()) {
            return CommandResult(
                success = false,
                output = "",
                error = "Komut boş."
            )
        }

        return try {

            val process = Runtime.getRuntime().exec(
                arrayOf(
                    "su",
                    "-c",
                    command
                )
            )

            val output = process
                .inputStream
                .bufferedReader()
                .use { reader ->
                    reader.readText()
                }

            val error = process
                .errorStream
                .bufferedReader()
                .use { reader ->
                    reader.readText()
                }

            val exitCode = process.waitFor()

            CommandResult(
                success = exitCode == 0,
                output = output,
                error = error,
                exitCode = exitCode
            )

        } catch (exception: Exception) {

            CommandResult(
                success = false,
                output = "",
                error = exception.message
                    ?: "Root komutu çalıştırılamadı."
            )
        }
    }

    /**
     * Root shell'in gerçekten kullanılabilir olup olmadığını
     * kontrol eder.
     */
    fun canExecutePrivilegedCommand(): Boolean {
        return isRootAvailable()
    }

    /**
     * Root üzerinden dosya var mı kontrolü.
     */
    fun fileExists(
        path: String
    ): Boolean {

        if (path.isBlank()) {
            return false
        }

        val result = executeCommand(
            "test -e '${escapeShellArgument(path)}'"
        )

        return result.success
    }

    /**
     * Root üzerinden dosya/klasör siler.
     */
    fun delete(
        path: String
    ): Boolean {

        if (path.isBlank()) {
            return false
        }

        val escapedPath =
            escapeShellArgument(path)

        val result = executeCommand(
            "rm -rf -- '$escapedPath'"
        )

        return result.success
    }

    /**
     * Root üzerinden klasör oluşturur.
     */
    fun createDirectory(
        path: String
    ): Boolean {

        if (path.isBlank()) {
            return false
        }

        val escapedPath =
            escapeShellArgument(path)

        val result = executeCommand(
            "mkdir -p -- '$escapedPath'"
        )

        return result.success
    }

    /**
     * Shell özel karakterlerinden temel olarak kaçış yapar.
     */
    private fun escapeShellArgument(
        value: String
    ): String {
        return value
            .replace("\\", "\\\\")
            .replace("'", "'\\''")
    }

    data class CommandResult(
        val success: Boolean,
        val output: String,
        val error: String,
        val exitCode: Int = -1
    )
}
