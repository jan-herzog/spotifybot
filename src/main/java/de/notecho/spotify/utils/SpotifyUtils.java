package de.notecho.spotify.utils;


import com.google.gson.JsonParser;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class SpotifyUtils {

    public static String getArtists(Track track) {
        StringBuilder artists = new StringBuilder();
        for (ArtistSimplified artist : track.getArtists()) {
            if (artists.toString().equalsIgnoreCase("")) {
                artists.append(artist.getName());
                continue;
            }
            artists.append(", ").append(artist.getName());
        }
        return artists.toString();
    }

    public static String getUriFromJson(String json) {
        return new JsonParser().parse(json).getAsJsonObject().get("item").getAsJsonObject().get("uri").getAsString();
    }

    public static String getIdFromUri(String uri) {
        return uri.replace("spotify:track:", "");
    }
}
