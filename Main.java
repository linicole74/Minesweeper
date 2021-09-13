/*
 * Nicole Li, Emily Zhao, Vani Arora, Annika Le, Abby Levit
 * October 8, 2019
 * AP CSA Freebuild
 * Main
 */

package li.five;

// <Nicole Li and Emily Zhao>
public class Main {
              
    public static void main(String[] args) {
    	
    	// <Nicole Li>
    	// Creates a MineField to play on.
    	MineField mines = new MineField();
    	
    	// Asks the user for the dimensions of the MineField.
    	mines.getDimensions();
    	
    	// Plays games over and over again until the user closes the window.
    	while (mines.getGameState() == "start") {
    		
    		// Sets up the MineField.
        	mines.setup();
        	
        	// Waits until generation can be started.
        	// The condition for being able to start generation is the clicking of one square.
        	while (!mines.getCanStartGeneration()) {
        		try {
        		    Thread.sleep(200);
        		}
        		catch(InterruptedException e) {}
        	}
        	
        	// Prints "regenerating..." until an usable board is generated.
        	do {
        		mines.generate();
        		System.out.println("regenerating...");
        	}
        	while (mines.getSelectedMineFieldSquare() != 0);
        	System.out.println("generated");
        	
        	// Starts the GUI's actions after the first click and generation have been completed.
        	mines.guiStart();
        	// </Nicole Li>
        	
        	// <Emily Zhao>
        	// Waits until the player wins or loses the game.
        	while (mines.getGameState() == "start") {
        		try {
        		    Thread.sleep(200);
        		}
        		catch(InterruptedException e) {}
        	}
        	
        	// Waits until the player leaves the win/lose screen.
        	while (mines.getGameState() == "win" || mines.getGameState() == "lose") {
        		try {
        		    Thread.sleep(200);
        		}
        		catch(InterruptedException e) {}
        	}
        	// </Emily Zhao>
    	}
    }
}
// </Nicole Li and Emily Zhao>