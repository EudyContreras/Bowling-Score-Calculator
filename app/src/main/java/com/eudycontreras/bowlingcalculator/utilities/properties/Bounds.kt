package com.eudycontreras.bowlingcalculator.utilities.properties

/**
 * Created by eudycontreras.
 */

data class Bounds(
    var coordinate: Coordinate = Coordinate(),
    var dimension: Dimension = Dimension()
) {
    var left: Float
        get() = coordinate.x
        set(value) {
            coordinate.x = value
        }

    var right: Float
        get() = coordinate.x + dimension.width
        set(value) {
            dimension.width = value - left
        }

    var top: Float
        get() = coordinate.y
        set(value) {
            coordinate.y = value
        }

    var bottom: Float
        get() = coordinate.y + dimension.height
        set(value) {
            dimension.height = value - top
        }

    val height: Float
        get() = bottom - top

    val width: Float
        get() = right - left

    var paddings: Padding? = null
        set(value) {
            field = value
        }

    var margins: Margin? = null
        set(value) {
            field = value
            field?.let {
                val new = add(it)
                coordinate = new.coordinate
                dimension = new.dimension
            }
        }

    val drawableArea: Bounds
        get() {
            return if (paddings != null) {
                this.subtract(paddings!!)
            } else {
                this
            }
        }

    fun copyProps(): Bounds {
        val coordinates = this.coordinate.copyProps()
        val dimensions = this.dimension.copyProps()
        return Bounds(coordinates, dimensions)
    }

    fun intercepts(other: Bounds): Boolean {
        val x = this.coordinate.x
        val y = this.coordinate.y
        val width = this.dimension.width
        val height = this.dimension.height

        return x < other.coordinate.x + other.dimension.width && x + width > other.coordinate.x && y < other.coordinate.y + other.dimension.height && y + height > other.coordinate.y
    }

    fun isInside(x: Float, y: Float): Boolean {
        return (x > left && x < right && y > top && y < bottom)
    }

    fun isInside(bounds: Bounds): Boolean {
        return isInside(bounds.top, bounds.left, bounds.bottom, bounds.right)
    }

    fun isInside(top: Float, left: Float, bottom: Float, right: Float): Boolean {
        return this.top >= top && this.left >= left && this.bottom <= bottom && this.right <= right
    }

    fun add(dp: Float): Bounds {
        val newX = coordinate.x - dp
        val newY = coordinate.y - dp
        val newWidth = dimension.width + (dp * 2)
        val newHeight = dimension.height + (dp * 2)
        return Bounds(Coordinate(newX, newY), Dimension(newWidth, newHeight))
    }

     fun subtract(dp: Float): Bounds {
        val newX = coordinate.x + dp
        val newY = coordinate.y + dp
        val newWidth = dimension.width - (dp * 2)
        val newHeight = dimension.height - (dp * 2)
        return Bounds(Coordinate(newX, newY), Dimension(newWidth, newHeight))
    }

    fun add(margin: Margin): Bounds {
        val newX = coordinate.x - margin.start
        val newY = coordinate.y - margin.top
        val newWidth = dimension.width + (margin.end + margin.start)
        val newHeight = dimension.height + (margin.bottom + margin.top)
        return Bounds(Coordinate(newX, newY), Dimension(newWidth, newHeight))
    }

    fun subtract(margin: Margin): Bounds {
        val newX = coordinate.x + margin.start
        val newY = coordinate.y + margin.top
        val newWidth = dimension.width - (margin.end + margin.start)
        val newHeight = dimension.height - (margin.bottom + margin.top)
        return Bounds(Coordinate(newX, newY), Dimension(newWidth, newHeight))
    }

    fun add(padding: Padding): Bounds {
        val newX = coordinate.x - padding.start
        val newY = coordinate.y - padding.top
        val newWidth = dimension.width + (padding.end + padding.start)
        val newHeight = dimension.height + (padding.bottom + padding.top)
        return Bounds(Coordinate(newX, newY), Dimension(newWidth, newHeight))
    }

    fun subtract(padding: Padding): Bounds {
        val newX = coordinate.x + padding.start
        val newY = coordinate.y + padding.top
        val newWidth = dimension.width - (padding.end + padding.start)
        val newHeight = dimension.height - (padding.bottom + padding.top)
        return Bounds(Coordinate(newX, newY), Dimension(newWidth, newHeight))
    }

    fun subtractDimension(width: Float, height: Float): Bounds {
        val newWidth = dimension.width - width
        val newHeight = dimension.height - height
        return Bounds(Coordinate(coordinate.x, coordinate.y), Dimension(newWidth, newHeight))
    }

    fun subtractCoordinates(x: Float, y: Float): Bounds {
        val newX = coordinate.x + x
        val newY = coordinate.y + y
        return Bounds(Coordinate(newX, newY), Dimension(dimension.width, dimension.height))
    }

    fun addDimension(width: Float, height: Float): Bounds {
        val newWidth = dimension.width + width
        val newHeight = dimension.height + height
        return Bounds(Coordinate(coordinate.x, coordinate.y), Dimension(newWidth, newHeight))
    }

    fun addCoordinates(x: Float, y: Float): Bounds {
        val newX = coordinate.x - x
        val newY = coordinate.y - y
        return Bounds(Coordinate(newX, newY), Dimension(dimension.width, dimension.height))
    }

    fun updateCoordinates(x: Float, y: Float) {
        coordinate.x = x
        coordinate.y = y
    }

    fun updateDimensions(width: Float = this.width, height: Float = this.height) {
        dimension.width = width
        dimension.height = height
    }

    fun update(bounds: Bounds, notifyChange: Boolean = true) {
        this.coordinate.x = bounds.coordinate.x
        this.coordinate.y = bounds.coordinate.y
        this.dimension.width = bounds.dimension.width
        this.dimension.height = bounds.dimension.height
    }

    fun addPadding(padding: Padding?): Bounds {
        padding?.let {
            coordinate.x = coordinate.x + it.start
            coordinate.y = coordinate.y + it.top
            dimension.width = dimension.width - (it.start + it.end)
            dimension.height = dimension.height - (it.top + it.bottom)
        }
        return this
    }

    fun reset(){
        coordinate.x = 0f
        coordinate.y = 0f
        dimension.width = 0f
        dimension.height = 0f
    }
}

