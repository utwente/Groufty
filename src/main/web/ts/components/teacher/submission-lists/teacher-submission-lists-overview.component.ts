import {Component, OnInit} from "@angular/core";
import {Message} from "../../../model/utils/message";
import {Page} from "../../../model/utils/page";
import {TaskListService} from "../../../services/task-list.service";
import {MessagesComponent} from "../../utils/messages.component";
import {EnumCleanerPipe} from "../../../utils/enum-cleaner.pipe";
import {MomentPipe} from "../../../utils/moment.pipe";
import {Router, RouteParams} from "@angular/router-deprecated";
import {SubmissionListOverview} from "../../../model/submissions/submission-list-overview";
import {TaskListDetail} from "../../../model/tasks/task-list-detail";
import {TeacherSubmissionListsExpandComponent} from "./teacher-submission-lists-expand.component";
import {GrouftyHTTPService} from "../../../services/http.service";

@Component({
    templateUrl: './templates/teacher/submission-lists/teacher-submission-lists-overview.component.html',
    directives: [MessagesComponent, TeacherSubmissionListsOverviewComponent, TeacherSubmissionListsExpandComponent],
    providers: [TaskListService],
    pipes: [EnumCleanerPipe, MomentPipe]
})
export class TeacherSubmissionListsOverviewComponent  implements OnInit{
    // Message array
    public messages : Message[] = [];

    // Page object
    public page : Page<SubmissionListOverview> = new Page<SubmissionListOverview>();

	// Detail object
	public taskListDetail : TaskListDetail = new TaskListDetail();

    // Pagination defaults
    public currentPage : number = 0;
    public defaultSize : number = 10;

	// Difference filter default
	public difference : number = null;
	
    public signedLists : boolean = true;
	public unSignedLists : boolean = true;

    // Expanded items array - representing expanded task lists
    public expanded : Array<boolean>;

    public constructor(private _taskListService : TaskListService, private _router : Router, private _routeParams : RouteParams) {}

    public ngOnInit() : void {
	    var taskListId = parseInt(this._routeParams.get('id'));
        this.getSubmissionListsById(taskListId, this.difference);
	    this.getTaskListDetails(taskListId);
    }

    public exportTaskList() : void {
        window.location.href = GrouftyHTTPService.getAPI() + "/tasklists/" + this.taskListDetail.taskListId + "/export";
    }


    /* Filter section */

	public onSubmit() {
		this.getSubmissionListsById(this.taskListDetail.taskListId, this.difference);
	}

	public onResetFilters() {
		this.difference = null;
		this.getSubmissionListsById(this.taskListDetail.taskListId, this.difference);
	}

    /* Linking section */
	public browseToGrouping(groupingId : number) {
		//TODO: impl
		/*
		 this._router.navigate(['/TeacherSubmissionLists/TeacherSubmissionListsOverview', {id: taskListId}]);*/
	}	
	
	public browseToEditTaskList() {
		//TODO: impl
	}
	
	private getTaskListDetails(taskListId : number) {
		this._taskListService.getTaskListDetail(taskListId).subscribe(
			data => {
				this.taskListDetail = data;
			}, e => {
				this.messages.push(new Message("The task list details could not be loaded. Please try again later.", "danger", false));
			});
	}

    private getSubmissionListsById(taskListId : number, diff : number) : void {
        this._taskListService.getTeacherSubmissionListOverviews(taskListId, diff, this.defaultSize , this.currentPage)
            .subscribe(
                data => {
                    this.page = data;
                    this.expanded = new Array<boolean>(data.size);
                }, e => {
                    this.messages.push(new Message("The submission lists could not be loaded. Please try again later.", "danger", false));
                });
    }

    public getNumber(length : number) : Array<number> {
        return new Array<number>(length);
    }

    public nextPage() : void {
        if (this.currentPage < this.page.totalPages) {
            this.currentPage += 1;
	        this.getSubmissionListsById(this.taskListDetail.taskListId, this.difference);
        }
    }

    public prevPage() : void {
        if (this.currentPage > 0) {
            this.currentPage -= 1;
            this.getSubmissionListsById(this.taskListDetail.taskListId, this.difference);
        }
    }

    public navigate(page : number) : void {
        this.currentPage = page;
	    this.getSubmissionListsById(this.taskListDetail.taskListId, this.difference);
    }

    public taskListExpand(index : number ) {
        if (typeof this.expanded[index] === "undefined") {
            this.expanded[index] = true
        } else {
            this.expanded[index] = !this.expanded[index];
        }
    }
}