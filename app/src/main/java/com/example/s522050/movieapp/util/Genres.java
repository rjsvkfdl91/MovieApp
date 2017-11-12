package com.example.s522050.movieapp.util;


public class Genres {

    public Genres() {
    }

    private static String getGenre(int genre_id){

        String genre = null;

        switch (genre_id){
            case 12:
                genre = "Adventure";
                break;
            case 14:
                genre = "Fantasy";
                break;
            case 16:
                genre = "Animation";
                break;
            case 18:
                genre = "Drama";
                break;
            case 27:
                genre = "Horror";
                break;
            case 28:
                genre = "Action";
                break;
            case 35:
                genre = "Comedy";
                break;
            case 36:
                genre = "History";
                break;
            case 37:
                genre = "Western";
                break;
            case 53:
                genre = "Thriller";
                break;
            case 80:
                genre = "Crime";
                break;
            case 99:
                genre = "Documentary";
                break;
            case 878:
                genre = "Science Fiction";
                break;
            case 9648:
                genre = "Mystery";
                break;
            case 10402:
                genre = "Music";
                break;
            case 10749:
                genre = "Romance";
                break;
            case 10751:
                genre = "Family";
                break;
            case 10752:
                genre = "War";
                break;
            case 10770:
                genre = "TV Movie";
                break;
        }
        return genre;
    }
}
