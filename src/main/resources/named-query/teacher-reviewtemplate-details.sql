-- Identical to reviewtemplate-overview, but returns at most one result
SELECT
rt.*,
(
  SELECT COUNT(*) FROM GROUFTY_REVIEW_TEMPLATE_PROPERTY rtp
  WHERE rtp.review_template_id = rt.review_template_id
) properties,
(
  SELECT COUNT(*) FROM GROUFTY_TASK t
  WHERE t.review_template_id = rt.review_template_id
) tasks,
(
  SELECT COUNT(*) FROM GROUFTY_REVIEW r
  JOIN GROUFTY_TASK t
  ON r.submission_task_id = t.task_id
  WHERE t.review_template_id = rt.review_template_id
) reviews
FROM GROUFTY_REVIEW_TEMPLATE rt
WHERE rt.review_template_id = :review_template_id