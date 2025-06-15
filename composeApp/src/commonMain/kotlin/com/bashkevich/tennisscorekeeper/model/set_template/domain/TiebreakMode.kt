package com.bashkevich.tennisscorekeeper.model.set_template.domain

enum class TiebreakMode{
    NO,EARLY,LATE
    // NO - без тай-брейка, EARLY - если сет до 6, то при счете 5:5, LATE - если сет до 6, то при счете 6:6
}