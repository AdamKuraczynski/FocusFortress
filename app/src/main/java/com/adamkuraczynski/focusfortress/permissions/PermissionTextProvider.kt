package com.adamkuraczynski.focusfortress.permissions

/**
 * Provides descriptions for a specific permission.
 *
 * This interface defines methods to retrieve descriptions for display
 * in the permission item and the permission dialog. Implementations
 * of this interface supply the specific text related to each permission.
 *
 * @author Adam Kuraczyński
 * @version 1.6
 *
 **/

interface PermissionTextProvider {
    fun getItemDescription(): String
    fun getDialogDescription(): String
}

class UsageAccessPermissionTextProvider : PermissionTextProvider {
    override fun getItemDescription(): String {
        return "Monitor your app usage."
    }

    override fun getDialogDescription(): String {
        return "We need Usage Access permission to monitor app usage for better insights."
    }
}

class OverlayPermissionTextProvider : PermissionTextProvider {
    override fun getItemDescription(): String {
        return "Display content over other apps."
    }

    override fun getDialogDescription(): String {
        return "We need permission to display over other apps to provide overlay features."
    }
}

class NotificationPermissionTextProvider : PermissionTextProvider {
    override fun getItemDescription(): String {
        return "Access your notifications."
    }

    override fun getDialogDescription(): String {
        return "We need Notification Access to monitor and manage your notifications."
    }
}

class AccessibilityPermissionTextProvider : PermissionTextProvider {
    override fun getItemDescription(): String {
        return "Monitor your device's screen."
    }

    override fun getDialogDescription(): String {
        return "We need Accessibility permission to allow app blocking and screen monitoring."
    }

}

