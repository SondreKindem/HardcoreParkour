package no.sonkin.hardcoreParkour.objects;

/**
 * Object for the parkourController to report success to the commmand classes with a custom message.
 * This lets the plugin report more accurate error messages
 */
public class CommandResult {
    private String message;
    private boolean success;

    public CommandResult(boolean success) {
        this.success = success;
        if (success) {
            this.message = "Command executed successfully";
        } else {
            this.message = "Could not execute command";
        }
    }

    public CommandResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
