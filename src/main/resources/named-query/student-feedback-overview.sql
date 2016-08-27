-- Select all task lists a student can see with some extra properties from the submission list (if it exists)
-- where the submission deadline is in the past
-- This includes task lists assigned to a group the student is part of
-- returns
-- task_list_id, name, review_deadline, start_date, state, submission_deadline, submission_audience, author_id, author_name
-- computed_grade, final_grade, last_edited, task_count, submitted_submission_count

SELECT
tl.*, -- all task list related attributes (and author id + name)

-- make sure not to include the grade when the submission list is not finalized
CASE WHEN sl.finalized THEN sl.override_final_grade ELSE null END final_grade,

(
  SELECT MAX(s.last_edited) FROM GROUFTY_SUBMISSION s
  WHERE s.task_list_id = tl.task_list_id
) last_edited,
(
	SELECT COUNT(*) FROM GROUFTY_TASK t
	WHERE t.task_list_id = tl.task_list_id
) task_count, -- total nr of tasks in the submission list
(
	SELECT COUNT(*) FROM GROUFTY_SUBMISSION s
	WHERE s.task_list_id = tl.task_list_id
	AND s.author_id = tl.author_id
	AND s.submitted
) submitted_submission_count -- nr of completed submissions

FROM (
	-- Select user tasklists
	SELECT tl.task_list_id, tl.name, tl.review_deadline, tl.start_date, tl.state, tl.submission_deadline, tl.submission_audience, u.author_id, u.full_name as author_name
	FROM GROUFTY_TASK_LIST tl
	INNER JOIN GROUFTY_GROUPING_USER gu
	ON gu.groupings_grouping_id = tl.grouping_id
	INNER JOIN GROUFTY_USER u
	ON u.author_id = gu.USERS_AUTHOR_ID
	WHERE tl.submission_audience = 'INDIVIDUAL' AND gu.users_author_id = :author_id

	UNION ALL

	-- Select group tasklists
	SELECT tl.task_list_id, tl.name, tl.review_deadline, tl.start_date, tl.state, tl.submission_deadline, tl.submission_audience, g.author_id, g.group_name as author_name
	FROM GROUFTY_TASK_LIST tl
	INNER JOIN GROUFTY_GROUP g
	ON g.grouping_id = tl.grouping_id
	INNER JOIN GROUFTY_GROUP_USER gu
	ON gu.groups_author_id = g.author_id
	WHERE tl.submission_audience = 'GROUP' AND gu.users_author_id = :author_id
) tl
LEFT JOIN GROUFTY_SUBMISSION_LIST sl
ON sl.task_list_id = tl.task_list_id
AND sl.author_id = tl.author_id
WHERE tl.state != 'DRAFT'
AND tl.submission_deadline < now()
ORDER BY tl.submission_deadline, tl.name ASC
