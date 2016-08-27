export type Audience = "GROUP" | "INDIVIDUAL";
export type StudentTaskListState = "IN_PROGRESS" | "OPEN" | "SUBMITTED" |
                                "UNDER_REVIEW" | "REVIEWED" | "FINALIZED";
export type TeacherTaskListState = "ACTIVE" | "FINALIZED" | "DRAFT";

// The base task list object
export class TaskList {
	public taskListId : number;
	public taskListName : string;
	public submissionAudience : Audience;
	public submissionDeadline : number;
	public reviewDeadline : number;
	
	public authorName : string; // Submission author's name
	
	public taskCount : number; // number of tasks in the task list
	public submittedSubmissionsCount : number; // number of submitted tasks for this task list
}

// Task list used on student task list-overview page.
// This object also partly models submissionlist
export class StudentTaskListOverview extends TaskList{
	public state : StudentTaskListState;

    public authorId : number; // Groufty author id
    public lastEdited : number;
    public grade : number;
}

// Task list used on teacher task list overview pages.
// This object contains more general information about a task list
export class TeacherTaskListOverview extends TaskList{
	public state : TeacherTaskListOverview;

	public startDate : number; // From this date on, task list will be visible for students
    public anonymousSubmissions : boolean;
    public anonymousReviews : boolean;
    public authorUserId : string; // Uni user id (m123..)

	public submitterCount : number; // Number of students submitting to this task list (counting students in groupings)

	public groupingId : number; // Grouping to which task list was assigned
	public groupingName : string; // Name of the grouping

	public reviewercount : number; // number of reviewers assigned by review selection strategy
	public submittedReviewsCount : number; // number of submitted reviews
}