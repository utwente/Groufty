SELECT *
FROM (
       SELECT
         tla.author_id,
         tla.user_type,
         tla.user_number,
         tla.author_name,
         sl.finalized,
         sl.override_final_grade,
         (
           SELECT COUNT(*)
           FROM GROUFTY_SUBMISSION s
           WHERE s.task_list_id = sl.task_list_id
                 AND s.author_id = sl.author_id
                 AND s.submitted
         ) submission_count,
         (
           SELECT AVG(s.grade)
           FROM GROUFTY_SUBMISSION s
           WHERE s.task_list_id = sl.task_list_id
                 AND s.author_id = sl.author_id
                 AND s.submitted
         ) calculated_grade,

         (
           SELECT COUNT(*)
           FROM GROUFTY_REVIEW r
           WHERE r.submission_task_list_id = tla.task_list_id
                 AND r.submission_author_id = tla.author_id
                 AND r.submitted
                 AND NOT r.disabled
         ) submitted_review_count,
         (
           SELECT COUNT(*)
           FROM GROUFTY_REVIEW r
           WHERE r.submission_task_list_id = tla.task_list_id
                 AND r.submission_author_id = tla.author_id
                 AND NOT r.disabled
         ) total_review_count,
         (
           SELECT MAX(r.grade) - MIN(r.grade) diff
           FROM GROUFTY_REVIEW r
           WHERE r.submission_task_list_id = tla.task_list_id
                 AND r.submission_author_id = tla.author_id
                 AND r.submitted
                 AND NOT r.disabled
           GROUP BY r.submission_author_id, r.submission_task_id
           ORDER BY diff DESC
           LIMIT 1
         ) max_diff,

         EXISTS(
             SELECT *
             FROM GROUFTY_REVIEW_FLAG r
               INNER JOIN groufty_review gr ON r.review_id = gr.review_id
             WHERE gr.submission_task_list_id = tla.task_list_id
                   AND gr.submission_author_id = tla.author_id
                   AND NOT gr.disabled
             -- not submitted cant be flagged anyways
         ) has_flagged_reviews

       FROM (
              SELECT
                tl.task_list_id, u.author_id, u.user_type, u.user_number, u.full_name author_name
              FROM GROUFTY_TASK_LIST tl
                JOIN GROUFTY_GROUPING_USER gu
                  ON tl.grouping_id = gu.groupings_grouping_id
                JOIN GROUFTY_USER u
                  ON gu.users_author_id = u.author_id
              WHERE SUBMISSION_AUDIENCE = 'INDIVIDUAL'

              UNION ALL

              SELECT
                tl.task_list_id, g.author_id, null, null, g.group_name author_name
              FROM GROUFTY_TASK_LIST tl
                JOIN GROUFTY_GROUP g
                  ON g.grouping_id = tl.grouping_id
              WHERE SUBMISSION_AUDIENCE = 'GROUP'
            ) tla

         LEFT JOIN GROUFTY_SUBMISSION_LIST sl
           ON sl.task_list_id = tla.task_list_id
              AND sl.author_id = tla.author_id

       WHERE tla.task_list_id = :task_list_id

       ORDER BY tla.user_number, tla.author_name ASC
     ) x
WHERE
  CASE WHEN :filter_min_diff
    THEN x.max_diff >= :min_diff
  ELSE true
  END