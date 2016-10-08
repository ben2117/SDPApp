package com.e.sdp.sdpapp;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.EducationalBackground;

/**
 * Created by kisungtae on 07/10/2016.
 */
public class FirebaseNodeEntryGenerator{

    static private int idgeneratorCount;
    static private String nextKey ="";

    static public String generateKey(final String key) {
        idgeneratorCount = 0;
        nextKey = "";

        char[] charArray = key.toCharArray();

        for(int i = 0; i < charArray.length; i++) {
            if(Character.isLetter(charArray[i])) {
                nextKey += charArray[i];
                idgeneratorCount++;
            } else if(Character.isDigit(charArray[i]) && Character.getNumericValue(charArray[i]) != 0) {
                int nextId = Integer.valueOf(key.substring(i)) + 1;
                String stringOfNextId = String.valueOf(nextId);
                int zeroNum = key.length() - idgeneratorCount - stringOfNextId.length();
                for(int j = 0; j < zeroNum; j++) {
                    nextKey += "0";
                }
                nextKey += stringOfNextId;
                break;
            }

        }
        return nextKey;
    }
}
