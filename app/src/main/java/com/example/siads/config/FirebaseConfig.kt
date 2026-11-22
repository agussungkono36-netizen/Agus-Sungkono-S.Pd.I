package com.example.siads.config

import android.content.Context

/**
 * Mode sumber data untuk memisahkan data demo preview dari database produksi Firebase.
 */
enum class DataEnvironmentMode {
    DEMO_PREVIEW,
    PRODUCTION_FIREBASE
}

/**
 * Nama koleksi resmi Firestore sesuai rancangan SIADS SD Negeri 1 Ngantru.
 */
object FirestoreCollections {
    const val USERS = "users"
    const val TEACHERS = "teachers"
    const val CATEGORIES = "categories"
    const val DOCUMENTS = "documents"
    const val ACCREDITATION_STANDARDS = "accreditationStandards"
    const val EVIDENCE = "evidence"
    const val VERIFICATIONS = "verifications"
    const val ACTIVITY_LOGS = "activityLogs"

    val allCollections: List<String> = listOf(
        USERS,
        TEACHERS,
        CATEGORIES,
        DOCUMENTS,
        ACCREDITATION_STANDARDS,
        EVIDENCE,
        VERIFICATIONS,
        ACTIVITY_LOGS
    )
}

/**
 * Metadata status kesiapan Firebase.
 * Tidak menyimpan kredensial atau API key palsu di dalam kode sumber.
 * Untuk mengaktifkan Firebase di tahap produksi:
 * 1. Hubungkan project Firebase resmi dan unduh file google-services.json ke modul app/.
 * 2. Aktifkan dependensi firebase-firestore & firebase-auth di app/build.gradle.kts.
 * 3. Terapkan Firestore Security Rules sesuai FirestoreSchemaContract.
 */
data class FirebaseIntegrationStatus(
    val isGoogleServicesPresent: Boolean,
    val isFirebaseSdkEnabled: Boolean,
    val activeMode: DataEnvironmentMode,
    val statusTitle: String,
    val statusDescription: String
)

object FirebaseConfig {

    /**
     * Memeriksa secara aman apakah konfigurasi Firebase (google_app_id dari google-services.json)
     * sudah tersedia di resource aplikasi tanpa memasukkan kredensial palsu.
     */
    fun inspectStatus(context: Context): FirebaseIntegrationStatus {
        val googleAppIdRes = context.resources.getIdentifier(
            "google_app_id",
            "string",
            context.packageName
        )
        val hasGoogleServices = googleAppIdRes != 0 &&
            runCatching { context.getString(googleAppIdRes).isNotBlank() }.getOrDefault(false)

        val sdkEnabled = runCatching {
            Class.forName("com.google.firebase.firestore.FirebaseFirestore")
            true
        }.getOrDefault(false)

        val isReadyForProduction = hasGoogleServices && sdkEnabled

        return FirebaseIntegrationStatus(
            isGoogleServicesPresent = hasGoogleServices,
            isFirebaseSdkEnabled = sdkEnabled,
            activeMode = if (isReadyForProduction) {
                DataEnvironmentMode.PRODUCTION_FIREBASE
            } else {
                DataEnvironmentMode.DEMO_PREVIEW
            },
            statusTitle = if (isReadyForProduction) {
                "Firebase Terkonfigurasi"
            } else {
                "Mode Preview Demo (Firebase Belum Dikonfigurasi)"
            },
            statusDescription = if (isReadyForProduction) {
                "google-services.json & SDK Firestore terdeteksi."
            } else {
                "Menunggu file google-services.json resmi dan aktivasi dependensi Firebase. " +
                    "Seluruh data saat ini berjalan di sandbox Demo Preview yang terpisah dari produksi."
            }
        )
    }
}
