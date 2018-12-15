import java.awt.*;
import javax.swing.*;

//Creates 
public class MiniMap extends JPanel{
	
	protected Maze maze;
	
	public MiniMap(Maze tempMaze){
		this.maze = tempMaze;
		Dimension d = new Dimension(350,205);
		this.setPreferredSize(d);
	}
	
	public void paint(Graphics g){
		//Rows and Columns
		int rows = maze.getRows();
		int columns = maze.getColumns();
		
		//MiniMap attributes
		int tileDimensions = 45; //size of each room, x and y
		int x = 5; //initial x starting point
		int y = 0; //initial y starting point
		
		//Cycle through each room and draw them according to the Maze
		for (int j = 0; j < rows; j++){
			for (int k = 0; k < columns; k++){
				//Get a GameTile and use that to correctly assign walls/colors
				GameTile tile = maze.getGameTile(j,k);
				String tileTexture = tile.getTexture();

				//Set wall colors
				if (tileTexture.equals("texture1"))
					g.setColor(Color.black); //Black
				else if (tileTexture.equals("texture2"))
					g.setColor(Color.green); //Green
				else if (tileTexture.equals("texture3"))
					g.setColor(Color.magenta); //Magenta
				else if (tileTexture.equals("texture4"))
					g.setColor(Color.blue); //Blue

				//Draw lines based on if n,s,e,w has a wall or not
				if (tile.hasNorthWall() == true)
					g.drawLine(x, y, x + tileDimensions, y);
				if (tile.hasWestWall() == true)
					g.drawLine(x, y, x, y + tileDimensions);
				if (tile.hasEastWall() == true)
					g.drawLine(x + tileDimensions, y, x + tileDimensions, y + tileDimensions);
				if (tile.hasSouthWall() == true)
					g.drawLine(x, y + tileDimensions, x + tileDimensions, y + tileDimensions);

				//Draw Treasures
				if (tile.isTreasure()){
					//Draw Chalice - yellow
					if (tile.getTreName().equals("Chalice")){
						//Get location
						int xLoc = x + (3/4)*tileDimensions+10;
						int yLoc = y + (1/4)*(tileDimensions)+5;
						
						g.setColor(Color.yellow);
						g.drawOval(xLoc, yLoc, 10, 10);
						g.fillOval(xLoc, yLoc, 10, 10);
					}
					//Draw Gem - red
					else if (tile.getTreName().equals("Gem")){
						//Get location
						int xLoc = x + (3/4)*tileDimensions+ 10;
						int yLoc = y + (1/4)*(tileDimensions)+5;
						
						g.setColor(Color.RED);
						g.drawOval(xLoc, yLoc, 10, 10);
						g.fillOval(xLoc, yLoc, 10, 10);
					}
					//Draw Gold Bag - black
					else if (tile.getTreName().equals("Gold Bag")){
						//Get Location
						int xLoc = x + (3/4)*tileDimensions+10;
						int yLoc = y + (1/4)*(tileDimensions)+5;

						g.setColor(Color.black);
						g.drawOval(xLoc, yLoc, 10, 10);
						g.fillOval(xLoc, yLoc, 10, 10);
					}
					//Draw Health Potion - blue
					else if (tile.getTreName().equals("Health Potion")){
						//Get location
						int xLoc = x+(3/4)*tileDimensions+10;
						int yLoc = y + (1/4)*(tileDimensions)+5;

						g.setColor(Color.blue);
						g.drawOval(xLoc, yLoc, 10, 10);
						g.fillOval(xLoc, yLoc, 10, 10);
					}
				}

				//Draw Monsters
				if (tile.isMonster()){
					g.setColor(Color.black);

					//Gets the first letter from the Monster name
					Character tempChar = new Character(tile
					.getMonName().charAt(0));
					String converter = tempChar.toString();

					//Draws the monster
					g.drawString(converter, x + (1/4)*tileDimensions+10, y + (1/4)*tileDimensions+35); //-10
				}

				//Draw Player
				if (tile.isPlayer() == true){
					g.setColor(Color.black);
					//Denote player with a 'U'
					g.drawString("U", x + (tileDimensions)-20, y+tileDimensions-10);
				}

				//Change x variable to make rooms all across the map
				x = x + tileDimensions + 3; //refers to the spacing between each room
				g.setColor(Color.black);
			}
			//Change y variable to make rooms all across the map
			x = 5; //starting x coodinate for each new row of rooms
			y = y + tileDimensions + 3; //refers to the spacing between each room and the room size
		}
	}
	
	public void update(Maze maze){
		this.maze = maze;
	}
}

