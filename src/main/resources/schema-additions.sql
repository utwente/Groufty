-- PostgreSQL script which gets executed after schema generation in profile=PROD

-- Add trigger to prevent orphaned OIDs from filling up the disk
-- See http://www.postgresql.org/docs/9.4/static/lo.html
CREATE TRIGGER t_oid_manage BEFORE UPDATE OR DELETE ON groufty_submission
  FOR EACH ROW EXECUTE PROCEDURE lo_manage(file);

CREATE TRIGGER t_oid_manage BEFORE UPDATE OR DELETE ON groufty_task
    FOR EACH ROW EXECUTE PROCEDURE lo_manage(file);