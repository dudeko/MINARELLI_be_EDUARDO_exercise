package com.ecore.roles.exception;

public class UserIsNotAssignedToMembershipException extends RuntimeException {

    public UserIsNotAssignedToMembershipException() {
        super("Invalid 'Membership' object. The provided user doesn't belong to the provided team.");
    }
}
