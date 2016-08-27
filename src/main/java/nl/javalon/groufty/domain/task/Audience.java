package nl.javalon.groufty.domain.task;

/**
 * Represents the type of reviewAudience a task or review can target
 */
public enum Audience {

	/**
	 * A group task must be handed in by a group member for the entire group.
	 */
	GROUP,

	/**
	 * An individual task must be handed in by every user individually.
	 */
	INDIVIDUAL
}
