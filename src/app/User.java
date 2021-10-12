package app;

import lombok.Getter;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

@Getter
public class User extends JPanel {
    @Getter
    private static final List<User> users = new ArrayList<>();

    private static final Path USERS = Path.of("C:/Program Files (x86)/Steam/config/loginusers.vdf");
    private static final String IMAGE = "img/profile/{username}.jpg";
    private static final String URL = "https://steamcommunity.com/profiles/{id}?xml=1";

    private static final Dimension IMAGE_SIZE = new Dimension(100, 100);
    private static final float USERNAME_SIZE = 11;
    private static final float NAME_SIZE = 15;
    private static final float TIME_SIZE = 10;
    private static final Insets BORDER_INSETS = new Insets(2, 2, 2, 2);

    @SneakyThrows
    public static void parse() {
        String username = "AccountName";
        String name = "PersonaName";
        String time = "Timestamp";

        Scanner scanner = new Scanner(Files.readString(USERS));

        User user = new User();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim().replaceAll("\"", "");

            if (Character.isDigit(line.charAt(0)))
                user.id = line;
            else if (line.contains(username))
                user.username = get(line, username);
            else if (line.contains(name))
                user.name = get(line, name);
            else if (line.contains(time)) {
                user.time = get(line, time);
                users.add(user);
                user.paint();
                user = new User();
            }
        }
    }

    private static String get(String line, String value) {
        return line.replace(value, "")
                .trim();
    }

    private String id, username, name, time;

    @SneakyThrows
    private void paint() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.gridx = 0;

        if (!new File(IMAGE.replace("{username}", username)).exists())
            getImage();

        var image = ImageIO.read(new File(IMAGE.replace("{username}", username)))
                .getScaledInstance(IMAGE_SIZE.width, IMAGE_SIZE.height, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        c.insets = new Insets(5, 0, 0, 0);
        add(imageLabel, c);
        c.gridy++;
        c.insets = new Insets(0, 0, 0, 0);

        JLabel usernameLabel = new JLabel(username, SwingConstants.CENTER);
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(USERNAME_SIZE));
        setDefaultSize(usernameLabel);
        add(usernameLabel, c);
        c.gridy++;

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, NAME_SIZE));
        setDefaultSize(nameLabel);
        add(nameLabel, c);
        c.gridy++;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());
        JLabel timeLabel = new JLabel(dateFormat.format(Long.parseLong(time)*1000),
                SwingConstants.CENTER);
        timeLabel.setFont(timeLabel.getFont().deriveFont(TIME_SIZE));
        setDefaultSize(timeLabel);
        c.insets = new Insets(0, 0, 5, 0);
        add(timeLabel, c);
        c.gridy++;

        setPreferredSize(new Dimension(IMAGE_SIZE.width+10, getPreferredSize().height));
        setBorder(new EmptyBorder(BORDER_INSETS));
        addMouseListener(new MouseAdapter() {
            final Color color = Color.decode("#2C3E50");
            @Override
            public void mousePressed(MouseEvent e) {
                users.forEach(u -> u.setBorder(new EmptyBorder(BORDER_INSETS)));
                setBorder(new LineBorder(color.darker(), BORDER_INSETS.bottom));

                GUI.getBottomPanel().selected(User.this);
            }
        });
    }

    private void setDefaultSize(JLabel label) {
        Dimension size = new Dimension(IMAGE_SIZE.width, label.getPreferredSize().height);
        label.setMinimumSize(size);
        label.setMaximumSize(size);
        label.setPreferredSize(size);
    }

    @SneakyThrows
    private void getImage() {
        var object = new URL(URL.replace("{id}", id)).openConnection().getInputStream();

        Scanner scanner = new Scanner(object);

        String url;
        while (scanner.hasNextLine()) {
            if (!(url = scanner.nextLine()).contains("full.jpg"))
                continue;
            url = url.replace("<avatarFull><![CDATA[", "")
                    .replace("]]></avatarFull>", "").trim();

            var image = new URL(url).openConnection().getInputStream();
            Files.copy(image, Path.of(IMAGE.replace("{username}", username)));
            break;
        }
    }

}