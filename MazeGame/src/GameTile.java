import java.awt.Color;
import java.util.Hashtable;

public class GameTile {

	//Object attributes
	public boolean hasTreasure, hasMonster, hasPlayer, hasExit, isVisible, hasNWall, hasEWall, hasSWall, hasWWall;
	
	//Object Instantiation
	public Monster mTemp;
	public Treasure tTemp;
	public Player p;
	String mName;
	String tName;
	String Texture;
	
	public GameTile(String mon, String tre, String wNorth, String wEast, String wSouth, String wWest, String texture, Hashtable<String,Monster>monsters, Hashtable<String,Treasure> treasures, int xCord, int yCord)
	{
		
		mName = mon;
		tName = tre;
		
		int row = xCord;
		int column = yCord;
		
		//Monsters
		//If there is a monster, create it
		if(mName.equals("none") == false){
			hasMonster = true;

			//Create new monster variables using hashTable
			Monster tempM = monsters.get(mName);
			String monName = tempM.getName();
			String file1 = tempM.getFile(1);
			String file2 = tempM.getFile(2);
			int x = tempM.getX();
			int y = tempM.getY();
			int health = tempM.getHealth();
			int damage = tempM.getDamage();
			int coolDownTime = tempM.getCDT();
			int monHitP = tempM.getMonProb(); //probability monster hits player
			int pHitM = tempM.getPlayProb(); //probability player hits monster

			//Create instance of Game to pass into the Monster class
			GamePlay game = tempM.getGame();
			
			//Create monster
			mTemp = new Monster(game, monName, file1, file2, x, y, health, damage,coolDownTime, monHitP, pHitM);
			mTemp.setXY(row, column); //set tracker row,col
			
		}
		else{
			hasMonster = false;
		}
		//Treasures
		if(tName.equals("none") == false){
			hasTreasure = true;

			//Create new treasure variables using hashtable
			Treasure tempT = treasures.get(tName);
			String treName = tempT.getName();
			String file = tempT.getFileName();
			int gold = tempT.getValue(); //gets gold value
			int healthBoost = tempT.getHR();
			int height = tempT.getIHeight();
			int width = tempT.getIWidth();
			int x = tempT.getX();
			int y = tempT.getY();

			//Create treasure
			tTemp = new Treasure(treName, file, gold, healthBoost, width, height, x, y);
		}
		else
			hasTreasure = false;

		//Create Player object...temp set to null
		hasPlayer = false;
		p = null;
		Texture = texture;

		//Set wall booleans
		if(wNorth.equals("true")){
			hasNWall = true;
		}
		else{
			hasNWall = false;
		}
		
		if(wEast.equals("true")){
			hasEWall = true;
		}
		else {
			hasEWall = false;
		}
		
		if(wSouth.equals("true")){
			hasSWall = true;
		}
		else{
			hasSWall = false;
		}
		
		if(wWest.equals("true")){
			hasWWall = true;
		}
		else{
			hasWWall = false;
		}
		
	}

	public void addPlayer(Player p){
		this.p = p;
		hasPlayer = true;
	}
	
	public void removePlayer(){
		this.p = null;
		hasPlayer = false;
	}

	public String getTexture(){
		return Texture;
	}

	public boolean isMonster(){
		return hasMonster;
	}

	public boolean isTreasure(){
		return hasTreasure;
	}

	public String getMonName(){
		return mTemp.getName();
	}

	public String getTreName(){
		return tTemp.getName();
	}

	public Player getPlayer(){
		return p;
	}

	public boolean isPlayer(){
		return hasPlayer;
	}

	public boolean hasNorthWall(){
		return hasNWall;
	}

	public boolean hasSouthWall(){
		return hasSWall;
	}

	public boolean hasEastWall(){
		return hasEWall;
	}

	public boolean hasWestWall(){
		return hasWWall;
	}

	public Monster getMonster() {
		return mTemp;
	}
	
	public Treasure getTreasure(){
		return tTemp;
	}

	public void removeMonster() {
		hasMonster = false;
		mTemp = null;
		
	}

	public void addMonster(Monster mTemp) {
		hasMonster = true;
		this.mTemp = mTemp;
		
	}

	public void removeTreasure() {
		tTemp = null;
		hasTreasure = false;
		
	}
}
