/// <reference path="../../../../typings/index.d.ts" />

import {Component, OnInit, ViewChildren, QueryList} from "@angular/core";
import {RouteParams} from "@angular/router-deprecated";
import {ReviewDetailService} from "../../../services/review-detail.service";
import {PDFComponent} from "../../utils/pdf.component";
import {MarkdownComponent} from "../../utils/markdown.component";
import {MomentPipe} from "../../../utils/moment.pipe";
import {ReviewDetail} from "../../../model/reviews/review-detail";
import {StudentReviewEditor} from "../review-editor/student-review-editor";
import {AlertComponent} from "../../utils/alert.component";

@Component({
    templateUrl: './templates/student/reviews/student-review-detail.component.html',
    directives: [PDFComponent, MarkdownComponent, StudentReviewEditor, AlertComponent],
    providers: [ReviewDetailService],
    pipes: [MomentPipe]
})
export class StudentReviewDetailComponent implements OnInit {
	// Error bool
	private showErrorPage : boolean;
	private errorMsg : string;

	// Review detail object and related fields
    private reviewId:number;
    private reviewDetail : ReviewDetail = new ReviewDetail();
    private groupReview : boolean;
	private submitted : boolean;
	private deadlinePassed : boolean;
	private displaySubmission : boolean = false;
	private pdfSubmission : boolean = false;
	private fileSubmission : boolean = false;
	private hasTaskPdf : boolean = false;

    // The active PDF components
    @ViewChildren(PDFComponent) private _pdfs : QueryList<PDFComponent>;

	// Focus bools
	private defaultFocus : boolean = true;
	private rightFocus : boolean = false;

	// Expand / collapse bools
	private expandTask : boolean = true;
	private expandSubmission : boolean = true;

    public constructor(private _reviewDetailService:ReviewDetailService, private _routeParams:RouteParams) {
    }

    public ngOnInit():void {
        this.reviewId = parseInt(this._routeParams.get('id'));
        this.getReviewDetailsById(this.reviewId);
    }

    private getReviewDetailsById(reviewId:number):void {
        this._reviewDetailService.getReviewDetails(reviewId).subscribe(
            reviewDetails => {
                this.reviewDetail = reviewDetails;
                this.hasTaskPdf = reviewDetails.taskFileName != undefined;
                this.submitted = reviewDetails.submitted;
                this.displaySubmission = reviewDetails.submissionFile != undefined;
                this.pdfSubmission = reviewDetails.submissionContentType == "PDF_SUBMISSION";
                this.fileSubmission = reviewDetails.submissionContentType == "FILE_SUBMISSION";
                this.groupReview = reviewDetails.reviewAudience == "GROUP";
                let today = moment();
                let deadline = moment(reviewDetails.reviewDeadline);
                this.deadlinePassed = today.isAfter(deadline);

            }, error => {
                this.showErrorPage = true;
		        if (error.status == 404) {
			        this.errorMsg = "404 - The review task could not be found.";
		        } else {
			        this.errorMsg = (error.message) ? error.message : error.status ? `${error.status} - ${error.statusText}` : 'Server error';
		        }
            }
        );
    }

	// Expand or collapse the task menu
    public changeTaskSize() {
        this.expandTask = !this.expandTask;
    }

	// Expand or collapse the submission menu
    public changeSubmissionSize() {
        this.expandSubmission = !this.expandSubmission;
    }


	// Focuses the right part of the page by enlarging the column width
	public setRightFocus() {
        this.defaultFocus = !this.defaultFocus;
        this.rightFocus = !this.defaultFocus;
        setTimeout(() => this._pdfs.forEach(t => t.resize()), 100);

	}

}