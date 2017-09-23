package com.satsolver.dpll

import com.satsolver.Literal
import scala.Vector

object DPLLSolver {

  def solve(no_of_var: Int, clauses: List[List[Literal]]): Boolean = {
    sat(clauses, Vector(), no_of_var)
  }

  def sat(clauses: List[List[Literal]], model: Vector[Boolean], no_of_var: Int): Boolean = {

    val formula = reduceFormula(clauses, model)

    if (areAllClausesTrue(formula, model)) true
    else if (isSomeClauseFalse(formula, model)) false
    else { //Unknown   
      if (model.size < no_of_var) {

        val idClauses = formula.filter(clause => isIdentity(clause, model.size))
        if (idClauses.size > 0) {

          if (isConflict(idClauses)) {
            false
          } else {
            val bool = makeTrue(idClauses(0))
            sat(formula, model :+ bool, no_of_var)
          }

        } else {
          sat(formula, model :+ false, no_of_var) || sat(formula, model :+ true, no_of_var)
        }
      } else { //All permutations checked
        false
      }
    }
  }

  def isIdentity(clause: List[Literal], cur_var: Int): Boolean = {
    (clause.size == 1 && clause(0).getID == cur_var)
  }

  def makeTrue(clause: List[Literal]): Boolean = {
    clause(0).getPolarity
  }
  
  def isConflict(clauses: List[List[Literal]]): Boolean = {
    val tpolarity = clauses.filter(clause => makeTrue(clause))
    val fpolarity = clauses.filter(clause => !makeTrue(clause))
    (tpolarity.size > 0 && fpolarity.size > 0)
  }

  def reduceFormula(clauses: List[List[Literal]], model: Vector[Boolean]): List[List[Literal]] = {
    clauses.filterNot(clause => isClauseTrue(clause, model)).map(clause => reduceClause(clause, model))
  }

  def reduceClause(clause: List[Literal], model: Vector[Boolean]): List[Literal] = {
    clause.filterNot(lit => isLitFalse(lit, model))
  }

  def areAllClausesTrue(clauses: List[List[Literal]], model: Vector[Boolean]): Boolean = {
    clauses match {
      case clause :: rest => isClauseTrue(clause, model) match {
        case false => false
        case true  => areAllClausesTrue(rest, model)
      }
      case Nil => true
    }
  }

  def isClauseTrue(clause: List[Literal], model: Vector[Boolean]): Boolean = {
    clause match {
      case lit :: rest => isLitTrue(lit, model) match {
        case true  => true
        case false => isClauseTrue(rest, model)
      }
      case Nil => false
    }
  }

  def isLitTrue(lit: Literal, model: Vector[Boolean]): Boolean = {
    if (lit.getID >= model.size) false
    else {
      if (lit.getPolarity) model(lit.getID) else !model(lit.getID)
    }
  }

  def isSomeClauseFalse(clauses: List[List[Literal]], model: Vector[Boolean]): Boolean = {
    clauses match {
      case clause :: rest => isClauseFalse(clause, model) match {
        case true  => true
        case false => isSomeClauseFalse(rest, model)
      }
      case Nil => false
    }
  }

  def isClauseFalse(clause: List[Literal], model: Vector[Boolean]): Boolean = {
    clause match {
      case lit :: rest => isLitFalse(lit, model) match {
        case false => false
        case true  => isClauseFalse(rest, model)
      }
      case Nil => false
    }
  }

  def isLitFalse(lit: Literal, model: Vector[Boolean]): Boolean = {
    if (lit.getID >= model.size) false
    else { // If known
      if (lit.getPolarity) !model(lit.getID) else model(lit.getID)
    }
  }
}