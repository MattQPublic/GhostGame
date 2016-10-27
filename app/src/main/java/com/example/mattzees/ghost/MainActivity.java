package com.example.mattzees.ghost;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    // Define variables
    int activePlayer = 1;    	    // 1 = Papa, 2 = Ghouls
    int displaySymbol = 0;	        // 0 = Cross, 1 = Papa, 2 = Ghouls
    double defaultAlpha = 0.40; 	// Default alpha level for the Cross Icon on the game board.
    boolean gameIsActive = true;	// Is the game active (is it not yet a win or a draw)?
    boolean gameIsWon = false;	    // If game is over, has it been won?
    int winnerOfGame = 0;       	// If game has been won, who won it?  1 = Papa, 2 = Ghouls
    int numberOfMoves = 0;	        // Can't be over 9.  If no winner by 9, then it's a draw.
    int chantCounter = 0;	        // This varies the chant played each time a move is made.  No need to reset this on new game.
    int playerOneBGCounter = 0;	    // Used to cycle through Player One's backround images.  No need to reset this on new game.
    int playerTwoBGCounter = 0;	    // Used to cycle through Player Two's backround images.  No need to reset this on new game.

    // Used to dim the squares occupied by the losing player.
    int[] winningPatten = {0, 0, 0};

    // Array of filenames used to cycle through audio files each time a move is made.
    int[] chantArray = { R.raw.ghost01, R.raw.ghost02, R.raw.ghost03, R.raw.ghost04, R.raw.ghost05, R.raw.ghost06 };

    //Array of Player One backround images.
    int[] playerOneBGArray = { R.drawable.papabg00, R.drawable.papabg01,  R.drawable.papabg02,  R.drawable.papabg03,  R.drawable.papabg04,
            R.drawable.papabg05, R.drawable.papabg06,  R.drawable.papabg07,  R.drawable.papabg08,  R.drawable.papabg09,  R.drawable.papabg10};

    //Array of Player Two backround images.
    int[] playerTwoBGArray = { R.drawable.ghoulbg00, R.drawable.ghoulbg01, R.drawable.ghoulbg02, R.drawable.ghoulbg03, R.drawable.ghoulbg04,
            R.drawable.ghoulbg05, R.drawable.ghoulbg06};

    // 2D array of INTs use to initialize the gameboard.  0 = empty, 1 = Papa, 2 = Ghouls
    int[] gameBoard = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    // Defines a the eight possible winning game patterns, which is used to evaluate if game has been won.
    int[][] winningPositions = {{0,1,2}, {3,4,5}, {6,7,8}, {0,3,6}, {1,4,7}, {2,5,8}, {0,4,8}, {2,4,6}};

    // This will be used to play all our songs and chants.
    MediaPlayer mplayer;



    // User clicks on a square, and we do the following:
    public void squareClicked(View view) {

        ImageView activeSquareView = (ImageView) view;

        // This gives us the number of the square that was clicked.
        int activeSquare = Integer.parseInt(activeSquareView.getTag().toString());

        // If the game is still active AND square is not already occupied, then keep going, otherwise ignore the click.
        if (gameIsActive == true && gameBoard[activeSquare] == 0) {
            System.out.println("Active Player = " + activePlayer);
            // Record move in the gameBoard array.
            gameBoard[activeSquare] = activePlayer;

            // Update number of moves made.
            numberOfMoves = numberOfMoves + 1;

            // Game cannot be finished in less than five moves.
            // If game has gone on for five or more moves, then evaluate whether game is still active.
            if (numberOfMoves > 4) {

                // Interate through array of the eight possible winning patterns.
                for (int[] winningPosition : winningPositions) {

                    // Evaluate each possible winning pattern.
                    // Does same player have all three squares? AND none of the squares are empty.
                    if (gameBoard[winningPosition[0]] == gameBoard[winningPosition[1]] &&
                            gameBoard[winningPosition[1]] == gameBoard[winningPosition[2]] &&
                            gameBoard[winningPosition[0]] != 0) {

                        // Somebody has won the game!
                        gameIsActive = false;
                        gameIsWon = true;
                        winnerOfGame = activePlayer;
                        winningPatten = winningPosition;
                    }
                }
            }

            // If game has gone on for nine moves, and there are no winners, then it must be a draw.
            if (numberOfMoves > 8 && gameIsWon == false) {

                // Call it a draw.
                gameIsActive = false;
                gameIsWon = false;
                winnerOfGame = 0;

            }

            // Take results of the above logic, and display them to the user.
            // First we update the symbol in the clicked square.
            switch (gameBoard[activeSquare]) {

                case 0:	activeSquareView.setImageResource(R.drawable.cross_icon);
                    activeSquareView.animate().alpha(1f).setDuration(500);
                    break;
                case 1:	activeSquareView.setImageResource(R.drawable.papa_icon);
                    activeSquareView.animate().alpha(1f).setDuration(500);
                    break;
                case 2:	activeSquareView.setImageResource(R.drawable.ghoul_icon);
                    activeSquareView.animate().alpha(1f).setDuration(500);
                    break;

            }

            // Next, if game is still active, play one of six cyclical chants, and toggle activePlayer.
            if (gameIsActive == true) {

                playChant();
                togglePlayer();
                // TO DO: Toggle backround images for each player!


            }

            // If game is not active, and has been WON, then do the following.
            if (gameIsActive == false && gameIsWon == true) {

                // Play win song.
                playSong(winnerOfGame);
                gameOver(winnerOfGame);

            }

            // If game is not active, and is a DRAW, then do the following.
            if (gameIsActive == false && gameIsWon == false) {

                // Play draw song.
                playSong(winnerOfGame);
                gameOver(winnerOfGame);

            }
        }
    }



    // Displays the appropriate "Game Over" images, text message and "Play" button.
    public void gameOver(int winner) {

        // Crossfade appropriate image of the winning player
        ImageView victoriousBG = (ImageView) findViewById(R.id.winnerBGView);
        ImageView playerOne = (ImageView) findViewById(R.id.playerOneBGView);
        ImageView playerTwo = (ImageView) findViewById(R.id.playerTwoBGView);

        switch (winner) {

            case 0:	// This is a draw.  Continue to display default BG.
                playerOne.animate().alpha(0f).setDuration(1000);
                playerTwo.animate().alpha(0f).setDuration(1000);
                break;
            case 1: // Papa wins!  Crossfade to Player One BG.
                victoriousBG.setImageResource(R.drawable.papa_wins_bg);
                victoriousBG.animate().alpha(1f).setDuration(1000);
                playerOne.animate().alpha(0f).setDuration(1000);
                playerTwo.animate().alpha(0f).setDuration(1000);
                break;

            case 2: // Nameless Ghouls win!  Crossfade to Player Two BG.
                victoriousBG.setImageResource(R.drawable.ghoul_wins_bg);
                victoriousBG.animate().alpha(1f).setDuration(1000);
                playerTwo.animate().alpha(0f).setDuration(1000);
                playerOne.animate().alpha(0f).setDuration(1000);
                break;
        }


        // If game is won, dim the spaces occupied by the losing player by setting them to defaultAlpha.
        if (gameIsWon == true) {

            GridLayout gridLayout = (GridLayout) findViewById(R.id.gridLayout);

            for (int i = 0; i < gameBoard.length; i++) {

                if (winningPatten[0] != i && winningPatten[1] != i && winningPatten[2] != i && gameBoard[i] != 0) {

                    (gridLayout.getChildAt(i)).animate().alpha((float) defaultAlpha).setDuration(500);

                }
            }
        } else {
        // If game is not won, dim all the spaces by setting them to defaultAlpha.
            GridLayout gridLayout = (GridLayout) findViewById(R.id.gridLayout);

            for(int i = 0; i < gameBoard.length; i++) {
                (gridLayout.getChildAt(i)).animate().alpha((float)defaultAlpha).setDuration(500);
            }

        }


        // Display "Play Again" button
        LinearLayout layout = (LinearLayout)findViewById(R.id.resetTheGameLayout);
        layout.setVisibility(View.VISIBLE);

    }



    // Plays a chant everytime player makes a move.  If we reach the end of the chant array, start again at zero.
    public void playChant() {

        mplayer = MediaPlayer.create(this, chantArray[chantCounter]);
        mplayer.start();

        chantCounter = chantCounter + 1;

        if (chantCounter > (chantArray.length - 1)){

            chantCounter = 0;

        }
    }



    // Plays a song when game is over.
    public void playSong(int song) {

        mplayer.stop();

        switch (song) {

            case 0: mplayer = MediaPlayer.create(this, R.raw.spoksonat);
                mplayer.start();
                break;
            case 1: mplayer = MediaPlayer.create(this, R.raw.squarehammer);
                mplayer.start();
                break;
            case 2: mplayer = MediaPlayer.create(this, R.raw.yearzero);
                mplayer.start();
                break;
        }
    }



    // Toggles value of activePlayer variable.  Toggles player backrounds through their respective image arrays.
    public void togglePlayer() {

        ImageView playerOne = (ImageView) findViewById(R.id.playerOneBGView);
        ImageView playerTwo = (ImageView) findViewById(R.id.playerTwoBGView);

        if (activePlayer == 1) {

            playerOne.setImageResource(playerOneBGArray[playerOneBGCounter]);
            playerOne.animate().alpha(1f).setDuration(1000);
            playerTwo.animate().alpha(0f).setDuration(1000);

            playerOneBGCounter = playerOneBGCounter + 1;

            if (playerOneBGCounter > (playerOneBGArray.length -1)) {

                playerOneBGCounter = 0;

            }

        }

        if (activePlayer == 2) {

            playerTwo.setImageResource(playerTwoBGArray[playerTwoBGCounter]);
            playerTwo.animate().alpha(1f).setDuration(1000);
            playerOne.animate().alpha(0f).setDuration(1000);

            playerTwoBGCounter = playerTwoBGCounter + 1;

            if (playerTwoBGCounter > (playerTwoBGArray.length - 1)) {

                playerTwoBGCounter = 0;

            }
        }

        if (activePlayer == 1) {

            activePlayer = 2;

        } else {

            activePlayer = 1;

        }
    }



    // Set variables to default values.  This is used when game is started or reset.
    public void resetTheGame(View view) {
        activePlayer = 1;	// 1 = Papa makes the first move.
        displaySymbol = 0;	// 0 = Cross is the default symbol.
        gameIsActive = true;	// Game is now started.
        gameIsWon = false;	// Game has not been won yet.
        winnerOfGame = 0;	// There is no winner yet.
        numberOfMoves = 0;	// There have been no moves yet.

        // Hide the PLAY GAME button.
        LinearLayout layout = (LinearLayout)findViewById(R.id.resetTheGameLayout);
        layout.setVisibility(View.INVISIBLE);

        // Iterate through the game board and reset each square to default values.

        GridLayout gridLayout = (GridLayout)findViewById(R.id.gridLayout);

        for(int i = 0; i < gameBoard.length; i++) {
            gameBoard[i] = 0;
            ((ImageView) gridLayout.getChildAt(i)).setImageResource(R.drawable.cross_icon);
            (gridLayout.getChildAt(i)).animate().alpha((float)defaultAlpha).setDuration(0);
        }

        // Fade out player and winner backrounds, leaving the game backround displayed.
        ImageView victoriousBG = (ImageView) findViewById(R.id.winnerBGView);
        victoriousBG.animate().alpha(0f).setDuration(2000);
        ImageView playerOne = (ImageView) findViewById(R.id.playerOneBGView);
        playerOne.animate().alpha(0f).setDuration(2000);
        ImageView playerTwo = (ImageView) findViewById(R.id.playerTwoBGView);
        playerTwo.animate().alpha(0f).setDuration(2000);


        // Stop sounds from previous game, is any.
        mplayer.stop();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
