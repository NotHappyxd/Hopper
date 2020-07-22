package me.happy.license.server;

public enum Result {
    TO_MUCH_FAILED_ATTEMPTS("You have had too much invalid attempts for the past minute."),
    INVALID_LICENSE("The license you have used is invalid."),
    EXPIRED("The license has expired."),
    ALREADY_USED("This license is already used in another license."),
    SUCCESSFUL("Your license has been accepted.")
    ;
    private final String message;

    Result(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
