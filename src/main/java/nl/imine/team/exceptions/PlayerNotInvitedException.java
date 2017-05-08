package nl.imine.team.exceptions;

/**
 * @author Dennis
 */
public class PlayerNotInvitedException extends Exception{

    public PlayerNotInvitedException() {
    }

    public PlayerNotInvitedException(String message) {
        super(message);
    }
    
}
