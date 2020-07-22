package me.happy.license.server;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class Server {

    // TODO: Replace this with a database.
    private static final List<License> licenses = new ArrayList<>();

    private static final Map<String, Integer> traffic = new HashMap<>();

    private static MongoClient client;

    public static void main(String[] args) throws IOException {

        MongoCredential credential = MongoCredential.createCredential("username", "authDb", "password".toCharArray());

        client = new MongoClient(new ServerAddress("host", 37017), Collections.singletonList(credential));

        MongoDatabase licenseDb = client.getDatabase("license");
        MongoCollection<Document> licenseCollection = licenseDb.getCollection("licenses");

        licenseCollection.find().into(new HashSet<>()).forEach(document -> {
            licenses.add(new License(document.getString("license"), document.getString("clientName"), document.getLong("addedAt"),
                    document.getLong("expiresAt"), document.getInteger("ipLimit")));
        });



        ServerSocket serverSocket = new ServerSocket(3926);

        while (true) {
            Socket socket = serverSocket.accept();

            InputStreamReader input = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(input);

            String key = bufferedReader.readLine();
            String address = socket.getInetAddress().getHostAddress();


            License license = licenses.stream().filter(license1 -> license1.getLicense().equalsIgnoreCase("key")).findFirst().orElse(null);
            boolean result = license != null;

            System.out.println(result);
            if (!result) {
                traffic.putIfAbsent(address, 0);
                if (traffic.get(address) != 3)
                    traffic.merge(address, 1, Integer::sum);
                new Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                traffic.remove(address);
                            }
                        }, 1000 * 60);
            }

            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            if (!result && traffic.get(address) == 3) {
                writer.println(Result.TO_MUCH_FAILED_ATTEMPTS);
                System.out.println(Result.TO_MUCH_FAILED_ATTEMPTS);
            }else if (!result) {
                writer.println(Result.INVALID_LICENSE);
            }else if(license.hasExpired()) {
                writer.println(Result.EXPIRED);
            }else if (!license.getIps().contains(key) && license.getIpLimit() == license.getIps().size()) {
                writer.println(Result.ALREADY_USED);
            }else {
                writer.println(Result.SUCCESSFUL);
                license.getIps().add(key);
            }

            writer.flush();

            System.out.println(Boolean.valueOf(result));
            System.out.println("Recieved and wrote license (Source: " + address + ")");

            input.close();
            bufferedReader.close();
            writer.close();
        }
    }
}
