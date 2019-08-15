package view;

import javax.swing.*;
import java.awt.*;

class ViewCellHelper {
    private static final int TEXT_SIZE = 7;
    private static final int HEIGHT = 30;
    private static int WIDTH_1;
    private static int WIDTH_2;
    private static int WIDTH_3;
    private final static Color BUSY_COLOR = new Color(123,104,238);
    private final static Color BUSY_COLOR_FREE_CELL = new Color(192,192,192);
    private final static Color BUSY_TEXT_COLOR = Color.WHITE;
    private final static Color AVAILABLE_CEll = new Color(50,205,50);
    private final static Color DEFAULT_COLOR = new Color(255,228,225);
    private final static Color HIGHLIGHT_FIFO = new Color(255,165,0);
    private final static Color HIGHLIGHT = new Color(46,139,87);
    private final static Color BLOCKED_COLOR = new Color(0,0,0);

    private  JButton button1;
    private  JButton button2;
    private  JButton button3;
    private  JButton button4;
    private  JButton button5;
    private  JButton button6;
    private  JPanel panel;

    public ViewCellHelper(JPanel panel, JButton button1, JButton button2, JButton button3, JButton button4, JButton button5, JButton button6, int fullWidth, int column) {
        this.button1 = button1;
        this.button2 = button2;
        this.button3 = button3;
        this.button4 = button4;
        this.button5 = button5;
        this.button6 = button6;
        this.panel = panel;
        WIDTH_1 = (fullWidth - 20) / column;
        WIDTH_2 = WIDTH_1/2;
        WIDTH_3 = WIDTH_1/3;
    }

    public void addButtons(){
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridwidth = 3;
        constraints.gridy = 0;
        constraints.gridx = 0;
        panel.add(button1, constraints);

        constraints.gridwidth = 2;
        constraints.gridy = 1;
        constraints.gridx = 0;

        constraints.insets    = new Insets(0, 0, 0, WIDTH_3*2 -WIDTH_2);
        panel.add(button2, constraints);

        constraints.gridwidth = 2;
        constraints.gridy = 1;
        constraints.gridx = 1;
        constraints.insets    = new Insets(0, WIDTH_3*2 -WIDTH_2, 0, 0);
        panel.add(button3, constraints);

        constraints.insets    = new Insets(0, 0, 0, 0);
        constraints.gridwidth = 1;
        constraints.gridy = 2;
        constraints.gridx = 0;
        panel.add(button4, constraints);

        constraints.gridwidth = 1;
        constraints.gridy = 2;
        constraints.gridx = 1;
        panel.add(button5, constraints);

        constraints.gridwidth = 1;
        constraints.gridy = 2;
        constraints.gridx = 2;
        panel.add(button6, constraints);
    }
    public void configureButtons() {
        button1.setOpaque(true);
        button2.setOpaque(true);
        button3.setOpaque(true);
        button4.setOpaque(true);
        button5.setOpaque(true);
        button6.setOpaque(true);
        button1.setPreferredSize(new Dimension(WIDTH_1,HEIGHT));
        button2.setPreferredSize(new Dimension(WIDTH_2,HEIGHT));
        button3.setPreferredSize(new Dimension(WIDTH_2,HEIGHT));
        button4.setPreferredSize(new Dimension(WIDTH_3,HEIGHT));
        button5.setPreferredSize(new Dimension(WIDTH_3,HEIGHT));
        button6.setPreferredSize(new Dimension(WIDTH_3,HEIGHT));
        button1.setMaximumSize(new Dimension(WIDTH_1,HEIGHT));
        button2.setMaximumSize(new Dimension(WIDTH_2,HEIGHT));
        button3.setMaximumSize(new Dimension(WIDTH_2,HEIGHT));
        button4.setMaximumSize(new Dimension(WIDTH_3,HEIGHT));
        button5.setMaximumSize(new Dimension(WIDTH_3,HEIGHT));
        button6.setMaximumSize(new Dimension(WIDTH_3,HEIGHT));
        button1.setMinimumSize(new Dimension(WIDTH_1,HEIGHT));
        button2.setMinimumSize(new Dimension(WIDTH_2,HEIGHT));
        button3.setMinimumSize(new Dimension(WIDTH_2,HEIGHT));
        button4.setMinimumSize(new Dimension(WIDTH_3,HEIGHT));
        button5.setMinimumSize(new Dimension(WIDTH_3,HEIGHT));
        button6.setMinimumSize(new Dimension(WIDTH_3,HEIGHT));
        button1.setFont(new Font("Arial",Font.PLAIN,TEXT_SIZE));
        button2.setFont(new Font("Arial",Font.PLAIN,TEXT_SIZE));
        button3.setFont(new Font("Arial",Font.PLAIN,TEXT_SIZE));
        button4.setFont(new Font("Arial",Font.PLAIN,TEXT_SIZE));
        button5.setFont(new Font("Arial",Font.PLAIN,TEXT_SIZE));
        button6.setFont(new Font("Arial",Font.PLAIN,TEXT_SIZE));
    }
    public void renderButton(String label,int buttonNum){
        JButton tmp = null;
        switch (buttonNum){
            case 1:
                tmp = button1;
                break;
            case 2:
                tmp = button2;
                break;
            case 3:
                tmp = button3;
                break;
            case 4:
                tmp = button4;
                break;
            case 5:
                tmp = button5;
                break;
            case 6:
                tmp = button6;
                break;
            default:
              //  LoggerFiFo.getInstance().getRootLogger().debug(this.getClass().getSimpleName() + ": Button tmp is null or not equal 1-6.");
                break;
        }
        if (label.equals(" ")) {
            tmp.setText("<HTML></HTML>");
        } else if (label.startsWith("#")){
            tmp.setText("<HTML></HTML>");
            tmp.setBackground(BUSY_COLOR_FREE_CELL);
        } else if (label.startsWith("*")){
            tmp.setText("<HTML></HTML>");
            tmp.setBackground(AVAILABLE_CEll);
        } else if (label.startsWith("@")){
            tmp.setText("<HTML>" + label.substring(1) + "</HTML>");
            tmp.setForeground(Color.BLACK);
            tmp.setBackground(HIGHLIGHT_FIFO);
        } else if (label.startsWith("^")){
            tmp.setText("<HTML>" + label.substring(1) + "</HTML>");
            tmp.setForeground(Color.BLACK);
            tmp.setBackground(HIGHLIGHT);
        } else if (label.startsWith("=")){
            tmp.setText("<HTML></HTML>");
            tmp.setBackground(BLOCKED_COLOR);
        } else {
            tmp.setText("<HTML>" + label + "</HTML>");
            tmp.setBackground(BUSY_COLOR);
            tmp.setForeground(BUSY_TEXT_COLOR);
        }
    }
    public void setDefaultBackgrounds(){
        button1.setBackground(DEFAULT_COLOR);
        button2.setBackground(DEFAULT_COLOR);
        button3.setBackground(DEFAULT_COLOR);
        button4.setBackground(DEFAULT_COLOR);
        button5.setBackground(DEFAULT_COLOR);
        button6.setBackground(DEFAULT_COLOR);
    }

    public void setDefaultText() {
        button1.setText("");
        button2.setText("");
        button3.setText("");
        button4.setText("");
        button5.setText("");
        button6.setText("");
    }
}
