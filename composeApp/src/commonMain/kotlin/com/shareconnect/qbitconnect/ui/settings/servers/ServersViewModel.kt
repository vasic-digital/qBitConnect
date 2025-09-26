package com.shareconnect.qbitconnect.ui.settings.servers

import androidx.lifecycle.ViewModel
import com.shareconnect.qbitconnect.data.ServerManager

class ServersViewModel(
    private val serverManager: ServerManager,
) : ViewModel() {
    val servers = serverManager.serversFlow

    fun reorderServer(from: Int, to: Int) = serverManager.reorderServer(from, to)
}
