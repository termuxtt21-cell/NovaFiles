package com.novafiles

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

object TerminalManager {

    enum class ExecutionMode {
        NORMAL,
        SHIZUKU,
        ROOT
    }

    data class TerminalResult(
        val success: Boolean,
        val output: String,
        val error: String,
        val exitCode: Int
    )

    fun getAvailableMode(): ExecutionMode {

        return when {
            RootManager.isRootAvailable() ->
                ExecutionMode.ROOT

            ShizukuManager.canExecutePrivilegedCommand() ->
                ExecutionMode.SHIZUKU

            else ->
                ExecutionMode.NORMAL
        }
    }

    fun getStatus(): String {

        return when (getAvailableMode()) {

            ExecutionMode.ROOT ->
                "Root terminal aktif"

            ExecutionMode.SHIZUKU ->
                "Shizuku terminal aktif"

            ExecutionMode.NORMAL ->
                "Normal terminal"
        }
    }

    fun execute(
        command: String
    ): TerminalResult {

        if (command.isBlank()) {
            return TerminalResult(
                success = false,
                output = "",
                error = "Komut boş.",
                exitCode = -1
            )
        }

        return when (getAvailableMode()) {

            ExecutionMode.ROOT ->
                executeRoot(command)

            ExecutionMode.SHIZUKU ->
                executeShizuku(command)

            ExecutionMode.NORMAL ->
                executeNormal(command)
        }
    }

    private fun executeNormal(
        command: String
    ): TerminalResult {

        return executeProcess(
            arrayOf(
                "sh",
                "-c",
                command
            )
        )
    }

    private fun executeRoot(
        command: String
    ): TerminalResult {

        return try {

            val result =
                RootManager.executeCommand(command)

            TerminalResult(
                success = result.success,
                output = result.output,
                error = result.error,
                exitCode = result.exitCode
            )

        } catch (exception: Exception) {

            TerminalResult(
                success = false,
                output = "",
                error = exception.message
                    ?: "Root komutu çalıştırılamadı.",
                exitCode = -1
            )
        }
    }

    private fun executeShizuku(
        command: String
    ): TerminalResult {

        return try {

            val result =
                ShizukuManager.executeCommand(command)

            TerminalResult(
                success = result.success,
                output = result.output,
                error = result.error,
                exitCode = result.exitCode
            )

        } catch (exception: Exception) {

            /*
             * ShizukuManager'ın mevcut sürümünde
             * executeCommand bulunmuyorsa normal shell'e
             * düşmemek için kontrollü hata döndürülür.
             */
            TerminalResult(
                success = false,
                output = "",
                error = exception.message
                    ?: "Shizuku komutu çalıştırılamadı.",
                exitCode = -1
            )
        }
    }

    private fun executeProcess(
        command: Array<String>
    ): TerminalResult {

        return try {

            val process =
                ProcessBuilder(*command)
                    .redirectErrorStream(false)
                    .start()

            val outputReader =
                BufferedReader(
                    InputStreamReader(
                        process.inputStream
                    )
                )

            val errorReader =
                BufferedReader(
                    InputStreamReader(
                        process.errorStream
                    )
                )

            val output =
                outputReader
                    .readText()

            val error =
                errorReader
                    .readText()

            val finished =
                process.waitFor(
                    30,
                    TimeUnit.SECONDS
                )

            if (!finished) {

                process.destroyForcibly()

                return TerminalResult(
                    success = false,
                    output = output,
                    error = "Komut zaman aşımına uğradı.",
                    exitCode = -1
                )
            }

            val exitCode =
                process.exitValue()

            TerminalResult(
                success = exitCode == 0,
                output = output,
                error = error,
                exitCode = exitCode
            )

        } catch (exception: Exception) {

            TerminalResult(
                success = false,
                output = "",
                error = exception.message
                    ?: "Terminal komutu çalıştırılamadı.",
                exitCode = -1
            )
        }
    }

    fun canUseRoot(): Boolean {
        return RootManager.isRootAvailable()
    }

    fun canUseShizuku(): Boolean {
        return ShizukuManager.canExecutePrivilegedCommand()
    }

    fun canUseNormalShell(): Boolean {
        return true
    }
}
