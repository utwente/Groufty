-- Same as student-submission-details, but accepts author id of the direct author instead of a user author id
-- So in case of a group submission, it expects the groups author id, whereas student-submission-detail expects
-- the author_id of one of its members

SELECT
t.task_id, t.file_name task_file_name, t.name, t.description, t.content_type, t.review_template_id,
s.last_edited, s.submitted, s.text submission_text, s.file_name submission_file_name,
CASE WHEN sl.finalized THEN s.grade ELSE null END, -- hide grade when not finalized
tl.name task_list_name,
tl.review_deadline,
tl.start_date,
tl.state,
tl.submission_deadline,
tl.submission_audience,
tl.submission_author_id,
tl.submission_author_name

FROM GROUFTY_TASK t
INNER JOIN ( -- task list + submission author name and submission author id

  -- Find author if its an individual author
  SELECT tl.*,
  u.author_id submission_author_id,
  u.full_name submission_author_name

  FROM GROUFTY_TASK_LIST tl
  INNER JOIN GROUFTY_GROUPING_USER gu
  ON gu.groupings_grouping_id = tl.grouping_id
  INNER JOIN GROUFTY_USER u
  ON u.author_id = gu.USERS_AUTHOR_ID
  WHERE tl.submission_audience = 'INDIVIDUAL' AND gu.users_author_id = :author_id

  UNION ALL

  -- Find author if its a group author
  SELECT tl.*,
  g.author_id submission_author_id,
  g.group_name submission_author_name

  FROM GROUFTY_TASK_LIST tl
  INNER JOIN GROUFTY_GROUP g
  ON g.grouping_id = tl.grouping_id
  WHERE tl.submission_audience = 'GROUP' AND g.author_id = :author_id
) tl

ON tl.task_list_id = t.task_list_id
LEFT JOIN GROUFTY_SUBMISSION s
ON t.task_id = s.task_id
AND s.author_id = tl.submission_author_id
LEFT JOIN GROUFTY_SUBMISSION_LIST sl
ON sl.task_list_id = tl.task_list_id
AND sl.author_id = tl.submission_author_id

WHERE t.task_id = :task_id
AND tl.start_date < now()
AND tl.state != 'DRAFT'
