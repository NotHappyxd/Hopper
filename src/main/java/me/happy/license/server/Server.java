package me.happy.license.server;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    // TODO: Replace this with a database.
    private static final List<License> licenses = new ArrayList<>();

    private static MongoClient client;

    public static void main(String[] args) throws IOException {

        MongoCredential credential = MongoCredential.createCredential("username", "authDb", "password".toCharArray());

        client = new MongoClient(new ServerAddress("host", 37017), Collections.singletonList(credential));

        MongoDatabase license = client.getDatabase("license");
        MongoCollection<Document> licenseCollection = license.getCollection("licenses");

        licenseCollection.find().into(new HashSet<>()).forEach(document -> {
            licenses.add(new License(document.getString("license"), document.getString("clientName"), document.getLong("addedAt"),
                    document.getLong("expiresAt")));
        });

        ServerSocket serverSocket = new ServerSocket(3926);

        while (true) {
            Socket socket = serverSocket.accept();

            InputStreamReader input = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(input);

            String key = bufferedReader.readLine();

            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.println(licenses.stream().anyMatch(license1 -> license1.getLicense().equalsIgnoreCase(key)));
            writer.flush();

            String address = socket.getInetAddress().getHostAddress();

            System.out.println("Recieved and wrote license (Source: " + address + ")");

            input.close();
            bufferedReader.close();
            writer.close();
        }
    }
}
