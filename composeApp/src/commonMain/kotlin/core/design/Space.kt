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

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.SpaceSmall() {
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun RowScope.SpaceSmall() {
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun ColumnScope.SpaceMedium() {
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun RowScope.SpaceMedium() {
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ColumnScope.SpaceLarge() {
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun RowScope.SpaceLarge() {
    Spacer(modifier = Modifier.width(16.dp))
}

@Composable
fun ColumnScope.SpaceExtraLarge() {
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun RowScope.SpaceExtraLarge() {
    Spacer(modifier = Modifier.width(24.dp))
}