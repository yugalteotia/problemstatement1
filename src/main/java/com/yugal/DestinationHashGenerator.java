package com.yugal;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;

public class DestinationHashGenerator {
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN> <JSONFilePath>");
            return;
        }

        String prn = args[0].toLowerCase().replaceAll("\\s", "");
        String jsonFilePath = args[1];

        System.out.println("PRN: " + prn);
        System.out.println("JSON File Path: " + jsonFilePath);

        try {
            JSONObject jsonObject = new JSONObject(new JSONTokener(new FileReader(new File(jsonFilePath))));
            String destinationValue = findDestinationValue(jsonObject);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            String randomString = generateRandomString(8);
            String toHash = prn + destinationValue + randomString;
            String md5Hash = generateMD5Hash(toHash);

            System.out.println(md5Hash + ";" + randomString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String findDestinationValue(JSONObject jsonObject) {

        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String found = findDestinationValue((JSONObject) value);
                if (found != null) return found;
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


}
