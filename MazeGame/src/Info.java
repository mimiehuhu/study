import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;


public class Info extends JPanel{

	ImageIcon attackButton;
	ImageIcon movementButtons;
	
	Player p;
	
	char compassDirection;
	
	public Info(Player p){
		Dimension d = new Dimension(450,200);
		this.setPreferredSize(d);
		this.p = p;
		this.setBackground(new Color(200, 192, 232));
		
		attackButton = new ImageIcon("attackButton.png");
		movementButtons = new ImageIcon("movement.png");
	}

	public void paint(Graphics g){
		super.paint(g);
		
		//Draw Attack and Movement Buttons
		g.drawImage(attackButton.getImage(), 285, 20, null);
		g.drawImage(movementButtons.getImage(), 270, 75, null);
		
		//Health Bar
		int healthBar = p.getHealth();
		g.drawRect(16, 39, 201, 27);
		g.drawString("Health", 22, 25);
		if (healthBar >= 67)
			g.setColor(Color.green);
		else if ((healthBar < 67) && (healthBar > 33))
			g.setColor(Color.yellow);
		else if (healthBar <= 33)
			g.setColor(Color.red);
		//g.setColor(new Color(0, 255, 0));
		g.fillRect(17, 40, 2*healthBar-1, 25);
		
		//Treasure Count
		int treasureCount = p.getTreasures();
		String treCount = Integer.toString(treasureCount);
		g.setColor(Color.black);
		g.drawString("Treasure", 22, 110);
		g.drawString(treCount, 22, 130);
		
		//Draw Compass
		String pDirection = p.getPlayerDirection();
		compassDirection = pDirection.charAt(0);
		g.fillRect(234, 55, 22, 5);
		g.fillRect(242, 45, 5, 30);
		
		if(pDirection == "N"){
			g.drawString("N", 240, 37 );
		}
		else if(pDirection == "S"){
			g.drawString("N", 240, 90);
		}
		else if (pDirection == "W"){
			g.drawString("N", 220, 62);
		}
		else if (pDirection == "E"){
			g.drawString("N", 257, 62);
		}
	}

	public void update(Player temp){
		this.p = temp;
	}
}