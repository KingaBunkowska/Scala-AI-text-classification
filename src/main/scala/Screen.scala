import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.layout.StackPane
import javafx.event.ActionEvent
import javafx.event.EventHandler
import scalafx.scene.text.Font
import scalafx.Includes._
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.geometry.Insets
import scalafx.scene.control.{Button,ComboBox, TextArea, TextField}
import scalafx.scene.layout.{StackPane, VBox, HBox}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import java.io.File
import scala.io.Source


// Object responsible for listing all files inside the selected folder

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

// An object designed to read data from files

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

// function used to retrieve the model used for text analysis
  def setModel(newModel: Model, newAccuracy: Double): Unit ={
    model=newModel
    accuracy = newAccuracy
  }

 
  stage = new JFXApp.PrimaryStage {
    title = "Aplikacja do analizy tekstu"
    width = 900
    height = 600
    scene = new Scene {
      // The field for entering text for analysis
      val textField = new TextArea {
        editable = true
        promptText = "Wpisz tekst tutaj..."
        prefHeight = 100
        prefWidth = 440
        wrapText = true
      }
      
      // A field displaying the text analysis result
      val textArea = new TextArea {
        editable = false
        font = Font.font(30)
        prefHeight = 50
        prefWidth = 440

      }

// Button that turns on the analysis of the entered text
      val button1 = new Button {
        text = "Analiza tekstu wpisanego"
        onAction = new EventHandler[ActionEvent] {
              override def handle(event: ActionEvent): Unit = {
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

// filling the combo box with selectable folder filenames
      val initialDirectory = "pliki_testowe"
      val fileNames = FileLister.listFilesInDirectory(initialDirectory)
      comboBox.items = ObservableBuffer(fileNames)

// Button enabling analysis of text selected from the file
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

// A field that displays the effectiveness of the entire trained model in determining whether a given text is AI-generated.
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