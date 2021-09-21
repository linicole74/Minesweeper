/*
 * Nicole Li, Emily Zhao, Vani Arora, Annika Le, Abby Levit
 * October 8, 2019
 * AP CSA Freebuild
 * MineField
 */

package li.five;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JButton;  
import javax.swing.JFrame;
import javax.swing.SwingUtilities;  

public class MineField implements IMinesweeper {
	
	// Variables
	private int x;							// number of horizontal squares
	private int y;							// number of vertical squares
	private int numMines;					// total number of mines
	private int[][] mineField;				// the "answer key" to the mines
	private int[][] playerField;			// values displayed on the GUI 
											// (9 is mine and -1 is unclicked)
	private String gameState;				// the stage of the game
											// ("start", "win", or "lose")
	private int selectedX;					// the horizontal square the player has chosen
	private int selectedY;					// the vertical square the player has chosen
	private JFrame window;					// window for the GUI
	private JButton[] buttons;				// array of square buttons for the GUI
	private int squareSide;					// side length of the squares
	private boolean canStartGeneration;		// whether mines are to be generated at the moment
	private JButton numMinesDisplay;		// button that displays the number of mines
	
	// <Annika Le>
	// Asks the user for the number of squares horizontally and vertically
	public void getDimensions() {
		this.gameState = "start";
		
		// Asks the user for the width.
		this.x = -1;
		while (x == -1) {
			try {
				System.out.print("width ");
				// Gets user input.
				BufferedReader inputWidth = new BufferedReader(new InputStreamReader(System.in));
				int width = Integer.parseInt(inputWidth.readLine());
				if (width < 5) {
					System.out.println("Please enter an integer equal to or greater than 5.");
				}
				else this.x = width;
			}
			catch (Exception e) {
				System.out.println("Please enter an integer.");
			}
		}
		
		// Asks the user for the height.
		this.y = -1;
		while (y == -1) {
			try {
				System.out.print("height ");
				// Gets user input.
				BufferedReader inputWidth = new BufferedReader(new InputStreamReader(System.in));
				int height = Integer.parseInt(inputWidth.readLine());
				if (height < 5) {
					System.out.println("Please enter an integer equal to or greater than 5.");
				}
				else this.y = height;
			}
			catch (Exception e) {
				System.out.println("Please enter an integer.");
			}
		}
	}
	
	// Makes all of the mines visible to the player.
	// Used when player loses.
	public void showMines() {
		for (int i = 0; i < this.y; i++) {
			for (int j = 0; j < this.x; j++) {
				if (this.mineField[i][j] == 9) {
					setSelectedPlayerFieldSquareToMineField(i, j);
				}	
			}
		}
	}
	
	// Checks if the player has won.
	public boolean checkWin() {
		int countSquares = 0;
		
		// Counts the number of unclicked squares.
		for (int i = 0; i < this.y; i++) {
			for (int j = 0; j < this.x; j++) {
				if (this.playerField[i][j] == -1) {
					countSquares++;
				}
			}
		}
		// Compares the number of unclicked squares to the number of mines.
		if (countSquares - 1 == this.numMines) {
			return true;
		}
		else return false;
	}
	
	// Creates the minefield but does not generate numbers.
	public void setup() {
		
		// Creates an unclicked playerField (unclicked meaning -1 for all values).
		this.playerField = new int[this.y][this.x];
		for (int i = 0; i < this.y; i++) {
			for (int j = 0; j < this.x; j++) {
				this.playerField[i][j] = -1;
			}
		}
		
		// Creates the window.
		this.window = new JFrame();
		this.window.pack();
		
		// Gets the dimensions of the screen minus the taskbar.
		int screenWidth = (int) GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
		int screenHeight = (int) GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height - window.getInsets().top - 100;
		
		// Calculates the size the squares should be.
		int possibleSide1 = screenWidth / this.x;
		int possibleSide2 = screenHeight / this.y;
		if (possibleSide1 < possibleSide2) {
			this.squareSide = possibleSide1;
		}
		else {
			this.squareSide = possibleSide2;
		}
		
		// Creates empty squares for the grid.
		this.buttons = new JButton[this.x * this.y];
		this.canStartGeneration = false;
		
		// Creates a button to display the number of mines.
		this.numMinesDisplay = new JButton("Mines: ");
		this.numMinesDisplay.setBounds(0, 0, this.squareSide * this.x, 100);
		window.add(this.numMinesDisplay);
		
		// Creates the squares.
		for (int i = 0; i < this.y; i++) {
			for (int j = 0; j < this.x; j++) {
				this.buttons[i * this.x + j] = new JButton();
				this.buttons[i * this.x + j].setBounds(j * this.squareSide, i * this.squareSide + 100, this.squareSide, this.squareSide);
				this.buttons[i * this.x + j].setMargin(new Insets(0, 0, 0, 0));
		        window.add(this.buttons[i * this.x + j]);
		        
		        // Listens for the first click of a square when the window loads.
		        this.buttons[i * this.x + j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) { 
		            	selectedX = ((Component) e.getSource()).getLocation().x / squareSide;
		            	selectedY = (((Component) e.getSource()).getLocation().y - 100) / squareSide;
		            	canStartGeneration = true;
		            } 
		        });
			}
		}
		
		// Sets the window to fullscreen.
		this.window.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		
		// Makes the window use absolute positioning.
		this.window.setLayout(null);
		
		// Makes the window visible.
		this.window.setVisible(true);  
                  
		// Makes the window closable.
		this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	// Starts the game after the first click.
	public void guiStart() {
		
		// Allows the effect of the first click to take place.
		setSelectedPlayerFieldSquareToMineField();
		
		// Displays the number of mines.
		this.numMinesDisplay.setText("Mines: " + this.numMines);
		
		// Restarts the game if the mines displayer is clicked after a game is won or lost.
		this.numMinesDisplay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
            	if (gameState == "win" || gameState == "lose") {
            		gameState = "start";
            	}
            } 
        });
		
		// Creates the squares' reactions to player clicks.
		for (int i = 0; i < this.y; i++) {
			for (int j = 0; j < this.x; j++) {
				
				// Listens for a right mouse click to flag a square.
				this.buttons[i * this.x + j].addMouseListener(new MouseAdapter(){
					public void mousePressed(MouseEvent e) {
						if (SwingUtilities.isRightMouseButton(e)) {
							// Flags a square by changing it to "F".
							((JButton) e.getSource()).setText("F");
	                    }
	                }
				});
				
		        // Listens for a left mouse click to reveal a square.
		        this.buttons[i * this.x + j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) { 
						if (gameState == "start") {
			            	
							// Sets selectedX and selectedY to the square the player selected.
							selectedX = ((Component) e.getSource()).getLocation().x / squareSide;
			            	selectedY = (((Component) e.getSource()).getLocation().y - 100) / squareSide;

			            	// Determines if win or lose conditions have been met.
			            	if (mineField[selectedY][selectedX] == 9) {
				            	numMinesDisplay.setText("You lost. Click here to play again.");
				            	gameState = "lose";
				            	showMines();
			            	}
			            	else if (checkWin()) {
				            	numMinesDisplay.setText("You won. Click here to play again.");
				            	gameState = "win";
			            	}
			            	
			            	// Reveals the value of the square clicked.
			            	setSelectedPlayerFieldSquareToMineField();
						}
		            } 
		        });
			}
		}
	}
	
	// Reveals groups of squares of zero and their bordering numbers touching the selected square.
	public void revealZeros(int yCoord, int xCoord) {
		
		// Reveals the currently selected square.
		setSelectedPlayerFieldSquareToMineFieldSimplified(yCoord, xCoord);
		
		// Reveals the group of squares.
		if (this.mineField[this.selectedY][this.selectedX] == 0) {
			for (int i = this.selectedY - 1; i < this.selectedY + 1; i++) {
				
				// Tests bordering squares of the selected square and bordering squares of bordering squares.
				for (int j = this.selectedX - 1; j < this.selectedX + 1; j++) {
					try {
						if (this.mineField[i - 1][j] != 9) {
							setSelectedPlayerFieldSquareToMineFieldSimplified(i - 1, j);
							this.selectedY--;
						}
					}
					catch (Exception e) {}
					try { 
						if (this.mineField[i - 1][j - 1] != 9) {
							setSelectedPlayerFieldSquareToMineFieldSimplified(i - 1, j - 1);
							this.selectedY--;
							this.selectedX--;
						}
					}
					catch (Exception e) {}
					try {
						if (this.mineField[i - 1][j + 1] != 9) {
							setSelectedPlayerFieldSquareToMineFieldSimplified(i - 1, j + 1);
							this.selectedY--;
							this.selectedX++;
						}
					}
					catch (Exception e) {}
					try { 
						if (this.mineField[i][j - 1] != 9) {
							setSelectedPlayerFieldSquareToMineFieldSimplified(i, j - 1);
							this.selectedX--;
						}
					}
					catch (Exception e) {}
					try { 
						if (this.mineField[i][j + 1] != 9) {
							setSelectedPlayerFieldSquareToMineFieldSimplified(i, j + 1);
							this.selectedX++;
						}
					}
					catch (Exception e) {}
					try {
						if (this.mineField[i + 1][j - 1] != 9) {
							setSelectedPlayerFieldSquareToMineFieldSimplified(i + 1, j - 1);
							this.selectedY++;
							this.selectedX--;
						}
					}
					catch (Exception e) {}
					try { 
						if (this.mineField[i + 1][j] != 9) {
							setSelectedPlayerFieldSquareToMineFieldSimplified(i + 1, j);
							this.selectedY++;
						}
					}
					catch (Exception e) {}
					try { 
						if (this.mineField[i + 1][j + 1] != 9) {
							setSelectedPlayerFieldSquareToMineFieldSimplified(i + 1, j + 1);
							this.selectedY++;
							this.selectedX++;
						}
					}
					catch (Exception e) {}				
				}
			}
		}
	}
	
	// Creates the "answer key".
	public void generate() {
		
		// Generates a number of mines following a modified formula.
		this.numMines = 1 + ThreadLocalRandom.current().nextInt((this.x - 1) * (this.y - 1) / 8, (this.x - 1) * (this.y - 1) / 5);
		
		// Sets all values in the mineField to zero.
		this.mineField = new int[this.y][this.x];
		for (int i = 0; i < this.y; i++) {
			for (int j = 0; j < this.x; j++) {
				this.mineField[i][j] = 0;
			}
		}
		
		// Creates a variable to be used as a tool to get random mine locations.
		int[] tempRandomLocations = new int[this.x * this.y];
		for (int i = 0; i < this.x * this.y; i++) {
			tempRandomLocations[i] = i;
		}
		
		// Generates a set number of random mines by randomly selecting from an array of all squares.
		int[] mineLocations = new int[this.numMines];
		int tempRandomNumber;
		for (int i = 0; i < this.numMines; i++) {
			tempRandomNumber = ThreadLocalRandom.current().nextInt(0, this.x * this.y - i);
			mineLocations[i] = tempRandomLocations[tempRandomNumber];
			tempRandomLocations[tempRandomNumber] = tempRandomLocations[this.x * this.y - i - 1];
		}
		
		// Puts the mines into mineField. (9 is a placeholder for a mine.)
		for (int i = 0; i < this.numMines; i++) {
			this.mineField[mineLocations[i] / this.x][mineLocations[i] % this.x] = 9;
		}
		
		// Counts the number of mines touching each square.
		// Places the values into mineField.
		for (int i = 0; i < this.y; i++) {
			for (int j = 0; j < this.x; j++) {
				if (this.mineField[i][j] != 9) {
					try {
						if (this.mineField[i - 1][j] == 9) {
							this.mineField[i][j]++;
						}
					}
					catch (Exception e) {}
					try { 
						if (this.mineField[i - 1][j - 1] == 9) {
							this.mineField[i][j]++;
						}
					}
					catch (Exception e) {}
					try {
						if (this.mineField[i - 1][j + 1] == 9) {
							this.mineField[i][j]++;
						}
					}
					catch (Exception e) {}
					try { 
						if (this.mineField[i][j - 1] == 9) {
							this.mineField[i][j]++;
						}
					}
					catch (Exception e) {}
					try { 
						if (this.mineField[i][j + 1] == 9) {
							this.mineField[i][j]++;
						}
					}
					catch (Exception e) {}
					try {
						if (this.mineField[i + 1][j - 1] == 9) {
							this.mineField[i][j]++;
						}
					}
					catch (Exception e) {}
					try { 
						if (this.mineField[i + 1][j] == 9) {
							this.mineField[i][j]++;
						}
					}
					catch (Exception e) {}
					try { 
						if (this.mineField[i + 1][j + 1] == 9) {
							this.mineField[i][j]++;
						}
					}
					catch (Exception e) {}				
				}
			}
		}
	}
	
	// Returns the player's selected square's value.
	public int getSelectedMineFieldSquare() {
		return this.mineField[this.selectedY][this.selectedX];
	}
	
	// Returns whether "answer key" generation can begin.
	public boolean getCanStartGeneration() {
		return this.canStartGeneration;
	}
	
	// Returns the player's selected square's X location.
	public int getSelectedX() {
		return this.selectedX;
	}
	
	// Returns the player's selected square's Y location.
	public int getSelectedY() {
		return this.selectedY;
	}
	
	// Returns the state of the game.
	public String getGameState() {
		return this.gameState;
	}
	
	// Changes the state of the game..
	public void setGameState(String gameState) {
		this.gameState = gameState;
	}

	// Sets a selected square in playerField to the "answer key"'s corresponding square.
	public void setSelectedPlayerFieldSquareToMineField(int yCoord, int xCoord) {
		this.playerField[yCoord][xCoord] = this.mineField[yCoord][xCoord];
		if (this.playerField[yCoord][xCoord] == 9) {
			this.buttons[yCoord * this.x + xCoord].setText("BOOM");
		}
		else if (this.playerField[yCoord][xCoord] == 0) {
			revealZeros(yCoord, xCoord);
		}
		else this.buttons[yCoord * this.x + xCoord].setText(Integer.toString(this.playerField[yCoord][xCoord]));
	}
	
	// Sets the player's selected square in playerField to the "answer key"'s corresponding square.
	public void setSelectedPlayerFieldSquareToMineField() {
		setSelectedPlayerFieldSquareToMineField(this.selectedY, this.selectedX);
	}
	
	// Sets the player's selected square in playerField to the "answer key"'s corresponding square without the extra features.
	public void setSelectedPlayerFieldSquareToMineFieldSimplified(int yCoord, int xCoord) {
		this.playerField[yCoord][xCoord] = this.mineField[yCoord][xCoord];
		this.buttons[yCoord * this.x + xCoord].setText(Integer.toString(this.mineField[yCoord][xCoord]));
	}
}
