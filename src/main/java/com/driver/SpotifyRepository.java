package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile)                                             // 1st API - Done
    {
        //create the user with given name and number

        for(User st : users){
            if(mobile.equals(st.getMobile()))
                return st;
        }

        User user = new User(name,mobile);
        users.add(user);

        return user;
    }

    public Artist createArtist(String name)                                                        // 2nd API - Done
    {
        //create the artist with given name

        for(Artist st : artists){
            if(name.equals(st.getName()))
                return st;
        }

        Artist artist = new Artist(name);
        artists.add(artist);

        return artist;
    }

    public Album createAlbum(String title, String artistName)                                      // 3rd API - Done
    {
        //If the artist does not exist, first create an artist with given name
        //Create an album with given title and artist

        for(Album st : albums){
            if(title.equals(st.getTitle()))
                return st;
        }

        Album album = new Album(title);
        albums.add(album);

        // Put in artist-Album-Map (HashMap)
        Artist key = createArtist(artistName);

        List<Album> albumList = artistAlbumMap.getOrDefault(key,new ArrayList<>());
        albumList.add(album);
        artistAlbumMap.put(key,albumList);

        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception            // 4th API - Done
    {
        //If the album does not exist in database, throw "Album does not exist" exception
        //Create and add the song to respective album

        Album albumKey = null;

        for(Album st : albums){
            if(albumName.equals(st.getTitle()))
            {
                albumKey = st;
                break;
            }
        }

        if(albumKey == null)
            throw new Exception("Album does not Exist !!");

        // Create Song
        Song song = new Song(title,length);
        songs.add(song);

        //  Put in album-Song-Map (HashMap)
        List<Song> songList = albumSongMap.getOrDefault(albumKey,new ArrayList<>());
        songList.add(song);
        albumSongMap.put(albumKey,songList);

        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception   // 5th API - Done
    {
        // Create a playlist with given title and add all songs having the given length in the database to that playlist
        // The creator of the playlist will be the given user and will also be the only listener at the time of playlist creation
        // If the user does not exist, throw "User does not exist" exception

        // if playlist already exist
        for(Playlist playlist : playlists){
            if(title.equals(playlist.getTitle()))
                return playlist;
        }

        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        User user = null;

        for(User st : users) {
            if (mobile.equals(st.getMobile())) {
                user = st;
                break;
            }
        }

        if(user == null)
            throw new Exception("User does not exist");

        //  Put in playlist-Song-Map , playlist-Listener-Map , creatorPlaylistMap , user-Playlist-Map  (HashMap)

        List<Song> songOfGivenLength = new ArrayList<>();

        for(Song song : songs){
            if(length == song.getLength())
                songOfGivenLength.add(song);
        }

        playlistSongMap.put(playlist,songOfGivenLength);

        List<User> userList = playlistListenerMap.getOrDefault(playlist,new ArrayList<>());
        userList.add(user);
        playlistListenerMap.put(playlist,userList);

        creatorPlaylistMap.put(user,playlist);

        List<Playlist> playlists = userPlaylistMap.getOrDefault(user,new ArrayList<>());
        playlists.add(playlist);
        userPlaylistMap.put(user,playlists);

        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception  // 6th API - Done
    {
        //Create a playlist with given title and add all songs having the given titles in the database to that playlist
        //The creator of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception

        for(Playlist playlist : playlists){
            if(title.equals(playlist.getTitle()))
                return playlist;
        }

        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        User user = null;

        for(User st : users){
            if(mobile.equals(st.getMobile())){
                user = st;
                break;
            }
        }

        if(user == null)
            throw new Exception("User does not exist");

        //  Put in playlist-Song-Map , playlist-Listener-Map , creatorPlaylistMap , user-Playlist-Map  (HashMap)

        List<Song> songOfGivenName = new ArrayList<>();

        for(Song song : songs){
            if(songTitles.contains(song.getTitle()))
                songOfGivenName.add(song);
        }

        playlistSongMap.put(playlist,songOfGivenName);

        List<User> userList = playlistListenerMap.getOrDefault(playlist,new ArrayList<>());
        userList.add(user);
        playlistListenerMap.put(playlist,userList);

        creatorPlaylistMap.put(user,playlist);

        List<Playlist> playlists = userPlaylistMap.getOrDefault(user,new ArrayList<>());
        playlists.add(playlist);
        userPlaylistMap.put(user,playlists);

        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception             // 7th API - done
    {
        // 1. Find the playlist with given title and add user as listener of that playlist and update user accordingly
        // 2. If the user is creator or already a listener, do nothing
        // 3. If the user does not exist, throw "User does not exist" exception
        // 4. If the playlist does not exist, throw "Playlist does not exist" exception
        // 5. Return the playlist after updating.

        Playlist playlist = null;

        for(Playlist st : playlists){
            if(playlistTitle.equals(st.getTitle()))
                playlist = st;
                break;
       }

        if(playlist == null)
            throw new Exception("Playlist does not exist");

        User user = null;
        for(User st : users){
            if(mobile.equals(st.getMobile())){
                user = st;
                break;
            }
        }

        if(user == null)
            throw new Exception("User does not exist");

        // 2.
        if(creatorPlaylistMap.containsKey(user))
            return playlist;

        // 1.
        List<User> userList = playlistListenerMap.get(playlist);

        for(User user1 : userList){
            if(user1 == user)
                return playlist;
        }

        userList.add(user);
        playlistListenerMap.put(playlist,userList);

        List<Playlist> playlists1 = userPlaylistMap.get(user);

        if(playlists1 == null)
            playlists1 = new ArrayList<>();

        playlists1.add(playlist);
        userPlaylistMap.put(user,playlists1);

        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception                         // 8th API - done
    {
        // 1. The user likes the given song. The corresponding artist of the song gets auto-liked
        // 2. A song can be liked by a user only once. If a user tried to like a song multiple times, do nothing
        // 3. However, an artist can indirectly have multiple likes from a user, if the user has liked multiple songs of that artist.
        // 4. If the user does not exist, throw "User does not exist" exception
        // 5. If the song does not exist, throw "Song does not exist" exception
        // 6. Return the song after updating

       // 5.
        User user = null;

        for(User user1 : users){
            if(user1.getMobile().equals(mobile)) {
                user = user1;
                break;
            }
        }

        if(user == null)
            throw new Exception("User does not exist");

        // 6.
        Song song = null;

        for(Song song1 : songs){
            if(song1.getTitle().equals(songTitle)){
                song = song1;
                break;
            }
        }

        if(song == null)
            throw new Exception("Song does not exist");

        if( songLikeMap.containsKey(song) )
        {
            List<User> list = songLikeMap.get(song);

            if(list.contains(song))
                return song;

            else
            {
                int likes = song.getLikes() + 1;
                song.setLikes(likes);
                list.add(user);
                songLikeMap.put(song,list);

                Album album = null;
                for(Album album1 : albumSongMap.keySet())
                {
                    List<Song> songList = albumSongMap.get(album1);

                    if(songList.contains(song)){
                        album = album1;
                        break;
                    }
                }

                Artist artist = null;
                for(Artist artist1 : artistAlbumMap.keySet())
                {
                    List<Album> albumList = artistAlbumMap.get(artist1);

                    if(albumList.contains(album)){
                        artist = artist1;
                        break;
                    }
                }

                int likes1 = artist.getLikes() + 1;
                artist.setLikes(likes1);
                artists.add(artist);
                return song;
            }
        }

        else
        {
            int likes = song.getLikes() + 1;
            song.setLikes(likes);
            List<User> list = new ArrayList<>();
            list.add(user);
            songLikeMap.put(song,list);

            Album album = null;
            for(Album album1 : albumSongMap.keySet())
            {
                List<Song> songList = albumSongMap.get(album1);

                if(songList.contains(song)){
                    album = album1;
                    break;
                }
            }

            Artist artist = null;
            for(Artist artist1 : artistAlbumMap.keySet())
            {
                List<Album> albumList = artistAlbumMap.get(artist1);

                if(albumList.contains(album)){
                    artist = artist1;
                    break;
                }
            }

            int likes1 = artist.getLikes() + 1;
            artist.setLikes(likes1);
            artists.add(artist);
            return song;
        }
    }

    public String mostPopularArtist()                                                              // 9th API - Done
    {
        //Return the artist name with maximum likes

        String ans = "";
        int max = 0;

        for(Artist st : artists)
        {
            if(max < st.getLikes()){
                max = st.getLikes();
                ans = st.getName();
            }
        }
        return ans;
    }

    public String mostPopularSong()                                                                // 10th API - Done
    {
        //return the song title with maximum likes

        String ans = "";
        int max = 0;

        for(Song st : songs)
        {
            if(max < st.getLikes()){
                max = st.getLikes();
                ans = st.getTitle();
            }
        }
        return ans;
    }
}
