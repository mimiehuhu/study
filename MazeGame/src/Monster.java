import java.util.ArrayList;
import java.util.Random;

import javax.net.ssl.SSLEngineResult.Status;
import javax.swing.ImageIcon;
import javax.swing.text.ChangedCharSetException;

public class Monster implements Runnable{
	
	public GamePlay tempGame;
	protected String name, file1, file2;
	private int xCord, yCord, health, damage, coolDownTime, monsterProb, playerProb, row, column, rowVariable, columnVariable;
	protected ImageIcon image1, image2;
	protected String current;

	public Monster(GamePlay game, String name, String file1, String file2, int x, int y, int health, int damage, int coolDownTime, int monProb, int playerProb){
		this.name = name;
		this.file1 = file1;
		this.file2 = file2;
		current = file1;
		image1 = new ImageIcon(file1); //for use later on
		image2 = new ImageIcon(file2); //for use later on
		xCord = x;
		yCord = y;
		//For use with keeping track of monster during gamePlay. Variable can change while other keeps track of previous
		rowVariable = 0;
		columnVariable = 0;
		row = 0;
		column = 0;
		this.health = health;
		this.damage = damage;
		this.coolDownTime = coolDownTime;
		monsterProb = monProb;
		this.playerProb = playerProb;
		this.tempGame = game;
	}

	public void run(){
		//Conditions for Monster interaction
		boolean pHealthParam = false;
		boolean mHealthParam = false;
		if (tempGame.getPHealth() > 0)
			pHealthParam = true;
		if (this.health > 0)
			mHealthParam = true;
		
		//Rooms around the Monster
		ArrayList<GameTile> rooms = collectGameTiles();

		//Random Number Generator for Probability of attack
		Random rand = new Random();
		

		while ((pHealthParam == true) && (mHealthParam == true)){
//		while ((tempGame.getPHealth() > 0) && (this.health > 0)){
			//Check if the player is next to the monster
			if(checkForPlayer() == true){
				//Since true, initiate attack on Player
				//Check if the Monster "misses"
				int attackProbability = rand.nextInt(100);
				if (monsterProb > attackProbability){
					//Attack commences
					tempGame.playMSound(); //play sound
					tempGame.lowerPHealth(damage); //lower the players health by monster damage
					//Update Thread and make Monster wait before moving
					try{
						Thread.sleep(coolDownTime*1000);
						updateFile();
						tempGame.repaintDisplay();
						Thread.sleep(coolDownTime*1000);
						tempGame.repaintDisplay();
					}
					catch (Exception e){
						e.printStackTrace();
					}
					System.out.println("Monster attacks.");
				}
				else{
					try{
						Thread.sleep(coolDownTime*1000);
						tempGame.repaintDisplay();
					}
					catch (Exception e){
						e.printStackTrace();
					}
					System.out.println("Monster misses attack.");
				}
				
				if (tempGame.getPHealth() > 0)
					pHealthParam = true;
				if (this.health > 0)
					mHealthParam = true;
			}
			//No Player nearby, Move the monster to a new location after making it wait briefly.
			else{
				monsterMovement();
				try{
					Thread.sleep(coolDownTime*1000);
				}
				catch (Exception e){
					e.printStackTrace();
				}				
			}
		}

	}

	public GamePlay getGame(){
		return this.tempGame;
	}
	
	public void setHealth(int amt){
		health = amt;
	}
	
	public void die(){
		health = 0;
	}
	
	public int getHealth(){
		return health;
	}
	
	public String getName(){
		return name;
	}
	
	public String getImage(){
		return current;
	}
	public String getFile(int choice){
		if (choice == 1)
			return file1;
		else
			return file2;
	}
	
	public void updateFile(){
		if(current.equals(file1)){
			current = file2;
		}
		else if (current.equals(file2)){
			current = file1;
		}
	}
	
	public int getX(){
		return xCord;
	}
	
	public int getY(){
		return yCord;
	}
	
	public int getDamage(){
		return damage;
	}
	
	public int getCDT(){
		return coolDownTime;
	}
	
	//probability of player hitting monster when attacking
	public int getPlayProb(){ 
		return playerProb;
	}
	
	//probability of monster hitting player when attacking
	public int getMonProb(){
		return monsterProb;
	}

	//Keeps track of current and previous row/column of monster. used for movement during gameplay
	public void setXY(int xCord2, int yCord2) {
		row = rowVariable;
		column = columnVariable;
		rowVariable = xCord2;
		columnVariable = yCord2;
	}

	//Looks at every space around the Monster and gets that tile. Added
	//to an arrayList to be passed back to checkForPlayer
	public ArrayList<GameTile> collectGameTiles(){
		GameTile northTile = null;
		GameTile southTile = null; 
		GameTile eastTile = null;
		GameTile westTile = null; 
		GameTile currentMonster;
		ArrayList<GameTile> result = new ArrayList<>();
		int numRows = tempGame.getNumRows();
		int numCols = tempGame.getNumCols();
		//Get north gameTile
		if (row > 0)
			northTile = tempGame.getGameTile(row - 1, column);
		//Get south gameTile
		if (row < numRows-1)
			southTile = tempGame.getGameTile(row + 1, column);
		//Get west gameTile
		if (column > 0)
			westTile = tempGame.getGameTile(row, column -1);
		//Get east gameTile
		if(column < numCols -1)
			eastTile = tempGame.getGameTile(row, column + 1);

		//Add to Arraylist
		result.add(northTile);
		result.add(southTile);
		result.add(westTile);
		result.add(eastTile);

		return result;
	}

	protected boolean checkForPlayer(){
		//Get the tile to check for walls. If there are walls by Monster, monster cannot attack.
		GameTile currentMonster = tempGame.getGameTile(row, column);
		boolean result = false;

		//Get Game Tiles
		ArrayList<GameTile> rooms = collectGameTiles();
		GameTile northTile = rooms.get(0); //Tile to the North
		GameTile southTile = rooms.get(1); //Tile to the South
		GameTile eastTile = rooms.get(2); //Tile to the East
		GameTile westTile = rooms.get(3); //Tile to the West
		
		//Get Maze Size 
		int numRows = tempGame.getNumRows();
		int numCols = tempGame.getNumCols();

		//Check if the Monster has walls on any sides
		boolean northWall, southWall, eastWall, westWall;
		northWall = currentMonster.hasNorthWall();
		southWall = currentMonster.hasSouthWall();
		eastWall = currentMonster.hasEastWall();
		westWall = currentMonster.hasWestWall();
		
		//Check if there is a player in the associated location by checking for a wall, checking
		//if the tile is null and finally checking for a Player in that tile. Return true if there is. 
		if((eastWall == false) && (eastTile!= null) && (eastTile.isPlayer()))
			result = true;
		else if ((westWall == false) && (westTile != null) && (westTile.isPlayer()))
			result = true;
		else if ((southWall == false) && (southTile != null) && (southTile.isPlayer()))
			result = true;
		else if ((northWall == false) && (northTile != null) && (northTile.isPlayer()))
			result = true;
		
		return result;
	}
 
	/*...Behavior is that the Monster will try to move on the x-axis first before trying the y-axis. 
	If either of those are impossible, it will do the other. If neither are possible, the player either
	does not exist, or the monster thinks it is in the same location as the player. 
	The move function will retrive all of the variables about the player and the monster
	from the current game (GamePlay class). Using that and the Player's location, it will assess if it 
	can move either east/west or north/south towards the player. Two 'Target row/col' variables will hold
	the location that the Monster will move towards. At the end of the fucntion the Monster
	is removed from its current location and placed within the new location.
	*/
	public void monsterMovement(){
		//Verify is used to determine the correct move to make
		boolean verify = false;
		
		Random rand = new Random();
		int chance = rand.nextInt(100);
		
		//Get the Monster and the gametiles around it
		GameTile tempMonster = tempGame.getGameTile(row, column);
		ArrayList<GameTile> rooms = collectGameTiles();
		GameTile northTile = rooms.get(0);
		GameTile southTile = rooms.get(1); //Tile to the South
		GameTile eastTile = rooms.get(2); //Tile to the East
		GameTile westTile = rooms.get(3); //Tile to the West
		
		//Check if the Monster has walls on any sides
		boolean northWall, southWall, eastWall, westWall;
		northWall = tempMonster.hasNorthWall();
		southWall = tempMonster.hasSouthWall();
		eastWall = tempMonster.hasEastWall();
		westWall = tempMonster.hasWestWall();

		//Get Player and Player location
		int pRow = tempGame.getPRow();
		int pCol = tempGame.getPCol();

		//Get the Maze size
		int mazeRows = tempGame.getNumRows();
		int mazeColumns = tempGame.getNumCols();

		//Temporary variables to hold the location that Monster will move to
		int targetRow = 0;
		int targetCol = 0;
		
		//Assess movement on the x-axis
		/* Depending on the location of the Monster relative to the Player, the Monster will attempt to move
		 * in one of two ways. First, it will use a randomly generated number between 0 and 100 to decide if it should attempt
		 * to move via the x-axis (up to 50) or the y-axis (over 50), first. The algorithm attempts to make the x and y axis of the Monster
		 * equal to that of the Player. After it decides which of those it should do, the Monster will check
		 * if the move is in bounds, if the target location is not occupied by another monster or player, and if the tile is not null.
		 * It will first check based on going up or down, and then left or right. If there is a valid move to be made, the Monster
		 * will then set its own location to the new target location.
		 */
		//If chance is les than 50, try to adjust x-axis
		if (chance <= 50){
			if (pCol > column){
				//Get new location to the east
				targetCol = column +1; //moving 
				targetRow = row;
				
				//Check if the location is a viable move
				if ((targetCol <= mazeColumns-1) && (eastWall == false) && (eastTile != null) && (eastTile.isMonster()== false) && (eastTile.isPlayer()==false)){
					verify = true;
				}
			}
			else if (pCol < column){
				//Get new location to the west
				targetCol = column -1;
				targetRow = row;
				
				//Check if the location is a viable move
				if ((targetCol >= 0) && (westWall == false) && (westTile != null) && (westTile.isMonster()==false) && (westTile.isPlayer()==false)){
					verify = true;
				}
			}
		}
		//Chance is greater than 50, so try to adjust y-axis
		else{
			if (pRow > row){
				//Get new location to the south
				targetRow = row + 1;
				targetCol = column;
				
				//Check if the location is a viable move
				if ((targetRow <= mazeRows -1) && (southWall == false) && (southTile != null) && (southTile.isMonster() == false)&&(southTile.isPlayer() == false)){
					verify = true;
				}
			}
			else if ((pRow < row)){
				//Get the location to the north
				targetRow = row - 1;
				targetCol = column;
				
				//Check if the location is a viable move
				if ((targetRow >= 0) && (northWall == false)&&(northTile!= null) && (northTile.isMonster() == false) && (northTile.isPlayer()== false)){
					verify = true;
				}
			}
		}
		
		//Check if either of those conditions were valid and if the  Monster can make a move
		if (verify == true){
			tempGame.deleteMonster(row, column);
			tempGame.addMonster(targetRow, targetCol, this);
			row = targetRow;
			column = targetCol;
		}
		//If Monster can't make a move, try again but adjust y-axis first
		else{
			//Reset
			targetRow = row;
			targetCol = column;
			
			//Result is false again
			verify = false;
			
			//If the chance is less than 50, attempt to move the y-axis
			if (chance <= 50){
				if (pRow > row){
					//Move south
					targetRow = row + 1;
					targetCol = column;
					
					//Check if target location is viable
					if ((targetRow <= mazeRows -1) && (southWall == false) && (southTile != null) && (southTile.isMonster() == false)&&(southTile.isPlayer() == false)){
						verify = true;
					}
				}
				else if ((pRow < row)){
					//Move north
					targetRow = row - 1;
					targetCol = column;
					
					//Check if target location is viable
					if ((targetRow >= 0) && (northWall == false)&&(northTile!= null) && (northTile.isMonster() == false) && (northTile.isPlayer()== false)){
						verify = true;
					}
				}
			}
			
			else{
				//Chance must be greater than 50, so try to adjust x-axis

				if (pCol > column){
					//Move east
					targetCol = column +1;
					targetRow = row;
					
					//Check if target location is viable
					if ((targetCol <= mazeColumns-1) && (eastWall == false) && (eastTile != null) && (eastTile.isMonster()== false) && (eastTile.isPlayer()==false)){
						verify = true;
					}
				}
				
				else if (pCol < column){
					//Move west
					targetCol = column -1;
					targetRow = row;
					
					//Check if target location is viable
					if ((targetCol >= 0) && (westWall == false) && (westTile != null) && (westTile.isMonster()==false) && (westTile.isPlayer()==false)){
						verify = true;
					}
				}
			}
			//If verify is true, move the monster
			if (verify == true){
				tempGame.deleteMonster(row, column);
				tempGame.addMonster(targetRow, targetCol, this);
				row = targetRow;
				column = targetCol;
			}
		}
		
	}	
}
