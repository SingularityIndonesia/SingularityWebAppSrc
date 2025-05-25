package lib.os

import lib.Uri

/**
 * Opens a file picker dialog and returns the selected file as a Uri
 * @return Result containing the selected file Uri, null if cancelled, or exception on error
 */
expect suspend fun pickFile(): Result<Uri>
