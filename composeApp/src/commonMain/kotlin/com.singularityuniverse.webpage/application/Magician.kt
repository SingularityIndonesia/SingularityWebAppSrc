package com.singularityuniverse.webpage.application

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
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
import com.singularityuniverse.webpage.core.design.Space24
import com.singularityuniverse.webpage.lib.open
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
                Space24()
                Image(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(120.dp)),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    painter = painterResource(Res.drawable.magician_profile_pict),
                    contentDescription = null
                )
                Space24()
                Text(
                    text = "Stefanus Ayudha",
                    style = MaterialTheme.typography.h5
                )
                Text(
                    text = "Expert Kotlin Developer",
                    style = MaterialTheme.typography.body2
                )

                Space24()
                TextButton(
                    onClick = {
                        open("https://wa.me/+62895340952006")
                    }
                ) {
                    Text("Contact the Magician")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            open("mailto:stefanus.ayudha@gmail.com")
                        }
                    ) {
                        Image(
                            modifier = Modifier.requiredHeight(22.dp),
                            contentScale = ContentScale.FillHeight,
                            painter = painterResource(Res.drawable.ic_gmail_24),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            open("https://github.com/stefanusayudha")
                        }
                    ) {
                        Image(
                            modifier = Modifier.requiredHeight(24.dp),
                            contentScale = ContentScale.FillHeight,
                            painter = painterResource(Res.drawable.ic_github_24),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            open("https://www.linkedin.com/in/stefanus-ayudha-447a98b5/")
                        }
                    ) {
                        Image(
                            modifier = Modifier.requiredHeight(24.dp),
                            contentScale = ContentScale.FillHeight,
                            painter = painterResource(Res.drawable.ic_linkedin_24),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            open("https://wa.me/+62895340952006")
                        }
                    ) {
                        Image(
                            modifier = Modifier.requiredHeight(25.dp),
                            contentScale = ContentScale.FillHeight,
                            painter = painterResource(Res.drawable.ic_whatsapp_24),
                            contentDescription = null
                        )
                    }
                }
                Space24()
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Icon(modifier: Modifier, onClick: () -> Unit) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.background)
                .onClick { onClick.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(Res.drawable.ic_kaito_60),
                contentDescription = null
            )
        }
    }
}