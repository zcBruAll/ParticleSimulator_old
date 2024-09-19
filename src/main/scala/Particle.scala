import scalafx.scene.paint.Color

case class Particle(
  var x: Double,
  var y: Double,
  var vx: Double,
  var vy: Double,
  val mass: Double,
  val radius: Double,
  val color: Color  // Add color property
) {
  // Move the particle
  def move(dt: Long): Unit = {
    x += vx * dt
    y += vy * dt
  }

  // Apply attraction between particles of the same color
  def applyAttraction(other: Particle, dt: Long): Unit = {

    val GravitationnalConstant = 6.67 * Math.pow(10, -11)

    val dx = other.x - this.x
    val dy = other.y - this.y
    val distance = math.sqrt(dx * dx + dy * dy)
    
    val force = (GravitationnalConstant * other.mass * this.mass) / Math.pow(distance, 2)

    val accelThis = force / this.mass

    // Direction of the force (normalize dx, dy)
    val ax = (dx / distance) * accelThis
    val ay = (dy / distance) * accelThis

    // Update velocities based on acceleration and time step
    this.vx += ax * dt
    this.vy += ay * dt

    // The other particle will experience equal but opposite force
    other.vx -= ax * dt
    other.vy -= ay * dt
  }

  // Check if the particle hits the window edges and bounce
  def checkBounds(width: Double, height: Double): Unit = {
    if (x + radius < 0) x = width
    else if (x > width) x = 0

    if (y + radius < 0) y = height
    else if (y > height) y = 0
  }
}
