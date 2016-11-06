package de.bowerth.examples.scheduling

import scala.io.Source
import scala.collection.mutable.ArrayBuffer

object ShortesJobFirst {

  def main(args: Array[String]): Unit = {

    if (args.isEmpty) {
      println("Usage: sbt 'runMain de.bowerth.examples.scheduling.ShortesJobFirst src/main/scala/de/bowerth/examples/scheduling/input03.txt'")
    } else {

      val inputFileName = args(0)
      val lines = Source.fromFile(inputFileName).getLines().toList
      val totalOrders = lines.head.toInt

      // store orders as tuples, sort to split at current time
      val allOrders = lines
        .tail
        .map(_.split(" ") match {
               case Array(i, j) => (i.toInt, j.toInt)
             }) sortWith (_._1 < _._1) // sort by first column

      if (allOrders.length != totalOrders) {
        println("Warning: Input file states " + totalOrders + " and contains " + allOrders.length + " orders")
      } else {
        println("Treating " + totalOrders + " orders")

        // initialize
        var currentTime = 0 // one oven
        val orderWaitingTime = new ArrayBuffer[Int] // could use immutable, total orders known
        var remainingOrders = allOrders // shuffle after each order

        while (remainingOrders.length > 0) { // known total number of orders; unlimited time
          val splitOrders = remainingOrders span ( _._1 <= currentTime ) // single pass list
          // prepare to queue orders, future order can receive priority
          val(arrivedOrders: List[(Int, Int)], futureOrders: List[(Int, Int)]) = splitOrders
          if (arrivedOrders.isEmpty) {
            currentTime +=1 // increment time until customer appears
          } else {
            val arrivedOrdersSorted = arrivedOrders sortWith (_._2 < _._2) // sort by second column
            val acceptedOrder = arrivedOrdersSorted.head                   // element with min cooking time
            remainingOrders = arrivedOrdersSorted.tail ::: futureOrders    // queue untreated orders
            val (arrivalTime, cookingTime) = acceptedOrder                 // split tuple
            currentTime = currentTime + cookingTime                        // increment oven runtime
            orderWaitingTime += currentTime - arrivalTime                  // log current order waiting time
          }
        }

        // currentTime
        // orderWaitingTime
        // remainingOrders

        val avgWaitTime = orderWaitingTime.sum / totalOrders.toDouble
        println(s"min average wait time: ~${avgWaitTime.toInt} (${avgWaitTime})")

      }
    }
  }
}

