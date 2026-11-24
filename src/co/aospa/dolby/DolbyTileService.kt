/*
 * Copyright (C) 2023-24 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log

private const val TAG = "DolbyTileService"

class DolbyTileService : TileService() {

    private val dolbyController by lazy { DolbyController.getInstance(applicationContext) }

    override fun onStartListening() {
        qsTile.apply {
            // The effect throws if the audio HAL is down or restarting.
            try {
                state = if (dolbyController.dsOn) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
                subtitle = dolbyController.getProfileName() ?: getString(R.string.dolby_unknown)
            } catch (e: RuntimeException) {
                Log.w(TAG, "Dolby effect unavailable", e)
                state = Tile.STATE_UNAVAILABLE
                subtitle = getString(R.string.dolby_unknown)
            }
            updateTile()
        }
        super.onStartListening()
    }

    override fun onClick() {
        try {
            val isDsOn = dolbyController.dsOn
            dolbyController.setDsOnAndPersist(!isDsOn) // toggle
            qsTile.apply {
                state = if (isDsOn) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE
                updateTile()
            }
        } catch (e: RuntimeException) {
            Log.w(TAG, "Dolby effect unavailable", e)
            qsTile.apply {
                state = Tile.STATE_UNAVAILABLE
                updateTile()
            }
        }
        super.onClick()
    }
}
