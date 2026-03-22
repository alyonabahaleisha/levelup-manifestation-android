package com.levelup.manifestation.data.content

// iOS Migration: -> AffirmationContent.swift — Direct port, same logic

import com.levelup.manifestation.Translations
import com.levelup.manifestation.data.model.Affirmation
import com.levelup.manifestation.ui.theme.LifeArea

object AffirmationContent {

    fun feed(areas: List<LifeArea> = emptyList()): List<Affirmation> {
        val pool = if (areas.isEmpty()) all() else affirmations(areas)
        return pool.shuffled()
    }

    private fun affirmations(areas: List<LifeArea>): List<Affirmation> =
        areas.flatMap { area ->
            Translations.affirmationStrings(area).map { text ->
                Affirmation(text = text, area = area)
            }
        }

    private fun all(): List<Affirmation> = affirmations(LifeArea.entries)
}
