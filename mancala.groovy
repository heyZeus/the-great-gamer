
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
    print "Enter the pot: ${rowTurn.validPotsForTurn()*.toChar()}: "
    pot = it.readLine() as char 
    Pot potObject = rowTurn.pots.find { it.toChar() == pot }
    if (potObject) {
      board.takeTurn(potObject)
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
  int currentTurnIndex

  public Board(String name1, String name2) {
    rows = [new Row(name2, 0), new Row(name1, 1)]
    currentTurnIndex = 1
  }

  public String toString() {
    return "${rows[0].toString(true)}\n${rows[1].toString(false)}"
  }

  public Row winnerRow() {
    if (!rows.every { it.hasAnyRemainingTurns()} ) {
      return rows[0].bankCount() > rows[1].bankCount() ? rows[0] : rows[1]
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

  public void takeTurn(Pot pot) {
    if (!currentRow().takeTurn(pot, alternateRow())) {
      currentTurnIndex = alternateRowIndex()
    }
  }

  public Row currentRow() {
    return rows[currentTurnIndex]
  }
  
  public Row alternateRow() {
    return rows[alternateRowIndex()]
  }

  public int alternateRowIndex() {
    currentTurnIndex == 0 ? 1 : 0
  }

}

class Pot {
  int index
  int beanCount
  Row row 

  public char toChar() {
    return (97 + index) as char
  }

  public boolean isBank() {
    return index == 6
  }

  @Override 
  public boolean equals(Object otherPot) {
    if (this.is(otherPot)) return true
    if (!(otherPot instanceof Pot) ) return false
    Pot that = (Pot) otherPot

    return index == that.index && row == that.row
  }

  public int hashCode() {
    return index.hashCode() + row.hashCode()
  }

  public String toString() {
    if (isBank()) {
      return "Bank ${beanCount}"
    }
    else {
      return "Pot ${toChar()}:${beanCount}"
    }
  }

  public int addBean(int beans) {
    beanCount += 1
    return beans - 1
  }

  public int resetBeanCount() {
    int tmpCount = beanCount
    beanCount = 0
    return tmpCount
  }

}

class Row { 
  Collection<Pot> pots = []
  String userName
  int index

  public Row(String userName, int index) {
    this.userName = userName
    this.index = index
    
    int potsPerSide = 6

    (0..(potsPerSide - 1)).each { int num ->
      pots << new Pot(index: num, beanCount: 4, row: this)
    }

    pots << new Pot(index: 6, beanCount: 0)
  }

  public Collection<Pot> validPotsForTurn() {
    return pots.findAll { Pot pot ->
      !pot.isBank() && pot.beanCount > 0
    }
  }

  public String toString(boolean reverse) {
    String potsString = reverse ? pots.reverse().toString() : pots.toString()
    return "Name: ${userName}, ${potsString}"
  }

  public String toString() {
    toString(false)
  }

  public boolean hasAnyRemainingTurns() {
    return potsMinusBank().any { it.beanCount > 0 }
  }

  private Collection<Pot> potsMinusBank() {
    pots.findAll { !it.isBank() }
  }

  public boolean takeTurn(Pot pot, Row otherRow) {
    int beans = pot.resetBeanCount()
    Pot nextPot = findNextPot(pot)
    Pot lastPot = nextPot

    while (nextPot != null && beans > 0) {
      Map results = addBeans(nextPot, beans, true)
      if (results.beans > 0) {
        nextPot = otherRow.getFirstPot()
        results = otherRow.addBeansMinusBank(nextPot, results.beans)
        nextPot = getFirstPot()
      }

      lastPot = results.lastPot
      beans = results.beans
    }

    if (lastPot == findBank()) {
      return true
    }
    else {
      return false
    }
  }

  public Pot getFirstPot() {
    return pots[0]
  }

  public Map addBeansMinusBank(Pot pot, int beans) {
    addBeans(pot, beans, false)
  }

  private Map addBeans(Pot pot, int beans, boolean includeBank) {
    Map results = [:]
    Pot lastPot = pot

    while (pot != null && beans > 0) {
      beans = pot.addBean(beans)
      lastPot = pot
      pot = findNextPot(pot)
      if (!includeBank && pot.isBank()) {
        pot = null
      }
    }

    results.beans = beans
    results.lastPot = lastPot

    return results
  }

  public Map addBeans(Pot pot, int beans) {
    addBeans(pot, beans, true)
  }

  private Pot findNextPot(Pot pot) { 
    int currentIndex = pots.indexOf(pot)

    if (currentIndex + 1 == pots.size()) {
      return null
    }
    else {
      return pots[currentIndex + 1]
    }
  }

  public int bankCount() {
    return findBank().beanCount
  }

  public Pot findBank() {
    pots.find { it.isBank() }
  }
   
}





