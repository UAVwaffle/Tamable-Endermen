package com.uavwaffle.tameableendermen.entity.custom;

public enum FollowState {
    SIT, FOLLOW, WANDER;

    public FollowState switchFollowState() {
        return switch (this) {
            case SIT -> FOLLOW;
            case FOLLOW -> WANDER;
            case WANDER -> SIT;
        };
    }

    public String getFollowStateText() {
        return switch (this) {
            case SIT -> "Standing";
            case FOLLOW -> "Following";
            case WANDER -> "Wandering";
        };
    }
}