package com.levelup.manifestation.data.content

import com.levelup.manifestation.data.model.Affirmation
import com.levelup.manifestation.ui.theme.LifeArea

object AffirmationContent {

    fun feed(areas: List<LifeArea> = emptyList()): List<Affirmation> {
        val pool = if (areas.isEmpty()) all else all.filter { it.area in areas }
        return pool.shuffled()
    }

    val money = listOf(
        Affirmation(text = "Money flows to me easily and often.", area = LifeArea.Money),
        Affirmation(text = "I am magnetic to wealth and abundance.", area = LifeArea.Money),
        Affirmation(text = "My income expands beyond anything I've imagined.", area = LifeArea.Money),
        Affirmation(text = "I deserve to be richly rewarded for my gifts.", area = LifeArea.Money),
        Affirmation(text = "Prosperity is my natural state.", area = LifeArea.Money),
        Affirmation(text = "Every day I create more value and receive more wealth.", area = LifeArea.Money),
        Affirmation(text = "Money loves me and I welcome it fully.", area = LifeArea.Money),
        Affirmation(text = "Abundance is always available to me.", area = LifeArea.Money),
        Affirmation(text = "I attract financial opportunities effortlessly.", area = LifeArea.Money),
        Affirmation(text = "I am worthy of a luxurious, expansive life.", area = LifeArea.Money),
        Affirmation(text = "Wealth flows through me like a river.", area = LifeArea.Money),
        Affirmation(text = "My bank account reflects my inner abundance.", area = LifeArea.Money),
        Affirmation(text = "I receive money with grace and gratitude.", area = LifeArea.Money),
        Affirmation(text = "Opportunities to grow my wealth appear constantly.", area = LifeArea.Money),
        Affirmation(text = "I am open to all the ways money can come to me.", area = LifeArea.Money),
        Affirmation(text = "Financial freedom is my reality.", area = LifeArea.Money),
        Affirmation(text = "I choose wealth. Wealth chooses me.", area = LifeArea.Money),
        Affirmation(text = "My relationship with money is healthy, loving, and expansive.", area = LifeArea.Money),
        Affirmation(text = "I attract abundance in all forms.", area = LifeArea.Money),
        Affirmation(text = "Today money chooses me.", area = LifeArea.Money)
    )

    val confidence = listOf(
        Affirmation(text = "I walk into every room knowing my worth.", area = LifeArea.Confidence),
        Affirmation(text = "I trust myself completely.", area = LifeArea.Confidence),
        Affirmation(text = "My presence alone creates impact.", area = LifeArea.Confidence),
        Affirmation(text = "I am exactly who I am meant to be.", area = LifeArea.Confidence),
        Affirmation(text = "I speak and people listen.", area = LifeArea.Confidence),
        Affirmation(text = "My confidence is rooted and unshakeable.", area = LifeArea.Confidence),
        Affirmation(text = "I own every room I enter.", area = LifeArea.Confidence),
        Affirmation(text = "I am enough, right now, exactly as I am.", area = LifeArea.Confidence),
        Affirmation(text = "I lead with certainty and grace.", area = LifeArea.Confidence),
        Affirmation(text = "My voice matters and deserves to be heard.", area = LifeArea.Confidence),
        Affirmation(text = "I radiate quiet, magnetic power.", area = LifeArea.Confidence),
        Affirmation(text = "I choose myself, always.", area = LifeArea.Confidence),
        Affirmation(text = "My standards are high because I know my value.", area = LifeArea.Confidence),
        Affirmation(text = "I am becoming more magnetic every day.", area = LifeArea.Confidence),
        Affirmation(text = "I trust my instincts completely.", area = LifeArea.Confidence),
        Affirmation(text = "I show up fully and unapologetically.", area = LifeArea.Confidence),
        Affirmation(text = "My inner knowing never steers me wrong.", area = LifeArea.Confidence),
        Affirmation(text = "I am bold, decisive, and clear.", area = LifeArea.Confidence),
        Affirmation(text = "I believe in myself as deeply as I believe in anything.", area = LifeArea.Confidence),
        Affirmation(text = "You walk into rooms and opportunities open.", area = LifeArea.Confidence)
    )

    val love = listOf(
        Affirmation(text = "I am deeply loved and cherished.", area = LifeArea.Love),
        Affirmation(text = "Love finds me wherever I go.", area = LifeArea.Love),
        Affirmation(text = "I am worthy of a deep, expansive love.", area = LifeArea.Love),
        Affirmation(text = "My heart is open and love flows freely.", area = LifeArea.Love),
        Affirmation(text = "I attract relationships that lift me higher.", area = LifeArea.Love),
        Affirmation(text = "I am loved for exactly who I am.", area = LifeArea.Love),
        Affirmation(text = "The love I give returns to me multiplied.", area = LifeArea.Love),
        Affirmation(text = "I am a magnet for genuine, lasting connection.", area = LifeArea.Love),
        Affirmation(text = "My relationships are nourishing and joyful.", area = LifeArea.Love),
        Affirmation(text = "I deserve to be someone's first priority.", area = LifeArea.Love),
        Affirmation(text = "Love is safe and I welcome it fully.", area = LifeArea.Love),
        Affirmation(text = "I am adored, appreciated, and cherished.", area = LifeArea.Love),
        Affirmation(text = "The right people are drawn to my energy.", area = LifeArea.Love),
        Affirmation(text = "I receive love as easily as I give it.", area = LifeArea.Love),
        Affirmation(text = "My capacity for love expands every day.", area = LifeArea.Love),
        Affirmation(text = "I am a safe place for love to land.", area = LifeArea.Love),
        Affirmation(text = "Real, lasting love is my reality.", area = LifeArea.Love),
        Affirmation(text = "My partner sees me, chooses me, and adores me.", area = LifeArea.Love),
        Affirmation(text = "Love surrounds me in every direction.", area = LifeArea.Love),
        Affirmation(text = "I am magnetic to the love that is truly meant for me.", area = LifeArea.Love)
    )

    val calm = listOf(
        Affirmation(text = "I am at peace with where I am right now.", area = LifeArea.Calm),
        Affirmation(text = "Stillness is my superpower.", area = LifeArea.Calm),
        Affirmation(text = "I breathe in calm and exhale all tension.", area = LifeArea.Calm),
        Affirmation(text = "My nervous system is safe and regulated.", area = LifeArea.Calm),
        Affirmation(text = "Peace is my natural state of being.", area = LifeArea.Calm),
        Affirmation(text = "I release what I cannot control.", area = LifeArea.Calm),
        Affirmation(text = "I move through life with ease and grace.", area = LifeArea.Calm),
        Affirmation(text = "Every breath brings me deeper into calm.", area = LifeArea.Calm),
        Affirmation(text = "I am grounded, centered, and serene.", area = LifeArea.Calm),
        Affirmation(text = "My mind is quiet and my heart is full.", area = LifeArea.Calm),
        Affirmation(text = "I trust the unfolding of my life.", area = LifeArea.Calm),
        Affirmation(text = "I am safe in this moment.", area = LifeArea.Calm),
        Affirmation(text = "Serenity lives within me always.", area = LifeArea.Calm),
        Affirmation(text = "I choose peace over worry, always.", area = LifeArea.Calm),
        Affirmation(text = "Calm is always only one breath away.", area = LifeArea.Calm),
        Affirmation(text = "I am the eye of the storm — still and certain.", area = LifeArea.Calm),
        Affirmation(text = "My inner peace cannot be disturbed.", area = LifeArea.Calm),
        Affirmation(text = "I rest deeply and wake up restored.", area = LifeArea.Calm),
        Affirmation(text = "Tranquility is my gift to myself.", area = LifeArea.Calm),
        Affirmation(text = "I am held. I am safe. I am calm.", area = LifeArea.Calm)
    )

    val career = listOf(
        Affirmation(text = "My work creates extraordinary impact.", area = LifeArea.Career),
        Affirmation(text = "I am recognized and rewarded for my brilliance.", area = LifeArea.Career),
        Affirmation(text = "Opportunities I deserve come to me naturally.", area = LifeArea.Career),
        Affirmation(text = "I am building something that truly matters.", area = LifeArea.Career),
        Affirmation(text = "My career grows in full alignment with my purpose.", area = LifeArea.Career),
        Affirmation(text = "I attract the right collaborators and clients.", area = LifeArea.Career),
        Affirmation(text = "Success comes naturally to me.", area = LifeArea.Career),
        Affirmation(text = "I do work I love and it pays me abundantly.", area = LifeArea.Career),
        Affirmation(text = "My unique skills are in high demand.", area = LifeArea.Career),
        Affirmation(text = "I lead with vision and execute with precision.", area = LifeArea.Career),
        Affirmation(text = "Every step I take moves me toward my dream work.", area = LifeArea.Career),
        Affirmation(text = "I am exactly where I need to be right now.", area = LifeArea.Career),
        Affirmation(text = "My ambition is matched by my ability.", area = LifeArea.Career),
        Affirmation(text = "Doors open for me wherever I go.", area = LifeArea.Career),
        Affirmation(text = "I create my own success on my own terms.", area = LifeArea.Career),
        Affirmation(text = "I am undeniable in my field.", area = LifeArea.Career),
        Affirmation(text = "My work is my art and the world pays for it.", area = LifeArea.Career),
        Affirmation(text = "I am becoming the person my future self is proud of.", area = LifeArea.Career),
        Affirmation(text = "Everything I touch turns into growth.", area = LifeArea.Career),
        Affirmation(text = "My best career chapter is the one I'm writing now.", area = LifeArea.Career)
    )

    val feminineEnergy = listOf(
        Affirmation(text = "My femininity is my greatest power.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "I lead with softness and it moves mountains.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "I receive as naturally as I give.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "My intuition is my most trusted guide.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "I am magnetic, radiant, and deeply alive.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "I allow myself to be fully nourished.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "My body is a temple I honor daily.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "I embody grace in everything I do.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "I am in flow with my natural rhythms.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "My softness is strength.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "I attract what I am — beauty, depth, and light.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "I move through the world with elegance and ease.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "My feminine essence draws abundance to me.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "I am enough in my most natural state.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "I radiate a warmth that touches everyone I meet.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "I am a woman who knows herself completely.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "I am the divine feminine, expressed fully.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "Being a woman is my greatest gift.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "My presence is a luxury.", area = LifeArea.FeminineEnergy),
        Affirmation(text = "I bloom in my own perfect time.", area = LifeArea.FeminineEnergy)
    )

    val relationships = listOf(
        Affirmation(text = "My relationships are built on trust and depth.", area = LifeArea.Relationships),
        Affirmation(text = "I am surrounded by people who celebrate me.", area = LifeArea.Relationships),
        Affirmation(text = "I attract soul-level connections.", area = LifeArea.Relationships),
        Affirmation(text = "The people in my life bring out my best.", area = LifeArea.Relationships),
        Affirmation(text = "I communicate with openness and authenticity.", area = LifeArea.Relationships),
        Affirmation(text = "I set boundaries that honor my peace.", area = LifeArea.Relationships),
        Affirmation(text = "I am a joy to be around.", area = LifeArea.Relationships),
        Affirmation(text = "My connections deepen and flourish naturally.", area = LifeArea.Relationships),
        Affirmation(text = "I choose relationships that evolve me.", area = LifeArea.Relationships),
        Affirmation(text = "I am valued in every relationship I'm in.", area = LifeArea.Relationships),
        Affirmation(text = "Safe, nourishing connection is my birthright.", area = LifeArea.Relationships),
        Affirmation(text = "I show up fully in my relationships.", area = LifeArea.Relationships),
        Affirmation(text = "I attract friends who feel like family.", area = LifeArea.Relationships),
        Affirmation(text = "My circle is small, intentional, and extraordinary.", area = LifeArea.Relationships),
        Affirmation(text = "I release relationships that no longer serve me with love.", area = LifeArea.Relationships),
        Affirmation(text = "I give and receive freely in all my connections.", area = LifeArea.Relationships),
        Affirmation(text = "My energy draws in the right people every time.", area = LifeArea.Relationships),
        Affirmation(text = "I am deeply seen by the people who matter.", area = LifeArea.Relationships),
        Affirmation(text = "Every relationship I have is a reflection of my inner love.", area = LifeArea.Relationships),
        Affirmation(text = "I belong to a community that lifts me up.", area = LifeArea.Relationships)
    )

    val selfWorth = listOf(
        Affirmation(text = "I am inherently valuable — no achievement required.", area = LifeArea.SelfWorth),
        Affirmation(text = "My worth is not up for debate.", area = LifeArea.SelfWorth),
        Affirmation(text = "I treat myself the way I deserve to be treated.", area = LifeArea.SelfWorth),
        Affirmation(text = "I am proud of who I am becoming.", area = LifeArea.SelfWorth),
        Affirmation(text = "I no longer shrink to make others comfortable.", area = LifeArea.SelfWorth),
        Affirmation(text = "I deserve the best — and I accept it.", area = LifeArea.SelfWorth),
        Affirmation(text = "My needs matter and I honor them.", area = LifeArea.SelfWorth),
        Affirmation(text = "I see myself clearly and I love what I see.", area = LifeArea.SelfWorth),
        Affirmation(text = "I am worthy of respect, love, and abundance.", area = LifeArea.SelfWorth),
        Affirmation(text = "I release the need for anyone's approval.", area = LifeArea.SelfWorth),
        Affirmation(text = "I belong in every room I enter.", area = LifeArea.SelfWorth),
        Affirmation(text = "My value is not determined by others' opinions.", area = LifeArea.SelfWorth),
        Affirmation(text = "I am whole, complete, and enough.", area = LifeArea.SelfWorth),
        Affirmation(text = "Self-love is not selfish — it is essential.", area = LifeArea.SelfWorth),
        Affirmation(text = "I choose myself first, always.", area = LifeArea.SelfWorth),
        Affirmation(text = "My worth was never something to earn.", area = LifeArea.SelfWorth),
        Affirmation(text = "I am allowed to take up space.", area = LifeArea.SelfWorth),
        Affirmation(text = "I am someone worth knowing deeply.", area = LifeArea.SelfWorth),
        Affirmation(text = "Everything I am is already enough.", area = LifeArea.SelfWorth),
        Affirmation(text = "I honor myself the way I honor those I love most.", area = LifeArea.SelfWorth)
    )

    val fear = listOf(
        Affirmation(text = "I move through fear with quiet courage.", area = LifeArea.Fear),
        Affirmation(text = "The life I want is on the other side of this.", area = LifeArea.Fear),
        Affirmation(text = "Fear is just excitement waiting for direction.", area = LifeArea.Fear),
        Affirmation(text = "I am brave enough to begin.", area = LifeArea.Fear),
        Affirmation(text = "My courage grows every time I act despite fear.", area = LifeArea.Fear),
        Affirmation(text = "I trust myself to handle whatever comes.", area = LifeArea.Fear),
        Affirmation(text = "I release the past and step into possibility.", area = LifeArea.Fear),
        Affirmation(text = "Fear is not a stop sign — it is an invitation.", area = LifeArea.Fear),
        Affirmation(text = "I am safe to take up space and be fully seen.", area = LifeArea.Fear),
        Affirmation(text = "Every step forward dissolves the fear behind me.", area = LifeArea.Fear),
        Affirmation(text = "I choose expansion over contraction.", area = LifeArea.Fear),
        Affirmation(text = "The unknown holds gifts I haven't imagined yet.", area = LifeArea.Fear),
        Affirmation(text = "I breathe into discomfort and grow.", area = LifeArea.Fear),
        Affirmation(text = "I am supported as I move through uncertainty.", area = LifeArea.Fear),
        Affirmation(text = "My future self is cheering me on right now.", area = LifeArea.Fear),
        Affirmation(text = "I am stronger than any fear I face.", area = LifeArea.Fear),
        Affirmation(text = "Each day I become a little bolder.", area = LifeArea.Fear),
        Affirmation(text = "I walk through the fear and find freedom on the other side.", area = LifeArea.Fear),
        Affirmation(text = "Courage is my natural state when I remember who I am.", area = LifeArea.Fear),
        Affirmation(text = "I am ready. I have always been ready.", area = LifeArea.Fear)
    )

    val body = listOf(
        Affirmation(text = "My body is beautiful exactly as it is.", area = LifeArea.Body),
        Affirmation(text = "I am grateful for everything my body does for me.", area = LifeArea.Body),
        Affirmation(text = "I nourish my body with love and intention.", area = LifeArea.Body),
        Affirmation(text = "My body grows stronger and more radiant every day.", area = LifeArea.Body),
        Affirmation(text = "I am at home in my body.", area = LifeArea.Body),
        Affirmation(text = "I move my body with pleasure and joy.", area = LifeArea.Body),
        Affirmation(text = "My body is my partner, not my enemy.", area = LifeArea.Body),
        Affirmation(text = "I treat my body like the luxury it is.", area = LifeArea.Body),
        Affirmation(text = "Health and vitality flow through every cell.", area = LifeArea.Body),
        Affirmation(text = "I listen to my body with kindness and curiosity.", area = LifeArea.Body),
        Affirmation(text = "My energy is vibrant and sustainable.", area = LifeArea.Body),
        Affirmation(text = "I glow from the inside out.", area = LifeArea.Body),
        Affirmation(text = "I deserve to feel incredible in my skin every day.", area = LifeArea.Body),
        Affirmation(text = "My body is a reflection of the love I give it.", area = LifeArea.Body),
        Affirmation(text = "I am becoming more radiant every day.", area = LifeArea.Body),
        Affirmation(text = "My body knows how to heal, thrive, and shine.", area = LifeArea.Body),
        Affirmation(text = "I choose foods and movement that make me feel alive.", area = LifeArea.Body),
        Affirmation(text = "My body is strong, capable, and worthy of love.", area = LifeArea.Body),
        Affirmation(text = "I am comfortable in my skin.", area = LifeArea.Body),
        Affirmation(text = "Every day my body and I grow closer together.", area = LifeArea.Body)
    )

    val all: List<Affirmation> = money + confidence + love + calm + career +
            feminineEnergy + relationships + selfWorth + fear + body
}
