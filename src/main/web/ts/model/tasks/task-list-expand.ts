import {ContentType} from "./task";

export class TaskListExpand {
    public taskId : number;
    public taskName : string;
	public contentType : ContentType;    
}

export class StudentTaskListExpand extends  TaskListExpand {
	public taskFileName : string;
	public lastEdited : number;
	public submitted : boolean;
	public submissionFileName : string;
	public grade : number;
}

export class TeacherTaskListExpand extends  TaskListExpand {
    public authorUserId : string;
	public authorName : string;
	public reviewTemplateId : string;
	public reviewTemplateName : string;
	public showGradesToReviewers : boolean;
	public submittedSubmissionsCount : number;
	public submittedReviewsCount : number;
	public reviewsCount : number;
	public averageGrade : number;
}





