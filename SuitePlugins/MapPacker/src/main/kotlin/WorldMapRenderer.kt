//import javafx.embed.swing.SwingFXUtils
//import javafx.scene.canvas.Canvas
//import store.CacheLibrary
//import store.io.impl.InputStream
//import util.HightCalcuation
//import util.JagexColorPalette
//import utility.XTEASManager
//import java.awt.Color
//import java.awt.image.BufferedImage
//import java.awt.image.BufferedImage.TYPE_INT_RGB
//
//
///**
// *
// * @project ValkyrCacheSuite
// * @author ReverendDread on 1/24/2020
// * https://www.rune-server.ee/members/reverenddread/
// */
//private var TILE_SHAPES: Array<IntArray> = arrayOf<IntArray>(
//intArrayOf(
//    1, 1, 1, 1,
//    1, 1, 1, 1,
//    1, 1, 1, 1,
//    1, 1, 1, 1),
//intArrayOf(
//    1, 0, 0, 0,
//    1, 1, 0, 0,
//    1, 1, 1, 0,
//    1, 1, 1, 1),
//intArrayOf(
//    1, 1, 0, 0,
//    1, 1, 0, 0,
//    1, 0, 0, 0,
//    1, 0, 0, 0),
//intArrayOf(
//    0, 0, 1, 1,
//    0, 0, 1, 1,
//    0, 0, 0, 1,
//    0, 0, 0, 1),
//intArrayOf(
//    0, 1, 1, 1,
//    0, 1, 1, 1,
//    1, 1, 1, 1,
//    1, 1, 1, 1),
//intArrayOf(
//    1, 1, 1, 0,
//    1, 1, 1, 0,
//    1, 1, 1, 1,
//    1, 1, 1, 1),
//intArrayOf(
//    1, 1, 0, 0,
//    1, 1, 0, 0,
//    1, 1, 0, 0,
//    1, 1, 0, 0),
//intArrayOf(
//    0, 0, 0, 0,
//    0, 0, 0, 0,
//    1, 0, 0, 0,
//    1, 1, 0, 0),
//intArrayOf(
//    1, 1, 1, 1,
//    1, 1, 1, 1,
//    0, 1, 1, 1,
//    0, 0, 1, 1),
//intArrayOf(
//    1, 1, 1, 1,
//    1, 1, 0, 0,
//    1, 0, 0, 0,
//    1, 0, 0, 0),
//intArrayOf(
//    0, 0, 0, 0,
//    0, 0, 1, 1,
//    0, 1, 1, 1,
//    0, 1, 1, 1),
//intArrayOf(
//    0, 0, 0, 0,
//    0, 0, 0, 0,
//    0, 1, 1, 0,
//    1, 1, 1, 1
//))
//
//private val TILE_ROTATIONS = arrayOf(
//intArrayOf(
//    0, 1, 2, 3,
//    4, 5, 6, 7,
//    8, 9, 10, 11,
//    12, 13, 14, 15),
//intArrayOf(
//    12, 8, 4, 0,
//    13, 9, 5, 1,
//    14, 10, 6, 2,
//    15, 11, 7, 3),
//intArrayOf(
//    15, 14, 13, 12,
//    11, 10, 9, 8,
//    7, 6, 5, 4,
//    3, 2, 1, 0),
//intArrayOf(
//    3, 7, 11, 15,
//    2, 6, 10, 14,
//    1, 5, 9, 13,
//    0, 4, 8, 12
//))
//
//val PIXELS_PER_TILE = 4
//val REGION_WIDTH = 64
//val REGION_HEIGHT = 64
//val colorPalette = JagexColorPalette(0.9, 0, 512).colorPalette!!
//
//class WorldMapRenderer(view: Canvas) : Runnable {
//
//    //The region currently rendering
//    var regionId: Int = 12342
//    set(value) {
//        field = value
//        update = value >= 0
//    }
//    //If the renderer needs to be terminated
//    var terminte: Boolean = false
//    //If the renderer needs to be updated
//    var update: Boolean = true
//    //The canvas we drawing to
//    val canvas: Canvas = view
//    //The jagex color palette
//
//    //Render the map
//    override fun run() {
//        while (!terminte) {
//            if (update) { //region needs updated
//                val region: Region = Region(regionId)
//                val context = canvas.graphicsContext2D
//                val width: Int = canvas.width.toInt()
//                val height: Int = canvas.height.toInt()
//                val image = BufferedImage(PIXELS_PER_TILE * REGION_WIDTH, PIXELS_PER_TILE * REGION_HEIGHT, TYPE_INT_RGB)
//                region.draw(image)
//                context.drawImage(SwingFXUtils.toFXImage(image, null), (width * PIXELS_PER_TILE).toDouble(), (height * PIXELS_PER_TILE).toDouble())
//                update = false
//            }
//        }
//    }
//
//    class Region(regionId: Int){
//
//        var baseX = regionId.shr(8) and 0xff shl 6
//        var baseY = regionId.and(0xff) shl 6
//        var regionX = baseX shl 6
//        var regionY = baseY shl 6
//        var objects = arrayOf<Array<Array<RSObject>>>()
//        var underwater: Boolean = false
//        var hideUnderlay: Boolean = false
//        var rotation: Int = 0
//        val tileInformation = mutableMapOf<Position, TileInfo>()
//
//        init {
//
//            defaults()
//
//            println("Region x $regionX y $regionY")
//
//            val library: CacheLibrary = CacheLibrary.get()
//            val xteas: IntArray? = XTEASManager.lookup(regionId)
//
//            val mapData: ByteArray? = library.getIndex(5).getArchive("m" + (regionX.shr(3) / 8) + "_" + (regionY.shr(3) / 8))?.data
//            mapData?.let {
//                val mapBuffer = InputStream(it)
//                decodeTerrainLayer(mapBuffer)
//            }
//
//            val landscapeData: ByteArray? = library.getIndex(5).getArchive("l" + (regionX.shr(3) / 8) + "_" + (regionY.shr(3) / 8), xteas)?.data
//            landscapeData?.let {
//                val landscapeBuffer = InputStream(it)
//                decodeObjectLayer(landscapeBuffer)
//            }
//
//        }
//
//        //Decodes the mx_y map layer
//        private fun decodeTerrainLayer(inputStream: InputStream) {
//            inputStream.apply {
//                for (plane in 0 until 4) {
//                    for (x in 0 until 64) {
//                        for (y in 0 until 64) {
//                            val position = Position(x = x, y = y, z = plane);
//                            val tileInfo = TileInfo()
//                            tileInformation[position] = tileInfo
//                            if (!underwater && !hideUnderlay)
//                                tileInformation[position.copy(z = 0)]?.flags = 0
//                            while (true) {
//                                var opcode = readUnsignedByte()
//                                if (opcode == 0) {
//                                    if (underwater)
//                                        tileInfo.height = 0
//                                    else if (plane == 0)
//                                        tileInformation[position.copy(z = 0)]?.height = -HightCalcuation.calculate(baseX + x + 0xe3b7b, baseY + y + 0x87cce) * 8;
//                                    else
//                                        tileInfo.height = tileInformation[position.copy(z = position.z - 1)]?.height ?: 0 - 960
//                                    break
//                                }
//                                if (opcode == 1) {
//                                    var height = readUnsignedByte()
//                                    if (!underwater) {
//                                        if (height == 1)
//                                            height = 0;
//                                        if (plane == 0)
//                                            tileInformation[position.copy(z = 0)]?.height = -height * 8 shl 2
//                                        else
//                                            tileInfo.height = tileInformation[position.copy(z = position.z - 1)]?.height
//                                                    ?: 0 - (height * 8 shl 2)
//                                    } else {
//                                        tileInformation[position.copy(z = 0)]?.height = (8 * height.shl(2))
//                                    }
//                                    break
//                                }
//                                if (opcode <= 49) {
//                                    if (hideUnderlay)
//                                        readUnsignedByte()
//                                    else {
//                                        tileInfo.overlayId = readByte()
//                                        tileInfo.overlayShape = (opcode - 2) / 4
//                                        tileInfo.overlayRotation = (opcode - 2 + rotation and 0x3)
//                                    }
//                                } else if (opcode <= 81) {
//                                    if (!underwater && !hideUnderlay)
//                                        tileInfo.flags = (opcode - 49)
//                                } else if (!hideUnderlay) {
//                                    tileInfo.underlayId = (opcode - 81) and 0xff
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//        }
//
//        //Decodes the lx_y object layer
//        private fun decodeObjectLayer(inputStream: InputStream) {
//            inputStream.apply {
//                var objectId = -1
//                var objectIncrement: Int = -1
//                while (({ objectIncrement = readSmart2(); objectIncrement }()) != 0) {
//                    objectId += objectIncrement
//                    var chunk = 0
//                    var increment2 = -1
//                    while (({ increment2 = readSmart2(); objectIncrement }()) != 0) {
//                        chunk += increment2.minus(1)
//                        val localX = chunk shr 6 and 0x3f
//                        val localY = chunk and 0x3f
//                        val plane = chunk shr 12
//                        val objectHash = readUnsignedByte()
//                        val type = objectHash shr 2
//                        val rotation = objectHash and 0x3
//                        if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64) {
//                            continue
//                        }
//                        var objectHeight = plane
//                        val position = Position(x = localX, y = localY, z = plane)
//                        if (tileInformation[position.copy(z = 1)]?.flags?.and(2) == 2) {
//                            objectHeight--
//                        }
//                        if (objectHeight < 0 || objectHeight >= 4 || plane < 0 || plane >= 0) {
//                            continue
//                        }
//                        objects[plane][localX][localY] = RSObject(objectId, type, rotation, localX, localY, plane)
//                    }
//                }
//            }
//        }
//
//        fun draw(image: BufferedImage) {
//            drawFloor(0, image)
//        }
//
//        private fun drawFloor(z: Int, image: BufferedImage) {
//            for (x in 0 until REGION_WIDTH) {
//                for (y in 0 until REGION_HEIGHT) {
//                    val underlayId = tileInformation[Position(x, y, z)]?.underlayId
//                    underlayId?.let {
//                        val underlay = MapPlugin.underlays.underlays[it]
//                        underlay?.apply {
//                            var rgb: Int = Color.BLACK.rgb
//                            try {
//                                var hsl = JagexColorPalette.packHsl(hue, saturation, lightness)
//                                var index = JagexColorPalette.getJagexColorIndex(hsl, 96)
//                                rgb = colorPalette[index]
//                            } catch (ex: Exception) {}
//                            drawMapRegion(image, x, y, rgb, -1, -1)
//                        }
//                    }
//                }
//            }
//        }
//
//        private fun drawMapRegion(image: BufferedImage, x: Int, y: Int, overlayRGB: Int, shape: Int, rotation: Int) {
//            if (shape > -1) {
//                val shapeMatrix = TILE_SHAPES[shape]
//                val rotationMatrix = TILE_ROTATIONS[rotation and 0x3]
//                var shapeIndex = 0
//                for (tilePixelY in 0 until PIXELS_PER_TILE) {
//                    for (tilePixelX in 0 until PIXELS_PER_TILE) {
//                        val drawx: Int = x * PIXELS_PER_TILE + tilePixelX
//                        val drawy: Int = y * PIXELS_PER_TILE + tilePixelY
//                        if (shapeMatrix[rotationMatrix[shapeIndex++]] != 0) {
//                            image.setRGB(drawx, drawy, overlayRGB)
//                        }
//                    }
//                }
//            } else {
//                for (tilePixelY in 0 until PIXELS_PER_TILE) {
//                    for (tilePixelX in 0 until PIXELS_PER_TILE) {
//                        val drawx: Int = x * PIXELS_PER_TILE + tilePixelX
//                        val drawy: Int = y * PIXELS_PER_TILE + tilePixelY
//                        image.setRGB(drawx, drawy, overlayRGB)
//                    }
//                }
//            }
//        }
//
//        private fun defaults(){
//            for (plane in 0 until 4) {
//                for (x in 0 until REGION_WIDTH) {
//                    for (y in 0 until REGION_HEIGHT) {
//                        val position = Position(x = x, y = y, z = plane);
//                        val tileInfo = TileInfo()
//                        tileInformation[position] = tileInfo
//                    }
//                }
//            }
//        }
//
//    }
//
//    data class TileInfo(var underlayId: Int = -1, var overlayId: Int = -1, var overlayShape: Int = 0, var overlayRotation: Int = 0, var height: Int = 0, var flags: Int = 0)
//    data class RSObject(val id: Int, val type: Int, val rotation: Int, val x: Int, val y: Int, val z: Int)
//    data class Position(val x: Int, val y: Int, val z: Int)
//
//}