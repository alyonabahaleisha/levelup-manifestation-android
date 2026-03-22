package com.levelup.manifestation.data.content

// iOS Migration: -> ProgramContent.swift — Direct port

import com.levelup.manifestation.Translations
import com.levelup.manifestation.data.model.HiddenProgram
import com.levelup.manifestation.ui.theme.LifeArea

object ProgramContent {

    fun programs(area: LifeArea): List<HiddenProgram> =
        Translations.programPairs(area).map { (limiting, rewrite) ->
            HiddenProgram(limiting = limiting, rewrite = rewrite, area = area)
        }

    val all: List<HiddenProgram>
        get() = LifeArea.entries.flatMap { programs(it) }
}
