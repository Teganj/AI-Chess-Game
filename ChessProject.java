import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;

/*
	Tegan Jennings
	X18303941
	National College of Ireland
	Introduction into Artificial Intelligence
	Continuous Assessment 2
*/

public class ChessProject extends JFrame implements MouseListener, MouseMotionListener {
    JLayeredPane layeredPane;
    JPanel chessBoard;
    JLabel chessPiece;
    int xAdjustment;
    int yAdjustment;
    int startX;
    int startY;
    int initialX;
    int initialY;
    JPanel panels;
    JLabel pieces;
    String winner;
    Boolean agentwins = false;
    Boolean white2Move = false;
    static int selectedAi;
    AIAgent agent = new AIAgent();


    public ChessProject() {
        Dimension boardSize = new Dimension(600, 600);

        // Use a Layered Pane for this application
        layeredPane = new JLayeredPane();
        getContentPane().add(layeredPane);
        layeredPane.setPreferredSize(boardSize);
        layeredPane.addMouseListener(this);
        layeredPane.addMouseMotionListener(this);

        // Add a chess board to the Layered Pane
        chessBoard = new JPanel();
        layeredPane.add(chessBoard, JLayeredPane.DEFAULT_LAYER);
        chessBoard.setLayout(new GridLayout(8, 8));
        chessBoard.setPreferredSize(boardSize);
        chessBoard.setBounds(0, 0, boardSize.width, boardSize.height);

        for (int i = 0; i < 64; i++) {
            JPanel square = new JPanel(new BorderLayout());
            chessBoard.add(square);

            int row = (i / 8) % 2;
            if (row == 0)
                square.setBackground(i % 2 == 0 ? Color.white : Color.gray);
            else
                square.setBackground(i % 2 == 0 ? Color.gray : Color.white);
        }

        // Setting up the Initial Chess board.
        for (int i = 8; i < 16; i++) {
            pieces = new JLabel(new ImageIcon("WhitePawn.png"));
            panels = (JPanel) chessBoard.getComponent(i);
            panels.add(pieces);
        }
        pieces = new JLabel(new ImageIcon("WhiteRook.png"));
        panels = (JPanel) chessBoard.getComponent(0);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteKnight.png"));
        panels = (JPanel) chessBoard.getComponent(1);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteKnight.png"));
        panels = (JPanel) chessBoard.getComponent(6);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteBishop.png"));
        panels = (JPanel) chessBoard.getComponent(2);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteBishop.png"));
        panels = (JPanel) chessBoard.getComponent(5);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteKing.png"));
        panels = (JPanel) chessBoard.getComponent(3);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteQueen.png"));
        panels = (JPanel) chessBoard.getComponent(4);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteRook.png"));
        panels = (JPanel) chessBoard.getComponent(7);
        panels.add(pieces);
        for (int i = 48; i < 56; i++) {
            pieces = new JLabel(new ImageIcon("BlackPawn.png"));
            panels = (JPanel) chessBoard.getComponent(i);
            panels.add(pieces);
        }
        pieces = new JLabel(new ImageIcon("BlackRook.png"));
        panels = (JPanel) chessBoard.getComponent(56);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackKnight.png"));
        panels = (JPanel) chessBoard.getComponent(57);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackKnight.png"));
        panels = (JPanel) chessBoard.getComponent(62);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackBishop.png"));
        panels = (JPanel) chessBoard.getComponent(58);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackBishop.png"));
        panels = (JPanel) chessBoard.getComponent(61);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackKing.png"));
        panels = (JPanel) chessBoard.getComponent(59);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackQueen.png"));
        panels = (JPanel) chessBoard.getComponent(60);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackRook.png"));
        panels = (JPanel) chessBoard.getComponent(63);
        panels.add(pieces);
    }

    /*
         This method checks if there is a piece present on a particular square.
     */
    public Boolean piecePresent(int x, int y) {
        Component c = chessBoard.findComponentAt(x, y);
        return !(c instanceof JPanel);
    }

    public int getPieceNameFromMove(int x, int y) {
        x = x * 75;
        y = y * 75;
        int pieceWeighting = 0;
        if (chessBoard.findComponentAt(x, y) instanceof JLabel) {
            Component c1 = chessBoard.findComponentAt(x, y);
            JLabel awaitingPiece = (JLabel) c1;
            String name = awaitingPiece.getIcon().toString();
            if (name.contains("BlackKing")) {
                pieceWeighting = 100;
            } else if (name.contains("BlackQueen")) {
                pieceWeighting = 9;
            } else if (name.contains("BlackRook")) {
                pieceWeighting = 5;
            } else if (name.contains("BlackBishop")) {
                pieceWeighting = 3;
            } else if (name.contains("BlackKnight")) {
                pieceWeighting = 3;
            } else if (name.contains("BlackPawn")) {
                pieceWeighting = 2;
            } else if (name.isEmpty()) {
                pieceWeighting = 1;
            }
        }
        return pieceWeighting;
    }

    /*
      Method to check were a Black Pawn can move to. There are two main conditions here. Either the Black Pawn is in
      its starting position in which case it can move either one or two squares, or it has already moved, and it can only
      one square down the board. The Pawn can also take an opponent piece in a diagonal movement. and if it makes it to the
      bottom of the board it turns into a Queen (this should be handled where the move is actually being made and not in this
      method).
    */
    private Stack getWhitePawnSquares(int x, int y, String piece) {
        Stack moves = new Stack();
        Square startingSquare = new Square(x, y, piece);
        Move validMove;
        int tmpx1 = x + 1;
        int tmpx2 = x - 1;
        int tmpy1 = y + 1;
        int tmpy2 = y + 2;
        if (y == 1) {
            if (!piecePresent((x * 75) + 20, (tmpy1 * 75) + 20)) {
                Square tmp = new Square(x, tmpy1);
                validMove = new Move(startingSquare, tmp, getPieceNameFromMove(x, tmpy1));
                moves.push(validMove);
            }

            if (!piecePresent((x * 75) + 20, (tmpy1 * 75) + 20) && !piecePresent((x * 75) + 20, (tmpy2 * 75) + 20)) {
                Square tmp = new Square(x, tmpy2);
                validMove = new Move(startingSquare, tmp, getPieceNameFromMove(x, tmpy2));
                moves.push(validMove);
            }
            if (piecePresent((tmpx1 * 75) + 20, (tmpy1 * 75) + 20) && tmpx1 >= 0 && tmpx1 <= 7 && tmpy1 >= 0
                    && tmpy1 <= 7) {
                if (checkWhiteOpponent((tmpx1 * 75) + 20, (tmpy1 * 75) + 20)) {
                    Square tmp = new Square(tmpx1, tmpy1);
                    validMove = new Move(startingSquare, tmp, getPieceNameFromMove(tmpx1, tmpy1));
                    moves.push(validMove);
                }
            }
            if (piecePresent((tmpx2 * 75) + 20, (tmpy1 * 75) + 20) && tmpx2 >= 0 && tmpx2 <= 7 && tmpy1 >= 0
                    && tmpy1 <= 7) {
                if (checkWhiteOpponent((tmpx2 * 75) + 20, (tmpy1 * 75) + 20)) {
                    Square tmp = new Square(tmpx2, tmpy1);
                    validMove = new Move(startingSquare, tmp, getPieceNameFromMove(tmpx2, tmpy1));
                    moves.push(validMove);
                }
            }
        }

        //Moves available to white pawns after they have moved from their initial starting position.
        else {
            if (!piecePresent((x * 75) + 20, (tmpy1 * 75) + 20) && tmpy1 >= 0 && tmpy1 <= 7) {
                Square tmp = new Square(x, tmpy1);
                validMove = new Move(startingSquare, tmp, getPieceNameFromMove(x, tmpy1));
                moves.push(validMove);
            }
            if (piecePresent((tmpx1 * 75) + 20, (tmpy1 * 75) + 20) && tmpx1 >= 0 && tmpx1 <= 7 && tmpy1 >= 0
                    && tmpy1 <= 7) {
                if (checkWhiteOpponent((tmpx1 * 75) + 20, (tmpy1 * 75) + 20)) {
                    Square tmp = new Square(tmpx1, tmpy1);
                    validMove = new Move(startingSquare, tmp, getPieceNameFromMove(tmpx1, tmpy1));
                    moves.push(validMove);
                }
            }
            if (piecePresent((tmpx2 * 75) + 20, (tmpy1 * 75) + 20) && tmpx2 >= 0 && tmpx2 <= 7 && tmpy1 >= 0
                    && tmpy1 <= 7) {
                if (checkWhiteOpponent((tmpx2 * 75) + 20, (tmpy1 * 75) + 20)) {
                    Square tmp = new Square(tmpx2, tmpy1);
                    validMove = new Move(startingSquare, tmp, getPieceNameFromMove(tmpx2, tmpy1));
                    moves.push(validMove);
                }
            }
        }
        return moves;
    }

    private Stack getBlackPawnSquares(int x, int y, String piece) {
        Stack moves = new Stack();
        Square startingSquare = new Square(x, y, piece);
        Move validM;
        int tmpx1 = x + 1;
        int tmpx2 = x - 1;
        int tmpy1 = y - 1;
        int tmpy2 = y - 2;
        if (y == 6) {
            if (!piecePresent((x * 75) + 20, (tmpy1 * 75) + 20)) {
                Square tmp = new Square(x, tmpy1);
                validM = new Move(startingSquare, tmp, getPieceNameFromMove(x, tmpy1));
                moves.push(validM);
            }
            if (!piecePresent((x * 75) + 20, (tmpy1 * 75) + 20) && !piecePresent((x * 75) + 20, (tmpy2 * 75) + 20)) {
                Square tmp = new Square(x, tmpy2);
                validM = new Move(startingSquare, tmp, getPieceNameFromMove(x, tmpy2));
                moves.push(validM);
            }
            if (piecePresent((tmpx1 * 75) + 20, (tmpy1 * 75) + 20) && tmpx1 >= 0 && tmpx1 <= 7 && tmpy1 >= 0
                    && tmpy1 <= 7) {
                if (checkBlackOpponent((tmpx1 * 75) + 20, (tmpy1 * 75) + 20)) {
                    Square tmp = new Square(tmpx1, tmpy1);
                    validM = new Move(startingSquare, tmp, getPieceNameFromMove(tmpx1, tmpy1));
                    moves.push(validM);
                }
            }
            if (piecePresent((tmpx2 * 75) + 20, (tmpy1 * 75) + 20) && tmpx2 >= 0 && tmpx2 <= 7 && tmpy1 >= 0
                    && tmpy1 <= 7) {
                if (checkBlackOpponent((tmpx2 * 75) + 20, (tmpy1 * 75) + 20)) {
                    Square tmp = new Square(tmpx2, tmpy1);
                    validM = new Move(startingSquare, tmp, getPieceNameFromMove(tmpx2, tmpy1));
                    moves.push(validM);
                }
            }
        } else {
            if (!piecePresent((x * 75) + 20, (tmpy1 * 75) + 20) && tmpy1 >= 0 && tmpy1 <= 7) {
                Square tmp = new Square(x, tmpy1);
                validM = new Move(startingSquare, tmp, getPieceNameFromMove(x, tmpy1));
                moves.push(validM);
            }
            if (piecePresent((tmpx1 * 75) + 20, (tmpy1 * 75) + 20) && tmpx1 >= 0 && tmpx1 <= 7 && tmpy1 >= 0
                    && tmpy1 <= 7) {
                if (checkBlackOpponent((tmpx1 * 75) + 20, (tmpy1 * 75) + 20)) {
                    Square tmp = new Square(tmpx1, tmpy1);
                    validM = new Move(startingSquare, tmp, getPieceNameFromMove(tmpx1, tmpy1));
                    moves.push(validM);
                }
            }
            if (piecePresent((tmpx2 * 75) + 20, (tmpy1 * 75) + 20) && tmpx2 >= 0 && tmpx2 <= 7 && tmpy1 >= 0
                    && tmpy1 <= 7) {
                if (checkBlackOpponent((tmpx2 * 75) + 20, (tmpy1 * 75) + 20)) {
                    Square tmp = new Square(tmpx2, tmpy1);
                    validM = new Move(startingSquare, tmp, getPieceNameFromMove(tmpx2, tmpy1));
                    moves.push(validM);
                }
            }
        }
        return moves;
    }

    /*
      Method to check if there is a BlackKing in the surrounding squares of a given Square.
      The method should return true if there is no King in any of the squares surrounding
      the square that was submitted to the method. The method checks the grid below:


                                             _|_____________|_________|_____________|_
                                              |             |         |             |
                                              |(x-75, y-75) |(x, y-75)|(x+75, y-75) |
                                             _|_____________|_________|_____________|_
                                              |             |         |             |
                                              |(x-75, y)    | (x, y)  |(x+75, y)    |
                                             _|_____________|_________|_____________|_
                                              |             |         |             |
                                              |(x-75, y+75) |(x, y+75)|(x+75, y+75) |
                                             _|_____________|_________|_____________|_
                                              |             |         |             |


    */
    private Boolean checkSurroundingSquares(Square s) {
        Boolean possible = false;
        int x = s.getXC() * 75;
        int y = s.getYC() * 75;
        if (!((getPieceName((x + 75), y).contains("BlackKing")) || (getPieceName((x - 75), y).contains("BlackKing")) ||
                (getPieceName(x, (y + 75)).contains("BlackKing")) || (getPieceName((x), (y - 75)).contains("BlackKing")) ||
                (getPieceName((x + 75), (y + 75)).contains("BlackKing")) || (getPieceName((x - 75), (y + 75)).contains("BlackKing")) ||
                (getPieceName((x + 75), (y - 75)).contains("BlackKing")) || (getPieceName((x - 75), (y - 75)).contains("BlackKing")))) {
            possible = true;
        }
        return possible;
    }

    /*
      The getKingSquares method takes as an input any coordinates from a square and returns a stack of all the possible
      valid moves that the WhiteKing can move to.

      So let's consider how the King can move. The King can essentially move in any direction as long as there is not another
      king in an adjacent square to where the king lands. Additionally, the King can only move one square at a time.

      To support this method we will also create a helper method called checkSurroundingSquares(Square s){, see above
      that returns a Boolean value to let us know if a supplied square will be adjacent to another square with a
      BlackKing present. Essentially if we consider that the board is a set of squares with coordinates for each square (x, y), this
      allows us to identify the possible squares that we should be investigating, see below;

                                           _|___________|_________|___________|_
                                            |           |         |           |
                                            |(x-1, y-1) |(x, y-1) |(x+1, y-1) |
                                           _|___________|_________|___________|_
                                            |           |         |           |
                                            |(x-1, y)   | (x, y)  |(x+1, y)   |
                                           _|___________|_________|___________|_
                                            |           |         |           |
                                            |(x-1, y+1) | (x, y+1)|(x+1, y+1) |
                                           _|___________|_________|___________|_
                                            |           |         |           |


      This shows us that for a single square with coordinates of (x, y) we need to check eight other potential squares.
      Remember we only need to check squares and construct moves if the movement (Piece on a Square --> a new Square) is
      going to be placing the piece back on the board, if we are not taking our own piece and if the resulting landing square
      is not adjacent to the enemy King.
    */
    private Stack getKingSquares(int x, int y, String piece) {
        Square startingSquare = new Square(x, y, piece);
        Stack moves = new Stack();
        Move validM, validM2, validM3, validM4;
        int tmpx1 = x + 1;
        int tmpx2 = x - 1;
        int tmpy1 = y + 1;
        int tmpy2 = y - 1;

        /*
        If we consider the grid above, we can create three different columns to check.
         - if x increases by one square, using the variable tmpx1 (x+1)
         - if x decreases by one square, using the variable tmpx2 (x-1)
          - or if x stays the same.
         */
        if (!((tmpx1 > 7))) {
            /*
      This is the first condition where we will be working with the column where x increases.
      If we consider x increasing, we need to make sure that we don't fall off the board, so we use
      a condition here to check that the new value of x (tmpx1) is not greater than 7.

      From the grid above we can see in this column that there are three possible squares for us to check in
      this column:
      - were y decreases, y-1
      - were y increases, y+1
      - or were y stays the same

      The first step is to construct three new Squares for each of these possibilities.
      As the unchanged y value is already a location on the board we don't need to check the location and can simply
      make a call to checkSurroundingSquares for this new Square.

      If checkSurroundingSquares returns a positive value we jump inside the condition below:
        - firstly we create a new Move, which takes the starting square and the landing square that we have just checked with
          checkSurroundingSquares.
        - Next we need to figure out if there is a piece present on the square and if so make sure
          that the piece is an opponents piece.
        - Once we make sure that we are either moving to an empty square or we are taking our opponents piece we can push this
          possible move onto our stack of possible moves called "moves".

      This process is followed again for the other temporary squares created.

      After we check for all possible squares on this column, we repeat the process for the other columns as identified above
      in the grid.
    */
            Square tmp = new Square(tmpx1, y, piece);
            Square tmp1 = new Square(tmpx1, tmpy1, piece);
            Square tmp2 = new Square(tmpx1, tmpy2, piece);
            if (checkSurroundingSquares(tmp)) {
                validM = new Move(startingSquare, tmp);
                if (!piecePresent(((tmp.getXC() * 75) + 20), (((tmp.getYC() * 75) + 20)))) {
                    moves.push(validM);
                } else {
                    if (checkWhiteOpponent(((tmp.getXC() * 75) + 20), (((tmp.getYC() * 75) + 20)))) {
                        moves.push(validM);
                    }
                }
            }
            if (!(tmpy1 > 7)) {
                if (checkSurroundingSquares(tmp1)) {
                    validM2 = new Move(startingSquare, tmp1);
                    if (!piecePresent(((tmp1.getXC() * 75) + 20), (((tmp1.getYC() * 75) + 20)))) {
                        moves.push(validM2);
                    } else {
                        if (checkWhiteOpponent(((tmp1.getXC() * 75) + 20), (((tmp1.getYC() * 75) + 20)))) {
                            moves.push(validM2);
                        }
                    }
                }
            }
            if (!(tmpy2 < 0)) {
                if (checkSurroundingSquares(tmp2)) {
                    validM3 = new Move(startingSquare, tmp2);
                    if (!piecePresent(((tmp2.getXC() * 75) + 20), (((tmp2.getYC() * 75) + 20)))) {
                        moves.push(validM3);
                    } else {
                        System.out.println("The values that we are going to be looking at are : " + ((tmp2.getXC() * 75) + 20) + " and the y value is : " + ((tmp2.getYC() * 75) + 20));
                        if (checkWhiteOpponent(((tmp2.getXC() * 75) + 20), (((tmp2.getYC() * 75) + 20)))) {
                            moves.push(validM3);
                        }
                    }
                }
            }
        }
        if (!((tmpx2 < 0))) {
            Square tmp3 = new Square(tmpx2, y, piece);
            Square tmp4 = new Square(tmpx2, tmpy1, piece);
            Square tmp5 = new Square(tmpx2, tmpy2, piece);
            if (checkSurroundingSquares(tmp3)) {
                validM = new Move(startingSquare, tmp3);
                if (!piecePresent(((tmp3.getXC() * 75) + 20), (((tmp3.getYC() * 75) + 20)))) {
                    moves.push(validM);
                } else {
                    if (checkWhiteOpponent(((tmp3.getXC() * 75) + 20), (((tmp3.getYC() * 75) + 20)))) {
                        moves.push(validM);
                    }
                }
            }
            if (!(tmpy1 > 7)) {
                if (checkSurroundingSquares(tmp4)) {
                    validM2 = new Move(startingSquare, tmp4);
                    if (!piecePresent(((tmp4.getXC() * 75) + 20), (((tmp4.getYC() * 75) + 20)))) {
                        moves.push(validM2);
                    } else {
                        if (checkWhiteOpponent(((tmp4.getXC() * 75) + 20), (((tmp4.getYC() * 75) + 20)))) {
                            moves.push(validM2);
                        }
                    }
                }
            }
            if (!(tmpy2 < 0)) {
                if (checkSurroundingSquares(tmp5)) {
                    validM3 = new Move(startingSquare, tmp5);
                    if (!piecePresent(((tmp5.getXC() * 75) + 20), (((tmp5.getYC() * 75) + 20)))) {
                        moves.push(validM3);
                    } else {
                        if (checkWhiteOpponent(((tmp5.getXC() * 75) + 20), (((tmp5.getYC() * 75) + 20)))) {
                            moves.push(validM3);
                        }
                    }
                }
            }
        }
        Square tmp7 = new Square(x, tmpy1, piece);
        Square tmp8 = new Square(x, tmpy2, piece);
        if (!(tmpy1 > 7)) {
            if (checkSurroundingSquares(tmp7)) {
                validM2 = new Move(startingSquare, tmp7);
                if (!piecePresent(((tmp7.getXC() * 75) + 20), (((tmp7.getYC() * 75) + 20)))) {
                    moves.push(validM2);
                } else {
                    if (checkWhiteOpponent(((tmp7.getXC() * 75) + 20), (((tmp7.getYC() * 75) + 20)))) {
                        moves.push(validM2);
                    }
                }
            }
        }
        if (!(tmpy2 < 0)) {
            if (checkSurroundingSquares(tmp8)) {
                validM3 = new Move(startingSquare, tmp8);
                if (!piecePresent(((tmp8.getXC() * 75) + 20), (((tmp8.getYC() * 75) + 20)))) {
                    moves.push(validM3);
                } else {
                    if (checkWhiteOpponent(((tmp8.getXC() * 75) + 20), (((tmp8.getYC() * 75) + 20)))) {
                        moves.push(validM3);
                    }
                }
            }
        }
        return moves;
    } // end of the method getKingSquares()

    /*
        Method to return all the possible moves that a Queen can make
    */
    private Stack getQueenMoves(int x, int y, String piece) {
        Stack completeMoves = new Stack();
        Stack tmpMoves = new Stack();
        Move tmp;
  /*
      The Queen is a pretty easy piece to figure out if you have completed the
      Bishop and the Rook movements. Either the Queen is going to move like a
      Bishop or It's going to move like a Rook, so all we have to do is make a call to both of these
      methods.
  */
        tmpMoves = getRookMoves(x, y, piece);
        while (!tmpMoves.empty()) {
            tmp = (Move) tmpMoves.pop();
            completeMoves.push(tmp);
        }
        tmpMoves = getBishopMoves(x, y, piece);
        while (!tmpMoves.empty()) {
            tmp = (Move) tmpMoves.pop();
            completeMoves.push(tmp);
        }
        return completeMoves;
    }// end of the method getQueenMoves()

    /*
      Method to return all the squares that a Rook can move to. The Rook can either move in an x direction or
      in a y direction as long as there is nothing in the way and it can take its opponents piece but not its
      own piece. As seen in the below grid the Rook can either move in a horizontal direction (x changing value)
      or in a vertical movement (y changing direction)

                                _|_____________|___________|_________|___________|___________|_
                                 |             |           |         |           |           |
                                 |             |           |(x, y-N) |           |           |
                                _|_____________|___________|_________|___________|___________|_
                                 |             |           |         |           |           |
                                 |             |           |(x, y-2) |           |           |
                                _|_____________|___________|_________|___________|___________|_
                                 |             |           |         |           |           |
                                 |             |           |(x, y-1) |           |           |
                                _|_____________|___________|_________|___________|___________|_
                                 |             |           |         |           |           |
                                 | (x-N, y)    |(x-1, y)   | (x, y)  |(x+1, y)   |(x+N, y)   |
                                _|_____________|___________|_________|___________|___________|_
                                 |             |           |         |           |           |
                                 |             |           | (x, y+1)|           |           |
                                _|_____________|___________|_________|___________|___________|_
                                 |             |           |         |           |           |
                                 |             |           |(x, y+2) |           |           |
                                _|_____________|___________|_________|___________|___________|_
                                 |             |           |         |           |           |
                                 |             |           |(x, y+N) |           |           |
                                _|_____________|___________|_________|___________|___________|_
                                 |             |           |         |           |           |
    */
    private Stack getRookMoves(int x, int y, String piece) {
        Square startingSquare = new Square(x, y, piece);
        Stack moves = new Stack();
        Move validM, validM2, validM3, validM4;
        /*
    There are four possible directions that the Rook can move to:
      - the x value is increasing
      - the x value is decreasing
      - the y value is increasing
      - the y value is decreasing

    Each of these movements should be catered for. The loop guard is set to increment up to the maximum number of squares.
    On each iteration of the first loop we are adding the value of i to the current x coordinate.
    We make sure that the new potential square is going to be on the board and if it is we create a new square and a new potential
    move (originating square, new square).If there are no pieces present on the potential square we simply add it to the Stack
    of potential moves.
    If there is a piece on the square we need to check if it's an opponent piece. If it is an opponent piece it's a valid move, but we
    must break out of the loop using the Java break keyword as we can't jump over the piece and search for squares. If it's not
    an opponent piece we simply break out of the loop.

    This cycle needs to happen four times for each of the possible directions of the Rook.
  */
        for (int i = 1; i < 8; i++) {
            int tmpx = x + i;
            int tmpy = y;
            if (!(tmpx > 7 || tmpx < 0)) {
                Square tmp = new Square(tmpx, tmpy, piece);
                validM = new Move(startingSquare, tmp);
                if (!piecePresent(((tmp.getXC() * 75) + 20), (((tmp.getYC() * 75) + 20)))) {
                    moves.push(validM);
                } else {
                    if (checkWhiteOpponent(((tmp.getXC() * 75) + 20), ((tmp.getYC() * 75) + 20))) {
                        moves.push(validM);
                        break;
                    } else {
                        break;
                    }
                }
            }
        }//end of the loop with x increasing and Y doing nothing...
        for (int j = 1; j < 8; j++) {
            int tmpx1 = x - j;
            int tmpy1 = y;
            if (!(tmpx1 > 7 || tmpx1 < 0)) {
                Square tmp2 = new Square(tmpx1, tmpy1, piece);
                validM2 = new Move(startingSquare, tmp2);
                if (!piecePresent(((tmp2.getXC() * 75) + 20), (((tmp2.getYC() * 75) + 20)))) {
                    moves.push(validM2);
                } else {
                    if (checkWhiteOpponent(((tmp2.getXC() * 75) + 20), ((tmp2.getYC() * 75) + 20))) {
                        moves.push(validM2);
                        break;
                    } else {
                        break;
                    }
                }
            }
        }//end of the loop with x increasing and Y doing nothing...
        for (int k = 1; k < 8; k++) {
            int tmpx3 = x;
            int tmpy3 = y + k;
            if (!(tmpy3 > 7 || tmpy3 < 0)) {
                Square tmp3 = new Square(tmpx3, tmpy3, piece);
                validM3 = new Move(startingSquare, tmp3);
                if (!piecePresent(((tmp3.getXC() * 75) + 20), (((tmp3.getYC() * 75) + 20)))) {
                    moves.push(validM3);
                } else {
                    if (checkWhiteOpponent(((tmp3.getXC() * 75) + 20), ((tmp3.getYC() * 75) + 20))) {
                        moves.push(validM3);
                        break;
                    } else {
                        break;
                    }
                }
            }
        }//end of the loop with x increasing and Y doing nothing...
        for (int l = 1; l < 8; l++) {
            int tmpx4 = x;
            int tmpy4 = y - l;
            if (!(tmpy4 > 7 || tmpy4 < 0)) {
                Square tmp4 = new Square(tmpx4, tmpy4, piece);
                validM4 = new Move(startingSquare, tmp4);
                if (!piecePresent(((tmp4.getXC() * 75) + 20), (((tmp4.getYC() * 75) + 20)))) {
                    moves.push(validM4);
                } else {
                    if (checkWhiteOpponent(((tmp4.getXC() * 75) + 20), ((tmp4.getYC() * 75) + 20))) {
                        moves.push(validM4);
                        break;
                    } else {
                        break;
                    }
                }
            }
        }//end of the loop with x increasing and Y doing nothing...
        return moves;
    }// end of the method getRookMoves()

    /*
      Method to return all the squares that a Bishop can move to. As seen in the below grid, the Bishop
      can move in a diagonal movement. There are essentially four different directions from a single
      square that the Bishop can move along. The Bishop can move any distance along this diagonal
      as long as there is nothing in the way. The Bishop can also take an opponent piece but cannot take its
      own piece.


                                  _|_____________|___________|_________|___________|___________|_
                                   |             |           |         |           |           |
                                   |             |           |         |           |           |
                                  _|_____________|___________|_________|___________|___________|_
                                   |             |           |         |           |           |
                                   | (x-N, y-N)  |           |         |           |(x+N, y-N) |
                                  _|_____________|___________|_________|___________|___________|_
                                   |             |           |         |           |           |
                                   |             | (x-1, y-1)|         | (x+1, y-1)|           |
                                  _|_____________|___________|_________|___________|___________|_
                                   |             |           |         |           |           |
                                   |             |           | (x, y)  |           |           |
                                  _|_____________|___________|_________|___________|___________|_
                                   |             |           |         |           |           |
                                   |             |(x-1, y+1) |         | (x+1, y+1)|           |
                                  _|_____________|___________|_________|___________|___________|_
                                   |             |           |         |           |           |
                                   |(x-N, y+N)   |           |         |           |(x+N, y+N) |
                                  _|_____________|___________|_________|___________|___________|_
                                   |             |           |         |           |           |
                                   |             |           |         |           |           |
                                  _|_____________|___________|_________|___________|___________|_
                                   |             |           |         |           |           |

    */
    private Stack getBishopMoves(int x, int y, String piece) {
        Square startingSquare = new Square(x, y, piece);
        Stack moves = new Stack();
        Move validM, validM2, validM3, validM4;
  /*
    The Bishop can move along any diagonal until it hits an enemy piece or its own piece
    it cannot jump over its own piece. We need to use four different loops to go through the possible movements
    to identify possible squares to move to. The temporary squares, i.e. the values of x and y must change by the
    same amount on each iteration of each of the loops.

    If the new values of x and y are on the board, we create a new square and a new move (from the original square to the new
    square). We then check if there is a piece present on the new square:
    - if not we add the move as a possible new move
    - if there is a piece we make sure that we can capture our opponents piece, and we cannot take our own piece,
    and then we break out of the loop

    This process is repeated for each of the other three possible diagonals that the Bishop can travel along.

  */
        for (int i = 1; i < 8; i++) {
            int tmpx = x + i;
            int tmpy = y + i;
            if (!(tmpx > 7 || tmpx < 0 || tmpy > 7 || tmpy < 0)) {
                Square tmp = new Square(tmpx, tmpy, piece);
                validM = new Move(startingSquare, tmp);
                if (!piecePresent(((tmp.getXC() * 75) + 20), (((tmp.getYC() * 75) + 20)))) {
                    moves.push(validM);
                } else {
                    if (checkWhiteOpponent(((tmp.getXC() * 75) + 20), ((tmp.getYC() * 75) + 20))) {
                        moves.push(validM);
                        break;
                    } else {
                        break;
                    }
                }
            }
        } // end of the first for Loop
        for (int k = 1; k < 8; k++) {
            int tmpk = x + k;
            int tmpy2 = y - k;
            if (!(tmpk > 7 || tmpk < 0 || tmpy2 > 7 || tmpy2 < 0)) {
                Square tmpK1 = new Square(tmpk, tmpy2, piece);
                validM2 = new Move(startingSquare, tmpK1);
                if (!piecePresent(((tmpK1.getXC() * 75) + 20), (((tmpK1.getYC() * 75) + 20)))) {
                    moves.push(validM2);
                } else {
                    if (checkWhiteOpponent(((tmpK1.getXC() * 75) + 20), ((tmpK1.getYC() * 75) + 20))) {
                        moves.push(validM2);
                        break;
                    } else {
                        break;
                    }
                }
            }
        } //end of second loop.
        for (int l = 1; l < 8; l++) {
            int tmpL2 = x - l;
            int tmpy3 = y + l;
            if (!(tmpL2 > 7 || tmpL2 < 0 || tmpy3 > 7 || tmpy3 < 0)) {
                Square tmpLMov2 = new Square(tmpL2, tmpy3, piece);
                validM3 = new Move(startingSquare, tmpLMov2);
                if (!piecePresent(((tmpLMov2.getXC() * 75) + 20), (((tmpLMov2.getYC() * 75) + 20)))) {
                    moves.push(validM3);
                } else {
                    if (checkWhiteOpponent(((tmpLMov2.getXC() * 75) + 20), ((tmpLMov2.getYC() * 75) + 20))) {
                        moves.push(validM3);
                        break;
                    } else {
                        break;
                    }
                }
            }
        }// end of the third loop
        for (int n = 1; n < 8; n++) {
            int tmpN2 = x - n;
            int tmpy4 = y - n;
            if (!(tmpN2 > 7 || tmpN2 < 0 || tmpy4 > 7 || tmpy4 < 0)) {
                Square tmpNmov2 = new Square(tmpN2, tmpy4, piece);
                validM4 = new Move(startingSquare, tmpNmov2);
                if (!piecePresent(((tmpNmov2.getXC() * 75) + 20), (((tmpNmov2.getYC() * 75) + 20)))) {
                    moves.push(validM4);
                } else {
                    if (checkWhiteOpponent(((tmpNmov2.getXC() * 75) + 20), ((tmpNmov2.getYC() * 75) + 20))) {
                        moves.push(validM4);
                        break;
                    } else {
                        break;
                    }
                }
            }
        }// end of the last loop
        return moves;
    }// end of the method getBishopMoves()

    /*
        Method fo return all the squares that a Knight can attack. The knight is possibly the simplest piece
        to get possible movements from. The Knight can essentially move in an L direction from any square on the
        board as long as the landing square is on the board, and we can take an opponents piece but not our own piece.
    */
    private Stack getKnightMoves(int x, int y, String piece) {
        Square startingSquare = new Square(x, y, piece);
        Stack moves = new Stack();
        Stack attackingMove = new Stack();
        Square s = new Square(x + 1, y + 2, piece);
        moves.push(s);
        Square s1 = new Square(x + 1, y - 2, piece);
        moves.push(s1);
        Square s2 = new Square(x - 1, y + 2, piece);
        moves.push(s2);
        Square s3 = new Square(x - 1, y - 2, piece);
        moves.push(s3);
        Square s4 = new Square(x + 2, y + 1, piece);
        moves.push(s4);
        Square s5 = new Square(x + 2, y - 1, piece);
        moves.push(s5);
        Square s6 = new Square(x - 2, y + 1, piece);
        moves.push(s6);
        Square s7 = new Square(x - 2, y - 1, piece);
        moves.push(s7);

        for (int i = 0; i < 8; i++) {
            Square tmp = (Square) moves.pop();
            Move tmpmove = new Move(startingSquare, tmp);
            if ((tmp.getXC() < 0) || (tmp.getXC() > 7) || (tmp.getYC() < 0) || (tmp.getYC() > 7)) {

            } else if (piecePresent(((tmp.getXC() * 75) + 20), (((tmp.getYC() * 75) + 20)))) {
                if (piece.contains("White")) {
                    if (checkWhiteOpponent(((tmp.getXC() * 75) + 20), ((tmp.getYC() * 75) + 20))) {
                        attackingMove.push(tmpmove);
                    }
                }
            } else {
                attackingMove.push(tmpmove);
            }
        }
        return attackingMove;
    }// end of the method getKnightMoves()

    /*
            Method to colour a stack of Squares
    */
    private void colorSquares(Stack squares) {
        Border greenBorder = BorderFactory.createLineBorder(Color.GREEN, 3);
        while (!squares.empty()) {
            Square s = (Square) squares.pop();
            int location = s.getXC() + ((s.getYC()) * 8);
            JPanel panel = (JPanel) chessBoard.getComponent(location);
            panel.setBorder(greenBorder);
        }
    }

    /*
        Method to get the landing square of a bunch of moves...
    */
    private void getLandingSquares(Stack found) {
        Move tmp;
        Square landing;
        Stack squares = new Stack();
        while (!found.empty()) {
            tmp = (Move) found.pop();
            landing = tmp.getLanding();
            squares.push(landing);
        }
        colorSquares(squares);
    }

    /*
      Method to find all the White Pieces.
    */
    private Stack findWhitePieces() {
        Stack squares = new Stack();
        String icon;
        int x;
        int y;
        String pieceName;
        for (int i = 0; i < 600; i += 75) {
            for (int j = 0; j < 600; j += 75) {
                y = i / 75;
                x = j / 75;
                Component tmp = chessBoard.findComponentAt(j, i);
                if (tmp instanceof JLabel) {
                    chessPiece = (JLabel) tmp;
                    icon = chessPiece.getIcon().toString();
                    pieceName = icon.substring(0, (icon.length() - 4));
                    if (pieceName.contains("White")) {
                        Square stmp = new Square(x, y, pieceName);
                        squares.push(stmp);
                    }
                }
            }
        }
        return squares;
    }

    private Stack findBlackPieces() {
        Stack squares = new Stack();
        String icon;
        int x;
        int y;
        String pieceName;
        for (int i = 0; i < 600; i += 75) {
            for (int j = 0; j < 600; j += 75) {
                y = i / 75;
                x = j / 75;
                Component tmp = chessBoard.findComponentAt(j, i);
                if (tmp instanceof JLabel) {
                    chessPiece = (JLabel) tmp;
                    icon = chessPiece.getIcon().toString();
                    pieceName = icon.substring(0, (icon.length() - 4));
                    if (pieceName.contains("Black")) {
                        Square stmp = new Square(x, y, pieceName);
                        squares.push(stmp);
                    }
                }
            }
        }
        return squares;
    }

    /*
        This is a method to check if a piece is a Black piece.
    */
    private Boolean checkWhiteOpponent(int newX, int newY) {
        Boolean opponent;
        Component c1 = chessBoard.findComponentAt(newX, newY);
        JLabel awaitingPiece = (JLabel) c1;
        String tmp1 = awaitingPiece.getIcon().toString();
        opponent = tmp1.contains("Black");
        return opponent;
    }

    /*
  This is a method to check if a piece is a Black piece.
*/
    private Boolean checkBlackOpponent(int newX, int newY) {
        Boolean opponent;
        Component c1 = chessBoard.findComponentAt(newX, newY);
        JLabel awaitingPiece = (JLabel) c1;
        String tmp1 = awaitingPiece.getIcon().toString();
        opponent = tmp1.contains("White");
        return opponent;
    }

    private void resetBorders() {
        Border empty = BorderFactory.createEmptyBorder();
        for (int i = 0; i < 64; i++) {
            JPanel tmppanel = (JPanel) chessBoard.getComponent(i);
            tmppanel.setBorder(empty);
        }
    }

    /*
      The method printStack takes in a Stack of Moves and prints out all possible moves.
    */
    private void printStack(Stack input) {
        Move m;
        Square s, l;
        while (!input.empty()) {
            m = (Move) input.pop();
            s = m.getStart();
            l = m.getLanding();
            System.out.println("The possible move that was found is : (" + s.getXC() + " , " + s.getYC() + "), landing at (" + l.getXC() + " , " + l.getYC() + ")");
        }
    }

    private void makeAIMove() {
        /*
         * When the AI Agent decides on a move, a red border shows the square from where
         * the move started and the landing square of the move.
         */
        resetBorders();
        layeredPane.validate();
        layeredPane.repaint();
        Stack white = findWhitePieces();
        Stack black = findBlackPieces();
        Stack completeMovesWhite = new Stack();
        Stack completeMovesBlack = new Stack();
        Move selectedAgentMove = null;
        Move tmp;
        Stack temporaryWhite = new Stack();
        Stack temporaryBlack = new Stack();

        // AI will use White pieces. Setting Black moves to white moves.
        black = white;

        /*
        While the white stack isn't empty, pop that stack
         */
        while (!white.empty()) {
            Square s = (Square) white.pop();
            String tmpString = s.getName();
            Stack tmpMoves = new Stack();

            /*
             * We need to identify all the possible moves that can be made by white pieces
             */
            if (tmpString.contains("Knight")) { tmpMoves = getKnightMoves(s.getXC(), s.getYC(), s.getName()); }
            else if (tmpString.contains("Bishop")) { tmpMoves = getBishopMoves(s.getXC(), s.getYC(), s.getName()); }
            else if (tmpString.contains("Pawn")) { tmpMoves = getWhitePawnSquares(s.getXC(), s.getYC(), s.getName()); }
            else if (tmpString.contains("Rook")) { tmpMoves = getRookMoves(s.getXC(), s.getYC(), s.getName()); }
            else if (tmpString.contains("Queen")) { tmpMoves = getQueenMoves(s.getXC(), s.getYC(), s.getName()); }
            else if (tmpString.contains("King")) { tmpMoves = getKingSquares(s.getXC(), s.getYC(), s.getName()); }
            while (!tmpMoves.empty()) {
                tmp = (Move) tmpMoves.pop();
                completeMovesWhite.push(tmp);
            }
        }
        /*
        While the black stack isn't empty, pop that stack
         */
        while (!black.empty()) {
            Square s = (Square) black.pop();
            String tmpString = s.getName();
            Stack tmpMoves = new Stack();

            /*
             * We need to identify all the possible moves that can be made by black pieces
             */
            if (tmpString.contains("Knight")) {
                tmpMoves = getKnightMoves(s.getXC(), s.getYC(), s.getName());
            } else if (tmpString.contains("Bishop")) {
                tmpMoves = getBishopMoves(s.getXC(), s.getYC(), s.getName());
            } else if (tmpString.contains("Pawn")) {
                tmpMoves = getBlackPawnSquares(s.getXC(), s.getYC(), s.getName());
            } else if (tmpString.contains("Rook")) {
                tmpMoves = getRookMoves(s.getXC(), s.getYC(), s.getName());
            } else if (tmpString.contains("Queen")) {
                tmpMoves = getQueenMoves(s.getXC(), s.getYC(), s.getName());
            } else if (tmpString.contains("King")) {
                tmpMoves = getKingSquares(s.getXC(), s.getYC(), s.getName());
            }
            while (!tmpMoves.empty()) {
                tmp = (Move) tmpMoves.pop();
                completeMovesBlack.push(tmp);
            }
        }

        temporaryWhite = (Stack) completeMovesWhite.clone();
        temporaryBlack = (Stack) completeMovesBlack.clone();
        getLandingSquares(temporaryWhite);
        printStack(temporaryWhite);

        /*
         * So now we should have a copy of all the possible moves to make in our Stack called completeMovesWhite
         */
        if (completeMovesWhite.size() == 0) {
            /*
             * In Chess if you cannot make a valid move, but you are not in Check this state
             * is referred to as a stalemate
             */
            JOptionPane.showMessageDialog(null,
                    "Congratulations, you have successfully placed the AI component in a Stale Mate Position");
            System.exit(0);

        } else {
            System.out.println("==================================");
            Stack movesWhite = new Stack();
            Stack movesBlack = new Stack();
            while (!completeMovesWhite.empty()) {
                Move tmpMove = (Move) completeMovesWhite.pop();
                Square s1 = tmpMove.getStart();
                Square s2 = tmpMove.getLanding();
                movesWhite.push(tmpMove);
            }
            while (!completeMovesBlack.empty()) {
                Move tmpMove = (Move) completeMovesBlack.pop();
                Square s1 = tmpMove.getStart();
                Square s2 = tmpMove.getLanding();
                movesWhite.push(tmpMove);
            }
            Border redBorder = BorderFactory.createLineBorder(Color.RED, 3);

            if (selectedAi == 0) {
                selectedAgentMove = agent.randomMove(movesWhite);
                System.out.println("Random AI Selected");
            }
            if (selectedAi == 1) {
                selectedAgentMove = agent.nextBestMove(movesWhite);
                System.out.println("Next Best AI Selected");
            }
            if (selectedAi == 2) {
                selectedAgentMove = agent.twoLevelsDeep(movesWhite, movesBlack);
                System.out.println("2 Deep AI Selected");
            }

            Square startingPoint = selectedAgentMove.getStart();
            Square landingPoint = selectedAgentMove.getLanding();
            int startX1 = (startingPoint.getXC() * 75) + 20;
            int startY1 = (startingPoint.getYC() * 75) + 20;
            int landingX1 = (landingPoint.getXC() * 75) + 20;
            int landingY1 = (landingPoint.getYC() * 75) + 20;
            System.out.println("AI Moved " + startingPoint.getName() + " from (" + startingPoint.getXC() + ", " + startingPoint.getYC() + ") to (" + landingPoint.getXC() + ", " + landingPoint.getYC() + ")");
            Component c = chessBoard.findComponentAt(startX1, startY1);
            Container parent = c.getParent();
            parent.remove(c);
            int panelID = (startingPoint.getYC() * 8) + startingPoint.getXC();
            panels = (JPanel) chessBoard.getComponent(panelID);
            panels.setBorder(redBorder);
            parent.validate();

            Component l = chessBoard.findComponentAt(landingX1, landingY1);
            if (l instanceof JLabel) {
                Container parentlanding = l.getParent();
                JLabel awaitingName = (JLabel) l;
                String agentCaptured = awaitingName.getIcon().toString();
                if (agentCaptured.contains("BlackKing")) {
                    agentwins = true;
                }
                parentlanding.remove(l);
                parentlanding.validate();
                pieces = new JLabel(new ImageIcon(startingPoint.getName() + ".png"));
                int landingPanelID = (landingPoint.getYC() * 8) + landingPoint.getXC();
                panels = (JPanel) chessBoard.getComponent(landingPanelID);
                panels.add(pieces);
                panels.setBorder(redBorder);
                layeredPane.validate();
                layeredPane.repaint();

                if (agentwins) {
                    JOptionPane.showMessageDialog(null, "The AI Agent has won!");
                    System.exit(0);
                }
            } else {
                pieces = new JLabel(new ImageIcon(startingPoint.getName() + ".png"));
                int landingPanelID = (landingPoint.getYC() * 8) + landingPoint.getXC();
                panels = (JPanel) chessBoard.getComponent(landingPanelID);
                panels.add(pieces);
                panels.setBorder(redBorder);
                layeredPane.validate();
                layeredPane.repaint();
            }
            white2Move = false;
        }
    }

    private String getPieceName(int x, int y) {
        Component c1 = chessBoard.findComponentAt(x, y);
        if (c1 instanceof JPanel) {
            return "empty";
        } else if (c1 instanceof JLabel) {
            JLabel awaitingPiece = (JLabel) c1;
            String tmp1 = awaitingPiece.getIcon().toString();
            return tmp1;
        } else {
            return "empty";
        }
    }

    private Boolean pieceMove(int x, int y) {
        return (startX != x) || (startY != y);
    }

    /*
        This method is called when we press the Mouse. So we need to find out what piece we have
        selected. We may also not have selected a piece!
    */
    public void mousePressed(MouseEvent e) {
        chessPiece = null;
        String name = getPieceName(e.getX(), e.getY());
        Component c = chessBoard.findComponentAt(e.getX(), e.getY());
        if (c instanceof JPanel)
            return;

        Point parentLocation = c.getParent().getLocation();
        xAdjustment = parentLocation.x - e.getX();
        yAdjustment = parentLocation.y - e.getY();
        chessPiece = (JLabel) c;
        initialX = e.getX();
        initialY = e.getY();
        startX = (e.getX() / 75);
        startY = (e.getY() / 75);

        if (name.contains("Knight")) {

            getKnightMoves(startX, startY, name);
        }

        chessPiece.setLocation(e.getX() + xAdjustment, e.getY() + yAdjustment);
        chessPiece.setSize(chessPiece.getWidth(), chessPiece.getHeight());
        layeredPane.add(chessPiece, JLayeredPane.DRAG_LAYER);
    }

    public void mouseDragged(MouseEvent me) {
        if (chessPiece == null) return;
        chessPiece.setLocation(me.getX() + xAdjustment, me.getY() + yAdjustment);
    }

    /*
       This method is used when the Mouse is released...we need to make sure the move was valid before
       putting the piece back on the board.
   */
    public void mouseReleased(MouseEvent e) {
        if (chessPiece == null) return;

        chessPiece.setVisible(false);
        Boolean success = false;
        Boolean promotion = false;
        Boolean progression = false;
        Component c = chessBoard.findComponentAt(e.getX(), e.getY());

        String tmp = chessPiece.getIcon().toString();
        String pieceName = tmp.substring(0, (tmp.length() - 4));
        Boolean validMove = false;

        int landingX = (e.getX() / 75);
        int landingY = (e.getY() / 75);

        int xMovement = Math.abs((e.getX() / 75) - startX);
        int yMovement = Math.abs((e.getY() / 75) - startY);
        System.out.println("----------------------------------------------");
        System.out.println("The piece that is being moved is : " + pieceName);
        System.out.println("The starting coordinates are : " + "( " + startX + "," + startY + ")");
        System.out.println("The xMovement is : " + xMovement);
        System.out.println("The yMovement is : " + yMovement);
        System.out.println("The landing coordinates are : " + "( " + landingX + "," + landingY + ")");
        System.out.println("----------------------------------------------");

        /*
          We need a process to identify who's turn it is to make a move.
        */
        Boolean possible = false;

        if (white2Move) {
            if (pieceName.contains("White")) {
                possible = true;
            }
        } else {
            if (pieceName.contains("Black")) {
                possible = true;
            }
        }
        if (possible) {

            if (pieceName.contains("Bishop")) {
          /*
            The Bishop is a simple piece and has a very long range so usually is used to attack from far away.
            It moves along the diagonal as long as nothing is in the way to its destination. Like all other Chess
            pieces it can take its opponent piece but cannot take its own piece.
          */
                Boolean inTheWay = false;
                if (((landingX < 0) || (landingX > 7)) || ((landingY < 0) || (landingY > 7))) {
                    validMove = false;
                } else if (!pieceMove(landingX, landingY)) {
                    validMove = false;
                } else {
                    validMove = true;
            /*
              The condition to check for a validMove simple asks is the x movement is the same as the y
              movement, if so we are moving along a diagonal. We then need to identify which diagonal and
              make sure there is nothing in the way.
            */
                    if (xMovement == yMovement) {
                        if ((startX - landingX < 0) && (startY - landingY < 0)) {
                            for (int i = 0; i < xMovement; i++) {
                                if (piecePresent((initialX + (i * 75)), (initialY + (i * 75)))) {
                                    inTheWay = true;
                                }
                            }
                        } else if ((startX - landingX < 0) && (startY - landingY > 0)) {
                            for (int i = 0; i < xMovement; i++) {
                                if (piecePresent((initialX + (i * 75)), (initialY - (i * 75)))) {
                                    inTheWay = true;
                                }
                            }
                        } else if ((startX - landingX > 0) && (startY - landingY > 0)) {
                            for (int i = 0; i < xMovement; i++) {
                                if (piecePresent((initialX - (i * 75)), (initialY - (i * 75)))) {
                                    inTheWay = true;
                                }
                            }
                        } else if ((startX - landingX > 0) && (startY - landingY < 0)) {
                            for (int i = 0; i < xMovement; i++) {
                                if (piecePresent((initialX - (i * 75)), (initialY + (i * 75)))) {
                                    inTheWay = true;
                                }
                            }
                        } // end of the conditions to check the appropriate diagonal to see if there is a piece in the way.
                        if (inTheWay) {
                            validMove = false;
                        } else {
                /*
                    This is cool there is nothing in the way, and we just need to make sure that we are not able to
                    take our own piece.
                */
                            if (piecePresent(e.getX(), (e.getY()))) {
                                if (pieceName.contains("White")) {
                                    if (checkWhiteOpponent(e.getX(), e.getY())) {
                                        validMove = true;
                                        if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                            winner = "White Wins!";
                                        }
                                    } else {
                                        validMove = false;
                                    }
                                } else {
                                    if (checkBlackOpponent(e.getX(), e.getY())) {
                                        validMove = true;
                                        if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                            winner = "Black Wins!";
                                        }
                                    } else {
                                        validMove = false;
                                    }
                                }
                            } else { // There is no piecePresent on the square that we are moving to.
                                validMove = true;
                            }
                        }
                    } else {
                        validMove = false;
                    }
                }
            } else if (pieceName.contains("Rook")) {
          /*
            The Rook moves along the x direction or the y direction any number of squares as long as there
            is nothing in its way. It can take an opponents piece and as you know it cannot take its own piece.
          */
                Boolean intheway = false;
                if (((landingX < 0) || (landingX > 7)) || ((landingY < 0) || (landingY > 7))) {
                    validMove = false;
                } else if (!pieceMove(landingX, landingY)) {
                    validMove = false;
                } else {
              /*
                The following condition simply asks if we have either a movement in the x direction
                or in the y direction.
              */
                    if (((xMovement != 0) && (yMovement == 0)) || ((xMovement == 0) && (yMovement != 0))) {
                        if (xMovement != 0) { // we have a movement in the x direction
                            if (startX - landingX > 0) {
                    /*
                        We need to check all the squares up to the landing square to make sure nothing
                        is in the way.
                    */
                                for (int i = 0; i < xMovement; i++) {
                                    if (piecePresent(initialX - (i * 75), e.getY())) {
                                        intheway = true;
                                        break;
                                    } else {
                                        intheway = false;
                                    }
                                }
                            } else { // we have a movement in the y direction
                                for (int i = 0; i < xMovement; i++) {
                                    if (piecePresent(initialX + (i * 75), e.getY())) {
                                        intheway = true;
                                        break;
                                    } else {
                                        intheway = false;
                                    }
                                }
                            }
                        } else { // we must have a movement in the y direction
                            if (startY - landingY > 0) {
                                for (int i = 0; i < yMovement; i++) {
                                    if (piecePresent(e.getX(), initialY - (i * 75))) {
                                        intheway = true;
                                        break;
                                    } else {
                                        intheway = false;
                                    }
                                }
                            } else {
                                for (int i = 0; i < yMovement; i++) {
                                    if (piecePresent(e.getX(), initialY + (i * 75))) {
                                        intheway = true;
                                        break;
                                    } else {
                                        intheway = false;
                                    }
                                }
                            }
                        }
                        if (intheway) {
                  /*
                      if we have found that there is something in the way to the square we are trying to
                      move to we simply set the validMove boolean to false...
                  */
                            validMove = false;
                        } else {
                  /*
                      okay so we have established that there is nothing in the way, and we want to make try and
                      move the piece. The other condition that we need to check is to see if there is a piece at
                      the landing square that we want to move to.
                  */
                            if (piecePresent(e.getX(), (e.getY()))) {
                                if (pieceName.contains("White")) {
                                    if (checkWhiteOpponent(e.getX(), e.getY())) {
                                        validMove = true;
                        /*
                          Every time we make a take a piece we need to check if the piece that has been captured
                          is a King.
                        */

                                        if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                            winner = "White Wins!";
                                        }
                                    } else {
                                        validMove = false;
                                    }
                                } else {
                                    if (checkBlackOpponent(e.getX(), e.getY())) {
                                        validMove = true;
                                        if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                            winner = "Black Wins!";
                                        }
                                    } else {
                                        validMove = false;
                                    }
                                }
                            } else {
                                validMove = true;
                            }
                        }
                    } else {
                        validMove = false;
                    }
                }
            } else if (pieceName.contains("Knight")) {

          /*
              We know that the Knight can move in an L direction. It means that if the xMovement == 1 then the yMovement
              must equal 2 and also the other way around...
              We need to check the square that we are moving to and make sure that if there is a piece present
              that it's not our own piece...
          */
                if (((xMovement == 1) && (yMovement == 2)) || ((xMovement == 2) && (yMovement == 1))) {
                    if (!piecePresent(e.getX(), e.getY())) {
                        validMove = true;
                    } else {
                        if (pieceName.contains("White")) {
                            if (checkWhiteOpponent(e.getX(), e.getY())) {
                                validMove = true;
                                if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                    winner = "White Wins!";
                                }
                            }
                        } else {
                            if (checkBlackOpponent(e.getX(), e.getY())) {
                                validMove = true;
                                if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                    winner = "Black Wins!";
                                }
                            }
                        }
                    }
                }
            } else if (pieceName.contains("King")) {
                if ((xMovement == 0) && (yMovement == 0)) {
                    validMove = false;
                } else {
                    if (((landingX < 0) || (landingX > 7)) || ((landingY < 0) || (landingY > 7))) {
                        validMove = false;
                    } else {
                        if ((xMovement > 1) || (yMovement > 1)) {
                            validMove = false;
                        } else {
                /*
                    The Kind can only move one square at a time but also needs to make sure that there is at least one square between
                    both Kings once the move has concluded.
                */
                            if ((getPieceName((e.getX() + 75), e.getY()).contains("King")) || (getPieceName((e.getX() - 75), e.getY()).contains("King")) || (getPieceName((e.getX()), (e.getY() + 75)).contains("King")) || (getPieceName((e.getX()), (e.getY() - 75)).contains("King")) || (getPieceName((e.getX() + 75), (e.getY() + 75)).contains("King")) || (getPieceName((e.getX() - 75), (e.getY() + 75)).contains("King")) || (getPieceName((e.getX() + 75), (e.getY() - 75)).contains("King")) || (getPieceName((e.getX() - 75), (e.getY() - 75)).contains("King"))) {
                                validMove = false;
                            } else {
                  /*
                      We need to make sure that if there is a piece on the potential landing square that the King cannot take its
                      own piece but can take an opponents piece.
                  */
                                if (piecePresent(e.getX(), e.getY())) {
                                    if (pieceName.contains("White")) {
                                        if (checkWhiteOpponent(e.getX(), e.getY())) {
                                            validMove = true;
                                        }
                                    } else {
                                        if (checkBlackOpponent(e.getX(), e.getY())) {
                                            validMove = true;
                                        }
                                    }
                                } else {
                                    validMove = true;
                                }
                            }
                        }
                    }
                }//end of zero distance check
            } else if (pieceName.contains("Queen")) {
                boolean inTheWay = false;
                if (((landingX < 0) || (landingX > 7)) || ((landingY < 0) || (landingY > 7))) {
                    validMove = false;
                } else if (!pieceMove(landingX, landingY)) {
                    validMove = false;
                } else {
		          /*
			           The Queen can either move like the Rook or a Bishop...You already have this code working!
			           The first check is to see if the Queen is making a valid Bishop Like move...
		          */
                    if (xMovement == yMovement) {
                        if ((startX - landingX < 0) && (startY - landingY < 0)) {
                            for (int i = 0; i < xMovement; i++) {
                                if (piecePresent((initialX + (i * 75)), (initialY + (i * 75)))) {
                                    inTheWay = true;
                                }
                            }
                        } else if ((startX - landingX < 0) && (startY - landingY > 0)) {
                            for (int i = 0; i < xMovement; i++) {
                                if (piecePresent((initialX + (i * 75)), (initialY - (i * 75)))) {
                                    inTheWay = true;
                                }
                            }
                        } else if ((startX - landingX > 0) && (startY - landingY > 0)) {
                            for (int i = 0; i < xMovement; i++) {
                                if (piecePresent((initialX - (i * 75)), (initialY - (i * 75)))) {
                                    inTheWay = true;
                                }
                            }
                        } else if ((startX - landingX > 0) && (startY - landingY < 0)) {
                            for (int i = 0; i < xMovement; i++) {
                                if (piecePresent((initialX - (i * 75)), (initialY + (i * 75)))) {
                                    inTheWay = true;
                                }
                            }
                        }

                        if (inTheWay) {
                            validMove = false;
                        } else {
                  /*
                    If there is nothing in the way when moving the Queen like a Bishop, we have two cases:
                      - there is a piece on the square we are trying to move to, and we need to make sure that it's
                        an enemy piece
                      - or there is nothing in the way and everything is cool
                  */
                            if (piecePresent(e.getX(), (e.getY()))) {
                                if (pieceName.contains("White")) {
                                    if (checkWhiteOpponent(e.getX(), e.getY())) {
                                        validMove = true;
                                        if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                            winner = "White Wins!";
                                        }
                                    }
                                } else { // We are moving the Black Queen...
                                    if (checkBlackOpponent(e.getX(), e.getY())) {
                                        validMove = true;
                                        if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                            winner = "Black Wins!";
                                        }
                                    }
                                }
                            } else {
                                validMove = true;
                            }
                        }
                    } // end of the condition where the Queen is moving like a Bishop
                    else if (((xMovement != 0) && (yMovement == 0)) || ((xMovement == 0) && (yMovement != 0))) {
                /*
                    This is the condition that handles the Queen movements that are moving like a Rook
                */
                        if (xMovement != 0) {
                            //we have movement along the x-axis
                            if (startX - landingX > 0) {
                                //movement in the left direction....
                                for (int i = 0; i < xMovement; i++) {
                                    if (piecePresent(initialX - (i * 75), e.getY())) {
                                        inTheWay = true;
                                        break;
                                    } else {
                                        inTheWay = false;
                                    }
                                }
                            } else {
                                for (int i = 0; i < xMovement; i++) {
                                    if (piecePresent(initialX + (i * 75), e.getY())) {
                                        inTheWay = true;
                                        break;
                                    } else {
                                        inTheWay = false;
                                    }
                                }
                            }
                        }// This is the end of the xMovement condition
                        else {
                            //we have movement along the y-axis
                            if (startY - landingY > 0) {
                                //movement in the left direction....
                                for (int i = 0; i < yMovement; i++) {
                                    if (piecePresent(e.getX(), initialY - (i * 75))) {
                                        inTheWay = true;
                                        break;
                                    } else {
                                        inTheWay = false;
                                    }
                                }
                            } else {
                                for (int i = 0; i < yMovement; i++) {
                                    if (piecePresent(e.getX(), initialY + (i * 75))) {
                                        inTheWay = true;
                                        break;
                                    } else {
                                        inTheWay = false;
                                    }
                                }
                            }
                        }

                        if (inTheWay) {
                            validMove = false;
                        } else {
                  /*
                    This is cool and just like before there is now nothing in the way and the Queen should be able to make the
                    movement. We need to check if there is something on the square that we are moving to and if so make sure it's
                    an enemy piece and if there is nothing on the square that we are moving to ... make the move.
                  */
                            if (piecePresent(e.getX(), (e.getY()))) {
                                if (pieceName.contains("White")) {
                                    if (checkWhiteOpponent(e.getX(), e.getY())) {
                                        validMove = true;
                                        if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                            winner = "White Wins!";
                                        }
                                    }
                                } else {
                                    if (checkBlackOpponent(e.getX(), e.getY())) {
                                        validMove = true;
                                        if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                            winner = "Black Wins!";
                                        }
                                    }
                                }
                            } else {
                                validMove = true;
                            }
                        }
                    }
                }
            } else if (pieceName.equals("BlackPawn")) {
                if (startY == 6) { // This is where the pawn is making it's first move....
            /*
            if the pawn is making its first movement....
            the pawn can either move one square or two squares...in the Y direction
            as long as we are moving up the board!!! and also there is no movement in the
            x direction
            */
                    if (((yMovement == 1) || (yMovement == 2)) && (startY > landingY) && (xMovement == 0)) {
                        if (yMovement == 2) {
                            if ((!piecePresent(e.getX(), e.getY())) && (!piecePresent(e.getX(), (e.getY() + 75)))) {
                                validMove = true;
                            }
                        } else {
                            if (!piecePresent(e.getX(), e.getY())) {
                                validMove = true;
                            }
                        }
                    } else if ((yMovement == 1) && (startY > landingY) && (xMovement == 1)) {
                        if (piecePresent(e.getX(), e.getY())) {
                            if (checkBlackOpponent(e.getX(), e.getY())) {
                                validMove = true;
                                if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                    winner = "Black Wins!";
                                }
                            }
                        }
                    }
                } else {// This is where the pawn is making all subsequent moves...
                    if (((yMovement == 1)) && (startY > landingY) && (xMovement == 0)) {
                        if (!piecePresent(e.getX(), e.getY())) {
                            validMove = true;
                /*
                  We need a variable to inform us that the Black Pawn has reached the top
                  of the board and can now turn into a Black Queen
                */
                            if (landingY == 0) {
                                progression = true;
                            }
                        }
                    } else if ((yMovement == 1) && (startY > landingY) && (xMovement == 1)) {
                        if (piecePresent(e.getX(), e.getY())) {
                            if (checkBlackOpponent(e.getX(), e.getY())) {
                                validMove = true;
                                if (landingY == 0) {
                                    progression = true;
                                }
                                if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                    winner = "Black Wins!";
                                }
                            }
                        }
                    }
                }
            } else if (pieceName.equals("WhitePawn")) {
                if (startY == 1) {
                    if (((xMovement == 0)) && ((yMovement == 1) || ((yMovement) == 2))) {
                        if (yMovement == 2) {
                            if ((!piecePresent(e.getX(), (e.getY()))) && (!piecePresent(e.getX(), (e.getY() - 75)))) {
                                validMove = true;
                            }
                        } else {
                            if ((!piecePresent(e.getX(), (e.getY())))) {
                                validMove = true;
                            }
                        }
                    } else if ((piecePresent(e.getX(), e.getY())) && (xMovement == yMovement) && (xMovement == 1) && (startY < landingY)) {
                 /*
                    This is the condition were we are checking for a move to take a piece when the white pawn is making its
                    first move. We check the following:
                      - that there is a piece present,
                      - we are moving in a diagonal,
                      - the movement is only one square,
                      - we are moving down the board
                 */
                        if (checkWhiteOpponent(e.getX(), e.getY())) {
                            validMove = true;
                            if (startY == 6) {
                                success = true;
                            }
                            if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                winner = "White Wins!";
                            }
                        }
                    }
                } // End of the movements for when the pawn is making its first move.
                else {
                    if ((startX - 1 >= 0) || (startX + 1 <= 7)) {
                        if ((piecePresent(e.getX(), e.getY())) && (xMovement == yMovement) && (xMovement == 1)) {
                            if (checkWhiteOpponent(e.getX(), e.getY())) {
                                validMove = true;
                                if (startY == 6) {
                                    success = true;
                                }
                                if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                    winner = "White Wins!";
                                }
                            }
                        }// checking the case for when there is a piecePresent
                        else {
                            if (!piecePresent(e.getX(), (e.getY()))) {
                                if (((xMovement == 0)) && ((e.getY() / 75) - startY) == 1) {
                                    if (startY == 6) {
                                        success = true;
                                    }
                                    validMove = true;
                                }
                            }
                        }
                    }
                }
            }
        } //end of possible.

        if (!validMove) {
            int location = 0;
            if (startY == 0) {
                location = startX;
            } else {
                location = (startY * 8) + startX;
            }
            String pieceLocation = pieceName + ".png";
            pieces = new JLabel(new ImageIcon(pieceLocation));
            panels = (JPanel) chessBoard.getComponent(location);
            panels.add(pieces);
        } else { // this is the condition where we have a valid move and need to visually show it.
            white2Move = !white2Move;

            if (progression) {
                int location = 0 + (e.getX() / 75);
                if (c instanceof JLabel) {
                    Container parent = c.getParent();
                    parent.remove(0);
                    pieces = new JLabel(new ImageIcon("BlackQueen.png"));
                    parent = (JPanel) chessBoard.getComponent(location);
                    parent.add(pieces);
                }
                if (winner != null) {
                    JOptionPane.showMessageDialog(null, winner);
                    System.exit(0);
                }
            } else if (success) {
                int location = 56 + (e.getX() / 75);
                if (c instanceof JLabel) {
                    Container parent = c.getParent();
                    parent.remove(0);
                    pieces = new JLabel(new ImageIcon("WhiteQueen.png"));
                    parent = (JPanel) chessBoard.getComponent(location);
                    parent.add(pieces);
                } else {
                    Container parent = (Container) c;
                    pieces = new JLabel(new ImageIcon("WhiteQueen.png"));
                    parent = (JPanel) chessBoard.getComponent(location);
                    parent.add(pieces);
                }
            } else {
                if (c instanceof JLabel) {
                    Container parent = c.getParent();
                    parent.remove(0);
                    parent.add(chessPiece);
                } else {
                    Container parent = (Container) c;
                    parent.add(chessPiece);
                }
                chessPiece.setVisible(true);
                if (winner != null) {
                    JOptionPane.showMessageDialog(null, winner);
                    System.exit(0);
                }
            }
            makeAIMove();
        } // end of the else condition
    }

    public void mouseClicked(MouseEvent e) { }

    public void mouseMoved(MouseEvent e) { }

    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }

    /*
        Main method that gets the ball moving.
    */
    public static void main(String[] args) {
        ChessProject frame = new ChessProject();
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        Object[] options = {"Random Moves", "Best First Search - Best Next Move", "Two Levels Deep - Based on Opponents Moves"};
        int n = JOptionPane.showOptionDialog(frame, "Lets play some Chess, choose your AI opponent", "Introduction Tegan's Chess AI", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
        System.out.println("The selected variable is : " + n);
        frame.makeAIMove();
    }
}
