package com.speedyfirecyclone.cardstore;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class CardlistCursorAdapter extends CursorAdapter {
    public CardlistCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.cardlist_adapter, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView listCardname = (TextView) view.findViewById(R.id.listCardnameCardlistAdapter);
        TextView hiddenIdentifier = (TextView) view.findViewById(R.id.identifierCardlistAdapter);

        int identifier = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String cardnameString = cursor.getString(cursor.getColumnIndexOrThrow("title"));

        hiddenIdentifier.setText(String.valueOf(identifier));
        listCardname.setText(cardnameString);
    }

}
