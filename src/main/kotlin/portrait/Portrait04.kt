package portrait

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extra.camera.Orbital
import org.openrndr.extra.fx.Post
import org.openrndr.extra.objloader.loadOBJEx
import org.openrndr.extra.objloader.loadOBJasVertexBuffer
import org.openrndr.poissonfill.PoissonFill

fun main() {
    application {

        configure {
            width = 720
            height = 720
        }
        program {
            val vertexBuffer = loadOBJasVertexBuffer("data/portrait/Scaniverse_2022_07_22_205615.obj")
            val texture = loadImage("data/portrait/Scaniverse_2022_07_22_205615.jpg")


            val pf = PoissonFill()
            val rt = renderTarget(width, height) {
                colorBuffer()
                depthBuffer()
            }

            val filled = colorBuffer(width, height)

            extend(Orbital()) {
                fov = 15.0
            }
            extend {

                drawer.isolatedWithTarget(rt) {
                    drawer.clear(ColorRGBa.TRANSPARENT)
                    val shadeStyle = shadeStyle {
                        fragmentTransform = """
                        
                        x_fill = texture(p_texture, va_texCoord0.xy);
                        x_fill.rgb *= cos(va_position.y*200.0 + p_time) * 0.5 + 0.5;
                    """.trimIndent()

                        parameter("texture", texture)
                        parameter("time", seconds)
                    }

                    drawer.shadeStyle = shadeStyle
                    drawer.scale(10.0)

                    drawer.vertexBuffer(vertexBuffer, DrawPrimitive.TRIANGLES)

                }
                pf.apply(rt.colorBuffer(0), filled)
                drawer.defaults()
                drawer.image(filled)

            }
        }
    }
}