package me.TechsCode.TechDiscordBot.client;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.mysql.storage.SongodaPurchase;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public class SongodaAPIClient extends APIClient {

    private List<SongodaPurchase> purchases;

    public SongodaAPIClient(String token) {
        super(token);
        purchases = null;
    }

    public boolean isLoaded() { return purchases != null; }

    public List<SongodaPurchase> getPurchases() { return purchases; }

    public List<SongodaPurchase> getPurchases(String discord) { return getPurchases().stream().filter(sp -> sp.getDiscord() != null && sp.getDiscord().equals(discord)).collect(Collectors.toList()); }

    public List<SongodaPurchase> getPurchases(User member) { return getPurchases().stream().filter(sp -> sp.getDiscord() != null && sp.getDiscord().equals(member.getName() + "#" + member.getDiscriminator())).collect(Collectors.toList()); }

    @Override
    public void run() {
        TechDiscordBot.log("Connecting to Songoda.com");
        while (true) {
            try {
                URI url = new URI("https://songoda.com/api/dashboard/payments?token=" + getToken() + "&per_page=2000000");
                HttpURLConnection httpcon = (HttpURLConnection) url.toURL().openConnection();
                httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
                httpcon.connect();
                JSONParser parser = new JSONParser();
                JSONObject root = (JSONObject) parser.parse(new InputStreamReader((InputStream) httpcon.getContent()));
                JSONArray data = (JSONArray) root.get("data");
                purchases = (List<SongodaPurchase>) data.stream().map(x -> new SongodaPurchase((String) ((JSONObject) x).get("product"), (String) ((JSONObject) x).get("discord"))).collect(Collectors.toList());
                httpcon.disconnect();
            } catch (IOException | URISyntaxException | ParseException e) {
                e.printStackTrace();
            }
            try {
                sleep(50000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}