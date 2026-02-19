package com.levelup.manifestation.data.content

import com.levelup.manifestation.data.model.HiddenProgram
import com.levelup.manifestation.ui.theme.LifeArea

object ProgramContent {

    fun programs(area: LifeArea): List<HiddenProgram> = all.filter { it.area == area }

    val money = listOf(
        HiddenProgram(limiting = "Money is hard to come by", rewrite = "Money flows to me through ease and alignment.", area = LifeArea.Money),
        HiddenProgram(limiting = "I have to work very hard for everything", rewrite = "I create effortlessly and I am richly rewarded.", area = LifeArea.Money),
        HiddenProgram(limiting = "Rich people are greedy or selfish", rewrite = "Wealth amplifies who I already am — generous, kind, and powerful.", area = LifeArea.Money),
        HiddenProgram(limiting = "I'm not ready to have that much money", rewrite = "I am ready now, and I grow into more every single day.", area = LifeArea.Money),
        HiddenProgram(limiting = "Money always slips through my fingers", rewrite = "Money loves to stay with me and quietly multiply.", area = LifeArea.Money),
        HiddenProgram(limiting = "I don't deserve financial abundance", rewrite = "Abundance is my birthright and I receive it fully.", area = LifeArea.Money)
    )

    val relationships = listOf(
        HiddenProgram(limiting = "Love always ends in pain", rewrite = "Love is safe, expansive, and always evolving.", area = LifeArea.Relationships),
        HiddenProgram(limiting = "I have to be perfect to be loved", rewrite = "I am loved for exactly who I am, edges and all.", area = LifeArea.Relationships),
        HiddenProgram(limiting = "People always leave", rewrite = "The right people stay and grow with me.", area = LifeArea.Relationships),
        HiddenProgram(limiting = "I push people away", rewrite = "I attract and keep the connections I truly deserve.", area = LifeArea.Relationships),
        HiddenProgram(limiting = "I'm too much for people", rewrite = "The right people find my depth and intensity magnetic.", area = LifeArea.Relationships),
        HiddenProgram(limiting = "I'm always the one who cares more", rewrite = "I attract relationships of mutual love and devotion.", area = LifeArea.Relationships)
    )

    val selfWorth = listOf(
        HiddenProgram(limiting = "I'm not good enough", rewrite = "I am more than enough — always have been.", area = LifeArea.SelfWorth),
        HiddenProgram(limiting = "I need to earn my worth", rewrite = "My worth is inherent, unconditional, and unshakeable.", area = LifeArea.SelfWorth),
        HiddenProgram(limiting = "Other people are more deserving than me", rewrite = "I deserve everything good that life has to offer.", area = LifeArea.SelfWorth),
        HiddenProgram(limiting = "I don't deserve to take up space", rewrite = "I belong here. My presence is a gift.", area = LifeArea.SelfWorth),
        HiddenProgram(limiting = "I'm too flawed to be truly loved", rewrite = "My wholeness includes all of my imperfections.", area = LifeArea.SelfWorth),
        HiddenProgram(limiting = "I have to be useful to have value", rewrite = "My value exists completely apart from what I produce.", area = LifeArea.SelfWorth)
    )

    val fear = listOf(
        HiddenProgram(limiting = "The world is not safe", rewrite = "I am protected, guided, and supported in every step.", area = LifeArea.Fear),
        HiddenProgram(limiting = "If I fail I won't recover", rewrite = "Every setback makes me wiser and more resilient.", area = LifeArea.Fear),
        HiddenProgram(limiting = "Something bad is always about to happen", rewrite = "Good things are always quietly unfolding for me.", area = LifeArea.Fear),
        HiddenProgram(limiting = "I'm not brave enough", rewrite = "Courage lives in me and grows every time I act.", area = LifeArea.Fear),
        HiddenProgram(limiting = "Being seen is dangerous", rewrite = "Being seen opens doors I haven't even imagined yet.", area = LifeArea.Fear),
        HiddenProgram(limiting = "I always mess things up", rewrite = "I learn, I adjust, and I always find my way forward.", area = LifeArea.Fear)
    )

    val body = listOf(
        HiddenProgram(limiting = "My body is broken or wrong", rewrite = "My body is constantly healing and moving toward wholeness.", area = LifeArea.Body),
        HiddenProgram(limiting = "I'll never look the way I want", rewrite = "My body transforms beautifully when I treat it with love.", area = LifeArea.Body),
        HiddenProgram(limiting = "I'm lazy and undisciplined", rewrite = "I move and nourish my body in ways that feel natural and joyful.", area = LifeArea.Body),
        HiddenProgram(limiting = "My worth is tied to how I look", rewrite = "My value has absolutely nothing to do with my appearance.", area = LifeArea.Body),
        HiddenProgram(limiting = "I've always struggled with my body", rewrite = "A new, loving relationship with my body begins right now.", area = LifeArea.Body),
        HiddenProgram(limiting = "My body betrays me", rewrite = "My body is always doing its best to support me.", area = LifeArea.Body)
    )

    val career = listOf(
        HiddenProgram(limiting = "Success is for other people", rewrite = "Success is my natural destination and I'm already on my way.", area = LifeArea.Career),
        HiddenProgram(limiting = "I'm not talented enough", rewrite = "My unique gifts are exactly what the world needs.", area = LifeArea.Career),
        HiddenProgram(limiting = "I have to hustle 24/7 to succeed", rewrite = "I succeed with ease, focus, and aligned action.", area = LifeArea.Career),
        HiddenProgram(limiting = "It's too late for me", rewrite = "This is exactly the right time for my breakthrough.", area = LifeArea.Career),
        HiddenProgram(limiting = "People don't take me seriously", rewrite = "I command respect through my presence and my work.", area = LifeArea.Career),
        HiddenProgram(limiting = "I don't know enough to be successful", rewrite = "I know enough to start, and I'll learn everything else on the way.", area = LifeArea.Career)
    )

    val all: List<HiddenProgram> = money + relationships + selfWorth + fear + body + career
}
