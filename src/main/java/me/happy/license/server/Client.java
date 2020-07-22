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

            Result result = Result.valueOf(bufferedReader.readLine());

            socket.close();

            System.out.println(result.getMessage());

            if (result != Result.SUCCESSFUL) {
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum Result {
        TO_MUCH_FAILED_ATTEMPTS("You have had too much invalid attempts for the past minute."),
        INVALID_LICENSE("The license you have used is invalid."),
        EXPIRED("The license has expired."),
        ALREADY_USED("This license is already used in another license."),
        SUCCESSFUL("Your license has been accepted.")
        ;
        private final String message;

        Result(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}