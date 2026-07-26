package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.SmartDisplay
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.load_video

/**
 * Поле ввода ссылки на трансляцию + кнопка загрузки (SmartDisplay).
 * Часть редактирования ссылки на видео — не зависит от видеоплеера,
 * поэтому живёт в commonMain и переиспользуется [MediaPlayerComponent] на Android.
 */
@Composable
fun VideoLinkField(
    modifier: Modifier = Modifier,
    videoLinkState: TextFieldState,
    onLoadVideoLink: (String) -> Unit,
) {
    Row(
        modifier = Modifier.then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextField(
            state = videoLinkState,
            modifier = Modifier.weight(1f),
            lineLimits = TextFieldLineLimits.SingleLine,
        )
        IconButton(onClick = { onLoadVideoLink(videoLinkState.text.toString().trim()) }) {
            Icon(
                imageVector = IconGroup.Default.SmartDisplay,
                contentDescription = stringResource(Res.string.load_video),
            )
        }
    }
}

@Preview
@Composable
fun VideoLinkFieldEmptyPreview() {
    MaterialTheme {
        Surface {
            VideoLinkField(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                videoLinkState = rememberTextFieldState(""),
                onLoadVideoLink = {},
            )
        }
    }
}

@Preview
@Composable
fun VideoLinkFieldFilledPreview() {
    MaterialTheme {
        Surface {
            VideoLinkField(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                videoLinkState = rememberTextFieldState("https://www.youtube.com/watch?v=dQw4w9WgXcQ"),
                onLoadVideoLink = {},
            )
        }
    }
}
