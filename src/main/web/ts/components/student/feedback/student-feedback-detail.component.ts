/// <reference path="../../../../typings/index.d.ts" />

import {Component, OnInit, ViewChildren, QueryList} from "@angular/core";
import {StudentFeedbackTemplateComponent} from "./student-feedback-template.component";
import {TaskDetail} from "../../../model/tasks/task-detail";
import {TaskDetailService} from "../../../services/task-detail.service";
import {RouteParams} from "@angular/router-deprecated";
import {MomentPipe} from "../../../utils/moment.pipe";
import {PDFComponent} from "../../utils/pdf.component";
import {MarkdownComponent} from "../../utils/markdown.component";
import {StudentReviewEditor} from "../review-editor/student-review-editor";
import {AlertComponent} from "../../utils/alert.component";

@Component({
    templateUrl: './templates/student/feedback/student-feedback-detail.component.html',
    directives: [StudentFeedbackTemplateComponent,
        StudentReviewEditor, PDFComponent, MarkdownComponent, AlertComponent],
    providers: [TaskDetailService],
    pipes: [MomentPipe]
})
export class StudentFeedbackDetailComponent implements OnInit{
    // Error bool
    private showErrorPage : boolean;
    private errorMsg : string;

	// Task detail object and related fields
	private taskDetail : TaskDetail = new TaskDetail();
	private deadlinePassed : boolean = false;
	private lastSaved : Date;
	private submitted : boolean;

	private submissionText : string;
	private displaySubmission : boolean = true;
	private pdfSubmission : boolean = false;
	private fileSubmission : boolean = false;
	private groupSubmission : boolean;
	private hasTaskPdf : boolean = false;

	// The active PDF components
	@ViewChildren(PDFComponent) private _pdfs : QueryList<PDFComponent>;
	
	// Expanded items array - represeting expanded reviews
    public expanded : Array<boolean>;

	// Focus bools
	private defaultFocus : boolean = true;
	private rightFocus : boolean = false;
	
	// Expand / collapse bools
	private expandTask : boolean = true;
	private expandSubmission : boolean = true;
	
    public constructor(private _taskDetailService : TaskDetailService, private _routeParams : RouteParams) {}

    public ngOnInit() : void {
        var taskId = parseInt(this._routeParams.get('id'));
        this.getTaskDetailsById(taskId);
    }

    public getTaskDetailsById(taskId : number) : void {
        this._taskDetailService.getStudentTaskDetails(taskId).subscribe(
            taskDetail => {
                this.taskDetail = taskDetail;
                this.hasTaskPdf = taskDetail.taskFileName != undefined;
                this.displaySubmission = taskDetail.submissionFileName != undefined;
                this.pdfSubmission = taskDetail.contentType == "PDF_SUBMISSION";
                this.fileSubmission = taskDetail.contentType == "FILE_SUBMISSION";
                var today = moment();
                var deadline = moment(taskDetail.reviewDeadline);
                this.deadlinePassed = today.isAfter(deadline);
                this.submissionText = taskDetail.submissionText;
                this.expanded = new Array<boolean>(taskDetail.reviews.length);
            }, error => {
                this.showErrorPage = true;
		        if (error.status == 404) {
			        this.errorMsg = "404 - The review could not be found.";
		        } else {
			        this.errorMsg = (error.message) ? error.message : error.status ? `${error.status} - ${error.statusText}` : 'Server error';
		        }
            }
        );
    }

    public reviewExpand(index : number ) {
        if (this.expanded[index] === undefined) {
            this.expanded[index] = true
        } else {
            this.expanded[index] = !this.expanded[index];
        }
    }

    public onUploadComplete() {
        this.displaySubmission = false;
        setTimeout(() => this.displaySubmission = true, 100);
    }

    public onChange(input : any) {
        this.submissionText = input.value;
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