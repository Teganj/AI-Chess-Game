import java.util.*;

public class AIAgent {
    Random rand;
    String landingPieceName;
    int landingPieceScore;
    int pieceScore = 0;
    Move newBestMove;

    public AIAgent() { rand = new Random(); }

    /*
        The method randomMove takes as input a stack of potential moves that the AI agent
        can make. The agent uses a random number generator to randomly select a move from
        the inputted Stack and returns this to the calling agent.
    */
    public Move randomMove(Stack possibilities) {
        int moveID = rand.nextInt(possibilities.size());
        System.out.println("Agent randomly selected move : " + moveID);
        for (int i = 1; i < (possibilities.size() - (moveID)); i++) {
            possibilities.pop();
        }
        Move selectedMove = (Move) possibilities.pop();
        return selectedMove;
    }

    /*
     * From the current position, check best valid move.
     */
    public Move nextBestMove(Stack possibilities) {
        Move bestMove;
        Move possibleBestMove = new Move();
        Stack movesStack = new Stack();
        Stack initialMoves = (Stack) possibilities.clone();
        for (int i = 0; i < initialMoves.size(); i++) {
            bestMove = (Move) possibilities.pop();
            Square landingSquare = bestMove.getLanding();
            int yPos = landingSquare.getYC();
            landingPieceName = landingSquare.getName();
            landingPieceScore = bestMove.pieceResultWeight;
            if ((landingPieceScore == 1) && (yPos == 3 || yPos == 4)) { movesStack.push(bestMove); }
            if ((landingPieceScore == 2)) { movesStack.clear(); movesStack.push(bestMove); }
            if ((landingPieceScore == 3)) { movesStack.clear(); movesStack.push(bestMove); }
            if ((landingPieceScore == 5)) { movesStack.push(bestMove); }
            if ((landingPieceScore == 9)) { movesStack.clear(); movesStack.push(bestMove); }
            if (landingPieceScore == 100) { movesStack.clear(); movesStack.push(bestMove); }
        }
        if (!movesStack.empty()) {
            for (int i = 0; i < (movesStack.size()); i++) {
                Move latestFilteredMove = (Move) movesStack.pop();
                System.out.println(movesStack.elements());
                if (latestFilteredMove.pieceResultWeight >= pieceScore) {
                    newBestMove = latestFilteredMove;
                    pieceScore = latestFilteredMove.pieceResultWeight;
                    System.out.println("Best Move has been Found!");
                    System.out.println("Start X: " + newBestMove.start.xCoor + "Start Y: " + newBestMove.start.yCoor);
                    System.out.println("Land X: " + newBestMove.landing.xCoor + "Land Y: " + newBestMove.landing.yCoor);
                }
                System.out.println(pieceScore);
                System.out.println(landingPieceScore);
            }
            pieceScore = 0;
            return newBestMove;
        } else {
            for (int i = 1; i < (initialMoves.size()); i++) {
                initialMoves.pop();
            }
            possibleBestMove = (Move) initialMoves.pop();
            return possibleBestMove;
        }

    }

    /*
     * From the current position, check valid moves and for each valid move, check next valid move.
     */
    public Move twoLevelsDeep(Stack whitePossibilities, Stack blackPossibilities) {
        Stack whiteMovePossibilities = (Stack) whitePossibilities.clone();
        Stack blackMovePossibilities = (Stack) blackPossibilities.clone();
        Move whitePlayerMovement = null;
        Move blackPlayerMovement = null;
        Square whitePosition;
        Square blackPosition;
        Stack filteredMoves = new Stack();
        int bestBlackScore = 0;
        int AIResult = 0;
        int bestAIResult = -100;
        while (!blackMovePossibilities.isEmpty()) {
            blackPlayerMovement = (Move) blackMovePossibilities.pop();
            blackPosition = blackPlayerMovement.getLanding();
            int blackLandingPieceScore = blackPlayerMovement.pieceResultWeight;
            /*
            if for getting the highest white score
             */
            if (bestBlackScore < blackLandingPieceScore) {
                bestBlackScore = blackLandingPieceScore;
            }

            /*
            While white stack is not empty
             */
            while (!whiteMovePossibilities.isEmpty()) {
                whitePlayerMovement = (Move) whiteMovePossibilities.pop();
                whitePosition = whitePlayerMovement.getLanding();
                int whiteLandingPieceScore = whitePlayerMovement.pieceResultWeight;
                AIResult = whiteLandingPieceScore - bestBlackScore;
                if (AIResult >= bestAIResult) {
                    if (AIResult != bestAIResult) {
                        filteredMoves.clear();
                    }
                    bestAIResult = AIResult;
                    filteredMoves.add(blackPlayerMovement);
                    return randomMove(filteredMoves);
                }
            }
        }
        return nextBestMove(whitePossibilities);
    }
}
