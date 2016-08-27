package nl.javalon.groufty.domain.task;

/**
 * @author Lukas Miedema
 */
public enum TaskListState {

	/**
	 * Task list is currently not visible for normal students, regardless of its other properties
	 */
	DRAFT,

	/**
	 * Task list is live and will be visible after the submissionDeadline
	 */
	ACTIVE,

	/**
	 * Task list is finalized and all grades are permanent.
	 */
	FINALIZED;
}
