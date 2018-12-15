import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import javax.swing.*;
import javax.swing.text.StyledEditorKit.BoldAction;
import javax.sound.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


//GamePlay acts as a mediator. It will create the game and build the GUI while facilitating game functions.
public class GamePlay extends JFrame implements MouseListener, KeyListener{

	private Maze maze;
	private MiniMap map;
	private Player p;
	private ArrayList<Monster> monsterList;
	protected Hashtable<String, Monster> monsters = new Hashtable<>();
	protected Hashtable<String, Treasure> treasures = new Hashtable<>();
	protected Info info;
	protected Display display;
	
	protected int currentMazeLevel;
	
	//GUI variables
	protected JPanel displayPanel, infoPanel, mapPanel;
	
	public GamePlay() throws IOException{
		super("Maze Game by Josh Boldt");
		p = new Player();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//*************MONSTERS**************
		try{
			
			//File names
			String monFile = "monsters.txt";
			
			//Monster Variables
			String name, file1, file2;
			String xCord, yCord, health, damage, coolDownTime, monsterProb, playerProb, numMonsters;
			
			//File Reader and Scanner
			FileReader fin = new FileReader(monFile);
			Scanner scan = new Scanner(fin);

			numMonsters = scan.nextLine();
			int numMon = Integer.parseInt(numMonsters);
			
			while(scan.nextLine() != "***"){
				name = scan.nextLine();
				file1 = scan.nextLine();
				file2 = scan.nextLine();
				
				//Int-Strings
				xCord = scan.nextLine();
				yCord = scan.nextLine();
				health = scan.nextLine();
				damage = scan.nextLine();
				coolDownTime = scan.nextLine();
				monsterProb = scan.nextLine();
				playerProb = scan.nextLine();
				
				//Convert Strings to Integer
				int x = Integer.parseInt(xCord);
				int y = Integer.parseInt(yCord);
				int h = Integer.parseInt(health);
				int d = Integer.parseInt(damage);
				int cDT = Integer.parseInt(coolDownTime);
				int monProb = Integer.parseInt(monsterProb);
				int playProb = Integer.parseInt(playerProb);
				
				//Create monster object, character delineation, and adds them to the hashtable
				Monster tempMon = new Monster(this, name, file1, file2, x, y, h, d, cDT, monProb, playProb);
				Character tempChar = name.charAt(0);
				monsters.put(name, tempMon);
				
				if (!scan.hasNextLine()){
					break;
				}
			}
			
			scan.close();
		}
		
		catch(FileNotFoundException e){
			System.out.println("Monster.txt not found.");
		}
		
		//*****************TREASURES*********************
		try{
			String treFile = "treasures.txt";
			
			//Treasure Variables
			String name, file1;
			String xCord, yCord, healthRestore, gold, imageWidth, imageHeight, numTreasures;
			
			//File Reader and Scanner
			FileReader find = new FileReader(treFile);
			Scanner scan = new Scanner(find);

			numTreasures = scan.nextLine();
			int numTre = Integer.parseInt(numTreasures);
			
			while(scan.nextLine() != "***"){
				name = scan.nextLine();
				file1 = scan.nextLine();
				
				//Int-Strings
				gold = scan.nextLine();
				healthRestore = scan.nextLine();
				imageWidth = scan.nextLine();
				imageHeight = scan.nextLine();
				xCord = scan.nextLine();
				yCord = scan.nextLine();
				
				//Convert Strings to Integer
				int x = Integer.parseInt(xCord);
				int y = Integer.parseInt(yCord);
				int hR = Integer.parseInt(healthRestore);
				int iW = Integer.parseInt(imageWidth);
				int iH = Integer.parseInt(imageHeight);
				int g = Integer.parseInt(gold);
				
				//Create monster object, character delineation, and adds them to the hashtable
				Treasure tempTre = new Treasure(name, file1, g, hR, iW, iH, x, y);
				treasures.put(name, tempTre);
				
				if (!scan.hasNextLine()){
					break;
				}
			}
			
			//Create Maze GUI
			maze = new Maze(monsters, treasures);
			monsterList = maze.getMonsters();
			
			//Create minimap to mount on maze
			map = new MiniMap(maze);
			
			//add player to the game
			maze.addPlayer(0, 0, p);
			
			//Create JFrame to hold everything
			this.setSize(800, 800);
			this.setLayout(new BorderLayout(3,3));
			this.addMouseListener(this);
			this.addKeyListener(this);
			
			//Create panels
			//Display Panel shows game graphics
			displayPanel = new JPanel();
			displayPanel.setPreferredSize(new Dimension(800,600));
			displayPanel.setBackground(Color.blue);
			displayPanel.setLayout(new BorderLayout());
			//Create display object to mount on DisplayPanel
			display = new Display(maze, p);
			display.setLayout(new BorderLayout());
			display.setBackground(Color.black);
			this.add(display, BorderLayout.NORTH);
			
			//Create InfoPanel
			//Info Panel shows game stats and attributes
			infoPanel = new JPanel();
			infoPanel.setPreferredSize(new Dimension(800,200));
			infoPanel.setBackground(new Color(200, 192, 232));
			infoPanel.setLayout(new BorderLayout(3,3));
			//Create info object to mount on InfoPanel
			info = new Info(p);
			info.setLayout(new BorderLayout());
			infoPanel.add(info, BorderLayout.CENTER);
			
			//Create minimap panel
			//Minimap panel holds the minimap
			mapPanel = new JPanel();
			mapPanel.setPreferredSize(new Dimension(350,200));
			mapPanel.setBackground(Color.white);
			mapPanel.add(map);
			
			//Add all the panels together and stack on the JFrame
			infoPanel.add(mapPanel, BorderLayout.EAST);
			this.add(infoPanel, BorderLayout.SOUTH);
			this.setVisible(true);
			
			
		}
		catch (FileNotFoundException e){
			System.out.println("Monsters.txt not found");
		}
		
	}
	
	public void addMonster(int row, int column, Monster creationMonster){
		//Add monster to the maze
		maze.addMonster(row, column, creationMonster);
		
		//Update MiniMap to hold new Monster and repaint the graphics
		map.update(maze);
		map.repaint();
		
		//Update and repaint graphics
		display.updateMP(maze, p);
		display.repaint();
	}
	
	public void deleteMonster(int row, int column){
		maze.removeMonster(row, column);
	} 
	
	public int getPHealth(){
		return p.getHealth();
	}
	
	public void lowerPHealth(int damageTaken){
		//Set new player health
		p.setHealth(damageTaken);
		
		//Update info display and reapint the graphics
		info.update(p);
		info.repaint();
	}
	
	public void repaintDisplay(){
		display.updateMP(maze, p);
		display.repaint();
		info.update(p);
		info.repaint();
	}
	
	public int getPRow(){
		return p.getRow();
	}
	
	public int getPCol(){
		return p.getCol();
	}
	
	public int getNumRows(){
		return maze.getRows();
	}
	
	public int getNumCols(){
		return maze.getColumns();
	}	
	
	public GameTile getGameTile(int x, int y){
		return maze.getGameTile(x, y);
	}
	
	
	
	public static void main(String[] args) throws IOException {
		GamePlay test = new GamePlay();
		test.startThreads();
	}
	
	//Starts the threads for each monster by reading them from the threadlist and intitializing the threads
	public void startThreads(){
		ArrayList<Thread> threads = maze.getThreadList();
		int size = threads.size();
		
		//Iterate through list of threads and start each thread (monster)
		for (int counter = 0; counter < size; counter++){
			//Inititialize new thread for the monster
			Thread newMonster = threads.get(counter);
			newMonster.start();
		}
	}

	//Identifies if move is within bounds
	public boolean inBounds(int pRow, int pCol, int numR, int numC){
		boolean colTest = false;
		boolean rowTest = false;
		
		if ((pCol >= 0) && (pCol <= numC-1)) //-1
			colTest = true;
		if ((pRow >= 0) && (pRow <= numR-1)) //-1
			rowTest = true;
		if ((rowTest && colTest) == true)
			return true;
		else 
			return false;
	}

	
	
	/*Mouse listener will implement the move function for the player
	
	MouseClick is a very complex method. It essentially is the 'move', 'attack', 'interact' methods of the Player. It first assess the player's movement.
	The difficult part here is to assess how that works depending on the existing direction of the player. 
	Therefore, it first acknowledges the Player's rotations to the right or to the left b/c those determine later movements. 
	Then it assess whether the movements around the player are in bounds. Then it starts with each movement going up, down, left and right. 
	For each movement, it uses the current player direction to know how it should move. There is a series of if/else statements for each movement that 
	corrects the movement according to the current player direction. Finally, after the movement has been made, the function checks if there player can 
	attack (if so it initiates the attack), and then it allows the player to pick up a treasure if there is one there. 
	 */
	public void mouseClicked(MouseEvent me) {
		
		//Get rows and columns of the Player
		int playerRowVariable = p.getRow(); //Player row copy that is adjustable along the way for use in subsequent methods
		int playerRow = playerRowVariable;
		
		int playerColVariable = p.getCol(); //Player column copy that is adjustable along the way for use in subsequent methods
		int playerCol = playerColVariable;
		
		//Get number of rows and columns
		int numRows = maze.getRows();
		int numCols = maze .getColumns();
		
		//Get player direction, tile
		String orientation= p.getPlayerDirection();
		GameTile currentPlayerTile = maze.getGameTile(playerRow, playerCol);
		boolean monster = false;
		boolean treasure = false;
		
		//Check for walls around the Player
		boolean northWall = currentPlayerTile.hasNorthWall();
		boolean southWall = currentPlayerTile.hasSouthWall();
		boolean eastWall = currentPlayerTile.hasEastWall();
		boolean westWall = currentPlayerTile.hasWestWall();
		
		// info about tile in front
		GameTile front = null;
		int frontX = -1; //-1 is basically a null value, will be set later
		int frontY = -1;
		
		//Get mouse coordinates
		int xMouseCord = (int) me.getX();
		int yMouseCord = (int) me.getY();
		
		//Button Clicks
		//Left Rotate
		boolean leftR = false; 
		boolean rightR = false;
		if ((xMouseCord > 268) && (xMouseCord < 320) && ( yMouseCord > 675) && (yMouseCord < 715))
			leftR = true;
		//Right Rotate
		else if ((xMouseCord > 368) && ( xMouseCord < 425) && ( yMouseCord > 675) && ( yMouseCord < 720))
			rightR = true;
		
		//Direction Buttons
		boolean upB = false;
		boolean leftB = false;
		boolean rightB = false;
		boolean downB = false;
		if ((xMouseCord > 320) && (xMouseCord < 360) && ( yMouseCord > 675) && ( yMouseCord < 720))
			upB = true; //Up
		else if ((xMouseCord > 320) && ( xMouseCord < 365) && (yMouseCord > 730) && ( yMouseCord < 775))
			leftB = true; //left
		else if ((xMouseCord > 320) && ( xMouseCord < 365) && (yMouseCord > 730) && ( yMouseCord < 775))
			downB = true; //down
		else if ((xMouseCord >370) && (xMouseCord < 420) && ( yMouseCord > 730) && ( yMouseCord < 775))
			rightB = true; // right
		
		//MOVEMENT
		//Set rows and columns based on direction player is facing
		if (orientation.equals("N"))
			playerRow = playerRow -1;
		else if (orientation.equals("S"))
			playerRow = playerRow + 1;
		else if (orientation.equals("E"))
			playerCol = playerCol + 1;
		else if (orientation.equals("W"))
			playerCol = playerCol - 1;
		
		//Check if location is within bounds
		boolean boundsCheck = inBounds(playerRow, playerCol, numRows, numCols);
		if (boundsCheck == true){
			//Get front tile
			front = maze.getGameTile(playerRow, playerCol);
			//Update monster and treasure availiblility
			monster = front.isMonster();
			treasure = front.isTreasure();
			
			frontX = playerRow;
			frontY = playerCol;
		}
		
		//Left Rotation
		//if ((xMouseCord > 268) && (xMouseCord < 320) && ( yMouseCord > 675) && (yMouseCord < 715)){
		if (leftR){
			System.out.println("Player: left rotation");
			//Adjust player position
			p.turnLeft();
			info.update(p);
			info.repaint();
			
			//Update Map
			display.updateMP(maze, p);
			display.repaint();
		}

		//Right player rotation
		//Gets bounds of mouse click
		else if ((xMouseCord > 368) && ( xMouseCord < 425) && ( yMouseCord > 675) && ( yMouseCord < 720)){
			//Update variables for player rotation
			p.turnRight();
			info.update(p);
			display.updateMP(maze, p);
			
			//Repaint graphics
			info.repaint();
			display.repaint();
			
			System.out.println("Player: rotate right.");
		}
		
		//Up movement by Player
		else if ((xMouseCord > 320) && (xMouseCord < 360) && ( yMouseCord > 675) && ( yMouseCord < 720) && (monster == false)){
			//Remove player from current location then move to the next
			maze.removePlayer(playerRowVariable, playerColVariable);
			
			//Player 'up' movement determined by given direction as follows
			if (orientation.equals("N")){
				playerRow = playerRowVariable -1;
			}
			else if (orientation.equals("S")){
				playerRow = playerRowVariable + 1;
			}
			else if (orientation.equals("E")){
				playerCol = playerColVariable + 1;
			}
			else if (orientation.equals("W")){
				playerCol = playerColVariable -1;
			}
			
			System.out.println("Player: move up");
			
			//Change player location and update map
			//Move north
			if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (northWall != true) && (orientation.equals("N"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRow, playerColVariable);
				maze.addPlayer(playerRow, playerColVariable, p);
				
				//update maps and walls
				map.update(maze);
				//p.changeXY(playerRow, playerColVariable);
				display.updateMP(maze, p);
				
				//Repaint graphics
				map.repaint();
				display.repaint();
			}
			
			//Move south
			else if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (southWall != true)&& (orientation.equals("S"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRow, playerColVariable);
				maze.addPlayer(playerRow, playerColVariable, p);
				
				//Update maps and walls
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint graphics
				map.repaint();
				display.repaint();
			}
			
			//Move East
			else if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (eastWall != true)&& (orientation.equals("E"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRowVariable, playerCol);
				maze.addPlayer(playerRowVariable, playerCol, p);

				//Update maps and walls
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint graphics
				map.repaint();
				display.repaint();
			}
			
			//Move West
			else if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (westWall != true)&& (orientation.equals("W"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRowVariable, playerCol);
				maze.addPlayer(playerRowVariable, playerCol, p);
				
				//Update maps and walls
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint graphics
				map.repaint();
				display.repaint();
			}
			
			//Wall in the way. Player does not move.
			else{
				maze.addPlayer(playerRowVariable, playerColVariable, p);
				System.out.println("Opps. Can't go there. That's a wall. Try again.");
			}
		}
		
		//Player moves to the left
		else if ((monster == false) && (xMouseCord>265) && (xMouseCord < 315) && (yMouseCord >730) && ( yMouseCord < 775)){
			maze.removePlayer(playerRowVariable,playerColVariable);
			if (orientation.equals("N")){
				playerCol = playerColVariable -1;
			}
			else if (orientation.equals("S")){
				playerCol = playerColVariable + 1;
			}
			else if (orientation.equals("E")){
				playerRow = playerRowVariable -1;
			}
			else if (orientation.equals("W")){
				playerRow = playerRowVariable + 1;
			}
			
			System.out.println("Player: move left.");
			
			//Move North
			if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (westWall != true)&& (orientation.equals("N"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRowVariable, playerCol);
				maze.addPlayer(playerRowVariable, playerCol, p);
				
				//Update map and walls
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint graphics
				map.repaint();
				display.repaint();
			}
			
			//Move South
			else if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (eastWall != true)&& (orientation.equals("S"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRowVariable, playerCol);
				maze.addPlayer(playerRowVariable, playerCol, p);
				
				//Update map and walls
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint graphics
				map.repaint();
				display.repaint();
			}
			
			//Move East
			else if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (northWall != true)&& (orientation.equals("E"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRow, playerColVariable);
				maze.addPlayer(playerRow, playerCol, p);
				
				//Update map and walls
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint graphics
				map.repaint();
				display.repaint();
			}
			
			//Move West
			else if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (southWall != true)&& (orientation.equals("W"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRow, playerColVariable);
				maze.addPlayer(playerRow, playerCol, p);
				
				//Update map and walls
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint graphics
				map.repaint();
				display.repaint();
			}
			
			else{
				maze.addPlayer(playerRowVariable, playerColVariable, p);
				System.out.println("Opps. Can't go there. That's a wall. Try again!");
			}
		}
		
		//Down Movement by Player
		else if ((xMouseCord > 320) && ( xMouseCord < 365) && (yMouseCord > 730) && ( yMouseCord < 775)){
			maze.removePlayer(playerRowVariable,playerColVariable);
			if (orientation.equals("N")){
				playerRow = playerRowVariable +1;
			}
			else if (orientation.equals("S")){
				playerRow = playerRowVariable -1;
			}
			else if (orientation.equals("E")){
				playerCol = playerColVariable -1;
			}
			else if (orientation.equals("W")){
				playerCol = playerColVariable + 1;
			}
			System.out.println("Player: move down");
			
			//Move North
			if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (southWall != true)&& (orientation.equals("N"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRow, playerColVariable);
				maze.addPlayer(playerRow, playerColVariable, p);
				
				//Update map and walls
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint graphics
				map.repaint();
				display.repaint();
			}
			
			//Move South
			else if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (northWall != true)&& (orientation.equals("S"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRow, playerColVariable);
				maze.addPlayer(playerRow, playerColVariable, p);
				
				//Update map and walls
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint graphics
				map.repaint();
				display.repaint();
			}
			
			//Move East
			else if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (westWall != true)&& (orientation.equals("E"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRowVariable, playerCol);
				maze.addPlayer(playerRowVariable, playerCol, p);
				
				//Update map and walls
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint graphics
				map.repaint();
				display.repaint();
			}
			
			//Move West
			else if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (eastWall != true)&& (orientation.equals("W"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRowVariable, playerCol);
				maze.addPlayer(playerRowVariable, playerCol, p);
				
				//Update map and wall
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint graphics
				map.repaint();
				display.repaint();
			}
			else{
				maze.addPlayer(playerRowVariable, playerColVariable, p);
				System.out.println("That's a wall. Can't go there.");
			}
		}
		
		//Right Player movement
		else if ((monster == false) && (xMouseCord >370) && (xMouseCord < 420) && ( yMouseCord > 730) && ( yMouseCord < 775)){
			System.out.println("Player: move right");
			maze.removePlayer(playerRowVariable,playerColVariable);
			if (orientation.equals("N")){
				playerCol = playerColVariable + 1;
			}
			else if (orientation.equals("S")){
				playerCol = playerColVariable - 1;
			}
			else if (orientation.equals("E")){
				playerRow = playerRowVariable + 1;
			}
			else if (orientation.equals("W")){
				playerRow = playerRowVariable - 1;
			}
			
			//Move North
			if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (eastWall != true)&& (orientation.equals("N"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRowVariable, playerCol);
				maze.addPlayer(playerRowVariable, playerCol, p);
				
				//update walls and map
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint Graphics
				map.repaint();
				display.repaint();
			}
			
			//move South
			else if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (westWall != true) && (orientation.equals("S"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRowVariable, playerCol);
				maze.addPlayer(playerRowVariable, playerCol, p);
				
				//Update walls and map
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint Graphics
				display.repaint();
				map.repaint();
			}
			
			//Move East
			else if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (southWall != true)&& (orientation.equals("E"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRow, playerColVariable);
				maze.addPlayer(playerRow, playerColVariable, p);
				
				//Update walls and map
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint Graphics
				map.repaint();
				display.repaint();
			}
			
			//Move West
			else if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (northWall != true)&& (orientation.equals("W"))){
				//Update player's coordinates and then use that to add to new location
				p.changeXY(playerRow, playerColVariable);
				maze.addPlayer(playerRow, playerColVariable, p);
				
				//Update walls and map
				map.update(maze);
				display.updateMP(maze, p);
				
				//Repaint Graphics
				map.repaint();
				display.repaint();
			}
			
			else{
				maze.addPlayer(playerRowVariable, playerColVariable, p);
				System.out.println("That's a wall. Look before you hit your head!");
			}
		}
		//If Player chooses to attack
		if ((front != null) && (xMouseCord >285) && ( xMouseCord < 425) && ( yMouseCord > 620) && ( yMouseCord < 660)){
			System.out.println("Player: attack!");
			
			if(monster == true){
				//Get monster
				this.playButtonSound();
				
				Monster enemy = front.getMonster();
				int monsterHealth = enemy.getHealth();
				
				//Generate attack probability
				Random random = new Random();
				int prob = random.nextInt(100);
				
				//Attack sequence
				int attackValue = p.getAttackValue();
				if(prob < enemy.getPlayProb()){
					enemy.setHealth(monsterHealth - attackValue);
					System.out.println("Monster health: " + enemy.getHealth());
					System.out.println("You attacked!");
				}
				
				else {
					System.out.println("You missed!");
				}
				
				//Update monster status if the Monster dies
				if(enemy.getHealth() < 0){
					//int removal = monsterList.indexOf(enemy);
					maze.removeMonster(playerRow, playerCol);
					//monsterList.remove(monsterList.indexOf(maze));
					
					
					//maze.updateM(monsterList);
					System.out.println("You killed the monster!");
				}
				
				//If monster survives, move them to another location
				else
					maze.addMonster(frontX, frontY, enemy);
			}
			
			//Update walls and map
			map.update(maze);
			display.updateMP(maze, p);
			
			//Repaint Graphics
			map.repaint();
			display.repaint();
		}
			if (treasure == true){
				//Get treasure
				
				Treasure item = front.getTreasure();
				
				//Get treasure click area
				int xParam = item.getX();
				int xArea = xParam + item.getIWidth();
				int yParam = item.getY();
				int yArea = yParam + item.getIHeight();
				
				//Check if click within bounds
				boolean check = false;
				if ((xMouseCord >= xParam) && (xMouseCord <= xArea) && (yMouseCord >= yParam) && (yMouseCord <= yArea)){
					check = true;
				}
				
				//Add treasure to player
				if (check == true){
					p.updateGold(item);
					p.updateHealth(item);
					
					//Update map and graphics
					this.playTSound();
					maze.removeTreasure(frontX, frontY);
					info.update(p);
					map.update(maze);
					display.updateMP(maze,p);
					
					//Redraw Graphics
					info.repaint();
					map.repaint();
					display.repaint();
				}
			}
		}
	//}
	

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//Get rows and columns of the Player
				int playerRowVariable = p.getRow(); //Player row copy that is adjustable along the way for use in subsequent methods
				int playerRow = playerRowVariable;
				
				int playerColVariable = p.getCol(); //Player column copy that is adjustable along the way for use in subsequent methods
				int playerCol = playerColVariable;
				
				//Get number of rows and columns
				int numRows = maze.getRows();
				int numCols = maze .getColumns();
				
				//Get player direction, tile
				String orientation= p.getPlayerDirection();
				GameTile currentPlayerTile = maze.getGameTile(playerRow, playerCol);
				boolean monster = false;
				boolean treasure = false;
				
				//Check for walls around the Player
				boolean northWall = currentPlayerTile.hasNorthWall();
				boolean southWall = currentPlayerTile.hasSouthWall();
				boolean eastWall = currentPlayerTile.hasEastWall();
				boolean westWall = currentPlayerTile.hasWestWall();
				
				// info about tile in front
				GameTile front = null;
				int frontX = -1; //-1 is basically a null value, will be set later
				int frontY = -1;
				
				//MOVEMENT
				//Set rows and columns based on direction player is facing
				if (orientation.equals("N"))
					playerRow = playerRow -1;
				else if (orientation.equals("S"))
					playerRow = playerRow + 1;
				else if (orientation.equals("E"))
					playerCol = playerCol + 1;
				else if (orientation.equals("W"))
					playerCol = playerCol - 1;
				
				//Keyboard input
				char keyChar = e.getKeyChar();
				int keyCode = e.getKeyCode();
		
				//Check if location is within bounds
				boolean boundsCheck = inBounds(playerRow, playerCol, numRows, numCols);
				if (boundsCheck == true){
					//Get front tile
					front = maze.getGameTile(playerRow, playerCol);
					//Update monster and treasure availiblility
					monster = front.isMonster();
					treasure = front.isTreasure();
					
					frontX = playerRow;
					frontY = playerCol;
				}
				
				//Up movement by Player
				if (((monster == false)&&(e.getKeyCode() == KeyEvent.VK_UP)) || ((monster == false)&&(e.getKeyChar() == 'w'))){
					//Remove player from current location then move to the next
					maze.removePlayer(playerRowVariable, playerColVariable);
					
					//Player 'up' movement determined by given direction as follows
					if (orientation.equals("N")){
						playerRow = playerRowVariable -1;
					}
					else if (orientation.equals("S")){
						playerRow = playerRowVariable + 1;
					}
					else if (orientation.equals("E")){
						playerCol = playerColVariable + 1;
					}
					else if (orientation.equals("W")){
						playerCol = playerColVariable -1;
					}
					
					System.out.println("Player: move up");
					
					//Change player location and update map
					//Move north
					if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (northWall != true) && (orientation.equals("N"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRow, playerColVariable);
						maze.addPlayer(playerRow, playerColVariable, p);
						
						//update maps and walls
						map.update(maze);
						//p.changeXY(playerRow, playerColVariable);
						display.updateMP(maze, p);
						
						//Repaint graphics
						map.repaint();
						display.repaint();
					}
					
					//Move south
					else if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (southWall != true)&& (orientation.equals("S"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRow, playerColVariable);
						maze.addPlayer(playerRow, playerColVariable, p);
						
						//Update maps and walls
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint graphics
						map.repaint();
						display.repaint();
					}
					
					//Move East
					else if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (eastWall != true)&& (orientation.equals("E"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRowVariable, playerCol);
						maze.addPlayer(playerRowVariable, playerCol, p);

						//Update maps and walls
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint graphics
						map.repaint();
						display.repaint();
					}
					
					//Move West
					else if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (westWall != true)&& (orientation.equals("W"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRowVariable, playerCol);
						maze.addPlayer(playerRowVariable, playerCol, p);
						
						//Update maps and walls
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint graphics
						map.repaint();
						display.repaint();
					}
					
					//Wall in the way. Player does not move.
					else{
						maze.addPlayer(playerRowVariable, playerColVariable, p);
						System.out.println("Opps. Can't go there. That's a wall. Try again.");
					}
				}
				
				//Player moves to the left
				else if (((monster == false)&&(e.getKeyCode() == KeyEvent.VK_LEFT)) || ((monster == false)&&(e.getKeyChar() == 'a'))){
					maze.removePlayer(playerRowVariable,playerColVariable);
					if (orientation.equals("N")){
						playerCol = playerColVariable -1;
					}
					else if (orientation.equals("S")){
						playerCol = playerColVariable + 1;
					}
					else if (orientation.equals("E")){
						playerRow = playerRowVariable -1;
					}
					else if (orientation.equals("W")){
						playerRow = playerRowVariable + 1;
					}
					
					System.out.println("Player: move left.");
					
					//Move North
					if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (westWall != true)&& (orientation.equals("N"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRowVariable, playerCol);
						maze.addPlayer(playerRowVariable, playerCol, p);
						
						//Update map and walls
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint graphics
						map.repaint();
						display.repaint();
					}
					
					//Move South
					else if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (eastWall != true)&& (orientation.equals("S"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRowVariable, playerCol);
						maze.addPlayer(playerRowVariable, playerCol, p);
						
						//Update map and walls
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint graphics
						map.repaint();
						display.repaint();
					}
					
					//Move East
					else if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (northWall != true)&& (orientation.equals("E"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRow, playerColVariable);
						maze.addPlayer(playerRow, playerCol, p);
						
						//Update map and walls
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint graphics
						map.repaint();
						display.repaint();
					}
					
					//Move West
					else if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (southWall != true)&& (orientation.equals("W"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRow, playerColVariable);
						maze.addPlayer(playerRow, playerCol, p);
						
						//Update map and walls
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint graphics
						map.repaint();
						display.repaint();
					}
					
					else{
						maze.addPlayer(playerRowVariable, playerColVariable, p);
						System.out.println("Opps. Can't go there. That's a wall. Try again!");
					}
				}
				
				//Down Movement by Player
				else if (((monster == false) && (e.getKeyCode() == KeyEvent.VK_DOWN)) || ((monster == false)&&(e.getKeyChar() == 's'))) {
					maze.removePlayer(playerRowVariable,playerColVariable);
					if (orientation.equals("N")){
						playerRow = playerRowVariable +1;
					}
					else if (orientation.equals("S")){
						playerRow = playerRowVariable -1;
					}
					else if (orientation.equals("E")){
						playerCol = playerColVariable -1;
					}
					else if (orientation.equals("W")){
						playerCol = playerColVariable + 1;
					}
					System.out.println("Player: move down");
					
					//Move North
					if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (southWall != true)&& (orientation.equals("N"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRow, playerColVariable);
						maze.addPlayer(playerRow, playerColVariable, p);
						
						//Update map and walls
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint graphics
						map.repaint();
						display.repaint();
					}
					
					//Move South
					else if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (northWall != true)&& (orientation.equals("S"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRow, playerColVariable);
						maze.addPlayer(playerRow, playerColVariable, p);
						
						//Update map and walls
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint graphics
						map.repaint();
						display.repaint();
					}
					
					//Move East
					else if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (westWall != true)&& (orientation.equals("E"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRowVariable, playerCol);
						maze.addPlayer(playerRowVariable, playerCol, p);
						
						//Update map and walls
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint graphics
						map.repaint();
						display.repaint();
					}
					
					//Move West
					else if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (eastWall != true)&& (orientation.equals("W"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRowVariable, playerCol);
						maze.addPlayer(playerRowVariable, playerCol, p);
						
						//Update map and wall
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint graphics
						map.repaint();
						display.repaint();
					}
					else{
						maze.addPlayer(playerRowVariable, playerColVariable, p);
						System.out.println("That's a wall. Can't go there.");
					}
				}
				
				//Right Player movement
				else if (((monster == false)&&(e.getKeyCode() == KeyEvent.VK_DOWN)) || ((monster == false)&&(e.getKeyChar() == 'd'))){
					System.out.println("Player: move right");
					maze.removePlayer(playerRowVariable,playerColVariable);
					if (orientation.equals("N")){
						playerCol = playerColVariable + 1;
					}
					else if (orientation.equals("S")){
						playerCol = playerColVariable - 1;
					}
					else if (orientation.equals("E")){
						playerRow = playerRowVariable + 1;
					}
					else if (orientation.equals("W")){
						playerRow = playerRowVariable - 1;
					}
					
					//Move North
					if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (eastWall != true)&& (orientation.equals("N"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRowVariable, playerCol);
						maze.addPlayer(playerRowVariable, playerCol, p);
						
						//update walls and map
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint Graphics
						map.repaint();
						display.repaint();
					}
					
					//move South
					else if ((inBounds(playerRowVariable, playerCol, numRows, numCols)) && (westWall != true) && (orientation.equals("S"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRowVariable, playerCol);
						maze.addPlayer(playerRowVariable, playerCol, p);
						
						//Update walls and map
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint Graphics
						display.repaint();
						map.repaint();
					}
					
					//Move East
					else if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (southWall != true)&& (orientation.equals("E"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRow, playerColVariable);
						maze.addPlayer(playerRow, playerColVariable, p);
						
						//Update walls and map
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint Graphics
						map.repaint();
						display.repaint();
					}
					
					//Move West
					else if ((inBounds(playerRow, playerColVariable, numRows, numCols)) && (northWall != true)&& (orientation.equals("W"))){
						//Update player's coordinates and then use that to add to new location
						p.changeXY(playerRow, playerColVariable);
						maze.addPlayer(playerRow, playerColVariable, p);
						
						//Update walls and map
						map.update(maze);
						display.updateMP(maze, p);
						
						//Repaint Graphics
						map.repaint();
						display.repaint();
					}
					
					else{
						maze.addPlayer(playerRowVariable, playerColVariable, p);
						System.out.println("That's a wall. Look before you hit your head!");
					}
				}
				else if (e.getKeyChar() == 'e')
				{
					//Player rotates right
					p.turnRight();
					info.update(p);
					info.repaint();
					display.updateMP(maze, p);
					display.repaint();
				}
				else if (e.getKeyChar() == 'q')
				{
					//Player rotates left
					p.turnLeft();
					info.update(p);
					info.repaint();
					display.updateMP(maze, p);
					display.repaint();
				}
				//If Player chooses to attack
				else if ( e.getKeyChar() == ' ')
				{
					System.out.println("Player: attack!");
					this.playButtonSound();
					
					if(monster == true){
						//Get monster
						Monster enemy = front.getMonster();
						int monsterHealth = enemy.getHealth();
						
						//Generate attack probability
						Random random = new Random();
						int prob = random.nextInt(100);
						
						//Attack sequence
						int attackValue = p.getAttackValue();
						if(prob < enemy.getPlayProb()){
							enemy.setHealth(monsterHealth - attackValue);
							System.out.println("Monster health: " + enemy.getHealth());
							System.out.println("You attacked!");
						}
						
						else {
							System.out.println("You missed!");
						}
						
						//Update monster status if the Monster dies
						if(enemy.getHealth() < 0){
							//int removal = monsterList.indexOf(enemy);
							maze.removeMonster(playerRow, playerCol);
							//monsterList.remove(monsterList.indexOf(maze));
							
							
							//maze.updateM(monsterList);
							System.out.println("You killed the monster!");
						}
						
						//If monster survives, move them to another location
						else
							maze.addMonster(frontX, frontY, enemy);
					}
					
					//Update walls and map
					map.update(maze);
					display.updateMP(maze, p);
					
					//Repaint Graphics
					map.repaint();
					display.repaint();
				}
				
		}


	@Override
	public void keyTyped(KeyEvent f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent f) {
		// TODO Auto-generated method stub
		
	}

	public void playMSound() {
		try{
			// TODO Auto-generated method stub
			//Get Sound clip
			File monSound = new File("monster.wav");

			//Get streamer to access the audio
			AudioInputStream input = AudioSystem.getAudioInputStream(monSound);
			AudioFormat formater = input.getFormat();

			//Create Clip to play the audio
			DataLine.Info data = new DataLine.Info(Clip.class, formater);
			Clip sound = (Clip) AudioSystem.getLine(data); //Cast the audio as a clip and get the sound 

			//Open the audio stream and play the sound
			sound.open(input);
			sound.start();
		}
		catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void playTSound() {
		try{
			// TODO Auto-generated method stub
			//Get Sound clip
			File monSound = new File("treasure.wav");

			//Get streamer to access the audio
			AudioInputStream input = AudioSystem.getAudioInputStream(monSound);
			AudioFormat formater = input.getFormat();

			//Create Clip to play the audio
			DataLine.Info data = new DataLine.Info(Clip.class, formater);
			Clip sound = (Clip) AudioSystem.getLine(data); //Cast the audio as a clip and get the sound 

			//Open the audio stream and play the sound
			sound.open(input);
			sound.start();
		}
		catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void playButtonSound() {
		try{
			// TODO Auto-generated method stub
			//Get Sound clip
			File monSound = new File("interface1.wav");

			//Get streamer to access the audio
			AudioInputStream input = AudioSystem.getAudioInputStream(monSound);
			AudioFormat formater = input.getFormat();

			//Create Clip to play the audio
			DataLine.Info data = new DataLine.Info(Clip.class, formater);
			Clip sound = (Clip) AudioSystem.getLine(data); //Cast the audio as a clip and get the sound 

			//Open the audio stream and play the sound
			sound.open(input);
			sound.start();
		}
		catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void playGameOverSound() {
		try{
			// TODO Auto-generated method stub
			//Get Sound clip
			File monSound = new File("gameover.wav");

			//Get streamer to access the audio
			AudioInputStream input = AudioSystem.getAudioInputStream(monSound);
			AudioFormat formater = input.getFormat();

			//Create Clip to play the audio
			DataLine.Info data = new DataLine.Info(Clip.class, formater);
			Clip sound = (Clip) AudioSystem.getLine(data); //Cast the audio as a clip and get the sound 

			//Open the audio stream and play the sound
			sound.open(input);
			sound.start();
		}
		catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}