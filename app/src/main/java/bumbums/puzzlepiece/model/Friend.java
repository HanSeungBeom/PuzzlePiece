package bumbums.puzzlepiece.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by han sb on 2017-02-07.
 */

public class Friend extends RealmObject{
    public static final String USER_ID = "id";
    public static final String PUZZLE_NUM = "puzzleNum";
    public static final String RANK = "rank";


    @PrimaryKey
    private long id;
    private String name;
    private String phoneNumber;
    private String relation;

    private int puzzleNum;
    private int rank;
    private String profileUrl;
    private String profilePath;


    private String profileName;
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
    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

}
