package org.example;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import entity.RegularUser;
import org.bson.Document;
import view.TopRecipesFrame;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;

public class MongoConnectionDemo {
    public static void main(String[] args) {
    }

    public static void newuser(String username, String passcode) {
        String connectionString = "mongodb+srv://aryamanbansalna:Password@cluster0.wdexumy.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

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

                // 1) Open your application database and the "users" collection:
                MongoDatabase appDb = mongoClient.getDatabase("Cluster0");
                MongoCollection<Document> users = appDb.getCollection("users");

                // 2) Insert a new user (if you haven’t already):
                Document newUser = new Document("username", username)
                        .append("password", passcode)
                        .append("history", new ArrayList<>())
                        .append("timestamp",new ArrayList<>());
                users.insertOne(newUser);

            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }

    public static void mealEntry(String username){
        String connectionString = "mongodb+srv://aryamanbansalna:Password@cluster0.wdexumy.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

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

                // 1) Open your application database and the "users" collection:
                MongoDatabase appDb = mongoClient.getDatabase("Cluster0");
                MongoCollection<Document> users = appDb.getCollection("users");

              // 3) Build a preference/recipes entry:
                Path path = Paths.get("recipes.txt");
                String content = Files.readString(path, StandardCharsets.UTF_8);
                LocalDateTime time = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMM dd 'at' HH:mm:ss");

                String formattedTime = time.format(formatter);

                // 4) Append that entry to the user’s history array:
                users.updateOne(
                        eq("username", username),
                        Updates.push("history", content)
                    );
                users.updateOne(
                        eq("username", username),
                        Updates.push("timestamp", formattedTime)
                );


            } catch (MongoException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static int numEntry(RegularUser user){
        String connectionString = "mongodb+srv://aryamanbansalna:Password@cluster0.wdexumy.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

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
                List<String> history = person.getList("history", String.class);

                return history.size();

            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static void makeRecipe(RegularUser user, int num) {
        String connectionString = "mongodb+srv://aryamanbansalna:Password@cluster0.wdexumy.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

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
                List<String> history = person.getList("history", String.class);
                String data = history.get(num);
                Path out = Paths.get("recipes.txt");
                Files.writeString(
                        out,
                        data,
                        StandardCharsets.UTF_8
                );
                TopRecipesFrame.main(new String[]{});

            } catch (MongoException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
