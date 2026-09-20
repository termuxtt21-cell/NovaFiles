package com.novafiles

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import rikka.shizuku.Shizuku

object ShizukuManager {

    private const val REQUEST_CODE = 1001

    /**
     * Shizuku servisi çalışıyor mu?
     */
    fun isAvailable(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Shizuku izni verilmiş mi?
     */
    fun hasPermission(): Boolean {

        if (!isAvailable()) {
            return false
        }

        return try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Shizuku.checkSelfPermission() ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
            } else {
                false
            }

        } catch (_: Exception) {
            false
        }
    }

    /**
     * Shizuku izin isteğini gönderir.
     */
    fun requestPermission() {

        if (!isAvailable()) {
            return
        }

        try {
            Shizuku.requestPermission(
                REQUEST_CODE
            )
        } catch (_: Exception) {
            // Shizuku mevcut değilse sessizce devam.
        }
    }

    /**
     * Shizuku durumunu metin olarak verir.
     */
    fun getStatus(): String {

        return when {

            !isAvailable() ->
                "Shizuku çalışmıyor"

            !hasPermission() ->
                "İzin gerekli"

            else ->
                "Shizuku aktif"
        }
    }

    /**
     * Yetkili komut çalıştırmadan önce kullanılacak kontrol.
     */
    fun canExecutePrivilegedCommand(): Boolean {
        return isAvailable() && hasPermission()
    }

    /**
     * Shizuku üzerinden shell komutu çalıştırır.
     *
     * Bu temel katmandır.
     * Daha sonra terminal, APK yöneticisi,
     * dosya işlemleri ve Root backend'i bunun
     * üzerine bağlanabilir.
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

        if (!canExecutePrivilegedCommand()) {
            return CommandResult(
                success = false,
                output = "",
                error = getStatus()
            )
        }

        return try {

            val method = Shizuku::class.java.getDeclaredMethod(
    "newProcess",
    Array<String>::class.java,
    Array<String>::class.java,
    String::class.java
)

method.isAccessible = true

val process =
    method.invoke(
        null,
        arrayOf(
            "sh",
            "-c",
            command
        ),
        null,
        null
    ) as rikka.shizuku.ShizukuRemoteProcess

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
                    ?: "Komut çalıştırılamadı."
            )
        }
    }

    data class CommandResult(
        val success: Boolean,
        val output: String,
        val error: String,
        val exitCode: Int = -1
    )
}
