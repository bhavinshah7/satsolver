package com.satsolver.parser

import scala.io.Source
import com.satsolver.Literal

object Dimacs {

  def parse(filename: String): (Int, List[List[Literal]]) = {

    val lines = Source.fromFile(filename).getLines().filterNot(line => line.startsWith("c")).toList

    val init_line :: _ = lines.filter(line => line.startsWith("p cnf"))
    val no_of_var: Int = init_line.split(' ')(2).toInt

    val clause_lines = lines.filterNot(line => line.startsWith("p cnf"))

    val clauses: List[List[Literal]] =
      clause_lines.map(line => {
        val numbers = line.split(' ').filterNot(_ == "").map(_.toInt).toList
        numbers.filterNot(i => i == 0).map(i =>
          if (i < 0) new Literal(-(i + 1), false)
          else new Literal(i - 1, true))
      })
    (no_of_var, clauses)
  }
}     
     
     
