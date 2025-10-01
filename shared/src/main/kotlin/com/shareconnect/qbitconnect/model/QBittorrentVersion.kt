package com.shareconnect.qbitconnect.model

sealed class QBittorrentVersion {
    data object Invalid : QBittorrentVersion()
    data class Valid(val major: Int, val minor: Int, val patch: Int) : QBittorrentVersion()

    companion object {
        fun fromString(versionString: String): QBittorrentVersion {
            val parts = versionString.split(".")
            if (parts.size < 2) return Invalid

            val major = parts[0].toIntOrNull() ?: return Invalid
            val minor = parts[1].toIntOrNull() ?: return Invalid
            val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0

            return Valid(major, minor, patch)
        }
    }
}