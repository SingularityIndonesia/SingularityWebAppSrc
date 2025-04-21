/**
 * Copyright (C) 2025  stefanus.ayudha@gmail.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
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