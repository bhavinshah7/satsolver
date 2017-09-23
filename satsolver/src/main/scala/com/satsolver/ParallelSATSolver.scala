package com.satsolver

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * Created by Anindo Saha on 18/9/17.
  */


object Master {
  def props(startTime: Long, equation: Equation, noOfActors: Int): Props =
    Props(new Master(startTime, equation, noOfActors))

  final case class Begin()

  final case class Result(subsetNo: Int, result: Boolean)

}

class Master(val startTime: Long, val equation: Equation, val noOfActors: Int) extends Actor {

  import Evaluator._
  import Master._

  var counter = noOfActors

  def receive = {
    case Begin =>
      (0 until noOfActors).foreach(i => {
        val evaluator: ActorRef = context.actorOf(Evaluator.props("Actor:" + i, noOfActors), "Actor:" + i)
        evaluator ! Task(equation, i)
      })
    case Result(subsetNo: Int, result: Boolean) =>
      if (result) {
        println("SAT")
        println(System.currentTimeMillis() - startTime)
        context.system.terminate()
      } else {
        counter = counter - 1
        if (counter == 0) {
          println("UNSAT")
          println(System.currentTimeMillis() - startTime)
          context.system.terminate()
        }
        sender() ! Stop
      }
  }
}

object Evaluator {
  def props(actorNo: String, noOfSubsets: Int): Props = Props(new Evaluator(actorNo, noOfSubsets))

  final case class Task(equation: Equation, subsetNo: Int)

  case object Stop

}

class Evaluator(val actorNo: String, val noOfSubsets: Int) extends Actor {

  import Evaluator._
  import Master._

  def receive = {
    case Task(equation, subsetNo) =>
      val fromRowNo = (Math.pow(2, equation.noOfVariables).toInt / noOfSubsets) * subsetNo
      val toRowNo = fromRowNo + (Math.pow(2, equation.noOfVariables).toInt / noOfSubsets)
      // evaluate
      val result = SATSolver.equationEvaluator(equation, fromRowNo, toRowNo)
      // send message to master
      context.parent ! Result(subsetNo, result)
    case Stop =>
      context.stop(self)
  }
}

object ParallelSATSolver extends App {

  import Master._

  truthTableSATParallel("f0020-04-u.cnf", 4)

  def truthTableSATParallel(inputFileName: String, noOfActors: Int): Unit = {
    // Read file
    val equation = SATSolver.readDIMACSFile(inputFileName)
    // Create the 'parallelSAT' actor system
    val system: ActorSystem = ActorSystem("parallelSAT")

    try {
      // Create the Master actor
      val master: ActorRef = system.actorOf(Master.props(System.currentTimeMillis(), equation, noOfActors), "masterActor")
      master ! Begin
    } finally {

    }
  }
}
