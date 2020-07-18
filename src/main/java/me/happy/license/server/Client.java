package me.happy.license.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public Client() {
        try {
            Socket socket = new Socket("localhost", 3926);

            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.println("license");
            writer.flush();

            socket.setSoTimeout(10 * 1000);

            InputStreamReader reader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(reader);

            boolean isValid = Boolean.parseBoolean(bufferedReader.readLine());

            if (!isValid) return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}