package com.levelup.manifestation.data.model

import com.levelup.manifestation.ui.theme.LifeArea
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
    val area: LifeArea,
    val fileName: String,
    val durationSeconds: Int
)
