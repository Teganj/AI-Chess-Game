class Move{
  Square start;
  Square landing;
  int pieceResultWeight;

  public Move(Square x, Square y){
    start = x;
    landing = y;
  }

  public Move(Square x, Square y, int scr){
    start = x;
    landing = y;
    pieceResultWeight = scr;
    System.out.println("==================================");
    System.out.print("Piece being Moved:" + start.pieceName + "\n");
    System.out.print("From X:" + x.xCoor + " Y:" + x.yCoor + "\n");
    System.out.print("To X:" + y.xCoor + " Y:" + y.yCoor + "\n");
  }

  public Move(){}

  public Square getStart(){
    return start;
  }

  public Square getLanding(){
    return landing;
  }

  public int getResult(){
    return pieceResultWeight;
  }
}
