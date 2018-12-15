//Import Statements
import java.io.*;
import java.util.*;

//Creates the maze. Takes in HashTables of monsters and treasures. 
public class Maze{
	
	//Maze Parameters
	protected String numRooms, numRows, numCols;
	private ArrayList<Monster> monsterList;
	private ArrayList<Thread> threads;
	protected int nRows, nCols, nR;
	protected String mon, tre;
	protected String wallNorth, wallEast, wallSouth, wallWest, texture;
	protected Monster tempMon;
	protected Treasure tempTre;

	
	public GameTile[][] gameBoard;

	public Maze(Hashtable<String, Monster> monsters, Hashtable<String, Treasure> treasures) throws IOException{
		
		try{
			//File Reader and Scanner
			String fileName = "map.txt";
			FileReader inFile = new FileReader(fileName);
			Scanner scan = new Scanner(inFile);

			//Get Rooms, Rows, Columns
			numRooms = scan.nextLine();
			numRows = scan.nextLine();
			numCols = scan.nextLine();
			
			nR = Integer.parseInt(numRooms);
			nRows = Integer.parseInt(numRows);
			nCols = Integer.parseInt(numCols);
			
			gameBoard = new GameTile[nRows][nCols];
			monsterList = new ArrayList<Monster>();
			threads = new ArrayList<Thread>();//Create arraylist to hold threads for the monsters

			//Get Room Attributes
			for (int i = 0; i < nRows; i++){
				for (int j = 0; j < nCols; j++){
					//line to remove '***' in between rooms
					String junk = scan.nextLine();
				
					mon = scan.nextLine();
					tre = scan.nextLine();
					wallNorth = scan.nextLine();
					wallEast = scan.nextLine();
					wallSouth = scan.nextLine();
					wallWest = scan.nextLine();
					texture = scan.nextLine();
					
					//Create GameTile object
					GameTile tile = new GameTile(mon, tre, wallNorth, wallEast, wallSouth, wallWest, texture, monsters, treasures, i, j);
					
					 //Add to gameBoard
					 gameBoard[i][j] = tile;

					//Add to Monster List and Thread List
					if (tile.isMonster()){
						monsterList.add(tile.getMonster());
						Thread tempThread = new Thread(tile.getMonster());
						threads.add(tempThread);
					}

					
					System.out.println();
					System.out.println();						
				}
			}
		}
			
		catch (Exception e){
			System.out.println("Map.txt not found.");
		}
	}

	//Gets the GameTile as long as it is within the bounds of the gameboord
	public GameTile getGameTile(int i, int j){
		boolean xBounds = false;
		boolean yBounds = false;
		if ((i >= 0) && (i <= nRows -1)){
			xBounds = true;
		}
		if ((j >= 0) && ( j <= nCols-1)){
		 	yBounds = true;
		}

		if ((xBounds == true) && (yBounds == true)){
			return gameBoard[i][j];
		}
		else{
			return null;
		}
	}
	
	public int getRows(){
		//return (Integer.parseInt(numRows));
		return nRows;
	}
	
	public int getColumns(){
		//return (Integer.parseInt(numCols));
		return nCols;
	}
	
	public int getRooms(){
		//return (Integer.parseInt(numRooms));
		return nR;
	}

	public ArrayList<Monster> getMonsters() {
		return monsterList;
	}

	public void updateM(ArrayList<Monster> mon){
		monsterList = mon;

		for (int i = 0; i < monsterList.size(); i++){
			Monster newMon = monsterList.get(i);

			//Remove the monster from the game
			gameBoard[newMon.getX()][newMon.getY()].removeMonster();

			//Add the monster to the game
			gameBoard[newMon.getX()][newMon.getY()].addMonster(newMon);
		}
	}
	
	public void addPlayer(int x, int y, Player tempP){
		gameBoard[x][y].addPlayer(tempP);
	}
	
	public void removePlayer(int x, int y){
		gameBoard[x][y].removePlayer();
	}
	
	public void addMonster(int x, int y, Monster m){
		gameBoard[x][y].addMonster(m);
	}
	
	public void removeMonster(int x, int y){
		gameBoard[x][y].removeMonster();
	}
	
	public void removeTreasure(int frontX, int frontY) {
		gameBoard[frontX][frontY].removeTreasure();
		
	}

	public ArrayList<Thread> getThreadList(){
		return threads;
	}
}