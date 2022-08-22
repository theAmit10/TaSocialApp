package com.example.tasocialapp.Model;

public class FollowModel {
    private String followedBy;
    private long followedAt;

    public FollowModel() {
    }

    public void setFollowedBy(String followedBy) {
        this.followedBy = followedBy;
    }

    public long getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(long followedAt) {
        this.followedAt = followedAt;
    }


    public String getFollowedBy() {
        return followedBy;
    }
}
