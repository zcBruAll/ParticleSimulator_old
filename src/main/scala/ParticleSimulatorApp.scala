import scalafx.application.JFXApp3
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.Pane
import scalafx.stage.StageStyle
import scalafx.Includes._
import scalafx.scene.input.KeyCode
import scalafx.application.Platform
import scalafx.scene.paint.Color

object ParticleSimulatorApp extends JFXApp3 {

  val DefaultWidth = 1920
  val DefaultHeight = 1200

  override def start(): Unit = {
    // Create the canvas for the particle simulation
    val canvas = new Canvas(DefaultWidth, DefaultHeight)
    
    val simulator = new Simulator(DefaultWidth, DefaultHeight)

    // Add some particles to the simulator
    val colors = List(Color.Red, Color.Blue, Color.Green, Color.Orange, Color.Purple)
    for (i <- 1 to 250) {
      val p = Particle(
        x = math.random() * DefaultWidth,
        y = math.random() * DefaultHeight,
        vx = 0,
        vy = 0,
        mass = Int.MaxValue / 50,
        radius = 3,
        color = colors(i % colors.length) // Assign one of the 5 colors in rotation
      )
      simulator.addParticle(p)
    }
    simulator.startSimulation(canvas)

    // Wrap the canvas in a Pane so it can be used as window content
    val contentPane = new Pane {
      children = canvas
    }

    // Set up the stage (using the default JFXApp3 stage)
    stage = new JFXApp3.PrimaryStage {
      title = "Particle Life Simulator"
      scene = new scalafx.scene.Scene {
        root = contentPane

        // Detect key presses and exit on 'Esc' key
        onKeyPressed = event => {
          if (event.code == KeyCode.Escape) {
            Platform.exit() // Exit the app when Esc is pressed
          }
        }
      }

      // Force the window to always stay in fullscreen mode
      fullScreen = true
      fullScreenExitHint = "" // Remove the fullscreen exit hint
      fullScreenExitKey = null // Disable Esc key for exiting fullscreen mode

      // Remove window decorations (close, minimize buttons) in fullscreen mode
      initStyle(StageStyle.Undecorated)
    }

    canvas.widthProperty().bind(stage.scene().widthProperty())
    canvas.heightProperty().bind(stage.scene().heightProperty())

    // Update the simulator's bounds when the canvas is resized
    canvas.widthProperty().addListener { (_, _, newWidth) =>
      simulator.updateBounds(newWidth.doubleValue(), canvas.height.value)
    }
    canvas.heightProperty().addListener { (_, _, newHeight) =>
      simulator.updateBounds(canvas.width.value, newHeight.doubleValue())
    }
  }
}
