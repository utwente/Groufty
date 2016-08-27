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
tl.anonymous_reviews,
-- above is identical to student-review-expand.sql

tl.review_deadline,
s.file_name submission_file_name,
s.text submission_text,
t.content_type,
t.task_id,
t.name task_name,
t.file_name task_file_name,
t.description task_description,
t.review_template_id,
t.show_grades_to_reviewers

FROM GROUFTY_REVIEW r

INNER JOIN
GROUFTY_TASK_LIST tl
ON r.submission_task_list_id = tl.task_list_id

INNER JOIN
GROUFTY_TASK t
ON r.submission_task_id = t.task_id

INNER JOIN
GROUFTY_SUBMISSION s
ON s.author_id = r.submission_author_id
AND s.task_id = r.submission_task_id
AND s.task_list_id = r.submission_task_list_id

WHERE (
  r.author_id = :author_id
  OR r.author_id IN (
    SELECT gu.groups_author_id
    FROM GROUFTY_GROUP_USER gu
    WHERE gu.users_author_id = :author_id
  )
)
AND r.review_id = :review_id
AND tl.submission_deadline < now()
