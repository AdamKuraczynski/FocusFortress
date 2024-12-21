package com.adamkuraczynski.focusfortress.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.adamkuraczynski.focusfortress.FocusFortressApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MotivationWorker (
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val blockedAppDao = FocusFortressApp.database.blockedAppDao()
            val blockedWebsiteDao = FocusFortressApp.database.blockedWebsiteDao()
            val blockedKeywordDao = FocusFortressApp.database.blockedKeywordDao()

            val appCount = withContext(Dispatchers.IO) {
                blockedAppDao.getBlockedAppsCount()
            }

            val websiteCount = withContext(Dispatchers.IO) {
                blockedWebsiteDao.getBlockedWebsitesCount()
            }

            val keywordCount = withContext(Dispatchers.IO) {
                blockedKeywordDao.getBlockedKeywordsCount()
            }

            val dailyMotivations = listOf(
                "How about upgrading your Fortress?",
                "Come visit FocusFortress, lets work together!",
                "You’ve blocked $appCount apps, how about blocking some more?",
                "You’ve blocked $websiteCount websites, do you have another one in mind?",
                "You’ve  blocked $keywordCount keywords, let's add a couple extra!",
            )

            val message = dailyMotivations.random()

            val notificationHelper = NotificationHelper(applicationContext)
            notificationHelper.motivationNotification(message)

            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }
}