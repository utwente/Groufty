
import {ContentType} from "../tasks/task";
export class SubmissionListExpand {
	public taskName : string;
	public taskId : number;
	public contentType : ContentType;

	public grade : number;
	public highestReviewGrade : number;
	public lowestReviewGrade : number;
	public largestDiffReviewGrade : number;

	public lastEdited : number;

	public submitted : boolean;
	public submittedReviewCount : number;
	public totalReviewCount : number;

	public hasFlaggedReviews : boolean;
}