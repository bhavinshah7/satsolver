package com.satsolver

import java.io.PrintWriter

import scala.io.Source

/**
  * Created by Anindo Saha on 13/9/17.
  */
object SATSolver {
  def main(args: Array[String]): Unit = {
    //randomCNFGenerator(4, 3, "dimacs.txt");
    val startTime = System.currentTimeMillis()
    println(if (truthTableSATSequential("f0020-04-u.cnf")) "SAT" else "UNSAT")
    println(System.currentTimeMillis() - startTime)
  }
  
  def solve(filename: String) {
     val out = new PrintWriter("./out/sat/" + filename);
    out.println(filename)
    val t1 = System.currentTimeMillis
    truthTableSATSequential(filename) match {
      case true => out.println("SAT")
      case false => out.println("UNSAT") 
    }    
    val t2 = System.currentTimeMillis
    out.println((t2 - t1) + " msecs")
    out.println("----------------------")
    out.close()
    println(filename)
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

  /**
    * Evaluate the CNF equation for given row of the truth table
    *
    * @param equation
    */
  def equationEvaluator(equation: Equation, fromRowNo: Int, untilRowNo: Int): Boolean = fromRowNo match {
    case fromRowNo if fromRowNo == untilRowNo => false
    case fromRowNo => {
      val variables: IndexedSeq[Variable] = (0 until equation.noOfVariables).map(variableNo => {
        //println("Row No. " + fromRowNo + " Variable No. " + variableNo + " Answer: " + (fromRowNo & (1 << variableNo)))
        val value: Boolean = if ((fromRowNo & (1 << variableNo)) > 0) true else false
        Variable(variableNo + 1, value)
      })
      if (clausesEvaluator(equation.clauses, variables))
        true
      else
        equationEvaluator(equation, fromRowNo + 1, untilRowNo)
    }

  }

  def clausesEvaluator(clauses: List[Clause], variables: IndexedSeq[Variable]): Boolean = {
    clauses match {
      case clause :: rest => literalsEvaluator(clause.literals, variables) match {
        case false => false
        case true => clausesEvaluator(rest, variables)
      }
      case Nil => true
    }
  }

  def literalsEvaluator(literals: List[CLiteral], variables: IndexedSeq[Variable]): Boolean = {
    literals match {
      case literal :: rest => literalEvaluator(literal, variables) match {
        case true => true
        case false => literalsEvaluator(rest, variables)
      }
      case Nil => false
    }
  }

  def literalEvaluator(literal: CLiteral, variables: IndexedSeq[Variable]): Boolean = {
    literal.value match {
      case true => variables(literal.name - 1).value
      case false => !(variables(literal.name - 1).value)
    }
  }

  /**
    * Task 2
    *
    * @param inputFileName
    */
  def truthTableSATSequential(inputFileName: String): Boolean = {
    val equation = readDIMACSFile(inputFileName)
    equationEvaluator(equation, 0, Math.pow(2, equation.noOfVariables).toInt)
  }

  def readDIMACSFile(inputFileName: String): Equation = {
    val noOfVariables = Source.fromFile(inputFileName)
      .getLines()
      .filter(line => (line.startsWith("p")))
      .map(line => {
        line.split(" ").toList(2)
      })

    val varNo = noOfVariables.next().toInt

    val clauses: Iterator[Clause] = Source.fromFile(inputFileName)
      .getLines()
      .filterNot(line => (line.startsWith("p") || line.startsWith("c")))
      .map(line => {
        val literals: List[CLiteral] = line.trim().split(" ").toList
          .filterNot(_.startsWith("0"))
          .map(token => {
            CLiteral(token.trim())
          })
        Clause(literals)
      })
    Equation(varNo, clauses.toList)
  }
}

// x1, x2, x3
case class Variable(name: Int, value: Boolean)

// x1, ~x1, x2, x3
case class CLiteral(name: Int, value: Boolean)

object CLiteral {
  def apply(token: String) = new CLiteral(if (token.trim().charAt(0) == '-') token.trim().substring(1).toInt else token.trim().toInt,
    if (token.charAt(0) == '-') false else true)
}

// ~x1 OR X2 OR X3
case class Clause(literals: List[CLiteral])

// x1 AND (~x1 OR X2 OR X3)
case class Equation(noOfVariables: Int, clauses: List[Clause])