/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.team.exceptions;

/**
 *
 * @author Dennis
 */
public class TeamFullException extends Exception {

    public TeamFullException() {
    }

    public TeamFullException(String message) {
        super(message);
    }
    
}
