package me.tahzam23.schoolpower.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tahzam23.schoolpower.PasswordManager
import me.tahzam23.schoolpower.data.RequestInformation
import me.tahzam23.schoolpower.html.DocumentCreator
import me.tahzam23.schoolpower.login

class SchoolPowerRequestWorker(
    private val passwordManager: PasswordManager,
    private val requestInformation: RequestInformation,
    private val client: HttpClient,
    private val documentCreator: DocumentCreator,
    appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val loginInformation = passwordManager.getLoginDetails()
                ?: return@withContext Result.failure()

            val success = login(requestInformation, client, loginInformation, documentCreator)
            return@withContext if (success) {
                Result.success()
            }
            else {
                Result.failure()
            }
        }
    }

}