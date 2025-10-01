package com.shareconnect.qbitconnect.di

import org.junit.Assert.assertNotNull
import org.junit.Test

class AppModuleTest {

    @Test
    fun `appModule should be defined`() {
        // Test that the module can be instantiated
        assertNotNull(appModule)
    }

    @Test
    fun `platformModule should be defined`() {
        // Test that the platform module can be instantiated
        assertNotNull(platformModule)
    }
}