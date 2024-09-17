import scala.collection.mutable.ArrayBuffer
import scalafx.animation.{AnimationTimer}
import scalafx.scene.canvas.Canvas

class Simulator(var width: Double, var height: Double) {

  val particles: ArrayBuffer[Particle] = ArrayBuffer()

  def addParticle(p: Particle): Unit = {
    particles += p
  }

  def update(): Unit = {
    particles.foreach { p =>
      p.move()
      p.checkBounds(width, height)
    }
  }

  def draw(canvas: Canvas): Unit = {
    val gc = canvas.graphicsContext2D
    gc.clearRect(0, 0, width, height)

    particles.foreach { p =>
      gc.fillOval(p.x - p.radius, p.y - p.radius, p.radius * 2, p.radius * 2)
    }
  }

  def startSimulation(canvas: Canvas): Unit = {
    val timer = AnimationTimer { _ =>
      update()
      draw(canvas)
    }
    timer.start()
  }
}
