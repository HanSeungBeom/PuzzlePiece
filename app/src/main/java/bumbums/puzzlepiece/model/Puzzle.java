package bumbums.puzzlepiece.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by han sb on 2017-02-07.
 */

public class Puzzle extends RealmObject {

    public static final String PUZZLE_ID = "id";
    public static final String FRIEND_ID ="friendId";
    public static final String DATE_TO_MILLISECONDS = "dateToMilliSeconds";

    @PrimaryKey
    private long id;
    private long friendId;
    private String friendName;
    private String text;
    private String date;
    private long dateToMilliSeconds;
    private boolean callShow;


    private String location;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public long getFriendId() {
        return friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public void setFriendId(long friendId) {
        this.friendId = friendId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getDateToMilliSeconds() {
        return dateToMilliSeconds;
    }

    public void setDateToMilliSeconds(long dateToMilliSeconds) {
        this.dateToMilliSeconds = dateToMilliSeconds;
    }
    public boolean getCallShow(){
        return callShow;
    }
    public void setCallShow(boolean callShow){
        this.callShow = callShow;
    }
}
