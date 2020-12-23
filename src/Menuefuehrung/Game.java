package Menuefuehrung;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class Game extends JPanel implements ActionListener, KeyListener{
    Timer t = new Timer(5,this);
    double x =300, y = 300, velx = 0, vely = 0;
    public Game(){
        t.start();
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.fill(new RoundRectangle2D.Double(x,y,40,40,3,3));
    }
    public void actionPerformed(ActionEvent e){
        repaint();
        x+=velx;
        y+=vely;
    }
    public void up(){
        if (y<5){ vely = 0;}
        else{
            vely = -2;
        }

    }
    public void down(){
        if (y>700){ vely = 0;}
        else{
            vely = 2;
        }
    }

    public void left(){
        if (x<5){ velx = 0;}
        else{
            velx = -2;
        }
    }
    public void right(){
        if (x>900){ velx = 0;}
        else{
            velx = 2;
        }

    }
    public void keyPressed(KeyEvent e){
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_UP){

            up();
        }
        if(code == KeyEvent.VK_DOWN){

            down();
        }
        if(code == KeyEvent.VK_LEFT){
            left();
        }
        if(code == KeyEvent.VK_RIGHT){
            right();
        }
    }

    public void keyTyped(KeyEvent e){}
    public void keyReleased(KeyEvent e){
        velx = 0;
        vely = 0;
    }
}
