package com.novafiles

object AccessManager {

    enum class AccessMode {
        NORMAL,
        SHIZUKU,
        ROOT
    }

    /**
     * Cihazdaki kullanılabilir en yüksek erişim seviyesini belirler.
     *
     * Öncelik:
     * ROOT > SHIZUKU > NORMAL
     */
    fun getCurrentMode(): AccessMode {

        return when {

            RootManager.isRootAvailable() ->
                AccessMode.ROOT

            ShizukuManager.canExecutePrivilegedCommand() ->
                AccessMode.SHIZUKU

            else ->
                AccessMode.NORMAL
        }
    }

    /**
     * Mevcut erişim seviyesini kullanıcıya gösterilecek
     * kısa bir metne dönüştürür.
     */
    fun getStatus(): String {

        return when (getCurrentMode()) {

            AccessMode.ROOT ->
                "Root aktif"

            AccessMode.SHIZUKU ->
                "Shizuku aktif"

            AccessMode.NORMAL -> {

                if (PermissionManager.hasStorageAccess()) {
                    "Normal erişim"
                } else {
                    "Depolama izni gerekli"
                }
            }
        }
    }

    /**
     * Root kullanılabiliyor mu?
     */
    fun hasRootAccess(): Boolean {
        return RootManager.isRootAvailable()
    }

    /**
     * Shizuku kullanılabiliyor mu?
     */
    fun hasShizukuAccess(): Boolean {
        return ShizukuManager.canExecutePrivilegedCommand()
    }

    /**
     * Normal depolama erişimi mevcut mu?
     */
    fun hasNormalStorageAccess(): Boolean {
        return PermissionManager.hasStorageAccess()
    }

    /**
     * Herhangi bir dosya erişimi kullanılabilir mi?
     */
    fun hasAnyAccess(): Boolean {

        return hasRootAccess() ||
            hasShizukuAccess() ||
            hasNormalStorageAccess()
    }

    /**
     * Sistem seviyesinde işlem yapılabiliyor mu?
     *
     * Root veya Shizuku gerekir.
     */
    fun hasPrivilegedAccess(): Boolean {

        return hasRootAccess() ||
            hasShizukuAccess()
    }

    /**
     * Kullanılabilir erişim yöntemlerini döndürür.
     */
    fun getAvailableModes(): List<AccessMode> {

        val modes = mutableListOf<AccessMode>()

        if (hasNormalStorageAccess()) {
            modes.add(AccessMode.NORMAL)
        }

        if (hasShizukuAccess()) {
            modes.add(AccessMode.SHIZUKU)
        }

        if (hasRootAccess()) {
            modes.add(AccessMode.ROOT)
        }

        return modes
    }

    /**
     * Dosya silme gibi ayrıcalıklı bir işlem için
     * hangi backend'in kullanılacağını belirler.
     */
    fun getPreferredMode(): AccessMode {

        return getCurrentMode()
    }

    /**
     * Kullanıcıya gösterilecek erişim açıklaması.
     */
    fun getDescription(): String {

        return when (getCurrentMode()) {

            AccessMode.ROOT ->
                "Root erişimi ile gelişmiş sistem işlemleri kullanılabilir."

            AccessMode.SHIZUKU ->
                "Shizuku erişimi ile gelişmiş sistem işlemleri kullanılabilir."

            AccessMode.NORMAL -> {

                if (PermissionManager.hasStorageAccess()) {
                    "Standart Android dosya erişimi kullanılabilir."
                } else {
                    "Dosya işlemleri için depolama erişimi gerekiyor."
                }
            }
        }
    }

    /**
     * Sistem erişimini yenilemek için kullanılacak
     * basit yardımcı fonksiyon.
     */
    fun refresh(): AccessMode {
        return getCurrentMode()
    }
}
