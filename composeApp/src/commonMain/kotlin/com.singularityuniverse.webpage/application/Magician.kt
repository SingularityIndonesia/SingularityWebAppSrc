package com.singularityuniverse.webpage.application

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.singularityuniverse.webpage.core.Application
import kotlinx.browser.window
import org.jetbrains.compose.resources.painterResource
import singularityuniverse.composeapp.generated.resources.*

class Magician : Application() {
    override val title: String = "Magician Profile"
    override val defaultMinSize: DpSize = DpSize(300.dp, 500.dp)

    @Composable
    override fun Draw(modifier: Modifier) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(120.dp)),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    painter = painterResource(Res.drawable.magician_profile_pict),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Stefanus Ayudha",
                    style = MaterialTheme.typography.h5
                )
                Text(
                    text = "Kotlin Developer Expert",
                    style = MaterialTheme.typography.body2
                )

                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
                        window.open("https://wa.me/+62895340952006")
                    }
                ) {
                    Text("Contact the Magician")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            window.open("mailto:stefanus.ayudha@gmail.com")
                        }
                    ) {
                        Image(
                            modifier = Modifier.requiredHeight(20.dp),
                            contentScale = ContentScale.FillHeight,
                            painter = painterResource(Res.drawable.ic_gmail),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            window.open("https://github.com/stefanusayudha")
                        }
                    ) {
                        Image(
                            modifier = Modifier.requiredHeight(24.dp),
                            contentScale = ContentScale.FillHeight,
                            painter = painterResource(Res.drawable.ic_github),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            window.open("https://www.linkedin.com/in/stefanus-ayudha-447a98b5/")
                        }
                    ) {
                        Image(
                            modifier = Modifier.requiredHeight(24.dp),
                            contentScale = ContentScale.FillHeight,
                            painter = painterResource(Res.drawable.ic_linkedin),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            window.open("https://wa.me/+62895340952006")
                        }
                    ) {
                        Image(
                            modifier = Modifier.requiredHeight(28.dp),
                            contentScale = ContentScale.FillHeight,
                            painter = painterResource(Res.drawable.ic_whatsapp),
                            contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}