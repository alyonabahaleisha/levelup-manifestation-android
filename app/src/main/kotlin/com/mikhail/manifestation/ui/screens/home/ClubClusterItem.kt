package com.mikhail.manifestation.ui.screens.home

// iOS Migration: -> ClubAnnotation.swift — ClusterItem -> MKAnnotation with clusteringIdentifier

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.mikhail.manifestation.data.model.Club

/**
 * Wraps a [Club] to make it usable with the maps-compose [com.google.maps.android.compose.clustering.Clustering]
 * composable. [ClusterItem] is the contract the clustering engine uses to read coordinates and
 * display labels — nothing more.
 */
data class ClubClusterItem(val club: Club) : ClusterItem {

    override fun getPosition(): LatLng = LatLng(club.latitude, club.longitude)

    override fun getTitle(): String = club.city

    // The snippet is the secondary line shown in the default info window, but since we use a
    // custom bottom sheet we don't rely on it for display; it is included for accessibility
    // label generation by the clustering engine.
    override fun getSnippet(): String = club.leaderName

    // ZIndex controls draw order when pins are at the same position. Default 0 is fine.
    override fun getZIndex(): Float = 0f
}
