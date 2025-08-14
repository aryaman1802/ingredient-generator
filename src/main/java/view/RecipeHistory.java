package view;

import entity.RegularUser;
import org.example.MongoMealDB;
import entity.RegularUser;
import view.MealPreferences;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

import javax.swing.*;
import java.awt.*;

import static javax.swing.BoxLayout.Y_AXIS;

public class RecipeHistory extends JFrame {

    public RecipeHistory(RegularUser user) {
        //Making the main frame
        setTitle("History");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(222,333);
        setLayout(new BorderLayout());

        //Gets the amount of times the user has generated recipes
        int size = MongoMealDB.numEntry(user);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, Y_AXIS));
        buttonPanel.setAlignmentX(LEFT_ALIGNMENT);

        String connectionString = "mongodb+srv://cx33366:Password@cluster0.wdexumy.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Send a ping to confirm a successful connection
                MongoDatabase database = mongoClient.getDatabase("admin");
                database.runCommand(new Document("ping", 1));
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");

                // Find the user in the collection
                MongoDatabase appDb = mongoClient.getDatabase("Cluster0");
                Document person = appDb.getCollection("users").find((eq("username", user.getUsername())))
                        .first();
                assert person != null;
                List<String> time = person.getList("timestamp1", String.class);

                //Loops through all the timestamps of when the user generated recipes and setting that as the button text
                //Then Linking each button to retrieve the recipe generated and display it using TopRecipes Frame
                for (int i = 0; i<size; i++) {
                    JButton entry = new JButton(time.get(i));
                    entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                    entry.setAlignmentX(Component.LEFT_ALIGNMENT);

                    buttonPanel.add(Box.createVerticalStrut(5));
                    buttonPanel.add(entry);
                    int finalI = i;
                    entry.addActionListener(e -> {
                        MongoMealDB.makeRecipe(user, finalI);
                        dispose();
                    });
                };

            } catch (MongoException e) {
                e.printStackTrace();
            }
        }

        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            new MealDBSwingApp.InputFrame(user).setVisible(true);
            dispose();
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);

        buttonPanel.setBorder(null);
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setBorder(null);
        setLocationRelativeTo(null);
        add(scrollPane, BorderLayout.CENTER);
    }
}