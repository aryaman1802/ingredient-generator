package data_access;

import use_case.gateway.UserRepository;
import entity.RegularUser;
import org.example.MongoConnectionDemo;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;

import java.util.Optional;

/**
 * MongoDB-based implementation of UserRepository.
 * Uses the "users" collection in the "Cluster0" database to store and retrieve credentials.
 */
public class FileUserRepository implements UserRepository {
    // Connection string to your MongoDB Atlas cluster
    private static final String CONNECTION_STRING =
            "mongodb+srv://elimliu:Password@cluster0.wdexumy.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

    /**
     * Finds a user by username in MongoDB.
     * @param username the username to look up
     * @return Optional containing RegularUser if found, or empty if not
     */
    @Override
    public Optional<RegularUser> findByUsername(String username) {
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                .serverApi(serverApi)
                .build();

        try (MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase appDb = mongoClient.getDatabase("Cluster0");
            MongoCollection<Document> users = appDb.getCollection("users");

            Document doc = users.find(eq("username", username)).first();
            if (doc == null) {
                return Optional.empty();
            }
            String user = doc.getString("username");
            String pass = doc.getString("password");
            return Optional.of(new RegularUser(user, pass));
        }
    }

    /**
     * Saves a new user to MongoDB by delegating to MongoConnectionDemo.newuser().
     * @param user the RegularUser containing username and password hash
     */
    @Override
    public void save(RegularUser user) {
        MongoConnectionDemo.newuser(user.getUsername(), user.getPasswordHash());
    }
}