
Board board = null

System.in.withReader { 
  print "Enter your a name for Player 1 : "
  String player1 = (it.readLine()) ?: "Player 1"

  print "Enter your a name for Player 2 : "
  String player2 = (it.readLine()) ?: "Player 2"

  board = new Board(player1, player2)

  println ""
  println board.toString()
  println ""
  Row rowTurn = board.nextRowTurn()

  while (rowTurn) {
    println "${rowTurn}"
    print "Enter the pot: ${rowTurn.validPots()}: "
    pot = it.readLine() as char 
    if (rowTurn.isValidPot(pot)) {
      println "You entered the letter: ${pot}"
      board.playTurn(pot)
      println ""
      println board.toString()
      println ""
    }
    else {
      println "Invalid pot ${pot}, try again."
    }

    rowTurn = board.nextRowTurn()
  }
}

println ""
println "The game is over."
println "The winner is ${board.winnerRow()}"

class Board {
  Collection<Row> rows = []
  int currentRowIdx = 1

  public Board(String name1, String name2) {
    rows = [new Row(name2, false), new Row(name1, true)]
  }

  public String toString() {
    return "${rows.join('\n')}"
  }

  public Row winnerRow() {
    if (!rows.every { it.hasAnyRemainingTurns()} ) {
      return rows[0].bank > rows[1].bank ? rows[0] : rows[1]
    }
    else {
      return null
    }
  }

  public Row nextRowTurn() {
    Row winner = winnerRow()
    if (winner) {
      return null
    }
    else {
      currentRow()
    }
  }

  public void playTurn(char pot) {
    if (!currentRow().moveBeansFromPot(pot)) {
      currentRowIdx = currentRowIdx == 0 ? 1 : 0
    }
  }

  public Row currentRow() {
    return rows[currentRowIdx]
  }

}

class Row { 
  int bank = 0
  LinkedHashMap pots = [:]
  String userName
  int turnsTaken = 0 
  boolean topRow = false

  public Row(String userName, topRow) {
    this.userName = userName
    this.topRow = topRow
    
    int potsPerSide = 6
    int aChar = 97

    (0..(potsPerSide - 1)).each { pot ->
      char letter = (pot + aChar) as char
      pots[letter] = 4
    }

    bank = 0
  }

  public Collection validPots() {
    return pots.findAll { pot, beadCount ->
      return beadCount > 0
    }.keySet()
  }

  public String toString() {
    return "TopRow: ${topRow}, Name: ${userName}, Bank: ${bank}, Pots: ${pots}"
  }

  public boolean isValidPot(char pot) {
    return validPots().contains(pot)
  }

  public boolean hasAnyRemainingTurns() {
    return turnsTaken == 0
  }

  public boolean moveBeansFromPot(char pot) {
    turnsTaken += 1
    println "Turns taken ${turnsTaken}"
    return false
  }

}





