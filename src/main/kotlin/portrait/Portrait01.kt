package portrait

import org.openrndr.application
import org.openrndr.draw.DrawPrimitive
import org.openrndr.draw.loadImage
import org.openrndr.draw.shadeStyle
import org.openrndr.extra.camera.Orbital
import org.openrndr.extra.objloader.loadOBJEx
import org.openrndr.extra.objloader.loadOBJasVertexBuffer

fun main() {
    application {

        program {
            val vertexBuffer = loadOBJasVertexBuffer("data/portrait/Scaniverse_2022_07_22_205615.obj")
            val texture = loadImage("data/portrait/Scaniverse_2022_07_22_205615.jpg")

            extend(Orbital()) {
                fov = 15.0
            }
            extend {

                val shadeStyle = shadeStyle {
                    fragmentTransform = """
                        x_fill = texture(p_texture, va_texCoord0.xy);
                    """.trimIndent()

                    parameter("texture", texture)
                }

                drawer.shadeStyle = shadeStyle
                drawer.scale(10.0)

                drawer.vertexBuffer(vertexBuffer, DrawPrimitive.TRIANGLES)
            }
        }
    }
}