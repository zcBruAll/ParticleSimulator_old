import scala.collection.mutable.ArrayBuffer
import scalafx.animation.AnimationTimer
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import java.security.Timestamp
import java.time.Instant

class Simulator(var width: Double, var height: Double) {
  private val particles = scala.collection.mutable.Buffer[Particle]()

  private var oldTimestamp = Instant.now().toEpochMilli()

  def addParticle(p: Particle): Unit = {
    particles += p
  }

  def update(dt: Long): Unit = {
    val rootBounds = (0.0, 0.0, width, height)
    val quadTree = particles.foldLeft(Empty: QuadTree) { (tree, p) =>
      insert(p, tree, rootBounds)
    }

    // Collect particles that have merged
    val mergedParticles = scala.collection.mutable.Set[Particle]()

    // Calculate the forces for each particle using the QuadTree
    particles.foreach { p =>
      if (!mergedParticles.contains(p)) {
        particles.foreach { other =>
          if (p != other && !mergedParticles.contains(other)) {
            if (p.applyAttraction(other, dt)) {
              mergedParticles += other // Mark the other particle as merged
            }
          }
        }
      }
    }

    // Remove merged particles
    particles --= mergedParticles

    // Move each particle based on its updated velocity
    particles.foreach(_.move(dt))
  }

  def startSimulation(canvas: Canvas): Unit = {
    val gc = canvas.graphicsContext2D

    // Create the AnimationTimer using the function-based approach
    val timer = AnimationTimer { now =>
      val timestampNow = Instant.now().toEpochMilli()
      update((timestampNow - oldTimestamp) / 2)
      oldTimestamp = timestampNow

      gc.clearRect(0, 0, canvas.width.value, canvas.height.value)

      particles.foreach { p =>
        gc.setFill(p.color) // Set the color for each particle
        gc.fillOval(p.x - p.radius, p.y - p.radius, p.radius * 2, p.radius * 2)
      }
    }

    timer.start() // Start the animation
  }
  
  def updateBounds(newWidth: Double, newHeight: Double): Unit = {
    this.width = newWidth
    this.height = newHeight
  }
}
