package view;

import javax.swing.*;
import java.awt.*;

public class MyViewCellButton extends JButton {

    private Color hoverBackgroundColor;
    private Color pressedBackgroundColor;

    public MyViewCellButton() {
        this(null);
    }

    public MyViewCellButton(String text) {
        super(text);
        super.setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isPressed()) {
            g.setColor(pressedBackgroundColor);
        } else if (getModel().isRollover()) {
            g.setColor(hoverBackgroundColor);
        } else {
            g.setColor(getBackground());
        }

        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawString(getText(),0,0);
     //   super.paintComponent(g);
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        super.setContentAreaFilled(false);
    }

    @Override
    public void setContentAreaFilled(boolean b) {
    }

    public Color getHoverBackgroundColor() {
        return hoverBackgroundColor;
    }

    public void setHoverBackgroundColor(Color hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
    }

    public Color getPressedBackgroundColor() {
        return pressedBackgroundColor;
    }

    public void setPressedBackgroundColor(Color pressedBackgroundColor) {
        this.pressedBackgroundColor = pressedBackgroundColor;
    }
}
