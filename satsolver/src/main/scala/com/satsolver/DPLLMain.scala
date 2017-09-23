package com.satsolver

import scala.io.Source
import com.satsolver.parser.Dimacs
import com.satsolver.dpll.DPLLSolver
import java.io.PrintWriter

object DPLLMain {

  def main(args: Array[String]): Unit = {
    
    if (args.length < 1) {
      println("USAGE: scala DPLLMain <filename>")
      System.exit(1)
    }
    
    val t1 = System.currentTimeMillis
    dpll(args(0)) match {
      case true => println("SAT")
      case false => println("UNSAT") 
    }    
    val t2 = System.currentTimeMillis
    println((t2 - t1) + " msecs")
  }
  
  def dpll(filename: String): Boolean = {
    val (nbVars, clauses) = Dimacs.parse(filename)
    DPLLSolver.solve(nbVars, clauses)
  }

}