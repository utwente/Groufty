SELECT
t.task_id,
t.name task_name,
t.content_type,
u.user_type,
u.user_number,
u.full_name,
t.review_template_id,
rt.name review_template_name,
t.show_grades_to_reviewers,

-- Computed
(
  SELECT COUNT(*)
  FROM groufty_submission s
  WHERE s.task_id = t.task_id AND s.submitted
) submitted_submissions_count,
(
  SELECT COUNT(*)
  FROM groufty_review r
  WHERE r.submission_task_id = t.task_id AND r.submitted
) submitted_reviews_count,
(
  SELECT COUNT(*)
  FROM groufty_review r
  WHERE r.submission_task_id = t.task_id
) reviews_count,
(
  SELECT AVG(r.grade)
  FROM groufty_review r
  WHERE r.submission_task_id = t.task_id
) avg_grade


FROM GROUFTY_TASK t
JOIN GROUFTY_USER u
ON t.author_id = u.author_id
LEFT JOIN GROUFTY_REVIEW_TEMPLATE rt
ON t.review_template_id = rt.review_template_id
WHERE t.task_list_id = :task_list_id
ORDER BY t.task_id ASC;