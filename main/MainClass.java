package main;
import main.Game;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainClass extends JFrame implements KeyListener {

    private static final long serialVersionUID = 1L;
    private JPanel panel;
    private JLabel label;

    public MainClass() {
        super("The Jon Snow Chronicles");

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panel = new JPanel();
        panel.setLayout(new FlowLayout());

        ImageIcon image = new ImageIcon("/Users/philipradeff/eclipse-workspace/JonSnow/res/splashScreen.png");
        label = new JLabel(image);

        panel.setPreferredSize(new Dimension(1040, 560));
        label.setPreferredSize(new Dimension(1040, 560));
        panel.add(label);
        add(panel, BorderLayout.CENTER);
        addKeyListener(this);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainClass();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            new Game();
            dispose();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
