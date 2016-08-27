package nl.javalon.groufty.dto.page;

/**
 * @author Lukas Miedema
 */
public enum SubmissionListState {

	/**
	 * The submission list is open (current time is past the start date and before the submissionDeadline) and nothing has been submitted yet
	 */
	OPEN,

	/**
	 * The submission list has work submitted to it (i.e. at least one submission is submitted)
	 */
	IN_PROGRESS,

	/**
	 * All submissions in the submission list have been submitted. Current time is before the submissionDeadline.
	 */
	SUBMITTED,

	/**
	 * Submissions that have been submitted are under review and the current time is after the submissionDeadline.
	 */
	UNDER_REVIEW,

	/**
	 * All submissions have been reviewed. A final grade is available, but not yet signed off by a teacher.
	 */
	REVIEWED,

	/**
	 * The submission list is finalized and the final grade has been signed off.
	 */
	FINALIZED
}
