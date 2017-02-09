package bumbums.puzzlepiece.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by han sb on 2017-02-07.
 */

public class Puzzle extends RealmObject {

    @PrimaryKey
    private long id;
    private long friendId;
    private String text;
    private String date;
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



}
