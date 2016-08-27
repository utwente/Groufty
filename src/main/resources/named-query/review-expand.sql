-- Select all reviews an author has to do for a specific task id where the review deadline is in the future
-- and the submission deadline in the past
-- This includes reviews assigned to a group the student is part of
-- returns
-- review_id, reviewer_name, reviewer_type, submitter_name, submission_audience, submitted, last_edited, grade,
-- anonymous_submissions, anonymous_reviews

SELECT
r.review_id,
(
  SELECT u.full_name
  FROM GROUFTY_USER u
  WHERE u.author_id = r.author_id

  UNION ALL

  SELECT g.group_name
  FROM GROUFTY_GROUP g
  WHERE g.author_id = r.author_id
) reviewer_name,
(
  SELECT 'INDIVIDUAL'
  FROM GROUFTY_USER u
  WHERE u.author_id = r.author_id

  UNION ALL

  SELECT 'GROUP'
  FROM GROUFTY_GROUP g
  WHERE g.author_id = r.author_id
) reviewer_type,
(
  SELECT u.full_name
  FROM GROUFTY_USER u
  WHERE u.author_id = r.submission_author_id

  UNION ALL

  SELECT g.group_name
  FROM GROUFTY_GROUP g
  WHERE g.author_id = r.submission_author_id
) submitter_name,
tl.submission_audience,
r.submitted,
r.last_edited,
r.grade,
tl.anonymous_submissions,
tl.anonymous_reviews

FROM GROUFTY_REVIEW r
INNER JOIN
GROUFTY_TASK_LIST tl
ON tl.task_list_id = r.submission_task_list_id
WHERE (
  r.author_id = :author_id
  OR r.author_id IN (
    SELECT gu.groups_author_id
    FROM GROUFTY_GROUP_USER gu
    WHERE gu.users_author_id = :author_id
  )
)
AND r.submission_task_id = :task_id
ORDER BY r.review_id ASC;
