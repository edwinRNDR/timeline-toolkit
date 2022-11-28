package portrait

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.events.Event
import org.openrndr.extra.camera.Orbital
import org.openrndr.extra.objloader.loadOBJEx
import org.openrndr.extra.objloader.loadOBJasVertexBuffer
import org.openrndr.shape.IntRectangle
import kotlin.math.floor

fun main() {
    application {

        program {
            val vertexBuffer = loadOBJasVertexBuffer("data/portrait/Scaniverse_2022_07_22_205615.obj")
            val texture = loadImage("data/portrait/Scaniverse_2022_07_22_205615.jpg")

            val idTexture = colorBuffer(256, 256)

            val pickBuffer = renderTarget(width, height) {
                colorBuffer()
                depthBuffer()
            }

            var renderPickBuffer = false
            mouse.buttonUp.listen {
                renderPickBuffer = true
            }

            val pickCursor = colorBuffer(16,16)


            val s = idTexture.shadow

            fun q(x: Double, steps: Int): Double {
                return (floor(x*(steps+1.0))/steps).coerceAtMost(1.0)
            }

            for (y in 0 until 256) {
                for (x in 0 until 256) {
                    s[x, y] = ColorRGBa( q(x / 256.0,4), q(y / 256.0, 4), 0.0, 1.0)
                }
            }
            s.upload()

            idTexture.filter(MinifyingFilter.NEAREST, MagnifyingFilter.NEAREST)

            extend(Orbital()) {
                fov = 15.0
            }

            var picked = Event<Pair<Int, ColorRGBa>>()
            var activeColor = ColorRGBa.BLACK

            picked.listen {
                println("you picked $it")
                activeColor = it.second
            }

            extend {

                drawer.scale(10.0)


                val shadeStyle = shadeStyle {
                    fragmentTransform = """
                        vec4 id = texture(p_idtexture, va_texCoord0.xy);
                        
                        float d = smoothstep(0.1, 0.0, distance(id.xy, p_activeColor.xy));
                        x_fill = mix(texture(p_texture, va_texCoord0.xy), vec4(1.0), d);
                    """.trimIndent()

                    parameter("texture", texture)
                    parameter("idtexture", idTexture)
                    parameter("activeColor", activeColor)
                }
                drawer.shadeStyle = shadeStyle
                drawer.vertexBuffer(vertexBuffer, DrawPrimitive.TRIANGLES)


                if (renderPickBuffer) {
                    drawer.isolatedWithTarget(pickBuffer) {
                        drawer.clear(ColorRGBa.BLACK)
                        val shadeStyle = shadeStyle {
                            fragmentTransform = """
                        x_fill = texture(p_texture, va_texCoord0.xy);
                    """.trimIndent()
                            parameter("texture", idTexture)
                        }
                        drawer.shadeStyle = shadeStyle
                        drawer.vertexBuffer(vertexBuffer, DrawPrimitive.TRIANGLES)
                    }
                    pickBuffer.colorBuffer(0).copyTo(pickCursor, sourceRectangle = IntRectangle(mouse.position.x.toInt()-16, mouse.position.y.toInt()-16, 32,32),
                    targetRectangle = IntRectangle(0, 0, 32,32))
                    renderPickBuffer = false
                    pickCursor.shadow.download()
                    val c = pickCursor.shadow[15,15]
                    val id = (c.r*5.0).toInt() * 5 + (c.b*5.0).toInt()
                    picked.trigger(Pair(id,c))
                }
                drawer.defaults()
                drawer.image(pickCursor)
            }
        }
    }
}