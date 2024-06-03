import org.apache.spark.sql.SparkSession

object SimpleApplication {
  def main(args: Array[String]): Unit = {
    // Spark config
    val spark = SparkSession.builder()
      .appName("Simple Application")
      .master("local")
      .getOrCreate()

    val dataset = new Dataset(spark)
    dataset.load("dataset/AI_Human_text.csv")
    // Show the first row of the DataFrame
    dataset.df.show(10)
    println(dataset.df.take(1001).tail(1).getString(0))

    val (train_X, train_Y, test_X, test_Y) = dataset.split(0.3, 42)


    val model = new Model(spark)

    println("wywolanie train")
    model.train(train_X, train_Y)
    println("zakonczono train")
    // println("Evalutation")
    // println(model.evaluate(test_X, test_Y))

    // println(model.predict("Hello, today we will talk about Scala programming language"))

    // End Spark session
    spark.stop()
  }
}