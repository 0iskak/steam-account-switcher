package app;

import lombok.Getter;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

public class GUI {
    @Getter
    private static Frame frame;
    @Getter
    private static final BottomPanel bottomPanel = new BottomPanel();
    private static final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    @Getter
    private static final JScrollPane pane = new JScrollPane(panel);

    public static void setup() {
        pane.setBorder(null);
        User.getUsers().forEach(panel::add);

        frame = new Frame();
    }
}

class Frame extends JFrame {
    private static final Dimension SIZE = new Dimension(500, 300);
    private static final String TITLE = "Steam Account Switcher";
    private static final Path LOGO = Path.of("img/logo");

    @SneakyThrows
    public Frame() {
        setLayout(new BorderLayout());
        setTitle(TITLE);
        setSize(SIZE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(GUI.getPane(), BorderLayout.CENTER);
        add(GUI.getBottomPanel(), BorderLayout.SOUTH);


        var icons = new ArrayList<Image>();
        for (File f : Objects.requireNonNull(LOGO.toFile().listFiles()))
            icons.add(ImageIO.read(f));
        setIconImages(icons);
    }
}

class BottomPanel extends JPanel{
    private final JLabel nameLabel = new JLabel("Ready");

    private User user;

    public BottomPanel() {
        super(new BorderLayout());

        setBorder(new EmptyBorder(3, 10, 3, 10));

        add(nameLabel, BorderLayout.WEST);

        JButton switchButton = new JButton("Switch");
        add(switchButton, BorderLayout.EAST);
        switchButton.addActionListener(this::actionPerformed);
    }

    private void actionPerformed(ActionEvent e) {
        Main.switchTo(user);
    }

    public void selected(User user) {
        this.user = user;
        nameLabel.setText("Selected: "+user.getName());
    }
}
