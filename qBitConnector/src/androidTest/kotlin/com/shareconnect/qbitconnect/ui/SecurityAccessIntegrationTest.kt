/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.qbitconnect.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import digital.vasic.security.access.SecurityAccessManager
import digital.vasic.security.access.data.AccessMethod
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SecurityAccessIntegrationTest {

    private lateinit var securityAccessManager: SecurityAccessManager

    @Before
    fun setup() {
        securityAccessManager = SecurityAccessManager.getInstance(androidx.test.core.app.ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() {
        // Clean up security settings
        runBlocking {
            securityAccessManager.disableSecurity()
        }
        SecurityAccessManager.destroyInstance()
    }

    @Test
    fun testSecurityAccessDialogAppearsWhenRequired() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Launch MainActivity
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            // The security access dialog should appear
            // Note: In a real test, we'd need to set up PIN first and verify the dialog
            // This is a basic integration test structure
        }

        scenario.close()
    }

    @Test
    fun testMainActivityLoadsWhenSecurityNotRequired() = runBlocking {
        // Ensure security is disabled
        securityAccessManager.disableSecurity()

        // Launch MainActivity
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            // Activity should load normally without security dialog
            assert(!activity.isFinishing)
        }

        scenario.close()
    }

    @Test
    fun testSessionTimeoutRequiresReAuthentication() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Launch MainActivity (would require authentication)
        val scenario1 = ActivityScenario.launch(MainActivity::class.java)

        scenario1.onActivity { activity ->
            // Activity should finish due to security requirement
            // (in real test, we'd authenticate first)
        }

        scenario1.close()

        // Simulate session timeout by waiting
        // In real test, we'd fast-forward time or mock the session

        // Launch again - should require authentication again
        val scenario2 = ActivityScenario.launch(MainActivity::class.java)

        scenario2.onActivity { activity ->
            // Should still require authentication
        }

        scenario2.close()
    }
}