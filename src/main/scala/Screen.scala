import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.StackPane
import javafx.event.ActionEvent
import javafx.event.EventHandler

import scalafx.scene.control.{Button,ComboBox, TextArea, TextField}
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.layout.HBox
import scalafx.collections.ObservableBuffer


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
  stage = new JFXApp.PrimaryStage {
    title = "Prosty ekran z guzikiem"
    width = 800
    height = 600
    scene = new Scene {
      // Definiujemy textField i button wewnątrz bloku inicjalizacyjnego sceny
      val textField = new TextField {
        promptText = "Wpisz tekst tutaj..."
      }
      
      // TextArea do wyświetlania tekstu
      val textArea = new TextArea {
        editable = true
        prefHeight = 150
      }


      val button1 = new Button {
        text = "Kliknij mnie!"
        onAction = new EventHandler[ActionEvent] {
              override def handle(event: ActionEvent): Unit = {
              textArea.text = textField.text.value
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

      val initialDirectory = "C:/Users/macie/projekt_Scala/pliki_do_analizy"
      val fileNames = FileLister.listFilesInDirectory(initialDirectory)
      comboBox.items = ObservableBuffer(fileNames)

      val button2 = new Button {
        text = "Kliknij mnie!"
        // onAction = () => println(s"Tekst z TextField: ${textField.text.value}")
        onAction = new EventHandler[ActionEvent] {
            override def handle(event: ActionEvent): Unit = {
            println(comboBox.value.value)
            val selectedFile = comboBox.value.value
            if (selectedFile != null) {
              val filePath = "C:/Users/macie/projekt_Scala/pliki_do_analizy/"+selectedFile
              val fileContent = FileReader.readFileToString(filePath)
              textArea.text = fileContent
            }
          }
        }
      }


      val vbox2 = new VBox {
        spacing = 10
        children = Seq(
          comboBox,
          button2
        )
      }

      val hbox = new HBox {
        spacing = 100
        children = Seq(
          vbox1,
          vbox2
        )
      }

      content = hbox

    }
  }
}