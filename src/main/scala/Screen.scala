import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.StackPane
import javafx.event.ActionEvent
import javafx.event.EventHandler

object Screen extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title = "Prosty ekran z guzikiem"
    width = 400
    height = 300
    scene = new Scene {
      content = new StackPane {
        children = Seq(
          new Button {
            text = "Kliknij mnie!"
            onAction = new EventHandler[ActionEvent] {
              override def handle(event: ActionEvent): Unit = {
                println("Przycisk zostal wcisniety!")
              }
            }
          }
        )
      }
    }
  }
}