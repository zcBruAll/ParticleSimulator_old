import scala.collection.mutable.ArrayBuffer
import scalafx.animation.AnimationTimer
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color

class Simulator(var width: Double, var height: Double, colorAttractionStrength: Map[Color, Double]) {
  private val particles = scala.collection.mutable.Buffer[Particle]()

  def addParticle(p: Particle): Unit = {
    particles += p
  }

  def update(): Unit = {
    // Apply attraction between particles of the same color
    for (i <- particles.indices) {
      for (j <- i + 1 until particles.length) {
        val p1 = particles(i)
        val p2 = particles(j)
        val attractionStrength = colorAttractionStrength.getOrElse(p1.color, 0.0)
        p1.applyAttraction(p2, attractionStrength)
      }
    }

    // Update the position and check bounds for each particle
    particles.foreach { p =>
      p.move()
      p.checkBounds(width, height)
    }
  }

  def startSimulation(canvas: Canvas): Unit = {
    val gc = canvas.graphicsContext2D

    // Create the AnimationTimer using the function-based approach
    val timer = AnimationTimer { now =>
      update()

      gc.clearRect(0, 0, canvas.width.value, canvas.height.value)

      particles.foreach { p =>
        gc.setFill(p.color) // Set the color for each particle
        gc.fillOval(p.x - p.radius, p.y - p.radius, p.radius * 2, p.radius * 2)
      }
    }

    timer.start() // Start the animation
  }
}
