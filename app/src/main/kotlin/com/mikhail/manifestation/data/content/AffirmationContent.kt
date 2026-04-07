package com.mikhail.manifestation.data.content

// iOS Migration: -> AffirmationContent.swift — Direct port, same logic

import com.mikhail.manifestation.Translations
import com.mikhail.manifestation.data.model.Affirmation
import com.mikhail.manifestation.ui.theme.LifeArea

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
