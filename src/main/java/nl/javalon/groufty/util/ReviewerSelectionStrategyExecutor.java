package nl.javalon.groufty.util;

import lombok.extern.slf4j.Slf4j;
import nl.javalon.groufty.domain.review.ReviewSelectionStrategyPerformException;
import nl.javalon.groufty.domain.review.ReviewerSelectionStrategy;
import nl.javalon.groufty.domain.review.instance.Review;
import nl.javalon.groufty.domain.task.Submission;
import nl.javalon.groufty.domain.user.Author;
import nl.javalon.groufty.repository.crud.ReviewRepository;
import nl.javalon.groufty.repository.crud.ReviewSelectionStrategyRepository;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Periodically checks if any {@link nl.javalon.groufty.domain.task.TaskList}s are past their deadline, have
 * an active {@link ReviewerSelectionStrategy} and execute it.
 * @author Lukas Miedema
 */
@Component
@Slf4j
public class ReviewerSelectionStrategyExecutor {

	@Inject private ReviewRepository reviewRepository;
	@Inject private ReviewSelectionStrategyRepository reviewSelectionStrategyRepository;
	@Inject private AutowireCapableBeanFactory autowireCapableBeanFactory;

	@Transactional
	@Scheduled(fixedRateString = "${groufty.reviewer-selection-executor.interval}")
	public void executeReviewSelectionStrategies() throws InterruptedException {

		// Get all pending review selection strategies
		List<ReviewerSelectionStrategy> strategies = reviewSelectionStrategyRepository.getPrimedPastSubmissionDeadline();
		log.debug("Performing " + strategies.size() + " ReviewSelectionStrategies");

		// Execute all
		for (ReviewerSelectionStrategy strategy: strategies) {
			log.info("Performing ReviewSelectionStrategy for task list: " + strategy.getTaskList().getName());
			String errorMessage = null;

			try {

				// Inject dependencies
				autowireCapableBeanFactory.autowireBean(strategy);

				// Perform
				Map<Submission, Set<Author>> selection = strategy.performSelection();
				strategy.setPrimed(false);
				strategy.setExecutionDate(new Date());

				// Set in database
				selection.forEach((submission, authors) -> {
					for (Author author: authors) {

						// Create review
						Review review = new Review();
						review.setAuthor(author);
						review.setSubmission(submission);
						review.setSubmitted(false);

						// And persist
						reviewRepository.save(review);
					}
				});

			} catch (ReviewSelectionStrategyPerformException e) {

				// This means there's a "nice" message
				errorMessage = e.getMessage();
			} catch (Exception e) {

				// There is no nice message
				errorMessage = "An unexpected exception occurred: " + e.getMessage();
				e.printStackTrace();
			}

			if (errorMessage != null) {
				log.error("Failed to execute ReviewerSelectionStrategy! " + errorMessage);
				log.warn("Disabling failed ReviewerSelectionStrategy");
				strategy.setErrorMessage(errorMessage);
				strategy.setPrimed(false);
			}
		}
	}
}
