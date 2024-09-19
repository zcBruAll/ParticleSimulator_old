import scalafx.scene.paint.Color

case class Particle(
  var x: Double,
  var y: Double,
  var vx: Double,
  var vy: Double,
  var mass: Double,
  var radius: Double,
  val color: Color  // Add color property
) {
  val GravitationnalConstant = 6.67 * Math.pow(10, -11)

  // Move the particle
  def move(dt: Long): Unit = {
    x += vx * dt
    y += vy * dt
  }

  def calculateForce(x: Double, y: Double, mass: Double): (Double, Double) = {
    val dx = x - this.x
    val dy = y - this.y
    val distance = math.sqrt(dx * dx + dy * dy)
    
    if (distance > 0) {
      val force = (GravitationnalConstant * mass * this.mass) / (distance * distance)
      ((dx / distance) * force, (dy / distance) * force)
    } else {
      (0, 0)
    }
  }

  // Apply attraction between particles of the same color
  def applyAttraction(other: Particle, dt: Double): Boolean = {
    val dx = other.x - this.x
    val dy = other.y - this.y
    val distance = math.sqrt(dx * dx + dy * dy)

    if (distance < this.radius + other.radius) {
      // Coalesce if particles are close enough
      merge(other)
      true // Indicate a merge has happened
    } else {
      // Normal gravitational attraction
      val force = (GravitationnalConstant * other.mass * this.mass) / math.pow(distance, 2)
      val ax = (dx / distance) * (force / this.mass)
      val ay = (dy / distance) * (force / this.mass)
      this.vx += ax * dt
      this.vy += ay * dt
      false
    }
  }

  def merge(other: Particle): Unit = {
    // Calculate new mass
    val totalMass = this.mass + other.mass
    
    // Compute mass-weighted velocity (momentum conservation)
    this.vx = (this.vx * this.mass + other.vx * other.mass) / totalMass
    this.vy = (this.vy * this.mass + other.vy * other.mass) / totalMass

    // Compute mass-weighted position
    this.x = (this.x * this.mass + other.x * other.mass) / totalMass
    this.y = (this.y * this.mass + other.y * other.mass) / totalMass

    // Update mass and radius
    this.mass = totalMass
    this.radius = math.sqrt(this.radius * this.radius + other.radius * other.radius)
  }

  // Check if the particle hits the window edges and bounce
  def checkBounds(width: Double, height: Double): Unit = {
    if (x + radius < 0) x = width
    else if (x > width) x = 0

    if (y + radius < 0) y = height
    else if (y > height) y = 0
  }
}
