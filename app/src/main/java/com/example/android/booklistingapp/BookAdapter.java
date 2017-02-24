package com.example.android.booklistingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import static android.view.View.GONE;

/**
 * Created by Steven on 24/02/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    //constructor for the BookAdapter object
    public BookAdapter(Context context, List<Book> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }
        //get current Book item
        Book currentBook = getItem(position);

        //find the TextViews for the attributes of the Book objects (e.g. title, author,...) and
        //set the right elements of the currentBook to the Views
        TextView titleTextView = (TextView) convertView.findViewById(R.id.title_textview);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.author_textview);
        TextView descriptionTextView = (TextView) convertView.findViewById(R.id.description_textview);

        //for Book objects with no subtitle in the JSON response, we have set the subtitle to null
        // for the other Books, the subtitle fields has the right value
        // therefore, if the Book has a subtitle, add it to the title textView, otherwise, only
        //show the title
        if(currentBook.getSubtitle() != null){
            titleTextView.setText(currentBook.getTitle() + " : " + currentBook.getSubtitle());
        } else{
            titleTextView.setText(currentBook.getTitle());
        }

        authorTextView.setText(currentBook.getAuthor());

        //same reasoning as above, but the corresponding is set to GONE if there is no
        // description available
        if(currentBook.getDescription() != null){
            descriptionTextView.setText(currentBook.getDescription());
        } else{
            descriptionTextView.setVisibility(GONE);
        }


        return convertView;
    }
}
