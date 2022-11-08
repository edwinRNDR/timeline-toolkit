package transcribe

import AudioStream
import bass.initBass
import library.readSRT
import org.openrndr.application
import org.openrndr.extra.parameters.description
import org.openrndr.shape.Rectangle
import org.openrndr.writer
import java.io.File

fun main() {
    application {
        program {
            initBass()
            val sound = AudioStream("data/transcribe/8F5P9NBgH4s.mp3", "speech")
            sound.play(1.0, 0, context = dispatcher)

            mouse.dragged.listen {
                val duration = sound.channel!!.getDuration()
                val relx = it.position.x / width
                sound.channel!!.setPosition(relx * duration)
            }

            val transcript = readSRT(File("data/transcribe/8F5P9NBgH4s.srt"))
            extend {
                val time = sound.channel!!.getPosition()
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
