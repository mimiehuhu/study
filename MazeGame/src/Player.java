
public class Player {
	private int maxHealth, currHealth, attack, row, column, numTreasures;
	protected String playerDirection;
	public Player()
	{
		//Max health is set at 100. Current health starts out as 100, maxHealth.
		maxHealth = 100;
		currHealth = maxHealth;

		//Set attack
		attack = 9;
		
		//Keeps track of player location
		row = 0;
		column = 0;

		//Other player variables
		numTreasures = 0;
		playerDirection = "S";
	}
	
	public int getAttackValue(){
		return attack;
	}
	
	public int getRow(){
		return row; 
	}
	
	public int getCol(){
		return column;
	}
	
	//Sets the Players locatino to the given variables
	public void changeXY(int x, int y){
		row = x;
		column = y;
	}
	
	public int getHealth(){
		return currHealth;
	}
	
	public void incTreasure(){
		numTreasures = numTreasures + 1;
	}
	
	public int getTreasures(){
		return numTreasures;
	}
	
	public String getPlayerDirection(){
		return playerDirection;
	}

	public void setHealth(int attackValue){
		currHealth = currHealth - attackValue;
	}
	
	//Resets the direction of the player if they turn right
	public void turnRight(){
		if(playerDirection.equals("N"))
			playerDirection = "E";
		else if(playerDirection.equals("S"))
			playerDirection = "W";
		else if(playerDirection.equals("E"))
			playerDirection = "S";
		else if(playerDirection.equals("W"))
			playerDirection = "N";
	}
	
	//Resets the direction of the player if they turn left
	public void turnLeft(){
		if(playerDirection.equals("N"))
			playerDirection = "W";
		else if(playerDirection.equals("S"))
			playerDirection = "E";
		else if (playerDirection.equals("E"))
			playerDirection = "N";
		else if(playerDirection.equals("W"))
			playerDirection = "S";
	}

	//Update gold value from treasure
	public void updateGold(Treasure temp){
		numTreasures = numTreasures + temp.getValue();
	}

	//Update health value from treasure
	public void updateHealth(Treasure temp){
		int additionalHealth = temp.getHR();
		currHealth += additionalHealth;
		if (currHealth > maxHealth)
			currHealth = maxHealth;
	}
}
