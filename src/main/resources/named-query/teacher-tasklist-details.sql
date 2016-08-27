SELECT
tl.task_list_id,
tl.name,
tl.state,
tl.start_date,
tl.submission_deadline,
tl.review_deadline,
tl.anonymous_reviews,
tl.anonymous_submissions,
u.user_number,
u.user_type,
u.full_name,
tl.submission_audience,
g.grouping_id,
g.grouping_name,

-- Computed
(
SELECT COUNT(*)
  FROM groufty_task t
  WHERE t.task_list_id = tl.task_list_id
) task_count,

(
SELECT COUNT(*)
  FROM groufty_submission s
  WHERE s.task_list_id = tl.task_list_id AND s.submitted
) submitted_submissions_count,

(
SELECT COUNT(*)
  FROM groufty_review r
  WHERE r.submission_task_list_id = tl.task_list_id AND r.submitted
) submitted_reviews_count,

(
  CASE WHEN tl.submission_audience = 'INDIVIDUAL' THEN (
    SELECT COUNT(*) FROM groufty_grouping_user gu WHERE gu.groupings_grouping_id = tl.grouping_id
  )
  ELSE (
    SELECT COUNT(*) FROM groufty_group gp WHERE gp.grouping_id = tl.grouping_id
  )
  END
) submitter_count,

(
SELECT COUNT(*)
  FROM groufty_review r
  WHERE r.submission_task_list_id = tl.task_list_id
) reviewer_count
FROM GROUFTY_TASK_LIST tl
JOIN GROUFTY_USER u
ON tl.author_id = u.author_id
JOIN GROUFTY_GROUPING g
ON tl.grouping_id = g.grouping_id
WHERE tl.task_list_id = :task_list_id