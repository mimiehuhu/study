
public class Treasure {
	
	protected String name, file;
	protected int healthRestore, imageWidth, imageHeight, xCord, yCord, gold;
	protected boolean availability;

	public Treasure(String name, String fileName, int gold, int healthRestore, int imageWidth, int imageHeight, int xCord, int yCord){
		this.name = name;
		this.file = fileName;
		this.gold = gold;
		this.healthRestore = healthRestore;
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		this.xCord = xCord;
		this.yCord = yCord;
		availability = false;
	}
	
	public String getName(){
		return name;
	}
	
	public String getFileName(){
		return file;
	}
	
	public int getHR(){
		return healthRestore;
	}
	
	public int getValue(){
		return gold;
	}
	
	
	public boolean checkAvailability(){
		return availability;
	}
	
	public int getIWidth(){
		return imageWidth;
	}
	
	public int getIHeight(){
		return imageHeight;
	}
	
	public int getX(){
		return xCord;
	}
	
	public int getY(){
		return yCord;
	}
}
