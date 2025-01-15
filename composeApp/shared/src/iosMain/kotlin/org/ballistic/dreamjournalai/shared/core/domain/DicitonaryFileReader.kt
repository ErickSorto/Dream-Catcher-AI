package org.ballistic.dreamjournalai.shared.core.domain

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import platform.posix.ENOENT
import platform.posix.errno

actual class DictionaryFileReader {
    @OptIn(ExperimentalForeignApi::class)
    actual fun readDictionaryWordsFromCsv(fileName: String): List<String> {
        val result = mutableListOf<String>()
        val bundle = NSBundle.mainBundle
        val fileNameWithoutExtension = fileName.substringBeforeLast(".")
        val fileExtension = fileName.substringAfterLast(".")

        val filePath = bundle.pathForResource(
            name = fileNameWithoutExtension,
            ofType = fileExtension
        ) ?: run {
            println("File $fileName not found in iOS bundle.")
            return emptyList()
        }

        val fileContent = NSString.stringWithContentsOfFile(filePath, NSUTF8StringEncoding, null)
        fileContent?.split("\n")?.forEach { line ->
            result.add(line)
        }
        return result
    }
}