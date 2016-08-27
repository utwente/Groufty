SELECT
t.task_id,
t.content_type,
t.name,
t.show_grades_to_reviewers,
s.grade,
s.last_edited,
s.submitted,

(
  SELECT MIN(r.grade) FROM GROUFTY_REVIEW r
  WHERE r.submission_task_id = s.task_id
  AND r.submission_author_id = s.author_id
  AND r.submitted
  AND NOT r.disabled
) min_grade,

(
  SELECT MAX(r.grade) FROM GROUFTY_REVIEW r
  WHERE r.submission_task_id = s.task_id
  AND r.submission_author_id = s.author_id
  AND r.submitted
  AND NOT r.disabled
) max_grade,

(
  SELECT COUNT(*) FROM GROUFTY_REVIEW r
  WHERE r.submission_task_id = s.task_id
  AND r.submission_author_id = s.author_id
  AND r.submitted
  AND NOT r.disabled
) submitted_review_count,

(
  SELECT COUNT(*) FROM GROUFTY_REVIEW r
  WHERE r.submission_task_id = s.task_id
  AND r.submission_author_id = s.author_id
  AND NOT r.disabled
) total_review_count,

EXISTS(
    SELECT *
    FROM GROUFTY_REVIEW_FLAG r
      INNER JOIN groufty_review gr ON r.review_id = gr.review_id
    WHERE gr.submission_task_list_id = s.task_list_id
          AND gr.submission_author_id = s.author_id
          AND NOT gr.disabled
  -- not submitted cant be flagged anyways
) has_flagged_reviews

FROM GROUFTY_TASK t
LEFT JOIN GROUFTY_SUBMISSION s
ON s.task_id = t.task_id
AND s.author_id = :author_id
WHERE t.task_list_id = :task_list_id

ORDER BY t.task_order, t.name