package transcribe

import library.readSRT
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.extra.minim.minim
import org.openrndr.shape.Rectangle
import org.openrndr.writer
import java.io.File

fun main() {
    application {
        configure {
            width = 1280
            height = 720
        }
        program {
            val minim = minim()
            val sound = minim.loadFile("data/transcribe/constant.wav")
            sound.loop()

            mouse.dragged.listen {
                val duration = sound.length()
                val relx = it.position.x / width
                sound.cue((relx * duration).toInt())
                //sound.channel!!.setPosition(relx * duration)
            }

            val transcript = readSRT(File("data/transcribe/8F5P9NBgH4s.srt"))

            extend {
                drawer.fontMap = loadFont("data/fonts/default.otf", 13.0)
                val time = sound.position() / 1000.0
                val active =  transcript.filter {
                    it.startTime <= time && it.endTime > time
                }
                writer {
                    box = Rectangle(40.0, 40.0, width - 80.0, height - 80.0)
                    for (item in transcript) {

                        if (item.startTime <= time && item.endTime > time) {
                            drawer.fill = ColorRGBa.YELLOW
                        } else {
                            drawer.fill = ColorRGBa.GRAY
                        }

                        for (textMessage in item.texts) {
                            text(textMessage + " ")
                        }
                    }
                }
            }
        }
    }
}
