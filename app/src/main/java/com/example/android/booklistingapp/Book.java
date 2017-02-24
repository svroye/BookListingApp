package com.example.android.booklistingapp;

/**
 * Created by Steven on 24/02/2017.
 */

public class Book {

    // title of the book
    private String mTitle;

    // subtitle of the book
    private String mSubtitle=null;

    // author of the book
    private String mAuthor;

    //description of the book
    private String mDescription;

    /**
     * public constructor for the book class
     * @param title : title of the book
     * @param author : author of the book
     */
    public Book(String title, String author, String subtitle,String description){
        mTitle = title;
        mAuthor=author;
        mSubtitle=subtitle;
        mDescription = description;
    }

    /**
     * method called to get the title of the Book object
     * @return : title of the Book
     */
    public String getTitle(){
        return mTitle;
    }

    /**
     * method called to get the subtitle of the Book object
     * @return : subtitle of the Book
     */
    public String getSubtitle(){
        return mSubtitle;
    }

    /**
     * method called to get the author of the Book object
     * @return : author of the Book
     */
    public String getAuthor(){
        return mAuthor;
    }

    /**
     * method called to get the description of the Book object
     * @return : description of the Book
     */
    public String getDescription(){
        return mDescription;
    }
}
