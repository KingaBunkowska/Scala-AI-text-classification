import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.StackPane
import javafx.event.ActionEvent
import javafx.event.EventHandler

import scalafx.scene.control.{Button, TextField}
import scalafx.scene.layout.{StackPane, VBox}

object Screen extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title = "Prosty ekran z guzikiem"
    width = 400
    height = 300
  scene = new Scene {
      // Definiujemy textField i button wewnątrz bloku inicjalizacyjnego sceny
      val textField = new TextField {
        promptText = "Wpisz tekst tutaj..."
      }

      val button = new Button {
        text = "Kliknij mnie!"
        // onAction = () => println(s"Tekst z TextField: ${textField.text.value}")
        onAction = new EventHandler[ActionEvent] {
              override def handle(event: ActionEvent): Unit = {
                println(textField.text.value)
          }
        }
      }


      content = new VBox {
        spacing = 10
        children = Seq(
          textField,
          button
        )
      }

      // content = new StackPane {
      //   children = Seq(
      //     new Button {
      //       text = "Kliknij mnie!"
      //       onAction = new EventHandler[ActionEvent] {
      //         override def handle(event: ActionEvent): Unit = {
      //           println("Przycisk zostal wcisniety!")
      //         }
      //       }
      //     }
      //   )
      // }
    }
  }
}