package com.adamkuraczynski.focusfortress.achievements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.database.Achievement
import com.adamkuraczynski.focusfortress.database.FocusFortressApp
import com.adamkuraczynski.focusfortress.service.NotificationHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AchievementsViewModel(application: Application) : AndroidViewModel(application) {

    private val achievementDao = FocusFortressApp.database.achievementDao()
    private val blockedAppDao = FocusFortressApp.database.blockedAppDao()
    private val blockedWebsiteDao = FocusFortressApp.database.blockedWebsiteDao()
    private val blockedKeywordDao = FocusFortressApp.database.blockedKeywordDao()
    private val passcodeDao = FocusFortressApp.database.passcodeDao()

    val allAchievements: StateFlow<List<Achievement>> = achievementDao.getAllAchievements()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private fun insertAchievements(achievements: List<Achievement>) {
        viewModelScope.launch {
            achievementDao.deleteAllAchievements()
            for (achievement in achievements) {
                achievementDao.insertAchievement(achievement)
            }
        }
    }

    init {
        viewModelScope.launch {
            val existingAchievements = achievementDao.getAllAchievements().stateIn(viewModelScope).value
            if (existingAchievements.isEmpty()) {
                insertAchievements(getAchievements())
            }
        }

        viewModelScope.launch {
            blockedAppDao.getBlockedApps().collect { blockedApps ->
                checkAchievements(blockedAppsCount = blockedApps.size)
            }
        }

        viewModelScope.launch {
            blockedWebsiteDao.getBlockedWebsites().collect { blockedWebsites ->
                checkAchievements(blockedWebsitesCount = blockedWebsites.size)
            }
        }

        viewModelScope.launch {
            blockedKeywordDao.getBlockedKeywords().collect { blockedKeywords ->
                checkAchievements(blockedKeywordsCount = blockedKeywords.size)
            }
        }

        viewModelScope.launch {
            passcodeDao.getPasscode().collect { passcode ->
                val hasPasscode = passcode != null
                checkAchievements(hasPasscode = hasPasscode)
            }
        }
    }

    private fun markAchievementCompleted(achievement: Achievement) {
        viewModelScope.launch {
            val updated = achievement.copy(isCompleted = true)
            achievementDao.updateAchievement(updated)
            notifyAchievementUnlocked(updated.title)
        }
    }

    private suspend fun checkAchievements(
        blockedAppsCount: Int? = null,
        blockedWebsitesCount: Int? = null,
        blockedKeywordsCount: Int? = null,
        hasPasscode: Boolean? = null
    ) {
        val achievements = allAchievements.value

        for (achievement in achievements) {
            if (!achievement.isCompleted) {
                when (achievement.title) {
                    "Keyword Guardian" -> {
                        if ((blockedKeywordsCount ?: getBlockedKeywordsCount()) >= 3) {
                            markAchievementCompleted(achievement)
                        }
                    }
                    "App Guardian" -> {
                        if ((blockedAppsCount ?: getBlockedAppsCount()) >= 3) {
                            markAchievementCompleted(achievement)
                        }
                    }
                    "Web Guardian" -> {
                        if ((blockedWebsitesCount ?: getBlockedWebsitesCount()) >= 3) {
                            markAchievementCompleted(achievement)
                        }
                    }
                    "Keyword Sentinel" -> {
                        if ((blockedKeywordsCount ?: getBlockedKeywordsCount()) >= 10) {
                            markAchievementCompleted(achievement)
                        }
                    }
                    "App Sentinel" -> {
                        if ((blockedAppsCount ?: getBlockedAppsCount()) >= 10) {
                            markAchievementCompleted(achievement)
                        }
                    }
                    "Web Sentinel" -> {
                        if ((blockedWebsitesCount ?: getBlockedWebsitesCount()) >= 10) {
                            markAchievementCompleted(achievement)
                        }
                    }
                    "Gatekeeper" -> {
                        if (hasPasscode == true || checkIfPasscodeExists()) {
                            markAchievementCompleted(achievement)
                        }
                    }
                }
            }
        }
    }

    private suspend fun getBlockedAppsCount(): Int {
        return blockedAppDao.getBlockedAppsCount()
    }

    private suspend fun getBlockedWebsitesCount(): Int {
        return blockedWebsiteDao.getBlockedWebsitesCount()
    }

    private suspend fun getBlockedKeywordsCount(): Int {
        return blockedKeywordDao.getBlockedKeywordsCount()
    }

    private suspend fun checkIfPasscodeExists(): Boolean {
        return passcodeDao.getPasscode().stateIn(viewModelScope).value != null
    }

    private fun notifyAchievementUnlocked(achievementTitle: String) {
        val context = getApplication<Application>().applicationContext
        val helper = NotificationHelper(context)
        helper.showAchievementNotification(achievementTitle)
    }

}

private fun getAchievements(): List<Achievement> {
    return listOf(
        Achievement(
            title = "Keyword Guardian",
            description = "Blocked a total of 3 keywords."
        ),
        Achievement(
            title = "App Guardian",
            description = "Blocked a total of 3 apps."
        ),
        Achievement(
            title = "Web Guardian",
            description = "Blocked a total of 3 websites."
        ),
        Achievement(
            title = "Keyword Sentinel",
            description = "Blocked a total of 10 keywords."
        ),
        Achievement(
            title = "App Sentinel",
            description = "Blocked a total of 10 apps."
        ),
        Achievement(
            title = "Web Sentinel",
            description = "Blocked a total of 10 websites."
        ),
        Achievement(
            title = "Gatekeeper",
            description = "Set a Protected strictness level."
        )
    )
}