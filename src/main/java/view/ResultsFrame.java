//package view;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.List;
//import entity.RegularUser;
//import entity.Recipe;
//
//public class ResultsFrame extends JFrame {
//    public ResultsFrame(List<Recipe> recipes, boolean isSurprise, RegularUser user) {
//        super(isSurprise ? "Surprise Recipe" : "Top Recipes");
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setSize(1000, 700);
//        setLocationRelativeTo(null);
//
//        JPanel root = new JPanel(new BorderLayout(10, 10));
//        root.setBorder(new EmptyBorder(12, 12, 12, 12));
//        setContentPane(root);
//
//        JLabel header = new JLabel(isSurprise ? "Here's a random pick for you" : "Top recipe picks");
//        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
//        root.add(header, BorderLayout.NORTH);
//
//        JPanel columns = new JPanel(new GridLayout(1, recipes.size(), 12, 12));
//        for (Recipe r : recipes) {
//            columns.add(makeRecipeCard(r, isSurprise));
//        }
//
//        JButton back = new JButton("Back to Meal Preferences");
//        back.addActionListener(e -> {
//            dispose();
//            new InputFrame(user).setVisible(true);
//        });
//
//        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        bottom.add(back);
//        add(bottom, BorderLayout.SOUTH);
//        setLocationRelativeTo(null);
//        setVisible(true);
//
//        JScrollPane scroll = new JScrollPane(columns);
//        scroll.getVerticalScrollBar().setUnitIncrement(16);
//        root.add(scroll, BorderLayout.CENTER);
//    }
//
//    private JComponent makeRecipeCard(Recipe r, boolean isSurprise) {
//        JPanel card = new JPanel();
//        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
//        card.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(0xDDDDDD)),
//                new EmptyBorder(12, 12, 12, 12)
//        ));
//
//        String titleTxt = r.getName() + "  [" + r.getDiet().label + "]";
//        if (isSurprise && r.getArea() != null && !r.getArea().isBlank()) {
//            titleTxt += "  •  " + r.getArea();
//        }
//        JLabel title = new JLabel("<html><b>" + escapeHtml(titleTxt) + "</b></html>");
//        title.setFont(title.getFont().deriveFont(16f));
//        title.setAlignmentX(Component.LEFT_ALIGNMENT);
//        card.add(title);
//        card.add(Box.createVerticalStrut(8));
//
//        JLabel ingHeader = new JLabel("Ingredients");
//        ingHeader.setFont(ingHeader.getFont().deriveFont(Font.BOLD));
//        ingHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
//        card.add(ingHeader);
//
//        JTextArea ingArea = new JTextArea(String.join("\n", prefix(r.getIngredients(), "• ")));
//        ingArea.setEditable(false);
//        ingArea.setLineWrap(true);
//        ingArea.setWrapStyleWord(true);
//        ingArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
//        JScrollPane ingScroll = new JScrollPane(ingArea);
//        ingScroll.setPreferredSize(new Dimension(280, 160));
//        ingScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
//        card.add(ingScroll);
//        card.add(Box.createVerticalStrut(8));
//
//        JLabel instrHeader = new JLabel("Instructions");
//        instrHeader.setFont(instrHeader.getFont().deriveFont(Font.BOLD));
//        instrHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
//        card.add(instrHeader);
//
//        List<String> numbered = new ArrayList<>();
//        for (int i = 0; i < r.getInstructions().size(); i++) {
//            numbered.add((i + 1) + ". " + r.getInstructions().get(i));
//        }
//        JTextArea instArea = new JTextArea(String.join("\n\n", numbered));
//        instArea.setEditable(false);
//        instArea.setLineWrap(true);
//        instArea.setWrapStyleWord(true);
//        instArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
//        JScrollPane instScroll = new JScrollPane(instArea);
//        instScroll.setPreferredSize(new Dimension(280, 300));
//        instScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
//        card.add(instScroll);
//
//        return card;
//    }
//
//    private static List<String> prefix(List<String> xs, String p) {
//        List<String> out = new ArrayList<>(xs.size());
//        for (String x : xs) out.add(p + x);
//        return out;
//    }
//
//    private static String escapeHtml(String s) {
//        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
//    }
//}