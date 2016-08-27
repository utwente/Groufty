-- User and group stuff
INSERT INTO
GROUFTY_AUTHOR("AUTHOR_ID", "TYPE")
VALUES
(1, 'USER'), (2, 'USER'), (3, 'USER'), (4, 'USER'), (5, 'USER'), (6, 'USER'), (7, 'USER'), (8, 'USER'), (9, 'USER'),
(100, 'GROUP'), (101, 'GROUP'), (102, 'GROUP'), (103, 'GROUP'), (104, 'GROUP');

INSERT INTO
GROUFTY_USER("USER_NUMBER", "AUTHOR_ID", "USER_TYPE", "AUTHORITY", "FULL_NAME")
VALUES
(0, 1, 'EMPLOYEE', 'ROLE_EDITOR', 'Joshua Rivera (Demo)'),
(1, 2, 'EMPLOYEE', 'ROLE_EDITOR', 'Rosemary Lewis (Demo)'),
(0, 3, 'STUDENT', 'ROLE_PARTICIPANT', 'Nelson Craig (Demo)'),
(1, 4, 'STUDENT', 'ROLE_PARTICIPANT', 'Tina Martin (Demo)'),
(2, 5, 'STUDENT', 'ROLE_PARTICIPANT', 'Clayton Cruz (Demo)'),
(3, 6, 'STUDENT', 'ROLE_PARTICIPANT', 'Jacqueline Ruiz (Demo)'),
(4, 7, 'STUDENT', 'ROLE_PARTICIPANT', 'Patrick Lopez (Demo)'),
(5, 8, 'STUDENT', 'ROLE_PARTICIPANT', 'Antonio Murphy (Demo)'),
(6, 9, 'STUDENT', 'ROLE_NONE', 'Thomas Mccoy (Demo)'); -- no role

INSERT INTO
GROUFTY_GROUPING("GROUPING_ID", "GROUPING_NAME")
VALUES
(1, 'Student Grouping'),
(2, 'Other student grouping'),
(3, 'Employees');

INSERT INTO
GROUFTY_GROUP("AUTHOR_ID", "GROUP_NAME", "GROUPING_ID")
VALUES
(100, 'Group A.1', 1),
(101, 'Group A.2', 1),
(102, 'Group B.1', 2),
(103, 'Group B.2', 2);

INSERT INTO
GROUFTY_GROUPING_USER("GROUPINGS_GROUPING_ID", "USERS_AUTHOR_ID")
VALUES
(1,3), (1,4), (1,5), (1,7), (2,8), (2,9), (2,6);

INSERT INTO
GROUFTY_GROUP_USER("GROUPS_AUTHOR_ID", "USERS_AUTHOR_ID")
VALUES
(100,3),(100,4),(100,5),(100,7),
(101,8),(101,9),(101,6),
(102,3),(102,5),(102,7),(102,9),
(103,4),(103,8),(103,6);


-- Review templates
INSERT INTO
GROUFTY_REVIEW_TEMPLATE("REVIEW_TEMPLATE_ID", "NAME")
VALUES
(1, 'Basic Review Template'),
(2, 'Complex Review Template');

INSERT INTO
GROUFTY_REVIEW_TEMPLATE_PROPERTY("REVIEW_TEMPLATE_PROPERTY_ID", "REVIEW_TEMPLATE_ID", "TYPE", "WEIGHT", "DESCRIPTION")
VALUES
(1, 1, 'TEXT', 0, 'What do you think about the layout?'), -- basic template
(2, 1, 'GRADE', 4, 'If you wrote this, what grade would you expect?'),

(3, 2, 'RUBRIC', 12, 'Please rate the following rubrics'), -- complex template
(4, 2, 'TEXT', 35, 'What could be **better** (*not worse*)?'),
(5, 2, 'GRADE', 4, 'How likely are you to recommend this paper to a friend?');

INSERT INTO
GROUFTY_RUBRIC_TEMPLATE_COLUMN("ID", "REVIEW_TEMPLATE_PROPERTY_ID", "LABEL", "VALUE")
VALUES
(1, 3, 'Poor', '1'),
(2, 3, 'Excellent', '10'),
(3, 3, 'Sufficient', '5.5');

INSERT INTO
GROUFTY_RUBRIC_TEMPLATE_ROW("ID", "REVIEW_TEMPLATE_PROPERTY_ID", "LABEL", "WEIGHT")
VALUES
(1, 3, 'Spelling', '5'),
(2, 3, 'Style usage', '1.2');

INSERT INTO GROUFTY_RUBRIC_TEMPLATE_CELL("ID", "RUBRIC_TEMPLATE_ROW_ID", "LABEL")
VALUES
(1, 1, 'Unreadable'),
(2, 1, 'Barely readable'),
(3, 1, 'I want more'),
(4, 2, 'No styles used'),
(5, 2, 'Some styles used'),
(6, 2, 'All styles used');


-- Task and task list stuff
INSERT INTO
GROUFTY_TASK_LIST("TASK_LIST_ID", "NAME", "AUTHOR_ID", "GROUPING_ID", "SUBMISSION_AUDIENCE", "ANONYMOUS_REVIEWS", "STATE", "START_DATE", "SUBMISSION_DEADLINE", "REVIEW_DEADLINE")
VALUES
(1, 'Week 1', 1, 1, 'INDIVIDUAL', false, 'FINALIZED', {ts '2016-01-02 00:00:00.00'}, {ts '2016-01-25 00:00:00.00'}, {ts '2016-02-15 00:00:00.00'}),
(2, 'Week 2', 1, 1, 'GROUP', false, 'ACTIVE', {ts '2016-01-02 00:00:00.00'}, {ts '2016-01-25 00:00:00.00'}, {ts '2016-12-15 00:00:00.00'}),
(3, 'Week 3 - Project', 2, 1, 'GROUP', false, 'ACTIVE', {ts '2016-03-01 00:00:00.00'}, {ts '2016-04-01 00:00:00.00'}, {ts '2016-05-01 00:00:00.00'}),
(4, 'Week ???', 2, 1, 'GROUP', false, 'ACTIVE', {ts '2016-03-01 00:00:00.00'}, {ts '2016-06-01 00:00:00.00'}, {ts '2016-07-01 00:00:00.00'}),
(5, 'Week 5', 2, 1, 'GROUP', true, 'ACTIVE', {ts '2016-04-01 00:00:00.00'}, {ts '2016-05-01 00:00:00.00'}, {ts '2016-06-01 00:00:00.00'}),
(6, 'Week 6', 2, 1, 'GROUP', false, 'DRAFT', {ts '2016-03-01 00:00:00.00'}, null, null);

INSERT INTO
GROUFTY_TASK("TASK_ID", "TASK_LIST_ID", "CONTENT_TYPE", "SHOW_GRADES_TO_REVIEWERS", "NAME", "DESCRIPTION", "AUTHOR_ID", "REVIEW_TEMPLATE_ID", "FILE", "FILE_NAME")
VALUES
(1, 1, 'PDF_SUBMISSION', true, 'Task A - Introduction', 'Here students will be introduced to the task system', 1, 1, null, null),
(2, 1, 'PDF_SUBMISSION', false, 'Task B - Serious business', null, 1, 1, null, null),
(3, 1, 'PDF_SUBMISSION', true, 'Task C - Number crunching', 'Everyone likes math, right?!', 1, 1, null, null),
(4, 1, 'PDF_SUBMISSION', false, 'Task D - NP=P?', 'Spannend', 1, 2, null, null),
(5, 1, 'PDF_SUBMISSION', true, 'Task E - Turing Machines', 'Its like video tapes all over again', 1, 2, null, null),
(6, 1, 'PDF_SUBMISSION', false, 'Task F - Finite groups', 'No group is truly finite if you allow repeated elements', 1, 2, null, null),
(7, 1, 'PDF_SUBMISSION', true, 'Task G - Infinite groups', 'Quite many elements indeed', 1, 2, null, null),
(8, 1, 'PDF_SUBMISSION', false, 'Task H - Dichromate groups', 'Color blind people wont relate', 1, 2, null, null),
(9, 1, 'PDF_SUBMISSION', true, 'Task I - Counting to a million', 'A million is only much when you have to count that far', 1, 1, null, null),
(10, 1, 'PDF_SUBMISSION', false, 'Task J - Counting to a billion', 'Better start now. Deadline is in 50 years', 1, 1, null, null),
(11, 1, 'PDF_SUBMISSION', true, 'Task K - Counting even further', 'And you thought a billion was a lot', 1, 2, null, null),
(30, 2, 'PDF_SUBMISSION', true, 'Task 1 - First group task', null, 1, 2, null, null),
(31, 2, 'PDF_SUBMISSION', true, 'Task 2 - Introduction', 'Here students will be introduced to the task system', 1, 2, FILE_READ('testdata/task-file/task-file-1.pdf'), 'uml.pdf'),
(32, 2, 'PDF_SUBMISSION', false, 'Task 3 - Working with UML', 'This one has a file as well', 1, 1, FILE_READ('testdata/uml.pdf'), 'uml.pdf'),
(33, 3, 'PDF_SUBMISSION', true, 'Final Assignment 1 - Basic', null, 1, 2, null, null),
(34, 3, 'PDF_SUBMISSION', false, 'Final Assignment 2 - Advanced', null, 1, 2, null, null),
(35, 4, 'PDF_SUBMISSION', true, 'Task 1 - Configure your laptop', null, 1, null, FILE_READ('testdata/task-file/task-file-1.pdf'), 'uml.pdf'),
(36, 4, 'PDF_SUBMISSION', false, 'Task 2 - Jump!', 'This exercise was to see if you would follow orders', 1, null, null, null),
(37, 4, 'PDF_SUBMISSION', true, 'Task 3 - Basic and advanced bufferoverflow', null, 1, null, FILE_READ('testdata/task-file/task-file-2.pdf'), 'uml.pdf'),
(38, 4, 'PDF_SUBMISSION', false, 'Task 4 - Penny waffes', 'Create your own brand of Penny Waffes. If you do not know what Penny Waffes are, go to the store and get some!', 1, null, null, null),
(39, 4, 'TEXT_SUBMISSION', true, 'Task 5 - Test printer', null, 2, null, FILE_READ('testdata/task-file/task-file-3.pdf'), 'Task-3.pdf'),
(40, 5, 'PDF_SUBMISSION', false, 'Task A - Data visualisation', 'Now we have had multiple tasks, we can make a proper data visualisation of that data.', 2, null, null, null),
(41, 5, 'PDF_SUBMISSION', true, 'Task B - Sorting', null, 2, null, FILE_READ('testdata/task-file/task-file-4.pdf'), 'Task-4.pdf'),
(42, 5, 'PDF_SUBMISSION', false, 'Task C - Generate tasks', 'Think of tasks that the create students can do', 2, null, null, null),
(43, 5, 'PDF_SUBMISSION', true, 'Task D - PHP shortcuts', null, 2, null, FILE_READ('testdata/task-file/task-file-5.pdf'), 'Task-5.pdf'),
(44, 6, 'PDF_SUBMISSION', false, 'Task 1 - Chapter one', 'This task is called chapter one because the next tasks is called chapter two and three', 2, null, null, null),
(45, 6, 'PDF_SUBMISSION', true, 'Task 1.1 - Chapter two and three', null, 2, null, FILE_READ('testdata/task-file/task-file-6.pdf'), 'Task-6.pdf'),
(46, 6, 'PDF_SUBMISSION', false, 'Task 1.2 - Chapter 4', 'See Task 1 - Chapter 1 why this task is called Chapter 4', 2, null, null, null),
(47, 6, 'PDF_SUBMISSION', true, 'Task 2 - Data visualisation', null, 2, null, FILE_READ('testdata/task-file/task-file-7.pdf'), 'Task-7.pdf'),
(48, 6, 'PDF_SUBMISSION', false, 'Task 3 - Game', 'Do you want to play a game?', 2, null, null, null);

INSERT INTO
GROUFTY_REVIEWER_SELECTION_STRATEGY("TASK_LIST_ID", "REVIEW_COUNT", "PRIMED", "REVIEW_AUDIENCE", "FROM_GROUPING_ID")
VALUES
(2, 4, true, 'INDIVIDUAL', 1);

INSERT INTO
GROUFTY_RANDOM_REVIEWER_SELECTION_STRATEGY("TASK_LIST_ID", "SKIP_SUBMISSIONLESS_AUTHORS")
VALUES
(2, false);


-- Submissions
INSERT INTO
GROUFTY_SUBMISSION_LIST("TASK_LIST_ID", "AUTHOR_ID", "OVERRIDE_FINAL_GRADE", "FINALIZED")
VALUES
(1, 3, '7.3', false),
(1, 4, null, false),
(2, 100, null, false),
(2, 101, null, false),
(3, 100, null, false),
(3, 101, null, false),
(4, 100, null, false),
(4, 101, null, false),
(5, 100, null, false),
(5, 101, null, false),
(6, 100, null, false),
(6, 101, null, false);


INSERT INTO
GROUFTY_SUBMISSION("TASK_LIST_ID", "TASK_ID", "AUTHOR_ID", "SUBMITTED", "LAST_EDITED", "GRADE", "FILE_NAME", "FILE")
VALUES
(1, 1, 3, true, {ts '2016-04-01 15:33:52.34'}, '8.1', 'downloadthisawesomepdffilenow.pdf', FILE_READ('testdata/file-submission/file-submission-1.pdf')),
(1, 3, 4, true, {ts '2016-04-03 15:43:52.34'}, null, 'Surely nobody would ever put illegal characters:\in/these"names.right?.pdf', FILE_READ('testdata/file-submission/file-submission-1.pdf')),
(2, 30, 100, false, {ts '2016-04-02 09:36:52.34'}, null, 'Submission 1.pdf', FILE_READ('testdata/file-submission/file-submission-1.pdf')),
(2, 30, 101, false, {ts '2016-04-03 15:43:52.34'}, null, 'Submission 2.pdf', FILE_READ('testdata/file-submission/file-submission-2.pdf')),
(2, 31, 101, true, {ts '2016-04-03 15:43:52.34'}, null, 'But why did the chicken cross the road.pdf', FILE_READ('testdata/file-submission/file-submission-2.pdf')),
(3, 34, 100, false, {ts '2016-04-03 15:43:52.34'}, null, 'But why did the chicken cross the road.pdf', FILE_READ('testdata/file-submission/file-submission-2.pdf')),
(4, 39, 100, false, {ts '2016-04-03 15:43:50.34'}, null, 'submission-4.pdf', FILE_READ('testdata/file-submission/file-submission-4.pdf')),
(5, 40, 100, true, {ts '2016-04-03 15:43:52.44'}, null, 'submission-3.pdf', FILE_READ('testdata/file-submission/file-submission-3.pdf')),
(5, 43, 100, true, {ts '2016-04-03 15:43:52.33'}, null, 'submission-5.pdf', FILE_READ('testdata/file-submission/file-submission-5.pdf')),
(6, 46, 100, false, {ts '2016-04-03 05:43:45.34'}, null, 'submission-6.pdf', FILE_READ('testdata/file-submission/file-submission-6.pdf')),
(6, 47, 100, true, {ts '2016-04-03 15:00:52.34'}, null, 'submission-7.pdf', FILE_READ('testdata/file-submission/file-submission-7.pdf'));


-- Reviews
INSERT INTO
GROUFTY_REVIEW("REVIEW_ID", "AUTHOR_ID", "SUBMISSION_TASK_LIST_ID", "SUBMISSION_TASK_ID", "SUBMISSION_AUTHOR_ID", "SUBMITTED", "GRADE")
VALUES
(1, 1, 1, 1, 3, false, '6.5'),
(2, 100, 2, 30, 101, false, null),
(3, 100, 2, 31, 101, false, null),
(4, 4, 1, 1, 3, true, '5.2'),
(5, 5, 1, 1, 3, true, '8.1'),
(6, 6, 1, 1, 3, true, '7.4'),
(7, 101, 1, 1, 3, true, '7.0');

INSERT INTO
GROUFTY_REVIEW_PROPERTY("REVIEW_PROPERTY_ID", "REVIEW_ID", "TYPE", "TEXT", "GRADE")
VALUES
(1, 1, 'TEXT', 'Its absolutely marvelous. I have **never** seen something like this in my life! <h1>Hi</h1>', null),
(2, 1, 'GRADE', null, '9.1'),
(3, 2, 'RUBRIC', null, null),
(4, 2, 'TEXT', 'I really like pi', null),
(5, 2, 'GRADE', null, '3.14');

INSERT INTO
GROUFTY_RUBRIC_OPTION("ID", "REVIEW_PROPERTY_ID", "OPTION")
VALUES
(1, 3, null),
(2, 3, 0),
(3, 3, 3);