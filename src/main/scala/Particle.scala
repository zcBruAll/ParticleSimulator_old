case class Particle(
    var x: Double,
    var y: Double,
    var vx: Double,   // velocity in the x direction
    var vy: Double,   // velocity in the y direction
    radius: Double    // radius of the particle
) {
  def move(): Unit = {
    x += vx
    y += vy
  }

  def checkBounds(width: Double, height: Double): Unit = {
    if (x - radius < 0) x = width - radius
    else if (x + radius > width) x = radius

    if (y - radius < 0) y = height - radius
    else if (y + radius > height) y = radius
  }
}
