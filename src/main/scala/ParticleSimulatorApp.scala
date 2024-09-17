import scalafx.application.JFXApp3
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.Pane
import scalafx.stage.StageStyle
import scalafx.Includes._
import scalafx.scene.input.KeyCode
import scalafx.application.Platform

object ParticleSimulatorApp extends JFXApp3 {

  var defaultWidth = 1920
  var defaultHeight = 1200

  override def start(): Unit = {
    // Create the canvas for the particle simulation
    val canvas = new Canvas(defaultWidth, defaultHeight)
    val simulator = new Simulator(defaultWidth, defaultHeight)

    // Add some particles to the simulator
    for (_ <- 1 to 500) {
      val p = Particle(
        x = math.random() * defaultWidth,
        y = math.random() * defaultHeight,
        vx = (math.random() - 0.5) * 5,
        vy = (math.random() - 0.5) * 5,
        radius = 3
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

      // Optional: Remove window decorations (close, minimize buttons) in fullscreen mode
      initStyle(StageStyle.Undecorated)
    }
  }
}
