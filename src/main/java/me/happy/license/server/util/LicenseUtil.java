package me.happy.license.server.util;

import java.util.concurrent.ThreadLocalRandom;

public class LicenseUtil {

    private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";

    public static String getRandomLicense() {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i != 19; i++) {
            if (i != 0 && i % 5 == 0) {
                output.append("-");
            }else {
                output.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length() - 1)));
            }
        }

        output.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length() - 1)));
        return output.toString();
    }

    public static int parseInt(String input) {
        try {
            return Integer.parseInt(input);
        }catch (IllegalArgumentException e) {
            return 0;
        }
    }

    public static long parse(String input) {
        if (input.equalsIgnoreCase("perm") || input.equalsIgnoreCase("permanent") || input.equalsIgnoreCase("forever")) return Long.MAX_VALUE;

        long result = 0;
        String number = "";
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) {
                number += c;
            } else if (Character.isLetter(c) && !number.isEmpty()) {
                result += convert(Integer.parseInt(number), c);
                number = "";
            }
        }
        return result;
    }

    private static long convert(int value, char unit) {
        switch(unit) {
            case 'd' : return value * 1000*60*60*24;
            case 'h' : return value * 1000*60*60;
            case 'm' : return value * 1000*60;
            case 's' : return value * 1000;
        }
        return 0;
    }

    public static String formatTimeMillis(long millis) {
        long seconds = millis / 1000L;

        if (seconds <= 0) {
            return "0 seconds";
        }

        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        long day = hours / 24;
        hours = hours % 24;
        long years = day / 365;
        day = day % 365;

        StringBuilder time = new StringBuilder();

        if (years != 0) {
            time.append(years).append(years == 1 ? " year " : " years ");
        }

        if (day != 0) {
            time.append(day).append(day == 1 ? " day " : " days ");
        }

        if (hours != 0) {
            time.append(hours).append(hours == 1 ? " hour " : " hours ");
        }

        if (minutes != 0) {
            time.append(minutes).append(minutes == 1 ? " minute " : " minutes ");
        }

        if (seconds != 0) {
            time.append(seconds).append(seconds == 1 ? " second " : " seconds ");
        }

        return time.toString().trim();
    }
}
