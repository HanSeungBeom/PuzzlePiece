package bumbums.puzzlepiece.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by han sb on 2017-02-07.
 */

public class Friend extends RealmObject{
    @PrimaryKey
    private long id;
    private String name;
    private String phoneNumber;
    private int puzzleNum;
    private int rank;
    private String profileUrl;
    private RealmList<Puzzle> puzzles;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPuzzleNum() {
        return puzzleNum;
    }

    public void setPuzzleNum(int puzzleNum) {
        this.puzzleNum = puzzleNum;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public RealmList<Puzzle> getPuzzles() {
        return puzzles;
    }

    public void setPuzzles(RealmList<Puzzle> puzzles) {
        this.puzzles = puzzles;
    }
    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

}
