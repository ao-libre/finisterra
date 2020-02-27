@file:Suppress("DEPRECATION")

package design

import com.soywiz.kaifu2x.Kaifu2x
import com.soywiz.korim.bitmap.BitmapChannel
import com.soywiz.korim.format.*
import com.soywiz.korio.Korio
import com.soywiz.korio.vfs.UniversalVfs

object Scale2x {

    @JvmStatic
    fun run(input: String, output: String) = Korio {
        defaultImageFormats.registerStandard()
        val image = UniversalVfs(input).readBitmapNoNative().toBMP32()
        val noiseReduction = 1

        val channels = BitmapChannel.ALL.toList()
        val noiseReductedImage = Kaifu2x.noiseReductionRgba(image, noiseReduction, channels, true, chunkSize = 128)
        val scaledImage = Kaifu2x.scaleRgba(noiseReductedImage, 2, channels, true, 128)

        val outFile = UniversalVfs(output).ensureParents()
        scaledImage.writeTo(outFile, ImageEncodingProps(quality = 100.toDouble() / 100.0))
    }

}
