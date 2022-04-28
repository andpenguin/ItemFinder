package com.and_penguin;

import kaptainwutax.mcutils.util.data.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WindowFrame {
    public JFrame jFrame;

    public WindowFrame() {
        jFrame = new JFrame();
        try {
            File img = new File("./images/egap.png");
            Image icon = ImageIO.read(img);
            final Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(icon);
        }
        catch (Exception e) {
            System.out.println(e);
        }

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String[] enchantNames = Main.getEnchantNames();


//        Font font = null;
//        try {
//            font = Font.createFont(Font.TRUETYPE_FONT, new File("MinecraftiaRegular.ttf"));
//            GraphicsEnvironment ge =
//                    GraphicsEnvironment.getLocalGraphicsEnvironment();
//            ge.registerFont(font);
//        } catch (IOException |FontFormatException e) {
//            System.out.println(e);
//        }

        JButton button = new JButton("Done");
        button.setBounds(160, 120, 90, 20);
//        button.setFont(font.deriveFont(Font.BOLD, 12f));
        JLabel output = new JLabel();
        output.setBounds(60, 120, 500, 100);
//        output.setFont(font.deriveFont(Font.BOLD, 12f));
        JLabel seedText = new JLabel();
        seedText.setBounds(20,20,80,20);
        seedText.setText("Seed:");
//        seedText.setFont(font.deriveFont(Font.BOLD, 12f));
        JTextField seedInput = new JTextField(20);
        seedInput.setBounds(120, 20, 180, 20);
//        seedInput.setFont(font.deriveFont(Font.BOLD, 12f));
        JTextField item = new JTextField(20);
        item.setBounds(120,45,180,20);
//        item.setFont(font.deriveFont(Font.BOLD, 12f));
        JLabel itemText = new JLabel();
        itemText.setBounds(20,45,80,20);
        itemText.setText("Item Name:");
//        itemText.setFont(font.deriveFont(Font.BOLD, 12f));
        JComboBox<String> enchantments = new JComboBox<>(enchantNames);
        enchantments.setBounds(120, 70, 180, 20);
//        enchantments.setFont(font.deriveFont(Font.BOLD, 12f));
        JTextField level = new JTextField(10);
        level.setBounds(300,70,40,20);
//        level.setFont(font.deriveFont(Font.BOLD, 12f));
        JLabel enchantText = new JLabel();
        enchantText.setBounds(20, 70, 100, 20);
        enchantText.setText("Enchant/lvl:");
//        enchantText.setFont(font.deriveFont(Font.BOLD, 12f));

        jFrame.add(button);
        jFrame.add(output);
        jFrame.add(seedText);
        jFrame.add(seedInput);
        jFrame.add(item);
        jFrame.add(itemText);
        jFrame.add(enchantments);
        jFrame.add(level);
        jFrame.add(enchantText);

        jFrame.getContentPane().setBackground(new Color(212,212,212));
        jFrame.setTitle("Item Finder");
        jFrame.setLayout(null);
        jFrame.setSize(400, 400);

        button.addActionListener(e -> {
            ArrayList<Pair<String, Integer>> enchants;
            String enchantName = (String) enchantments.getSelectedItem();
            if (level.getText().equals("") || enchantName.equals("none"))
                enchants = new ArrayList<>();
            else
                enchants = Main.getEnchantByName(enchantName, Integer.parseInt(level.getText()));
            long seed = Long.parseLong(seedInput.getText());
            Main.getItemByName(item.getText());
            String location = Main.findItemOnSeed(seed, enchants);
            output.setText("Seed: " + seed + " - Location: " + location);
        });
    }

    public void display() {
        jFrame.setVisible(true);
    }
}
