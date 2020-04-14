package design.editors.utils

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.esotericsoftware.minlog.Log
import model.textures.AOImage
import java.awt.Point
import java.awt.Rectangle
import java.util.*

/**
 * Motivation:  oftentimes a SpriteSheet is given without any further information. For example,
 * a spritesheet with twenty sprites from the famous Megaman series. How do you integrate this into your game?
 * Do you calculate the component.position and size of each sprite by hand? Of course not, apps like TexturePacker do a wonderful
 * job of taking individual sprites, packing them, and handing you an atlas with all necessary information.
 *
 * However, to do that, you need the individual sprites first! This class, SpriteSheetUnpacker, cuts up a SpriteSheet
 * and delivers the individual sprites.
 */
class Slicer(val file: FileHandle) {


    /**
     * Given a valid sprite sheet, detects and returns every individual sprite. The method may not be perfect and
     * return grouped sprites if they're not contiguous. Does not alter the source common in any way.
     *
     * @param spriteSheet the sprite sheet to unpack
     * @return list of extracted sprite images
     */
    fun slice(startingId: Int): SliceResult {
        val backgroundColor = Color(0f, 0f, 0f, 0f)
        val image = Pixmap(file)

        return discoverSprites(image, backgroundColor, startingId)
    }

    /**
     * Given a valid sprite sheet, detects and returns the bounding rectangle of every individual sprite. The method may not be perfect and
     * return grouped sprites if they're not contiguous. Does not alter the source common in any way.
     *
     * @param spriteSheet the sprite sheet to unpack
     * @return list of extracted sprite bounds
     */
    private fun discoverSprites(spriteSheet: Pixmap, backgroundColor: Color, startingId: Int): SliceResult {

        val spriteBoundsList = findSprites(spriteSheet, backgroundColor)
        Log.info("Found ${spriteBoundsList.count()} sprites.")

        // any sprites completely inside other sprites? exclude 'em.
        val filteredSpriteBoundsList = filterSpritesContainedCompletelyInOtherSprites(spriteBoundsList)
        Log.info("${spriteBoundsList.count()} sprites remaining after filtering.")

        // any sprites have one or more of their edges inside anothers? merge 'em.
        val filteredAndMergedSpriteBoundsList = mergeSpritesWithOverlappingEdges(filteredSpriteBoundsList)
        Log.debug("${spriteBoundsList.count()} sprites remaining after merging sprites with overlapping edges.")

        return toAOImages(filteredAndMergedSpriteBoundsList, startingId)
    }

    /*
     * Sometimes we have sprites that have components that aren't connected, but still part of the same sprite.
     *
     * See the `unpacker/Intersecting.png` image in the test/resources directly for an example of this. Goku's aura
     * near the top forms a crown that isn't directly connected to the rest of his sprite, but should clearly not be treated
     * as a separate sprite. Instead, we check if the edges of their corresponding boundaries are contained inside each other.
     *
     * It's very important that there is a distinction made between entire edges inside of other sprite bounds and not
     * simply searching for an intersection. Densely packed sprite sheets may have many intersections among sprites, but
     * should not be merged.
     *
     * As a consideration for the future, this option might become toggleable.
     */
    private fun mergeSpritesWithOverlappingEdges(spriteBoundsList: List<Rectangle>): List<Rectangle> {
        if (spriteBoundsList.size <= 1)
            return spriteBoundsList

        val mergedSpriteBoundsList = mutableListOf(*spriteBoundsList.toTypedArray())

        for (a: Rectangle in spriteBoundsList) {
            for (b in spriteBoundsList.reversed()) {
                if (a == b) {
                    break
                }

                if (a.containsLine(b)) {
                    mergedSpriteBoundsList.remove(a)
                    mergedSpriteBoundsList.remove(b)

                    val c = a.union(b)
                    mergedSpriteBoundsList.add(c)
                }
            }
        }

        return mergedSpriteBoundsList
    }

    /*
     * Some sprites are located entirely inside of other sprites. A sprite that is within another sprite completely
     * is considered part of that sprite and its boundaries are discarded.
     */
    private fun filterSpritesContainedCompletelyInOtherSprites(spriteBoundsList: List<Rectangle>): List<Rectangle> {
        if (spriteBoundsList.size <= 1)
            return spriteBoundsList

        val filteredSpriteBoundsList = mutableListOf<Rectangle>()

        val spriteBoundsListByAreaAsc = spriteBoundsList.sortedBy { r -> r.area() }
        for (spriteBoundsAsc in spriteBoundsListByAreaAsc) {
            for (spriteBoundsDesc in spriteBoundsListByAreaAsc.reversed()) {

                if (spriteBoundsAsc.area() >= spriteBoundsDesc.area()) {
                    filteredSpriteBoundsList.add(spriteBoundsAsc)
                    break
                }

                if (spriteBoundsDesc.contains(spriteBoundsAsc)) {
                    break
                }
            }
        }

        return filteredSpriteBoundsList
    }

    private fun findSprites(spriteSheet: Pixmap,
                            backgroundColor: Color): List<Rectangle> {

        val spriteBoundsList = ArrayList<Rectangle>()
        val width = spriteSheet.width
        val height = spriteSheet.height
        val white = Color(0f, 0f, 0f, 1f)

        for (x in 0..width) {
            for (y in 0..height) {
                val pixel = Color(spriteSheet.getPixel(x, y))
                if (pixel.a > 0f && !pixel.equals(white)) {
                    Log.info("Found a sprite starting at ($x, $y) with color $pixel")
                    val spritePlot = plotSprite(spriteSheet, Point(x, y), backgroundColor)
                    val spriteBounds = constructSpriteBoundsFromPlot(spritePlot)

                    Log.info("The identified sprite has an area of ${spriteBounds.width}x${spriteBounds.height}")

                    spriteBoundsList.add(spriteBounds)
                    erasePoints(spriteSheet, spritePlot, backgroundColor)
                }
            }
        }

        return spriteBoundsList
    }

    private fun toAOImages(spriteBoundsList: List<Rectangle>, startingId: Int): SliceResult {
        var id = startingId
        val builder = SliceResult.Builder.create()
        spriteBoundsList.forEach { rect -> builder.withImage(AOImage(id++, rect.x, rect.y, file.nameWithoutExtension().toInt(), rect.width, rect.height)) }
        return builder.build()
    }

    fun erasePoints(spriteSheet: Pixmap, spritePlot: List<Point>, backgroundColor: Color) {
        spriteSheet.blending = Pixmap.Blending.None
        val clearPixel = backgroundColor.toIntBits().shl(8)
        spritePlot.forEach { p -> spriteSheet.drawPixel(p.x, p.y, clearPixel) }
    }


    private fun plotSprite(spriteSheet: Pixmap, point: Point, @Suppress("UNUSED_PARAMETER") backgroundColor: Color): List<Point> {
        val unvisited = LinkedList<Point>()
        val visited = hashSetOf(point)

        unvisited.addAll(neighbors(point, spriteSheet).filter { Color(spriteSheet.getPixel(it.x, it.y)).a > 0f })

        while (unvisited.isNotEmpty()) {
            val currentPoint = unvisited.pop()
            val currentColor = Color(spriteSheet.getPixel(currentPoint.x, currentPoint.y))

            if (currentColor.a > 0f) {
                unvisited.addAll(neighbors(currentPoint, spriteSheet).filter {
                    val neighborColor = Color(spriteSheet.getPixel(it.x, it.y))
                    !visited.contains(it) &&
                            !unvisited.contains(it) &&
                            neighborColor.a > 0f
                })
                visited.add(currentPoint)
            }
        }

        return visited.toList()
    }

    private fun neighbors(point: Point, spriteSheet: Pixmap): List<Point> {
        val neighbors = ArrayList<Point>(8)
        val imageWidth = spriteSheet.width - 1
        val imageHeight = spriteSheet.height - 1

        // Left neighbor
        if (point.x > 0)
            neighbors.add(Point(point.x - 1, point.y))

        // Right neighbor
        if (point.x < imageWidth)
            neighbors.add(Point(point.x + 1, point.y))

        // Top neighbor
        if (point.y > 0)
            neighbors.add(Point(point.x, point.y - 1))

        // Bottom neighbor
        if (point.y < imageHeight)
            neighbors.add(Point(point.x, point.y + 1))

        // Top-left neighbor
        if (point.x > 0 && point.y > 0)
            neighbors.add(Point(point.x - 1, point.y - 1))

        // Top-right neighbor
        if (point.x < imageWidth && point.y > 0)
            neighbors.add(Point(point.x + 1, point.y - 1))

        // Bottom-left neighbor
        if (point.x > 0 && point.y < imageHeight - 1)
            neighbors.add(Point(point.x - 1, point.y + 1))

        // Bottom-right neighbor
        if (point.x < imageWidth && point.y < imageHeight)
            neighbors.add(Point(point.x + 1, point.y + 1))

        return neighbors
    }

    private fun constructSpriteBoundsFromPlot(points: List<Point>): Rectangle {
        if (points.isEmpty())
            throw IllegalArgumentException("No points to span Rectangle from.")

        val left = points.reduce { current, next ->
            if (current.x > next.x) next else current
        }
        val top = points.reduce { current, next ->
            if (current.y > next.y) next else current
        }
        val right = points.reduce { current, next ->
            if (current.x < next.x) next else current
        }
        val bottom = points.reduce { current, next ->
            if (current.y < next.y) next else current
        }

        return Rectangle(left.x, top.y,
                right.x - left.x + 1,
                bottom.y - top.y + 1)
    }
}

private fun Rectangle.area(): Int {
    return this.height * this.width
}

fun Rectangle.containsLine(line: List<Point>): Boolean {
    for (point in line)
        if (!this.contains(point))
            return false

    return true
}

fun Rectangle.containsLine(r: Rectangle): Boolean {
    val top = (this.x..this.x + this.width).map { Point(it, this.y) }
    val bottom = (this.x..this.x + this.width).map { Point(it, this.y + this.height) }
    val left = (this.y..this.y + this.height).map { Point(this.x, it) }
    val right = (this.y..this.y + this.height).map { Point(this.x + this.width, it) }

    return r.containsLine(top) ||
            r.containsLine(bottom) ||
            r.containsLine(left) ||
            r.containsLine(right)
}
