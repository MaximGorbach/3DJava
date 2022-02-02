package renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;


public class Rect  {
        private int x;
        private int y;
        private int dx;
        private int dy;
        private int width;
        private int height;
        private Color col = new Color(100,100,0);
    
        public Rect(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
        }
        
        public void move(){
            this.x += dx;
            this.y += dy;
        }

        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();
    
            if (key == KeyEvent.VK_LEFT) {
                dx = -2;
            }
    
            if (key == KeyEvent.VK_RIGHT) {
                dx = 2;
            }
    
            if (key == KeyEvent.VK_UP) {
                dy = -2;
            }
    
            if (key == KeyEvent.VK_DOWN) {
                dy = 2;
            }
        }
    
        public void keyReleased(KeyEvent e) {
            
            int key = e.getKeyCode();
    
            if (key == KeyEvent.VK_LEFT) {
                dx = 0;
            }
    
            if (key == KeyEvent.VK_RIGHT) {
                dx = 0;
            }
    
            if (key == KeyEvent.VK_UP) {
                dy = 0;
            }
    
            if (key == KeyEvent.VK_DOWN) {
                dy = 0;
            }
        }

        public void draw(Graphics g){
            g.setColor(this.col);
            g.fillRect(this.x, this.y, this.width, this.height);
        }
}