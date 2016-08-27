-- Return all submissions in a submission list
-- This will return phantom submissions for submissions that do not exist in the database yet
-- Arguments
-- author_id - author id of the user the list is for (currently authenticated user)
-- tasklist_id - the tasklist to search submissions for
-- Returns
-- task_id, task_file_name, name, content_type, last_edited, submitted, submission_text, submission_file_name, grade,
-- review_deadline

SELECT
t.task_id,
t.file_name task_file_name,
t.name,
t.content_type,
s.last_edited,
s.submitted,
s.file_name submission_file_name,
CASE WHEN sl.finalized THEN s.grade ELSE null END grade, -- grade hiding before finalized
tl.review_deadline

FROM GROUFTY_TASK t
INNER JOIN GROUFTY_TASK_LIST tl
ON t.task_list_id = tl.task_list_id
LEFT JOIN GROUFTY_SUBMISSION s
ON t.task_id = s.task_id AND
(s.author_id = :author_id OR s.author_id IN ( -- either the author id itself or one of its group author ids
  SELECT g.author_id FROM GROUFTY_GROUP g
  INNER JOIN GROUFTY_GROUP_USER gu
  ON gu.users_author_id = :author_id
  AND gu.groups_author_id = g.author_id)
)
LEFT JOIN GROUFTY_SUBMISSION_LIST sl
ON s.task_list_id = sl.task_list_id
AND s.author_id = sl.author_id
WHERE t.task_list_id = :tasklist_id
ORDER BY t.task_order, t.name ASC