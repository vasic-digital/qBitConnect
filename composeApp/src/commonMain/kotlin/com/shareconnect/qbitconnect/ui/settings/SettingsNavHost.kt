package com.shareconnect.qbitconnect.ui.settings

import androidx.compose.animation.EnterTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.shareconnect.qbitconnect.model.ServerConfig
import com.shareconnect.qbitconnect.registerCurrentScreenTelemetry
import com.shareconnect.qbitconnect.ui.components.PlatformNavHost
import com.shareconnect.qbitconnect.ui.main.DeepLinkDestination
import com.shareconnect.qbitconnect.ui.main.Destination
import com.shareconnect.qbitconnect.ui.settings.addeditserver.AddEditServerKeys
import com.shareconnect.qbitconnect.ui.settings.addeditserver.AddEditServerResult
import com.shareconnect.qbitconnect.ui.settings.addeditserver.AddEditServerScreen
import com.shareconnect.qbitconnect.ui.settings.addeditserver.advanced.AdvancedServerSettingsKeys
import com.shareconnect.qbitconnect.ui.settings.addeditserver.advanced.AdvancedServerSettingsScreen
import com.shareconnect.qbitconnect.ui.settings.appearance.AppearanceSettingsScreen
import com.shareconnect.qbitconnect.ui.settings.general.GeneralSettingsScreen
import com.shareconnect.qbitconnect.ui.settings.network.NetworkSettingsScreen
import com.shareconnect.qbitconnect.ui.settings.servers.ServersScreen
import com.shareconnect.qbitconnect.utils.DefaultTransitions
import com.shareconnect.qbitconnect.utils.getSerializable
import com.shareconnect.qbitconnect.utils.navigateWithLifecycle
import com.shareconnect.qbitconnect.utils.serializableNavType
import com.shareconnect.qbitconnect.utils.setSerializable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.reflect.typeOf

@Composable
fun SettingsNavHost(
    navigateToStartFlow: Flow<Unit>,
    settingsDeepLinkFlow: Flow<DeepLinkDestination>,
    onShowNotificationPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    registerCurrentScreenTelemetry(navController)

    LaunchedEffect(settingsDeepLinkFlow) {
        settingsDeepLinkFlow.collect { destination ->
            when (destination) {
                DeepLinkDestination.Settings -> {
                    navController.popBackStack<Destination.Settings.Main>(inclusive = false)
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(navigateToStartFlow) {
        navigateToStartFlow.collectLatest {
            navController.popBackStack<Destination.Settings.Main>(inclusive = false)
        }
    }

    PlatformNavHost(
        navController = navController,
        startDestination = Destination.Settings.Main,
        enterTransition = DefaultTransitions.Enter,
        exitTransition = DefaultTransitions.Exit,
        popEnterTransition = DefaultTransitions.PopEnter,
        popExitTransition = DefaultTransitions.PopExit,
        modifier = modifier,
    ) {
        composable<Destination.Settings.Main>(
            enterTransition = { EnterTransition.None },
            exitTransition = DefaultTransitions.Exit,
            popEnterTransition = DefaultTransitions.PopEnter,
            popExitTransition = DefaultTransitions.PopExit,
        ) {
            SettingsScreen(
                onNavigateToServerSettings = {
                    navController.navigateWithLifecycle(Destination.Settings.Server)
                },
                onNavigateToGeneralSettings = {
                    navController.navigateWithLifecycle(Destination.Settings.General)
                },
                onNavigateToAppearanceSettings = {
                    navController.navigateWithLifecycle(Destination.Settings.Appearance)
                },
                onNavigateToNetworkSettings = {
                    navController.navigateWithLifecycle(Destination.Settings.Network)
                },
            )
        }

        composable<Destination.Settings.Server> {
            val addEditServerChannel = remember { Channel<AddEditServerResult>() }
            val addEditServerResult = it.savedStateHandle.get<AddEditServerResult>(AddEditServerKeys.Result)
            LaunchedEffect(addEditServerResult) {
                if (addEditServerResult != null) {
                    addEditServerChannel.send(addEditServerResult)
                    it.savedStateHandle.remove<AddEditServerResult>(AddEditServerKeys.Result)
                }
            }

            ServersScreen(
                addEditServerFlow = addEditServerChannel.receiveAsFlow(),
                onNavigateBack = { navController.navigateUp() },
                onNavigateToAddEditServer = { serverId ->
                    navController.navigateWithLifecycle(Destination.Settings.AddEditServer(serverId))
                },
            )
        }

        composable<Destination.Settings.AddEditServer> {
            val args = it.toRoute<Destination.Settings.AddEditServer>()

            val advancedSettingsChannel = remember { Channel<ServerConfig.AdvancedSettings>() }
            val advancedSettings = it.savedStateHandle.getSerializable<ServerConfig.AdvancedSettings?>(
                AdvancedServerSettingsKeys.AdvancedSettings,
            )
            LaunchedEffect(advancedSettings) {
                if (advancedSettings != null) {
                    advancedSettingsChannel.send(advancedSettings)

                    it.savedStateHandle.remove<ServerConfig.AdvancedSettings?>(
                        AdvancedServerSettingsKeys.AdvancedSettings,
                    )
                }
            }

            AddEditServerScreen(
                serverId = args.serverId,
                advancedSettingsFlow = advancedSettingsChannel.receiveAsFlow(),
                onNavigateBack = { result ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(AddEditServerKeys.Result, result)

                    navController.navigateUp()

                    if (result == AddEditServerResult.Add) {
                        onShowNotificationPermission()
                    }
                },
                onNavigateToAdvancedSettings = { advancedSettings ->
                    navController.navigateWithLifecycle(Destination.Settings.Advanced(advancedSettings))
                },
            )
        }

        composable<Destination.Settings.Advanced>(
            typeMap = mapOf(
                typeOf<ServerConfig.AdvancedSettings>() to
                    serializableNavType<ServerConfig.AdvancedSettings>(),
            ),
        ) {
            val args = it.toRoute<Destination.Settings.Advanced>()
            AdvancedServerSettingsScreen(
                advancedSettings = args.advancedSettings,
                onNavigateBack = { navController.navigateUp() },
                onUpdate = { advancedSettings ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.setSerializable(AdvancedServerSettingsKeys.AdvancedSettings, advancedSettings)
                },
            )
        }

        composable<Destination.Settings.General> {
            GeneralSettingsScreen(
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Destination.Settings.Appearance> {
            AppearanceSettingsScreen(
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Destination.Settings.Network> {
            NetworkSettingsScreen(
                onNavigateBack = { navController.navigateUp() },
            )
        }
    }
}
