package me.tahzam23.schoolpower.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tahzam23.schoolpower.PasswordManager
import me.tahzam23.schoolpower.scraper.SchoolPowerScraper

class SchoolPowerRequestWorker(
    private val passwordManager: PasswordManager,
    private val client: HttpClient,
    private val schoolPowerScraper: SchoolPowerScraper,
    appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val loginInformation = passwordManager.getLoginDetails()
                ?: return@withContext Result.failure()

            val success = try {
                schoolPowerScraper.scrape(client, loginInformation)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

            return@withContext if (success) {
                Result.success()
            }
            else {
                Result.failure()
            }
        }
    }

}