package com.adamkuraczynski.focusfortress.permissions

/**
 * Provides descriptions for specific permissions.
 *
 * This interface defines methods to retrieve descriptions for display
 * in the permission item and the permission dialog. Implementations
 * of this interface supply the specific text related to each permission.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.6
 *
 * @see UsageAccessPermissionTextProvider
 * @see OverlayPermissionTextProvider
 * @see AccessibilityPermissionTextProvider
 * @see NotificationPermissionTextProvider
 */
interface PermissionTextProvider {
    /**
     * Returns a short description for the permission item.
     *
     * @return A string describing the permission.
     */
    fun getItemDescription(): String

    /**
     * Returns a detailed description for the permission dialog.
     *
     * @return A string with a detailed explanation of why the permission is needed.
     */
    fun getDialogDescription(): String
}

/**
 * Provides text descriptions for the Usage Access permission.
 */
class UsageAccessPermissionTextProvider : PermissionTextProvider {
    override fun getItemDescription(): String {
        return "Monitor your app usage."
    }

    override fun getDialogDescription(): String {
        return "We need Usage Access permission to monitor app usage for better insights."
    }
}

/**
 * Provides text descriptions for the Overlay permission.
 */
class OverlayPermissionTextProvider : PermissionTextProvider {
    override fun getItemDescription(): String {
        return "Display over other apps."
    }

    override fun getDialogDescription(): String {
        return "We need permission to display over other apps to provide overlay features."
    }
}

/**
 * Provides text descriptions for the Accessibility permission.
 */
class AccessibilityPermissionTextProvider : PermissionTextProvider {
    override fun getItemDescription(): String {
        return "Monitor device's screen."
    }

    override fun getDialogDescription(): String {
        return "We need Accessibility permission to allow app blocking and screen monitoring."
    }
}

/**
 * Provides text descriptions for the Notification permission.
 */
class NotificationPermissionTextProvider : PermissionTextProvider {
    override fun getItemDescription(): String {
        return "Allow app to send notifications."
    }

    override fun getDialogDescription(): String {
        return "We need permission to send you important notifications related to app functionality."
    }
}