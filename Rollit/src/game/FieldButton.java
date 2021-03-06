package game;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JButton;

public class FieldButton extends JButton {
    private static final long serialVersionUID = 1911931693968045137L;
    
    private Color color;
    private Color bgColor;
    
    public FieldButton() {
            this(false);
    }
    
    public FieldButton(boolean paintBorder) {
            color = Color.GRAY;
            bgColor = Color.BLACK;
            setBorderPainted(paintBorder);
    }
    
    public void setBackgroundColor(Color c) {
            bgColor = c;
    }
    
    public Color getBackgroundColor() {
            return bgColor;
    }
    
    public void setColor(Color c) {
            color = c;
    }
    
    public Color getColor() {
            return color;
    }
    
    @Override
    public void paintComponent(Graphics g) {
            Graphics graph = g.create();
            graph.setColor(bgColor);
            int width = getWidth();
            int height = getHeight();
            /*if (width > height) {
            	width = height;        	
            } else if (height > width) {
            	height = width;
            }  */        
            graph.fillRect(0, 0, width, height);
            graph.setColor(color);
            graph.fillOval(2, 2, width - 4, height - 4);
    }
}