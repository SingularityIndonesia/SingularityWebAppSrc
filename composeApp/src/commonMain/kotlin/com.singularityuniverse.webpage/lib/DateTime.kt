package com.singularityuniverse.webpage.lib

import kotlinx.datetime.TimeZone
import kotlinx.datetime.internal.JSJoda.DateTimeFormatter
import kotlinx.datetime.internal.JSJoda.LocalDateTime
import kotlinx.datetime.internal.JSJoda.ZoneId

val `dd HH:mm` = DateTimeFormatter.ofPattern("dd HH:mm")

val todayDateTime get() = LocalDateTime.now(ZoneId.of(TimeZone.currentSystemDefault().id))

val `timeInMMMM_dd_HH:mm`
    get() = todayDateTime.month().name().lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } +
            " " + todayDateTime.format(`dd HH:mm`)