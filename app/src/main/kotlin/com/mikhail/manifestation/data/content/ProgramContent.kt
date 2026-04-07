package com.mikhail.manifestation.data.content

// iOS Migration: -> ProgramContent.swift — Direct port

import com.mikhail.manifestation.Translations
import com.mikhail.manifestation.data.model.HiddenProgram
import com.mikhail.manifestation.ui.theme.LifeArea

object ProgramContent {

    fun programs(area: LifeArea): List<HiddenProgram> =
        Translations.programPairs(area).map { (limiting, rewrite) ->
            HiddenProgram(limiting = limiting, rewrite = rewrite, area = area)
        }

    val all: List<HiddenProgram>
        get() = LifeArea.entries.flatMap { programs(it) }
}
