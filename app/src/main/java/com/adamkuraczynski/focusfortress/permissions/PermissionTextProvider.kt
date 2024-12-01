package com.adamkuraczynski.focusfortress.permissions


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
        return "Display over other apps."
    }

    override fun getDialogDescription(): String {
        return "We need permission to display over other apps to provide overlay features."
    }
}


class AccessibilityPermissionTextProvider : PermissionTextProvider {
    override fun getItemDescription(): String {
        return "Monitor device's screen."
    }

    override fun getDialogDescription(): String {
        return "We need Accessibility permission to allow app blocking and screen monitoring."
    }
}


class NotificationPermissionTextProvider : PermissionTextProvider {
    override fun getItemDescription(): String {
        return "Allow app to send notifications."
    }

    override fun getDialogDescription(): String {
        return "We need permission to send you important notifications related to app functionality."
    }
}