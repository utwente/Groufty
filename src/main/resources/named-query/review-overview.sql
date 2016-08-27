-- Select all tasks for which the provided author has reviews to make
-- This includes task assigned to a group the student is part of
-- returns
-- task_list_id, task_id, task_list_name, task_name, show_grades_to_reviewers, review_deadline, submission_audience,
-- anonymous_reviews, total_review_count, submitted_review_count, last_edited

SELECT *
FROM (
  SELECT
  tl.task_list_id,
  t.task_id,

  tl.name task_list_name,
  t.name task_name,
  t.show_grades_to_reviewers,

  tl.review_deadline,
  tl.submission_audience,
  tl.anonymous_reviews,
  (
    SELECT COUNT(*)
    FROM GROUFTY_REVIEW r
    WHERE r.submission_task_id = t.task_id
    AND (
      r.author_id = :author_id
      OR r.author_id IN (
        SELECT gu.groups_author_id
        FROM GROUFTY_GROUP_USER gu
        WHERE gu.users_author_id = :author_id
      )
    )
  ) total_review_count,
  (
    SELECT COUNT(*)
    FROM GROUFTY_REVIEW r
    WHERE r.submission_task_id = t.task_id
    AND r.submitted
    AND (
      r.author_id = :author_id
      OR r.author_id IN (
        SELECT gu.groups_author_id
        FROM GROUFTY_GROUP_USER gu
        WHERE gu.users_author_id = :author_id
      )
    )
  ) submitted_review_count,
  (
    SELECT MAX(r.last_edited)
    FROM GROUFTY_REVIEW r
    WHERE r.submission_task_id = t.task_id
    AND r.submitted
    AND (
      r.author_id = :author_id
      OR r.author_id IN (
        SELECT gu.groups_author_id
        FROM GROUFTY_GROUP_USER gu
        WHERE gu.users_author_id = :author_id
      )
    )
  ) last_edited

  FROM GROUFTY_TASK t
  INNER JOIN GROUFTY_TASK_LIST tl
  ON t.task_list_id = tl.task_list_id
  ORDER BY tl.review_deadline, tl.name ASC
) t
WHERE
t.total_review_count > 0