package portrait

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extra.camera.Orbital
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.layer
import org.openrndr.extra.compositor.post
import org.openrndr.extra.fx.Post
import org.openrndr.extra.fx.distort.Perturb
import org.openrndr.extra.fx.distort.StretchWaves
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
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
            val gui = GUI()

            val vertexBuffer = loadOBJasVertexBuffer("data/portrait/Scaniverse_2022_07_22_205615.obj")
            val texture = loadImage("data/portrait/Scaniverse_2022_07_22_205615.jpg")

            val rt = renderTarget(width, height) {
                colorBuffer()
                depthBuffer()
            }

            extend(Orbital()) {
                fov = 15.0
            }


            val c = compose {

                layer {
                    draw {
                        drawer.image(rt.colorBuffer(0))
                    }
                    post(StretchWaves()).addTo(gui)
                }


            }

            extend(gui)
            extend {
                drawer.isolatedWithTarget(rt) {
                    drawer.clear(ColorRGBa.TRANSPARENT)
                    val shadeStyle = shadeStyle {
                        fragmentTransform = """
                        x_fill = texture(p_texture, va_texCoord0.xy);
                    """.trimIndent()

                        parameter("texture", texture)
                        parameter("time", seconds)
                    }

                    drawer.shadeStyle = shadeStyle
                    drawer.scale(10.0)

                    drawer.vertexBuffer(vertexBuffer, DrawPrimitive.TRIANGLES)
                }

                drawer.defaults()
                c.draw(drawer)

            }
        }
    }
}