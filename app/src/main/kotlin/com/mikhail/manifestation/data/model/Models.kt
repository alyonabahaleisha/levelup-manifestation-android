package com.mikhail.manifestation.data.model

// iOS Migration: -> Models.swift — data class -> struct (Codable, Identifiable), UUID -> UUID().uuidString

import com.mikhail.manifestation.ui.theme.LifeArea
import java.util.UUID

data class Affirmation(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val area: LifeArea,
    val isPersonal: Boolean = false
)

data class HiddenProgram(
    val id: String = UUID.randomUUID().toString(),
    val limiting: String,
    val rewrite: String,
    val area: LifeArea
)

data class Meditation(
    val id: String,
    val title: String,
    val description: String,
    val area: LifeArea,
    val fileName: String,
    val durationSeconds: Int
)

data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val fileName: String,
    val durationSeconds: Int
)

data class Club(
    val id: String,
    val country: String,
    val city: String,
    val region: String,
    val leaderName: String,
    val telegramUrl: String,
    val latitude: Double,
    val longitude: Double
)
