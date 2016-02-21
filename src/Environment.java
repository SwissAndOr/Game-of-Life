import java.util.ArrayList;
import java.util.List;

public class Environment {
	
	private int[][] grid;
	public boolean[] liveConditions = {false, false, true, true, false, false, false, false, false},
					 birthConditions = {false, false, false, true, false, false, false, false, false};
	public boolean[] stateLivingStatuses = {false, true, false, false, false, false, false, false};
	private int generation = 0;
	
	public Environment(int width, int height) {
		grid = new int[width][height];
	}
	
	public int getCellState(int x, int y) {
		return (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length) ? grid[x][y] : 0;
	}
	
	public boolean setCellState(int x, int y, int state) {
		if (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length) {
			grid[x][y] = state;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean invertCell(int x, int y) {
		if (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length) {
			grid[x][y] = grid[x][y] == 1 ? 0 : 1;
			return true;
		} else {
			return false;
		}
	}
	
	public int getGeneration() {
		return generation;
	}
	
	public int getWidth() {
		return grid.length;
	}
	
	public int getHeight() {
		return grid[0].length;
	}
	
	public void nextStep() {
		int[][] newGrid = new int[grid.length][grid[0].length];
		
		for (int x = 0; x < newGrid.length; x++) {
			for (int y = 0; y < newGrid[x].length; y++) {
				newGrid[x][y] = getCellState(x, y);
				
				int livingNeighbors = 0;
				List<int[]> livingNeighborsStateCount = new ArrayList<int[]>();
				int[][] neighbors = { {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1} }; // Array of neighboring positions
				
				for (int[] neighbor : neighbors) {
					if (stateLivingStatuses[getCellState(x + neighbor[0], y + neighbor[1])]) {
						livingNeighbors++;
						
						boolean foundStateCount = false;
						for (int[] stateCount : livingNeighborsStateCount) {
							if (stateCount[0] == getCellState(x + neighbor[0], y + neighbor[1])) {
								stateCount[1]++;
								foundStateCount = true;
								break;
							}
						}
						if (!foundStateCount) {
							int[] tempIntPleaseRemoveWhenFixIsFound = {getCellState(x + neighbor[0], y + neighbor[1]), 1};
							livingNeighborsStateCount.add(tempIntPleaseRemoveWhenFixIsFound);
						}
					}
				}
				
				if (stateLivingStatuses[getCellState(x, y)]) {				
					if (!liveConditions[livingNeighbors]) {
						newGrid[x][y] = 0;
					}
				} else {
					if (birthConditions[livingNeighbors]) {
						int[] mostPopularState = {0, 0};
						for (int[] stateCount : livingNeighborsStateCount) {
							if (stateCount[1] > mostPopularState[1]) {
								mostPopularState = stateCount;
							}
						}
						newGrid[x][y] = mostPopularState[0];
					}
				}
			}
		}
		
		generation++;
		grid = newGrid;
	}
	
}
