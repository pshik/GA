package view;

import javax.swing.*;
import java.awt.*;

class ViewCellHelper {
    private static final int TEXT_SIZE = 9;
    private static final int HEIGHT = 29;
    private static int WIDTH_1;
    private static int WIDTH_2;
    private static int WIDTH_3;
    private final static Color BUSY_COLOR = new Color(123,104,238);
    private final static Color BUSY_COLOR_FREE_CELL = new Color(192,192,192);
    //private final static Color BUSY_TEXT_COLOR = Color.WHITE;
    private final static Color BUSY_TEXT_COLOR = new Color(123,104,238);
    private final static Color AVAILABLE_CEll = new Color(50,205,50);
    private final static Color DEFAULT_COLOR = new Color(255,228,225);
    private final static Color HIGHLIGHT_FIFO = new Color(255,165,0);
    private final static Color HIGHLIGHT = new Color(46,139,87);
    private final static Color BLOCKED_COLOR = new Color(0,0,0);
    private static Icon iconPallet = null;
    private static Icon iconEmpty = null;
   // private int prevNum = 0;
    private Color prevFillColorB1 =null;
    private Color prevTextColorB1 =null;
    private Color prevFillColorB2 =null;
    private Color prevTextColorB2 =null;
    private Color prevFillColorB3 =null;
    private Color prevTextColorB3 =null;
    private Color prevFillColorB4 =null;
    private Color prevTextColorB4 =null;
    private Color prevFillColorB5 =null;
    private Color prevTextColorB5 =null;
    private Color prevFillColorB6 =null;
    private Color prevTextColorB6 =null;
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
        this.iconPallet = new ImageIcon(getClass().getResource("/icons/pallet.png"));
        this.iconEmpty = new ImageIcon(getClass().getResource("/icons/empty.png"));
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
        button1.setBorder(BorderFactory.createEtchedBorder(1));
        button2.setBorder(BorderFactory.createEtchedBorder(1));
        button3.setBorder(BorderFactory.createEtchedBorder(1));
        button4.setBorder(BorderFactory.createEtchedBorder(1));
        button5.setBorder(BorderFactory.createEtchedBorder(1));
        button6.setBorder(BorderFactory.createEtchedBorder(1));
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
            tmp.setIcon(iconEmpty);
        } else if (label.startsWith("#")){
            tmp.setText("<HTML></HTML>");
            tmp.setBackground(BUSY_COLOR_FREE_CELL);
         //   tmp.setForeground(BUSY_COLOR_FREE_CELL);
            tmp.setIcon(iconEmpty);
        } else if (label.startsWith("*")){
            tmp.setText("<HTML></HTML>");
            tmp.setBackground(AVAILABLE_CEll);
            tmp.setIcon(iconEmpty);
        } else if (label.startsWith("@")){
            tmp.setText("<HTML>" + label.substring(1) + "</HTML>");
            tmp.setForeground(HIGHLIGHT_FIFO);
            tmp.setIcon(iconPallet);
            tmp.setBackground(HIGHLIGHT_FIFO);
        } else if (label.startsWith("^")){
            tmp.setText("<HTML>" + label.substring(1) + "</HTML>");
            tmp.setForeground(HIGHLIGHT);
            tmp.setBackground(HIGHLIGHT);
            tmp.setIcon(iconPallet);
        } else if (label.startsWith("=")){
            tmp.setText("<HTML></HTML>");
            tmp.setBackground(BLOCKED_COLOR);
            tmp.setIcon(iconEmpty);
        } else {
            tmp.setText("<HTML>" + label + "</HTML>");
            tmp.setBackground(BUSY_COLOR);
            tmp.setForeground(BUSY_TEXT_COLOR);
            tmp.setIcon(iconPallet);
        }
    }
    public void setDefaultBackgrounds(){
        button1.setBackground(DEFAULT_COLOR);
        button2.setBackground(DEFAULT_COLOR);
        button3.setBackground(DEFAULT_COLOR);
        button4.setBackground(DEFAULT_COLOR);
        button5.setBackground(DEFAULT_COLOR);
        button6.setBackground(DEFAULT_COLOR);
        button1.setIcon(iconEmpty);
        button2.setIcon(iconEmpty);
        button3.setIcon(iconEmpty);
        button4.setIcon(iconEmpty);
        button5.setIcon(iconEmpty);
        button6.setIcon(iconEmpty);
    }

    public void setDefaultText() {
        button1.setText("");
        button2.setText("");
        button3.setText("");
        button4.setText("");
        button5.setText("");
        button6.setText("");
    }

    public void selectCell(int i) {
      //  prevNum = i;
        switch (i){
            case 0:
                prevFillColorB1 = button1.getBackground();
                prevTextColorB1 = button1.getForeground();
                removeSelection();
                button1.setBackground(Color.cyan);
                button1.setForeground(Color.cyan);
                break;
            case 1:
                prevFillColorB2 = button2.getBackground();
                prevTextColorB2 = button2.getForeground();
                removeSelection();
                button2.setBackground(Color.cyan);
                button2.setForeground(Color.cyan);
                break;
            case 2:
                prevFillColorB3 = button3.getBackground();
                prevTextColorB3 = button3.getForeground();
                removeSelection();
                button3.setBackground(Color.cyan);
                button3.setForeground(Color.cyan);
                break;
            case 3:
                prevFillColorB4 = button4.getBackground();
                prevTextColorB4 = button4.getForeground();
                removeSelection();
                button4.setBackground(Color.cyan);
                button4.setForeground(Color.cyan);
                break;
            case 4:
                prevFillColorB5 = button5.getBackground();
                prevTextColorB5 = button5.getForeground();
                removeSelection();
                button5.setBackground(Color.cyan);
                button5.setForeground(Color.cyan);
                break;
            case 5:
                prevFillColorB6 = button6.getBackground();
                prevTextColorB6 = button6.getForeground();
                removeSelection();
                button6.setBackground(Color.cyan);
                button6.setForeground(Color.cyan);
                break;
        }
    }

    public void removeSelection(){
        if(button1.getBackground() == Color.cyan){
            button1.setBackground(prevFillColorB1);
            button1.setForeground(prevTextColorB1);
        } else if (button2.getBackground() == Color.cyan) {
            button2.setBackground(prevFillColorB2);
            button2.setForeground(prevTextColorB2);
        } else if (button3.getBackground() == Color.cyan) {
            button3.setBackground(prevFillColorB3);
            button3.setForeground(prevTextColorB3);
        } else if (button4.getBackground() == Color.cyan) {
            button4.setBackground(prevFillColorB4);
            button4.setForeground(prevTextColorB4);
        } else if (button5.getBackground() == Color.cyan) {
            button5.setBackground(prevFillColorB5);
            button5.setForeground(prevTextColorB5);
        } else if (button6.getBackground() == Color.cyan) {
            button6.setBackground(prevFillColorB6);
            button6.setForeground(prevTextColorB6);
        }
    }
}
