package de.notecho.spotify.utils;


import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.regex.Pattern;

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

    private static final Pattern pattern = Pattern.compile("^(?:spotify:track:|https://open+\\.spotify\\.com/track/)(.*)$");

    @SneakyThrows
    public static Track getTrackFromString(String s, SpotifyApi spotifyApi) {
        if (pattern.matcher(s).matches()) {
            s = s.replace("https://open.spotify.com/track/", "");
            s = getIdFromUri(s);
            if (s.contains("&"))
                s = s.split("&")[0];
            return spotifyApi.getTrack(s).build().execute();
        }
        Paging<Track> search = spotifyApi.searchTracks(s).build().execute();
        if (search.getTotal() == 0)
            return null;
        return search.getItems()[0];
    }
}
