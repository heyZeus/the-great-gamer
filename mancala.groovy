
String player1 = null
String potLetter = null
String player2 = null
Board board = null

System.in.withReader { 
  print "Enter your a name for Player 1 : "
  player1 = (it.readLine()) 

  print "Enter your a name for Player 2 : "
  player2 = (it.readLine()) 

  board = new Board(player1, player2)

  println board.toString()

  while (!board.isGameOver()) {
    print "Enter the pot [A B C D E F]: "
    potLetter = it.readLine()
    println "You entered the letter: ${letter}"
  }
}

println ""
println "The game is over."
println "The winner is ${board.winner()}"

class Board {
  Row topRow
  Row bottomRow

  public Board(String name1, String name2) {
    topRow = new Row(name1)
    bottomRow = new Row(name2)
  }

  public String toString() {
    return "\nTop Row: ${topRow}\n\nBottom Row: ${bottomRow}\n"
  }

  public boolean isGameOver() {
    return true
  }

  public Row winner() {
    return topRow
  }

}

class Row { 
  int bank = 0
  Map pots = [:]
  String userName

  public Row(String userName) {
    this.userName = userName
    
    int potsPerSide = 6
    int aChar = 65

    (0..5).each { pot ->
      char letter = (pot + aChar) as char
      pots[letter] = 4
    }

    bank = 0
  }

  public String toString() {
    return "Name: ${userName}, Bank: ${bank}, Pots: ${pots}"
  }

}





