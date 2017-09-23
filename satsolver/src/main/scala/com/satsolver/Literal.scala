package com.satsolver

class Literal (val id:Int, val polarity: Boolean) {
    
  def getID = id
  
  def getPolarity = polarity
  
  override def toString: String = (if(!polarity) "-" else "") + "x" + (getID + 1)

}