package com.shareconnect.qbitconnect.di

import com.shareconnect.qbitconnect.data.ConfigMigrator
import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.data.notification.TorrentDownloadedNotifier
import com.shareconnect.qbitconnect.data.repositories.AddTorrentRepository
import com.shareconnect.qbitconnect.data.repositories.TorrentListRepository
import com.shareconnect.qbitconnect.data.repositories.log.LogRepository
import com.shareconnect.qbitconnect.data.repositories.rss.EditRssRuleRepository
import com.shareconnect.qbitconnect.data.repositories.rss.RssArticlesRepository
import com.shareconnect.qbitconnect.data.repositories.rss.RssFeedRepository
import com.shareconnect.qbitconnect.data.repositories.rss.RssRulesRepository
import com.shareconnect.qbitconnect.data.repositories.search.SearchPluginsRepository
import com.shareconnect.qbitconnect.data.repositories.search.SearchResultRepository
import com.shareconnect.qbitconnect.data.repositories.search.SearchStartRepository
import com.shareconnect.qbitconnect.data.repositories.torrent.TorrentFilesRepository
import com.shareconnect.qbitconnect.data.repositories.torrent.TorrentOverviewRepository
import com.shareconnect.qbitconnect.data.repositories.torrent.TorrentPeersRepository
import com.shareconnect.qbitconnect.data.repositories.torrent.TorrentTrackersRepository
import com.shareconnect.qbitconnect.data.repositories.torrent.TorrentWebSeedsRepository
import com.shareconnect.qbitconnect.network.ImageLoaderProvider
import com.shareconnect.qbitconnect.network.RequestManager
import com.shareconnect.qbitconnect.network.UpdateChecker
import com.shareconnect.qbitconnect.ui.addtorrent.AddTorrentViewModel
import com.shareconnect.qbitconnect.ui.log.LogViewModel
import com.shareconnect.qbitconnect.ui.rss.articles.RssArticlesViewModel
import com.shareconnect.qbitconnect.ui.rss.editrule.EditRssRuleViewModel
import com.shareconnect.qbitconnect.ui.rss.feeds.RssFeedsViewModel
import com.shareconnect.qbitconnect.ui.rss.rules.RssRulesViewModel
import com.shareconnect.qbitconnect.ui.search.plugins.SearchPluginsViewModel
import com.shareconnect.qbitconnect.ui.search.result.SearchResultViewModel
import com.shareconnect.qbitconnect.ui.search.start.SearchStartViewModel
import com.shareconnect.qbitconnect.ui.settings.addeditserver.AddEditServerViewModel
import com.shareconnect.qbitconnect.ui.settings.appearance.AppearanceSettingsViewModel
import com.shareconnect.qbitconnect.ui.settings.general.GeneralSettingsViewModel
import com.shareconnect.qbitconnect.ui.settings.network.NetworkSettingsViewModel
import com.shareconnect.qbitconnect.ui.settings.servers.ServersViewModel
import com.shareconnect.qbitconnect.ui.torrent.tabs.files.TorrentFilesViewModel
import com.shareconnect.qbitconnect.ui.torrent.tabs.overview.TorrentOverviewViewModel
import com.shareconnect.qbitconnect.ui.torrent.tabs.peers.TorrentPeersViewModel
import com.shareconnect.qbitconnect.ui.torrent.tabs.trackers.TorrentTrackersViewModel
import com.shareconnect.qbitconnect.ui.torrent.tabs.webseeds.TorrentWebSeedsViewModel
import com.shareconnect.qbitconnect.ui.torrentlist.TorrentListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    includes(platformModule)

    singleOf(::RequestManager)
    single { SettingsManager(get(named("settings"))) }
    single { ServerManager(get(named("servers"))) }
    singleOf(::TorrentDownloadedNotifier)
    single { ConfigMigrator(get(named("settings")), get(named("servers"))) }
    singleOf(::ImageLoaderProvider)
    singleOf(::UpdateChecker)

    singleOf(::TorrentListRepository)

    singleOf(::TorrentOverviewRepository)
    singleOf(::TorrentFilesRepository)
    singleOf(::TorrentTrackersRepository)
    singleOf(::TorrentPeersRepository)
    singleOf(::TorrentWebSeedsRepository)

    singleOf(::AddTorrentRepository)

    singleOf(::RssFeedRepository)
    singleOf(::RssArticlesRepository)
    singleOf(::RssRulesRepository)
    singleOf(::EditRssRuleRepository)

    singleOf(::SearchStartRepository)
    singleOf(::SearchResultRepository)
    singleOf(::SearchPluginsRepository)

    singleOf(::LogRepository)

    viewModelOf(::TorrentListViewModel)

    viewModel { (serverId: Int, hash: String) -> TorrentOverviewViewModel(serverId, hash, get(), get(), get()) }
    viewModel { (serverId: Int, hash: String) -> TorrentFilesViewModel(serverId, hash, get(), get()) }
    viewModel { (serverId: Int, hash: String) -> TorrentTrackersViewModel(serverId, hash, get(), get()) }
    viewModel { (serverId: Int, hash: String) -> TorrentPeersViewModel(serverId, hash, get(), get(), get()) }
    viewModel { (serverId: Int, hash: String) -> TorrentWebSeedsViewModel(serverId, hash, get(), get()) }

    viewModel { (initialServerId: Int?) -> AddTorrentViewModel(initialServerId, get(), get(), get()) }

    viewModel { (serverId: Int) -> RssFeedsViewModel(serverId, get()) }
    viewModel { (serverId: Int, feedPath: List<String>, uid: String?) ->
        RssArticlesViewModel(serverId, feedPath, uid, get(), get())
    }
    viewModel { (serverId: Int) -> RssRulesViewModel(serverId, get()) }
    viewModel { (serverId: Int, ruleName: String) -> EditRssRuleViewModel(serverId, ruleName, get(), get()) }

    viewModel { (serverId: Int) -> SearchStartViewModel(serverId, get()) }
    viewModel { (serverId: Int, searchQuery: String, category: String, plugins: String) ->
        SearchResultViewModel(serverId, searchQuery, category, plugins, get(), get(), get())
    }
    viewModel { (serverId: Int) -> SearchPluginsViewModel(serverId, get()) }

    viewModel { (serverId: Int) -> LogViewModel(serverId, get()) }

    viewModelOf(::ServersViewModel)
    viewModel { (serverId: Int?) -> AddEditServerViewModel(serverId, get(), get()) }
    viewModelOf(::GeneralSettingsViewModel)
    viewModelOf(::AppearanceSettingsViewModel)
    viewModelOf(::NetworkSettingsViewModel)
}

expect val platformModule: Module
