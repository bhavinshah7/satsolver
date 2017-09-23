package com.satsolver.cnf

import java.io.PrintWriter

/**
  * Created by Anindo Saha on 19/9/17.
  */
object RandomCNFGenerator {
  def main(args: Array[String]): Unit = {
    val noOfVariables: Int = 10
    val noOfClauses: Int = 10
    val outputFileName: String = "gen-" + noOfVariables + "-" + noOfClauses + ".cnf"
    randomCNFGenerator(noOfVariables, noOfClauses, outputFileName)
  }

  /**
    * Task 1
    *
    * @param noOfVariables
    * @param noOfClauses
    * @param outputFileName
    */
  def randomCNFGenerator(noOfVariables: Int, noOfClauses: Int, outputFileName: String): Unit = {
    val out = new PrintWriter(outputFileName);
    out.println("c " + outputFileName + " ")
    out.println("p cnf " + noOfVariables + " " + noOfClauses + " ")
    val r = scala.util.Random
    (1 to noOfClauses).foreach(x => {
      val varPerClause = r.nextInt(noOfVariables) + 1
      val vars = (1 to varPerClause).map(y => {
        (if (r.nextDouble() < 0.5) -1 else 1) * ((r.nextInt(noOfVariables) + 1))
      })
      val distinctVars = vars.distinct
      out.println(distinctVars.mkString(" ") + " 0")
    })
    out.close()
  }

}
