/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.home

import android.graphics.Bitmap
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.UUID

/**
 * Unit tests for the screenshot store. Though HomeTileScreenshotStoreIntegrationTest exists,
 * we run these as unit tests to conserve resources.
 *
 * We could improve the tests by additionally testing:
 * - Read/write locking works correctly.
 */
@RunWith(RobolectricTestRunner::class)
class HomeTileScreenshotStoreUnitTest {

    private lateinit var uuid: UUID

    @Before
    fun setUp() {
        RuntimeEnvironment.application.filesDir.listFiles().forEach { it.deleteRecursively() }
        uuid = UUID.randomUUID()
    }

    /** Assumes [HomeTileScreenshotStore.getFileForUUID] works correctly. */
    @Test
    fun testSaveAsyncDoesNotOverwrite() = runBlocking {
        val context = RuntimeEnvironment.application
        HomeTileScreenshotStore.saveAsync(context, uuid, getBitmap()).join()
        HomeTileScreenshotStore.saveAsync(context, UUID.randomUUID(), getBitmap()).join()

        assertEquals(2,
                HomeTileScreenshotStore.getFileForUUID(context, uuid).parentFile.list().size)
    }

    @Test
    fun testReadFileDoesNotExist() {
        val actualBitmap = HomeTileScreenshotStore.read(RuntimeEnvironment.application, uuid)
        assertNull(actualBitmap)
    }
}

private fun getBitmap() = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
