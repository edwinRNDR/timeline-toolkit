package transcribe

import library.readSRT
import org.openrndr.application
import org.openrndr.extra.minim.minim
import org.openrndr.shape.Rectangle
import org.openrndr.writer
import java.io.File

fun main() {
    application {
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
                val time = sound.position() / 1000.0
                val active =  transcript.filter {
                    it.startTime <= time && it.endTime > time
                }
                writer {
                    box = Rectangle(40.0, 40.0, width - 80.0, height - 80.0)
                    for (item in active) {
                        for (textMessage in item.texts) {
                            text(textMessage)
                        }
                    }
                }
            }
        }
    }
}
