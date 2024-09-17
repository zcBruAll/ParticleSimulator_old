import scalafx.scene.paint.Color

case class Particle(
  var x: Double,
  var y: Double,
  var vx: Double,
  var vy: Double,
  val radius: Double,
  val color: Color  // Add color property
) {
  // Move the particle
  def move(): Unit = {
    x += vx
    y += vy
  }

  // Apply attraction between particles of the same color
  def applyAttraction(other: Particle, attractionStrength: Double): Unit = {
    val dx = other.x - this.x
    val dy = other.y - this.y
    val distance = math.sqrt(dx * dx + dy * dy)

    if (distance > 0 && distance < 200) {  // Apply attraction if within a certain distance
      val force = attractionStrength / distance

      // Apply force to velocities
      this.vx += force * dx / distance
      this.vy += force * dy / distance
      other.vx -= force * dx / distance
      other.vy -= force * dy / distance
    }
  }

  // Check if the particle hits the window edges and bounce
  def checkBounds(width: Double, height: Double): Unit = {
    if (x + radius < 0) x = width
    else if (x > width) x = 0

    if (y + radius < 0) y = height
    else if (y > height) y = 0
  }
}
