package fr.flaily.xynon.utils.irc;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64; // Built-in Java class for decoding

// Imports for GSON
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import fr.flaily.xynon.Xynon;

public class XynonClient implements Runnable {

    /**
     * Custom class to hold the deserialized data after a successful login.
     */
    public static class LoginResult {
        public String discordId; 
        public String name, discordName;
        public long uid;
        public String authToken;
        
        @Override
        public String toString() {
            // Overriding toString for cleaner main() output
            return "Discord User: " + name + " (ID: " + discordId + ")\nAuthentication Token: " + authToken.substring(0, 20) + "...";
        }
    }
    
    private static final String BASE_URL = "https://xynon-backend.onrender.com"; // Your Node.js server address
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    /**
     * Executes the Discord OAuth Challenge-Polling flow to get an authentication token :3
     * @return The LoginResult object containing the user's name, ID, and the final JWT.
     * @throws Exception if the login process fails at any stage.
     */
    public LoginResult loginWithDiscord() throws Exception {
        // --- 1. START LOGIN AND GET CHALLENGE ID ---
        System.out.println("Starting Discord login challenge...");
        
        HttpRequest startRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/start-login"))
                .GET()
                .build();

        HttpResponse<String> startResponse = CLIENT.send(startRequest, HttpResponse.BodyHandlers.ofString());
        
        if (startResponse.statusCode() != 200) {
             throw new IOException("Failed to start login (HTTP " + startResponse.statusCode() + "): " + startResponse.body());
        }
        
        JsonObject startData;
        try {
            startData = JsonParser.parseString(startResponse.body()).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            throw new Exception("Failed to parse start login response: " + startResponse.body(), e);
        }

        String challengeId = startData.get("challenge_id").getAsString();
        String redirectUrl = startData.get("redirect_url").getAsString();

        System.out.println("Challenge ID: " + challengeId);
        System.out.println("Opening browser for Discord login...");
        
        // bee bzz
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(redirectUrl));
            System.out.println("Please open this URL in your browser to proceed: " + redirectUrl);
        } else {
            System.out.println("Please open this URL in your browser to proceed: " + redirectUrl);
        }

        // --- 3. POLL FOR STATUS ---
        System.out.println("Waiting for login to complete in browser. Polling status every 5 seconds...");
        String authToken = null;
        
        while (authToken == null) {
            Thread.sleep(5000); 

            HttpRequest statusRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/status?challenge_id=" + challengeId))
                    .GET()
                    .build();

            HttpResponse<String> statusResponse = CLIENT.send(statusRequest, HttpResponse.BodyHandlers.ofString());
            
            if (statusResponse.statusCode() != 200) {
                System.err.println("Polling error (HTTP " + statusResponse.statusCode() + "). Retrying...");
                continue;
            }

            JsonObject statusData;
            try {
                statusData = JsonParser.parseString(statusResponse.body()).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                 System.err.println("Failed to parse status response. Retrying...");
                 continue;
            }

            String status = statusData.get("status").getAsString();

            if ("success".equals(status)) {
                authToken = statusData.get("token").getAsString();
                System.out.println("Login successful! Token received.");
                return decodeJwt(authToken);
            }
                else if ("denied".equals(status)) {
                System.out.println("Access denied. Your Discord account is not authorized.");
                return null;
            } else if ("error".equals(status)) {
                throw new Exception("Login failed on server side during challenge process.");
            }
        }
        
        return null;
    }
    
    /**
     * Safely decodes the payload of a JWT token to extract user information.
     * @param jwtToken The full JWT string (header.payload.signature).
     * @return LoginResult containing discordId and username.
     * @throws Exception if the JWT structure is invalid or decoding fails.
     */
    private LoginResult decodeJwt(String jwtToken) throws Exception {
        String[] parts = jwtToken.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT structure. Must have 3 parts.");
        }
        
        // The payload is the second part (index 1) and is Base64URL encoded
        String payloadBase64 = parts[1];
        
        // Use the Base64 URL decoder which handles the JWT format
        String decodedPayload = new String(Base64.getUrlDecoder().decode(payloadBase64));

        JsonObject payloadJson;
        try {
            payloadJson = JsonParser.parseString(decodedPayload).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            throw new Exception("Failed to parse JWT payload as JSON.", e);
        }

        LoginResult result = new LoginResult();
//        result.discordId = payloadJson.get("discordId").getAsString();
//        result.name = payloadJson.get("username").getAsString();
//        result.authToken = jwtToken; // Store the original token for later use
        result.discordId = payloadJson.get("discordId").getAsString();
        result.discordName = payloadJson.get("discordName").getAsString();
        result.name = payloadJson.get("name").getAsString();
        result.uid = payloadJson.get("uid").getAsLong();
        result.authToken = jwtToken;
        
        return result;
    }
    
    // Example of how to call this (for testing):
    public static void main(String[] args) {
        try {
            
        } catch (Exception e) {
            System.err.println("\n--- LOGIN PROCESS FAILED ---");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            Xynon.INSTANCE.user = loginWithDiscord();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}