
export class SubmissionListOverview {
	public userId : string;
	public authorId : string;
	public authorName : string;
	public calculatedGrade : number;
	public finalized : boolean;
	public largestDiffReviewGrade : number;
	public overrideFinalGrade : number;
	
	public submissionCount : number;
	public submittedReviewCount : number;
	public totalReviewCount : number;

	public hasFlaggedReviews : boolean;
}