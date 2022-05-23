//Global Variables
var HGRID = arrayOf( //hidden grid
    arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
)
var VGRID = arrayOf( //visible grid
    arrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9, 9),
    arrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9, 9),
    arrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9, 9),
    arrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9, 9),
    arrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9, 9),
    arrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9, 9),
)
val MINES = 10
var FLAGS = 0
var SAFESPACES = 50

fun displayMenu(): Int {
    println("Menu:")
    println("1: Begin a new game.")
    println("2: Continue current game.")
    //println("3: Save game.")
    //println("4: Load saved game.")
    println("5: Quit game.")
    println("You can type 0 at any time to retrun to this menu.")
    var choice = readLine()!!.toInt()
    return choice
}

fun loadTestGrid() {
    for (x in 0..5) {
        for (y in 0..9) {
            if (x < 2 && y < 5) {
                HGRID[x][y] = -1
            }
            else {
                HGRID[x][y] = 0
                HGRID[x][y] = countMines(x, y)
            }
        }
    }
}

fun countSafeSpaces() {
    var count = 0
    for (x in 0..5) {
        for(y in 0..9) {
            if (VGRID[x][y] == 9 || VGRID[x][y] == -2) {
                count++
            }
        }
    }
    SAFESPACES = count - MINES
}

fun countMines(x: Int, y: Int): Int {
    var count = 0
 
    if (x < 5 && y < 9 && HGRID[x + 1][y + 1] < 0) { count++ } //bottom right
    if (x < 5 && HGRID[x + 1][y] < 0) { count++ }     //bottom
    if (x < 5 && y > 0 && HGRID[x + 1][y - 1] < 0) { count++ } //bottom left
    if (y > 0 && HGRID[x][y - 1] < 0) { count++ }     //left
    if (x > 0 && y > 0 && HGRID[x - 1][y - 1] < 0) { count++ } //top left
    if (x > 0 && HGRID[x - 1][y] < 0) { count++ }     //top
    if (x > 0 && y < 9 && HGRID[x - 1][y + 1] < 0) { count++ } //top right
    if (y < 9 && HGRID[x][y + 1] < 0) { count++ }     //right
    return count
}

fun makeHiddenGrid() {
    var row: Int
    var col: Int
    //set the mines
    for (mine in 1..10) {
        do {
            row = (0..5).random()
            col = (0..9).random()
        } while (HGRID[row][col] != 0)
        HGRID[row][col] = -1
    }
    //count the mines around each empty space to set the hints
    for (r in 0..5) {
        for (c in 0..9) {
            if (HGRID[r][c] == 0) {
                HGRID[r][c] = countMines(r, c)
            }
        }
    }
}

fun eraseGrids() {
    for (row in 0..5) {
        for (col in 0..9) {
            HGRID[row][col] = 0
            VGRID[row][col] = 9
        }
    }
}

fun drawGrid() {
    for (row in 0..5) {
        if (row == 0) {
            println("    1   2   3   4   5   6   7   8   9   10")
            println("  -----------------------------------------")
        }
        for (col in 0..9) {
            if (col == 0) {
                print(row + 1)
                print(" ")
            }
            when (VGRID[row][col]) {
                -2 -> print("| F ") //flag
                -1 -> print("| M ") //Mine
                0 -> print("|   ")  //Clear
                1, 2, 3, 4, 5, 6, 7, 8 -> print("| ${VGRID[row][col]} ") //hint
                9 -> print("| ? ")  //No interaction(Hidden)
            }
            if (col == 9) {
                println("|")
            }
        }
        if (row == 5) { println("  -----------------------------------------") }
        else { println("  |---|---|---|---|---|---|---|---|---|---|") }
    }
    println("               -MINE- -SAFE-")
    if (MINES - FLAGS < 10) { print("               |  ${MINES - FLAGS} | ") }
    else { print("               | ${MINES - FLAGS} | ") }
    if (SAFESPACES < 10) { println("|  ${SAFESPACES} |") }
    else { println("| ${SAFESPACES} |") }
    println("               ------ ------")
}

fun checkEmptyAdjacent(x: Int, y: Int, down: Boolean, left: Boolean, up: Boolean, right: Boolean) {
    if (x < 5) { //bottom
        VGRID[x + 1][y] = HGRID[x + 1][y]
        if (HGRID[x + 1][y] == 0 && down) {
            checkEmptyAdjacent(x + 1, y, true, true, false, true)
        }
    }
    if (y > 0) { //left
        VGRID[x][y - 1] = HGRID[x][y - 1]
        if (HGRID[x][y - 1] == 0 && left) {
            checkEmptyAdjacent(x, y - 1, true, true, true, false)
        }
    }
    if (x > 0) { //up
        VGRID[x - 1][y] = HGRID[x - 1][y]
        if (HGRID[x - 1][y] == 0 && up) {
            checkEmptyAdjacent(x - 1, y, false, true, true, true)
        }
    }
    if (y < 9) {  //right
        VGRID[x][y + 1] = HGRID[x][y + 1]
        if (HGRID[x][y + 1] == 0 && right) {
            checkEmptyAdjacent(x, y + 1, false, false, false, true)
        }
    }
}

fun playGame() {
    var action: String?
    var x: Int
    var y: Int
    var explode = false
    var win = false

    while (explode == false && win == false) {
        drawGrid()
        println("Would you like to place a flag(F) or a dig(D)?")
        do {
            print("Action: ")
            action = readLine()
            if (action == "0") { return } //back to menu
        } while (action != "F" && action != "f" && action != "D" && action != "d")

        println("What coordinates?")
        do {
            print("Row: ")
            x = readLine()!!.toInt()
            if (x == 0) { return } //back to menu
            print("Col: ")
            y = readLine()!!.toInt()
            if (y == 0) { return } //back to menu
        } while (x < 0 && x > 6 && y < 0 && y > 10)

        var r = x - 1
        var c = y - 1

        //returns to main to display menu

        if (action == "F" || action == "f") {
            VGRID[r][c] = -2
            FLAGS++
        }
        else if (action == "D" || action == "d") {
            if (VGRID[r][c] == -2) { FLAGS++ }
            when (HGRID[r][c]) {
                -1 -> explode = true
                0 -> { VGRID[r][c] = HGRID[r][c]; checkEmptyAdjacent(r, c, true, true, true, true); }
                1, 2, 3, 4, 5, 6, 7, 8 -> { VGRID[r][c] = HGRID[r][c] }
            }
            countSafeSpaces()
            if (SAFESPACES == 0) { win = true }
        }
    }
    if (win) { println("\n                  YOU WIN\n") }
    else if (explode) { println("\n                  KABOOM!\n                 GAME OVER\n") }
}

fun makeFirstMove() {
    var row: Int
    var col: Int
    do {
        row = (0..5).random()
        col = (0..9).random()
    } while (HGRID[row][col] != 0)
    VGRID[row][col] = 0
    checkEmptyAdjacent(row, col, true, true, true, true)
    countSafeSpaces()
}

fun newGame() {
    eraseGrids()
    makeHiddenGrid()
    makeFirstMove()
    playGame()
}

fun main(args : Array<String>) {
    var startup = 1
    var menuAction: Int

    do {
        if (startup == 1) { //when the loop is first started
            println("Welcome to Minesweeper!")
            startup += 2;
        }
        menuAction = displayMenu()
        when (menuAction) {
            1 -> newGame()
            2 -> playGame()
            //3 -> println("save game")//saveGame()
            4 -> { println("load game"); loadTestGrid() }
            5 -> break
            else -> println("Invalid Input");
        }
    } while (menuAction != 5)
}