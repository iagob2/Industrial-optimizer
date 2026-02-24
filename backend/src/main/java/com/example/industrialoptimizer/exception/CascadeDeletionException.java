package com.example.industrialoptimizer.exception;

/**
 * CascadeDeletionException: Custom exception for cascade deletion prevention.
 * 
 * Thrown when attempting to delete an entity that is referenced by other
 * entities
 * and cascade deletion is not configured.
 * 
 * Scenario 4: Cascade Deletion
 * - Prevents deletion of RawMaterial when ProductComposition references it
 * - Service layer throws this exception before database operation
 * 
 * Example:
 * throw new CascadeDeletionException(
 * "Cannot delete raw material MAT-001: it is used in 3 product recipes. " +
 * "Remove all product compositions first."
 * );
 */
public class CascadeDeletionException extends RuntimeException {

    /**
     * Constructs a CascadeDeletionException with a detailed message.
     *
     * @param message the error message explaining why deletion is prevented
     */
    public CascadeDeletionException(String message) {
        super(message);
    }

    /**
     * Constructs a CascadeDeletionException with a message and cause.
     *
     * @param message the error message
     * @param cause   the cause of the exception
     */
    public CascadeDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
