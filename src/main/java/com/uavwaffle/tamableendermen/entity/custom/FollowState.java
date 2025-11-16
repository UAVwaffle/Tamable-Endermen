package com.uavwaffle.tamableendermen.entity.custom;

public enum FollowState {
    SIT, FOLLOW, WANDER;

    public FollowState switchFollowState() {
        return switch (this) {
            case SIT -> FOLLOW;
            case FOLLOW -> WANDER;
            case WANDER -> SIT;
        };
    }
}

/*

Ok so the plan is to use this enum to keep track of if the enderman should sit, follow, or wander.
Of course this need to be put in the save data.
I left off with starting to put the switchFollowState() method in the entity interact method.
I need to make the sitting dependent on this too otherwise we will have sitting endermen when they should be wandering.
Also, I need/should get the sitting animation working
 */