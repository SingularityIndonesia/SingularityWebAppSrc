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
package core.design

import androidx.compose.foundation.layout.Arrangement

object Arrangement {
    val ChickenSpace = Arrangement.spacedBy(Size.Chicken)
    val ExtraSmallSpace = Arrangement.spacedBy(Size.ExtraSmall)
    val SmallSpace = Arrangement.spacedBy(Size.Small)
    val MediumSpace = Arrangement.spacedBy(Size.Medium)
    val LargeSpace = Arrangement.spacedBy(Size.Large)
    val ExtraLargeSpace = Arrangement.spacedBy(Size.ExtraLarge)
    val WowSpace = Arrangement.spacedBy(Size.Wow)
    val UnbelieveAbleSpace = Arrangement.spacedBy(Size.UnbelieveAble)
    val SteveSpace = Arrangement.spacedBy(Size.Steve)

    // im not sure if you need this but just in case
    val GovernmentSpace = Arrangement.spacedBy(Size.Government)
}
