package nl.imine.team.exceptions;

/**
 * @author Dennis
 */
public class PlayerAlreadyInvitedException extends Exception {

    public PlayerAlreadyInvitedException() {
    }

    public PlayerAlreadyInvitedException(String message) {
        super(message);
    }
    
}
