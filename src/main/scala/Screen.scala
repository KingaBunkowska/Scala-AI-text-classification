import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.StackPane
import javafx.event.ActionEvent
import javafx.event.EventHandler
import scalafx.scene.text.Font
import scalafx.Includes._
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.geometry.Insets

import scalafx.scene.control.{Button,ComboBox, TextArea, TextField}
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.layout.HBox
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos

import java.io.File
import scala.io.Source

object FileLister {
  def listFilesInDirectory(directoryPath: String): List[String] = {
    val directory = new File(directoryPath)
    if (directory.exists && directory.isDirectory) {
      val files = directory.listFiles
      if (files != null) {
        files.filter(_.isFile).map(_.getName).toList
      } else {
        List.empty[String]
      }
    } else {
      List.empty[String]
    }
  }
}


object FileReader {
  def readFileToString(filePath: String): String = {
    val source = Source.fromFile(filePath)
    try {
      source.mkString
    } finally {
      source.close()
    }
  }
}








object Screen extends JFXApp {


  var model: Model = _
  var accuracy: Double = _

  def setModel(newModel: Model, newAccuracy: Double): Unit ={
    model=newModel
    accuracy = newAccuracy
    print("dokladnosc modelu")
    print(accuracy)
    // accuracyValue.text = "Zmienione"
  }

  stage = new JFXApp.PrimaryStage {
    title = "Aplikacja do analizy tekstu"
    width = 900
    height = 600
    scene = new Scene {
      // Definiujemy textField i button wewnątrz bloku inicjalizacyjnego sceny
      val textField = new TextArea {
        editable = true
        promptText = "Wpisz tekst tutaj..."
        prefHeight = 100
        prefWidth = 440
        // alignment = Pos.TopLeft
        wrapText = true
      }
      
      // TextArea do wyświetlania tekstu
      val textArea = new TextArea {
        editable = false
        font = Font.font(30)
        prefHeight = 50
        prefWidth = 440

      }


      val button1 = new Button {
        text = "Analiza tekstu wpisanego"
        onAction = new EventHandler[ActionEvent] {
              override def handle(event: ActionEvent): Unit = {
                // textArea.text="Tekst napisany przez AI"
                var isAIGenerated=model.predict(textField.text.value)
                if(isAIGenerated==1.0){
                  textArea.text="Tekst napisany przez AI"
                }
                else{
                  textArea.text="Tekst autorski"
                }
                accuracyValue.text=f"$accuracy%.2f"
          }
        }
      }


      val vbox1 = new VBox {
        spacing = 10
        children = Seq(
          textField,
          button1,
          textArea
        )
      }


      val comboBox = new ComboBox[String] {
        promptText = "Wybierz plik z listy"
      }

      val initialDirectory = "pliki_testowe"
      val fileNames = FileLister.listFilesInDirectory(initialDirectory)
      comboBox.items = ObservableBuffer(fileNames)

      val button2 = new Button {
        text = "Analiza tekstu wybranego z foldera"
        onAction = new EventHandler[ActionEvent] {
            override def handle(event: ActionEvent): Unit = {
            println(comboBox.value.value)
            val selectedFile = comboBox.value.value
            if (selectedFile != null) {
              val filePath = "pliki_testowe/"+selectedFile
              val fileContent = model.predict(FileReader.readFileToString(filePath))
              if(fileContent==1.0){
                textArea.text="Tekst napisany przez AI"
              }
              else{
                textArea.text="Tekst autorski"
              }
              accuracyValue.text=f"$accuracy%.2f"
            }
          }
        }
      }

      val accuracyTitle = new TextArea {
        editable = false
        text="Dokladnosc modelu"
        font = Font.font(16)
        prefHeight = 20
        prefWidth = 80
        style = "-fx-border-color: transparent; -fx-background-color: transparent;"

      }

      val accuracyValue = new TextArea {
        editable = false
        text= ""
        font = Font.font(30)
        prefHeight = 40
        prefWidth = 80
        style = "-fx-border-color: transparent; -fx-background-color: transparent;"
      }


      val vbox2 = new VBox {
        spacing = 10
        children = Seq(
          comboBox,
          button2,
          accuracyTitle,
          accuracyValue
        )
      }

      val hbox = new HBox {
        spacing = 100
        children = Seq(
          vbox1,
          vbox2
        )
        padding = Insets(20)
      }

      content = hbox

    }
  }



}