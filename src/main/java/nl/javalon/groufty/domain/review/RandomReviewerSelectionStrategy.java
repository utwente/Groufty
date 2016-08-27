package nl.javalon.groufty.domain.review;

import lombok.Getter;
import lombok.Setter;
import nl.javalon.groufty.domain.task.Audience;
import nl.javalon.groufty.domain.task.Submission;
import nl.javalon.groufty.domain.task.SubmissionList;
import nl.javalon.groufty.domain.task.TaskList;
import nl.javalon.groufty.domain.user.Author;
import nl.javalon.groufty.domain.user.Group;
import nl.javalon.groufty.domain.user.Grouping;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.repository.crud.SubmissionListRepository;
import nl.javalon.groufty.repository.crud.SubmissionRepository;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Assigns reviewers to submissions randomly. It is as fair as possible, i.e. the minimum and maximum amount
 * of reviews any user has to write never differs more than 1.
 */
@Entity
@Getter
@Setter
public class RandomReviewerSelectionStrategy extends ReviewerSelectionStrategy {

	/**
	 * Do not assign authors who did not hand in a any submissions to review submissions of
	 * others. This is only applicable when {@link #from} equals {@link TaskList#grouping} and
	 * {@link #reviewAudience} equals {@link TaskList#submissionAudience}.
	 */
	@Column(name = "skip_submissionless_authors", nullable = false)
	private boolean skipSubmissionlessAuthors;

	// DI Fields //
	@Inject private transient SubmissionRepository submissionRepository;
	@Inject private transient SubmissionListRepository submissionListRepository;

	@Override
	@SuppressWarnings("unchecked")
	public Map<Submission, Set<Author>> performSelection() throws ReviewSelectionStrategyPerformException {

		TaskList taskList = this.getTaskList();
		Grouping source = taskList.getGrouping();
		Grouping dest = this.getFrom();
		Audience sourceTarget = taskList.getSubmissionAudience();
		Audience destTarget = this.getReviewAudience();
		int reviewCount = this.getReviewCount();

		// When set to true, only authors who handed it at least one submission will be allowed to participate
		// in the review process. This option only makes sense when the source and dest are equal.
		boolean skipSubmissionlessAuthors = false;

		// Dogfeeding - eating your own dog food, or in this case, the same grouping who wrote the submission does it
		boolean dogFeeding = source.equals(dest) && sourceTarget == destTarget;

		if (dogFeeding) {
			skipSubmissionlessAuthors = this.skipSubmissionlessAuthors;
		}

		// Create a pool of submissions
		List<Submission> submissionPool = submissionRepository.findSubmittedByTaskList(taskList);

		// Create a pool of authors
		List<Author> reviewerPool;
		if (skipSubmissionlessAuthors) {

			// Use the authors of the submission lists
			List<SubmissionList> submissionLists = submissionListRepository.findSubmittedByTaskList(taskList);
			reviewerPool = submissionLists.stream()
					.map(SubmissionList::getAuthor)
					.distinct()
					.collect(Collectors.toList());

		} else {

			// Find all authors of the required type in the grouping
			switch (destTarget) {
				case INDIVIDUAL:
					reviewerPool = new ArrayList<>(dest.getUsers());
					break;
				case GROUP:
					reviewerPool = new ArrayList<>(dest.getGroups());
					break;
				default:
					reviewerPool = null;
			}

		}

		// Precondition check
		if (submissionPool.isEmpty()) {
			throw new ReviewSelectionStrategyPerformException("No submissions to review");
		}
		if (reviewerPool.isEmpty()) {
			throw new ReviewSelectionStrategyPerformException("No authors available");
		}
		if (reviewerPool.size() < reviewCount) {
			throw new ReviewSelectionStrategyPerformException("Too few reviewers (" + reviewerPool.size() +
					") to write " + reviewCount + " review(s) per submission");
		}

		// Track assignments
		Map<Submission, Set<Author>> reviewerAssignments = new HashMap<>();

		// Create random source
		Random random = new SecureRandom();
		Iterator<Author> authorIterator = null;

		for (Submission submission: submissionPool) {
			for (int i = 0; i < reviewCount; i++) {

				// Create the assignment set
				Set<Author> authors = reviewerAssignments.get(submission);
				if (authors == null) {
					authors = new HashSet<>();
					reviewerAssignments.put(submission, authors);
				}

				// Check if they are not related
				Author submissionAuthor = submission.getId().getSubmissionList().getAuthor();
				Author reviewAuthor = null;

				boolean related = true;

				// To prevent infinite looping, this for loop
				// reviewerPool.size() * 2 because the array gets shuffled so we might iterate over the same element
				// more than once. *2 ensures we have seen it all at least once.
				for (int j = 0; j < reviewerPool.size() * 2 && related; j++) {
					// Get the next reviewAuthor
					if (authorIterator == null || !authorIterator.hasNext()) {
						// Shuffle the authors
						Collections.shuffle(reviewerPool, random);
						authorIterator = reviewerPool.iterator();
					}
					reviewAuthor = authorIterator.next();

					// Check already assigned
					if (authors.contains(reviewAuthor)) {
						continue;
					}

					// Check related
					// They are the same person or same group
					if (reviewAuthor.equals(submissionAuthor)) {
						continue;
					}

					// The submission author is a member of the review author group
					if (reviewAuthor instanceof Group && submissionAuthor instanceof User) {
						Group reviewGroup = (Group) reviewAuthor;
						if (reviewGroup.getUsers().contains(submissionAuthor)) {
							continue;
						}
					}

					// The submission author is a group with the review author as member
					if (reviewAuthor instanceof User && submissionAuthor instanceof Group) {
						Group submissionGroup = (Group) submissionAuthor;
						if (submissionGroup.getUsers().contains(reviewAuthor)) {
							continue;
						}
					}

					// Exit the loop. Match found
					related = false;
				}

				// Why did we exit?
				if (related) {
					throw new ReviewSelectionStrategyPerformException("Could not find review author who is not somehow part of the submission author");
				}

				// And add
				authors.add(reviewAuthor);
			}
		}

		return reviewerAssignments;
	}
}
