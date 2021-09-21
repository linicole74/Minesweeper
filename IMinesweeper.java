/*
 * Nicole Li, Emily Zhao, Vani Arora, Annika Le, Abby Levit
 * October 8, 2019
 * AP CSA Freebuild
 * IMinesweeper
 */

package li.five;

public interface IMinesweeper {
	
	public abstract void getDimensions();
	public abstract void showMines();
	public abstract boolean checkWin();
	public abstract void setup();
	public abstract void guiStart();
	public abstract void revealZeros(int yCoord, int xCoord);
	public abstract void generate();
	public abstract boolean getCanStartGeneration();
	public abstract int getSelectedX();
	public abstract int getSelectedY();
	public abstract int getSelectedMineFieldSquare();
	public abstract String getGameState();
	public abstract void setGameState(String gameState);
	public abstract void setSelectedPlayerFieldSquareToMineField();
	public abstract void setSelectedPlayerFieldSquareToMineField(int yCoord, int xCoord);
	public abstract void setSelectedPlayerFieldSquareToMineFieldSimplified(int yCoord, int xCoord);
}
