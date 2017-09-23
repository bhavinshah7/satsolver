package com.satsolver

import scala.io.Source
import com.satsolver.parser.Dimacs
import com.satsolver.dpll.DPLLSolver
import java.io.PrintWriter

object BatchSolver {

  def main(args: Array[String]): Unit = {
    
    if (args.length < 1) {
      println("USAGE: scala DPLLMain <filename>")
      System.exit(1)
    }
    
    val lines = Source.fromFile(args(0)).getLines().filter(line => line.endsWith("cnf")).toList
    lines.map(line => solve(line))   
  }
  
  def solve(filename: String) = {
     val out = new PrintWriter("./out/dpll/" + filename);
    out.println(filename)
    val t1 = System.currentTimeMillis
    dpll(filename) match {
      case true => out.println("SAT")
      case false => out.println("UNSAT") 
    }    
    val t2 = System.currentTimeMillis
    out.println((t2 - t1) + " msecs")
    out.println("----------------------")
    out.close()
    println(filename)
  }

  def dpll(filename: String): Boolean = {
    val (nbVars, clauses) = Dimacs.parse(filename)
    DPLLSolver.solve(nbVars, clauses)
  }

}