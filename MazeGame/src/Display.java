import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import java.awt.*;

import javax.swing.*;

//Design style of breaking the display graphics into a center, right and left came
//from a conversation with a classmate about the design. Previous design had been 
//focused around putting everything into one loop of if/else statements. This design
//allows for more versatility while also reducing the amount of code needed. It is
//also much easier to follow along in the code

public class Display extends JPanel{
	
	protected Player player;
	protected Maze maze;

	public boolean frontNorth, frontWest, frontEast, frontSouth;
	
	protected ArrayList<ImageIcon> texture1, texture2, texture3, texture4;
	
	public Display(Maze m, Player p){
		Dimension d = new Dimension(800,580);
		this.setPreferredSize(d);
		
		player = p;
		maze = m; 

	}

	//Gets GameTile in front of the player and returns it if applicable. Otherwise returns null.
	public GameTile getGameTileInFront(){
		
		//Player location variables
		String playerDirection = player.getPlayerDirection();
		int row = player.getRow();
		int column = player.getCol();
		int numRows = maze.getRows();
		int numColumns = maze.getColumns();

		//Get the tile variables
		GameTile temp = maze.getGameTile(row, column);
		boolean northDirection = temp.hasNorthWall();
		boolean southDirection = temp.hasSouthWall();
		boolean eastDirection = temp.hasEastWall();
		boolean westDirection = temp.hasWestWall();

		GameTile result;

		//Checks is the orientation the player is facing then sees if a move in that direction
		//is in bounds and does not have a wall blocking it
		if ((playerDirection.equals("N")) && (row > 0) && (northDirection != true)){
			result = maze.getGameTile(row - 1, column);
		}
		else if ((playerDirection.equals("S")) && ( row < numRows-1) && (southDirection != true)){
			result = maze.getGameTile(row + 1, column);
		}
		else if ((playerDirection.equals("E")) && (column < numColumns-1)&& (eastDirection != true)){
			result = maze.getGameTile(row, column+1);
		}
		else if ((playerDirection.equals("W"))&& (column > 0) && (westDirection != true)){
			result = maze.getGameTile(row,  column-1);
		}
		else{
			result = null;
		}

		return result;
	}

	//Update the player and map objects. Used for when player changes views.
	public void updateMP(Maze m, Player p){
		player = p;
		maze = m;
	}

	//Displays Treasure object if it exists
	public Treasure drawTreasure(){

		Treasure result = null; //Will be returned
		GameTile frontTile = getGameTileInFront();
		
		//If the tile should have a treasure, draw
		if(frontTile != null){
			if (frontTile.isTreasure()){
				result = frontTile.getTreasure();
			}
		}
		else{
			result = null;
		}
			
		return result;
	}

	//Displays Monster object if it exists
	public Monster drawMonster(){

		Monster resultMon = null; //Will be returned
		GameTile frontMTile = getGameTileInFront();

		//If the tile should have a Monster, draw
		if(frontMTile != null){
			if (frontMTile.isMonster()){
				resultMon = frontMTile.getMonster();
			}
		}
		else{
			resultMon = null;
		}
		
		return resultMon;
	}

	//Three methods will follow that calculate the drawing of the three wall images.
	//Each corresponds to a differnent image to be displayed. Determines which image
	//to draw based on the tile in front of the player and the players orientation.

	public String drawRightImage(){
		//Get location information
		String orientation = player.getPlayerDirection();
		int row = player.getRow();
		int column = player.getCol();

		//A arrayList of strings will hold the file names. One will be chosen depending 
		//on if conditions are met. 
		ArrayList<String> files = new ArrayList<String>();
		files.add(0,"_right.png");
		files.add(1,"_right_no_wall_back.png");
		files.add(2,"_right_no_wall_both.png");
		files.add(3,"_right_no_wall_front.png");
		int choice = 0; //will be the file to return using files.get(choice)

		//Get current GameTile to be displayed
		GameTile current = maze.getGameTile(row, column);
		boolean northWall = current.hasNorthWall();
		boolean southWall = current.hasSouthWall();
		boolean eastWall = current.hasEastWall();
		boolean westWall = current.hasWestWall();

		//Get tile in front of the player
		GameTile front = getGameTileInFront();
		boolean frontHasWall = true; //If tile in front of the player has a wall

		//If player is facing northWall and the tile in front is applicable
		if(front!= null){
			if ((orientation.equals("N"))){
				frontHasWall = front.hasEastWall();
				//If there is a wall in front of the player and a wall to the eastWall
				if ((frontHasWall) && (eastWall)){
					choice = 0;
				}
				//If there is a wall in front of the player and not a wall to the eastWall
				else if ((frontHasWall) && (!eastWall)){
					choice = 1;
				}
				//If there is no wall in front of the player and no wall to the eastWall
				else if ((!frontHasWall)&&(!eastWall)){
					choice = 2;
				}
				//If there is no wall in front of the player and a wall to the eastWall
				else if ((!frontHasWall) && (eastWall)){
					choice = 3;
				}
			}
			//If player is facing westWall and the tile in front is applicable
			else if ((orientation.equals("W"))){
				frontHasWall = front.hasNorthWall();
				//If there is a wall in front of player and a wall to northWall
				if ((frontHasWall) && (northWall)){
					choice = 0;
				}
				//If there is a wall in front of player and no wall to northWall
				else if ((frontHasWall) && (!northWall)){
					choice = 1; 
				}
				//If there is no wall in front of player and no wall to northWall
				else if ((!frontHasWall)&&(!northWall)){
					choice = 2;
				}
				//if there is no wall in front of player and a wall to northWall
				else if ((!frontHasWall) && (northWall)){
					choice = 3;
				}
			}
			//If player is facing southWall and the tile in front is applicable
			else if ((orientation.equals("S"))){
				frontHasWall = front.hasWestWall();
				//If there is a wall in front of the player and wall to the westWall
				if ((frontHasWall) && (westWall)){
					choice = 0;
				}
				//If there is a wall in front of the player and no wall to the westWall
				else if ((frontHasWall) && (!westWall)){
					choice = 1;
				}
				//If there is no wall in front of the player and no wall to the westWall
				else if ((!frontHasWall)&&(!westWall)){
					choice = 2;
				}
				//If there is no wall in front of player and a wall to the westWall
				else if ((!frontHasWall) && (westWall)){
					choice = 3;
				}
			}
			//If player is facing eastWall and the tile in front is applicable
			else if ((orientation.equals("E"))){
				frontHasWall = front.hasSouthWall();
				//If there is a wall in front of player and a wall to the southWall
				if ((frontHasWall) && (southWall)){
					choice = 0;
				}
				//if there is a wall in front of the player and no wall to southWall
				else if ((frontHasWall) && (!southWall)){
					choice = 1;
				}
				//If there is no wall in front of the player and no wall to the southWall
				else if ((!frontHasWall)&&(!southWall)){
					choice = 2;
				}
				//If there is no wall in front of the player and a wall to the southWall
				else if ((!frontHasWall) && (southWall)){
					choice = 3;
				}
			}
		}
		//If the wall in front is null
		//Determines what the image should be based on the orientation the player
		//is facing and wether there is a wall or not. 
		else if (front == null){
			//If player facing northWall
			if (orientation.equals("N")){
				//If there is a wall to the eastWall
				if (eastWall){
					choice = 3;
				}
				//No wall to the eastWall
				else if (!eastWall){
					choice =2;
				}
			}
			//If player facing East
			else if (orientation.equals("E")){
				//If south wall
				if (southWall){
					choice =3;
				}
				//no southwall
				else if (!southWall){
					choice =2;
				}
			}
			//Player facing west
			else if (orientation.equals("W")){
				//wall to the north
				if (northWall){
					choice =3;
				}
				//wall to the north
				else if (!northWall){
					choice =2;
				}
			}
			//Player facing south
			else if (orientation.equals("S")){
				//Wall to the west
				if (westWall){
					choice = 3;
				}
				else if (!westWall){
					choice = 2;
				}
			}
		}
		return (String) files.get(choice);
	}



	public String drawLeftImage(){
		//Get location information
		String orientation = player.getPlayerDirection();
		int row = player.getRow();
		int column = player.getCol();

		//A arrayList of strings will hold the file names. One will be chosen depending 
		//on if conditions are met. 
		ArrayList<String> files = new ArrayList<String>();
		files.add("_left.png");
		files.add("_left_no_wall_back.png");
		files.add("_left_no_wall_both.png");
		files.add("_left_no_wall_front.png");
		int choice = 0; //will be the file to return using files.add(choice)

		//Get current GameTile to be displayed
		GameTile current = maze.getGameTile(row, column);
		boolean northWall = current.hasNorthWall();
		boolean southWall = current.hasSouthWall();
		boolean eastWall = current.hasEastWall();
		boolean westWall = current.hasWestWall();

		//Get tile in front of the player
		GameTile front = getGameTileInFront();
		boolean frontHasWall = true; //If tile in front of the player has a wall

		//If player is facing northWall and the tile in front is applicable
		if (front!= null){
			if ((orientation.equals("N"))){
				frontHasWall = front.hasWestWall();
				//If there is a wall in front of the player and a wall to the westWall
				if ((frontHasWall) && (westWall)){
					choice = 0;
				}
				//If there is a wall in front of the player and not a wall to the westWall
				else if ((frontHasWall) && (!westWall)){
					choice = 1;
				}
				//If there is no wall in front of the player and no wall to the westWall
				else if ((!frontHasWall)&&(!westWall)){
					choice = 2;
				}
				//If there is no wall in front of the player and a wall to the westWall
				else if ((!frontHasWall) && (westWall)){
					choice = 3;
				}
			}
			//If player is facing eastWall and the tile in front is applicable
			else if ((orientation.equals("E"))){
				frontHasWall = front.hasNorthWall();
				//If there is a wall in front of player and a wall to the northWall
				if ((frontHasWall) && (northWall)){
					choice = 0;
				}
				//if there is a wall in front of the player and no wall to northWall
				else if ((frontHasWall) && (!northWall)){
					choice = 1;
				}
				//If there is no wall in front of the player and no wall to the northWall
				else if ((!frontHasWall)&&(!northWall)){
					choice = 2;
				}
				//If there is no wall in front of the player and a wall to the northWall
				else if ((!frontHasWall) && (northWall)){
					choice = 3;
				}
			}
			//If player is facing westWall and the tile in front is applicable
			else if ((orientation.equals("W"))){
				frontHasWall = front.hasSouthWall();
				//If there is a wall in front of player and a wall to southWall
				if ((frontHasWall) && (southWall)){
					choice = 0;
				}
				//If there is a wall in front of player and no wall to southWall
				else if ((frontHasWall) && (!southWall)){
					choice = 1;
				}
				//If there is no wall in front of player and no wall to southWall
				else if ((!frontHasWall)&&(!southWall)){
					choice = 2;
				}
				//if there is no wall in front of player and a wall to southWall
				else if ((!frontHasWall) && (southWall)){
					choice = 3;
				}
			}
			//If player is facing southWall and the tile in front is applicable
			else if ((orientation.equals("S"))){
				frontHasWall = front.hasEastWall();
				//If there is a wall in front of the player and wall to the eastWall
				if ((frontHasWall) && (eastWall)){
					choice = 0;
				}
				//If there is a wall in front of the player and no wall to the eastWall
				else if ((frontHasWall) && (!eastWall)){
					choice = 1;
				}
				//If there is no wall in front of the player and no wall to the eastWall
				else if ((!frontHasWall)&&(!eastWall)){
					choice = 2;
				}
				//If there is no wall in front of player and a wall to the eastWall
				else if ((!frontHasWall) && (eastWall)){
					choice = 3;
				}
			}
		}
		//If the wall in front is null
		//Determines what the image should be based on the orientation the player
		//is facing and wether there is a wall or not. 
		else if (front == null){
			//If player facing northWall
			if (orientation.equals("N")){
				//If there is a wall to the wesWall
				if (westWall){
					choice = 3;
				}
				//No wall to the westWall
				else if (!westWall){
					choice =2;
				}
			}
			//Player facing west
			else if (orientation.equals("W")){
				//wall to the south
				if (southWall){
					choice =3;
				}
				//wall to the south
				else if (!southWall){
					choice =2;
				}
			}
			//If player facing East
			else if (orientation.equals("E")){
				//If north wall
				if (northWall){
					choice =3;
				}
				//no north wall
				else if (!northWall){
					choice =2;
				}
			}
			//Player facing south
			else if (orientation.equals("S")){
				//Wall to the east
				if (eastWall){
					choice =3;
				}
				else if (!eastWall){
					choice =2;
				}
			}
		}
		return (String) files.get(choice);
	}


	public String drawCenterImage(){
		//Get location information
		String orientation = player.getPlayerDirection();
		int row = player.getRow();
		int column = player.getCol();

		//A arrayList of strings will hold the file names. One will be chosen depending 
		//on if conditions are met. 
		ArrayList files = new ArrayList();
		files.add("_center.png");
		files.add("_center_no_wall_back.png");
		files.add("_center_wall_in_face.png");
		int choice = 0; //will be the file to return using files.add(choice)

		//Get current GameTile to be displayed
		GameTile current = maze.getGameTile(row, column);
		boolean northWall = current.hasNorthWall();
		boolean southWall = current.hasSouthWall();
		boolean eastWall = current.hasEastWall();
		boolean westWall = current.hasWestWall();

		//Get tile in front of the player
		GameTile front = getGameTileInFront();
		boolean frontHasWall = true; //If tile in front of the player has a wall

		//Check if the tile in front is null
		if (front != null){
			//Check all of the compass directions
			//If the player is facing North
			if ((orientation.equals("N"))){
				frontHasWall = front.hasNorthWall();
				//If there is a wall to the north
				if ((northWall)){
					choice = 2;
				}
				//If there is a wall in front of player but not to the north
				else if ((frontHasWall) && (!northWall)){
					choice = 0;
				}
				//If there is no wall in front of player andno wall to the north
				else if ((!frontHasWall)&&(!northWall)){
					choice = 1;
				}
			}
			//If the player is facing east
			else if ((orientation.equals("E"))){
				frontHasWall = front.hasEastWall();
				//If there is a wall to the east
				if ((eastWall)){
					choice = 2;
				}
				//If there is a wall infront but not to the east
				else if ((frontHasWall) && (!eastWall)){
					choice = 0;
				}
				//If there is no wall in front or to the east
				else if ((!frontHasWall)&&(!eastWall)){
					choice = 1;
				}
			}
			//If the player is facing south
			else if ((orientation.equals("S"))){
				frontHasWall = front.hasSouthWall();
				//if there is a wall to the south
				if ((southWall)){
					choice = 2;
				}
				//If there is a wall in front but not to the south
				else if ((frontHasWall) && (!southWall)){
					choice = 0;
				}
				//If there is no wall in front or to the south
				else if ((!frontHasWall)&&(!southWall)){
					choice = 1;
				}
			}
			//If the player is facing West
			else if ((orientation.equals("W"))){
				frontHasWall = front.hasWestWall();
				//If therre is a wall to the west
				if ((westWall)){
					choice = 2;
				}
				//If there is a wall in front but not to the west
				else if ((frontHasWall) && (!westWall)){
					choice = 0;
				}
				//If there is no wall in front or to the west
				else if ((!frontHasWall)&&(!westWall)){
					choice = 1;
				}
			}	
		}
		//If the tile in front is null 
		else if (front == null){
			choice = 2;
		}

		return (String) files.get(choice);
	}

	//Paint function that calls all the other functinos to correctly draw the images. 
	//Uses the draw functions to find the correct file to be drawn.
	public void paint(Graphics g){
		
		//Setup file getters
		String textureFolder = "//Users/joshboldt/Documents/Git/MazeGame/textures//";
		String monsterFolder = "//Users/joshboldt/Documents/Git/MazeGame/monsters//";
		String treasureFolder = "//Users/joshboldt/Documents/Git/MazeGame/treasures//";

		//Get current GameTile
		GameTile tile = maze.getGameTile(player.getRow(), player.getCol());
		String tileTexture = tile.getTexture();
		
		//Get monstesr current file
		Monster monFile = drawMonster();
				
		//Get Treasure file
		Treasure treFile = drawTreasure();

		//Get filenames from draw methods
		String right = drawRightImage();
		String left = drawLeftImage();
		String center = drawCenterImage();

		//Create full file names
		String rightFileName = textureFolder + tileTexture + right;
		String leftFileName = textureFolder + tileTexture + left;
		String centerFileName = textureFolder + tileTexture + center;

		//Create Images for the display
		ImageIcon rightImage = new ImageIcon(rightFileName);
		ImageIcon leftImage = new ImageIcon(leftFileName);
		ImageIcon centerImage = new ImageIcon(centerFileName);
		
		//Draw Images on Display
		g.drawImage(rightImage.getImage(), 571, 0, null);
		g.drawImage(leftImage.getImage(), 0, 0, null);
		g.drawImage(centerImage.getImage(), 228, 0, null);
		
		//Draw monster image
		ImageIcon monImage = new ImageIcon();
		if(monFile != null){
			monImage = new ImageIcon("" + monsterFolder + monFile.getImage());
			g.drawImage(monImage.getImage(), monFile.getX(), monFile.getY(), null);
			monFile.updateFile();
		}

		//Draw Treasure image
		ImageIcon treImage = new ImageIcon();
		if(treFile != null){
			treImage = new ImageIcon("" + treasureFolder + treFile.getFileName());
			g.drawImage(treImage.getImage(), treFile.getX(), treFile.getY(), null);
		}
		
		if (player.getHealth() <= 0){
			g.setColor(Color.RED);
			Font f = new Font("Helvetica", Font.BOLD, 100);
			g.setFont(f);
			g.drawString("GAME OVER", 100, 300);
			GamePlay.playGameOverSound();
		}
		
		int numbRows = maze.getRows();
		int numbCols = maze.getColumns();
		
//		boolean result = false;
//		for (int r = 0; r < numbRows; r++){
//			for (int c = 0; c < numbCols; c++){
//				GameTile temp = maze.getGameTile(r, c);
//				if ((temp.isMonster() == false) && (temp.isTreasure()==false)){
//					result = true;
//				}
//			}
//		}
//		
//		System.out.println(result);
//		
//		if (result == true){
//			Font f = new Font("Helvetica", Font.BOLD, 100);
//			g.setFont(f);
//			//g.drawString("YOU WIN", 100, 300);
//		}
	}
}