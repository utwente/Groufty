/// <reference path="../../../../typings/index.d.ts" />

import {Component} from "@angular/core";
import {OnInit} from "@angular/core";
import {RouteParams} from '@angular/router-deprecated';
import {TaskDetail} from "../../../model/tasks/task-detail";
import {PDFComponent} from "../../utils/pdf.component";
import {MarkdownComponent} from "../../utils/markdown.component";
import {TaskDetailService} from "../../../services/task-detail.service";
import {FileUploadComponent} from "../../utils/file-upload.component";
import {MomentPipe} from "../../../utils/moment.pipe";
import {SubmissionService} from "../../../services/submission.service";
import {Submission} from "../../../model/submissions/submission";
import {MessagesComponent} from "../../utils/messages.component";
import {Message} from "../../../model/utils/message";
import {GrouftyFileDescriptor} from "../../../utils/file-upload/descriptor.file-upload";
import {AlertComponent} from "../../utils/alert.component";

@Component({
    templateUrl: './templates/student/tasks/student-task-detail.component.html',
    directives: [FileUploadComponent, PDFComponent, MarkdownComponent, MessagesComponent, AlertComponent],
    providers: [TaskDetailService, SubmissionService],
    pipes: [MomentPipe]
})
export class StudentTaskDetailComponent implements OnInit{
	// Error bool
	private showErrorPage : boolean;
	private errorMsg : string;

	// Message array
	private messages : Message[] = [];

	// Task detail object and related fields
    private taskDetail : TaskDetail = new TaskDetail();
    private deadlinePassed : boolean = false;
    private lastSaved : moment.Moment;
    private submitted : boolean;
	private groupSubmission : boolean;
	private hasTaskPdf : boolean = false;

	private submissionText : string;
	private displaySubmission : boolean = true;
	private pdfSubmission : boolean = false;
	private fileSubmission : boolean = false;


	private descriptors : GrouftyFileDescriptor[] = new Array<GrouftyFileDescriptor>(1);

	// Default PDF submission file upload descriptor
	public static submissionDescriptor = new GrouftyFileDescriptor("multipartFile", "submission", 1, 1, 160000000);

    public constructor(private _taskDetailService : TaskDetailService, private _submissionService : SubmissionService, private _routeParams : RouteParams) {}

    public ngOnInit() : void {
        this.descriptors[0] = StudentTaskDetailComponent.submissionDescriptor;
		var taskId = parseInt(this._routeParams.get('id'));
        this.getTaskDetailsById(taskId);
        this.lastSaved = moment();
    }

    private getTaskDetailsById(taskId : number) : void {
        this._taskDetailService.getStudentTaskDetails(taskId).subscribe(
            taskDetail => {
                this.taskDetail = taskDetail;
                this.hasTaskPdf = taskDetail.taskFileName != undefined;
				this.submissionText = taskDetail.submissionText;
                this.pdfSubmission = taskDetail.contentType == "PDF_SUBMISSION";
                this.fileSubmission = taskDetail.contentType == "FILE_SUBMISSION";
                this.groupSubmission = taskDetail.submissionAudience == "GROUP";

				if (!this.pdfSubmission && !this.fileSubmission) {
					if (taskDetail.submissionText == undefined) {
						this.displaySubmission = false;
					} else {
						this.displaySubmission = taskDetail.submissionText.trim() != "";
					}

				} else {
					this.displaySubmission = taskDetail.submissionFileName != undefined;
				}

                var today = moment();
                var deadline = moment(taskDetail.submissionDeadline);
                this.deadlinePassed = today.isAfter(deadline);
                this.submitted = taskDetail.submitted;
            }, error => {
		        if (error.status == 404) {
			        this.errorMsg = "404 - The review could not be found.";
		        } else {
			        this.errorMsg = (error.message) ? error.message : error.status ? `${error.status} - ${error.statusText}` : 'Server error';
		        }
			}
        );
    }

    public onUploadStart() {
        this.displaySubmission = false;
    }

    public onUploadComplete() {
        this.displaySubmission = true;
    }

    public onAutosaveDetect() {
        let now = moment();
        let diff = now.diff(this.lastSaved, 'seconds');
        if (diff > 60) {
            this._save(this.submitted);
        }
    }
    
    private submitSubmission(submit : boolean) {
        this._save(submit, false);
    }

	private _save(submit : boolean, silent : boolean = true) : void {
		// Create new submission to send
		this.lastSaved = moment();
		var submission = new Submission();
		submission.text = this.submissionText;
		submission.submitted = submit;

		this.messages = [];
		this._submissionService.updateSubmission(submission, this.taskDetail.taskId, this.taskDetail.authorId).subscribe(r => {
			if(submit) {
				this.submitted = true;
				if (!silent) {
					this.messages.push(new Message("The submission has been saved and submitted successfully.", "success", true, 2500));
				}
			} else {
				this.submitted = false;
				if (!silent) {
					this.messages.push(new Message("The submission has been saved. Be sure to sumbit your work once ready.", "warning", true, 8000));
				}
			}
		}, e => {
			this.messages.push(new Message("The submission could not be saved. Please try again later.", "danger"));
		});
	}
}