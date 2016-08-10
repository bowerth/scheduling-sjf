package de.alxbok.examples.scheduling

import java.io.BufferedReader
import java.io.FileReader
import java.util.Comparator
import java.util.{ PriorityQueue => Heap }
import java.io.InputStreamReader

case class PizzaOrder(arrivedAt: Int, cookingDuration: Int)

object ShortesJobFirst {

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      println("Usage: java [-Dlogging=on] -jar scheduling-0.0.0.jar <path_to_input_file>")
    } else {
      val inputFileName = args(0)
      val reader = new BufferedReader(new FileReader(inputFileName))
      try {
        val totalOrders = reader.readLine().trim.toInt
        println(s"total orders: $totalOrders")
        val orders = Iterator.continually(reader.readLine())
        processOrders(orders, totalOrders)
      } finally {
        reader.close()
      }
    }
  }

  def processOrders(orders: Iterator[String], totalOrders: Double): Unit = {
    val waiting = newMinHeap
    var lineNo = 2
    var ovenUsedUntil = 0
    var avgWaitTime = 0d

    var nextLine = Option(orders.next())
    while (nextLine.isDefined || !waiting.isEmpty()) {
      val next = nextLine.map(readOrder(_, lineNo))
      if (!waiting.isEmpty() && next.map(_.arrivedAt > ovenUsedUntil).getOrElse(true)) {
        val nextWaiting = waiting.poll()
        avgWaitTime += (ovenUsedUntil - nextWaiting.arrivedAt + nextWaiting.cookingDuration) / totalOrders
        ovenUsedUntil += nextWaiting.cookingDuration
        LOG(f"[ waiting ${waiting.size}%3d ] cook ${nextWaiting} from Q until $ovenUsedUntil, current AWT: $avgWaitTime")
      } else {
        for (n <- next) {
          if (n.arrivedAt < ovenUsedUntil) {
            waiting.offer(n)
            LOG(f"[ waiting ${waiting.size}%3d ] arrd ${n} < ovenUsedUntil ${ovenUsedUntil}, put into wait queue")
          } else {
            ovenUsedUntil = n.arrivedAt + n.cookingDuration
            avgWaitTime += n.cookingDuration / totalOrders
            LOG(f"[ waiting ${waiting.size}%3d ] cook ${n} until $ovenUsedUntil, current AWT: $avgWaitTime")
          }
        }
        nextLine = Option(orders.next())
        lineNo += 1
      }
    }
    println(s"min average wait time: ~${avgWaitTime.toInt} (${avgWaitTime})")
  }

  // NOT INTERESTING

  val linePattern = "(\\d+) (\\d+)".r

  def readOrder(line: String, lineNo: Int): PizzaOrder = line match {
    case linePattern(arrivedAt, cookingDuration) => PizzaOrder(arrivedAt.toInt, cookingDuration.toInt)
    case _                                       => throw new RuntimeException(s"Error in line: $lineNo, could not read input: '$line'")
  }

  def newMinHeap: Heap[PizzaOrder] = {
    val minHeap = new Heap(new Comparator[PizzaOrder] {
      def compare(o1: PizzaOrder, o2: PizzaOrder): Int = {
        val cmp1 = o1.cookingDuration.compareTo(o2.cookingDuration)
        if (cmp1 == 0)
          o1.arrivedAt.compareTo(o2.arrivedAt)
        else
          cmp1
      }
    })
    minHeap
  }

  val loggingOn = System.getProperty("logging") == "on"
  def LOG(action: => String): Unit = if (loggingOn) println(action)

}
