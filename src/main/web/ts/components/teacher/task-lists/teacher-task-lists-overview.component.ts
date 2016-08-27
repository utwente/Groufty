import {Component, OnInit} from "@angular/core";
import {Message} from "../../../model/utils/message";
import {Page} from "../../../model/utils/page";
import {TaskListService} from "../../../services/task-list.service";
import {TeacherTaskListOverview} from "../../../model/tasks/task-list-overview";
import {MessagesComponent} from "../../utils/messages.component";
import {EnumCleanerPipe} from "../../../utils/enum-cleaner.pipe";
import {MomentPipe} from "../../../utils/moment.pipe";
import {TeacherTaskListsExpandComponent} from "./teacher-task-lists-expand.component";
import {Router} from "@angular/router-deprecated";

@Component({
    templateUrl: './templates/teacher/task-lists/teacher-task-lists-overview.component.html',
    directives: [MessagesComponent, TeacherTaskListsExpandComponent],
    providers: [TaskListService],
    pipes: [EnumCleanerPipe, MomentPipe]
})
export class TeacherTaskListsOverviewComponent implements OnInit{
    // Message array
    public messages : Message[] = [];

    // Page object
    public page : Page<TeacherTaskListOverview> = new Page<TeacherTaskListOverview>();

    // Pagination defaults
    public currentPage : number = 0;
    public defaultSize : number = 10;

    public constructor(private _taskListService : TaskListService, private _router : Router ) {}

    public ngOnInit() : void {
        this.subscribeData();
    }

    /* Linking section */
    public browseToSubmissionLists(taskListId : number) {
        this._router.navigate(['/TeacherSubmissionLists/TeacherSubmissionListsOverview', {id: taskListId}]);
    }

    public browseToGrouping(groupingId : number) {
	    //TODO: impl
    }

    public browseToEditTaskList(taskListId : number) {
        //TODO: impl
    }
	
    public browseToCreateTaskList() {
	    //TODO: add proper link once task list creation page is done
    }

    private subscribeData() : void {
        this._taskListService.getTeacherTaskOverviews(this.defaultSize , this.currentPage)
            .subscribe(
                data => {
                    this.page = data;
                }, e => {
                    this.messages.push(new Message("The task lists could not be loaded. Please try again later.", "danger", false));
                });
    }

    public getNumber(length : number) : Array<number> {
        return new Array<number>(length);
    }

    public nextPage() : void {
        if (this.currentPage < this.page.totalPages) {
            this.currentPage += 1;
            this.subscribeData();
        }
    }

    public prevPage() : void {
        if (this.currentPage > 0) {
            this.currentPage -= 1;
            this.subscribeData();
        }
    }

    public navigate(page : number) : void {
        this.currentPage = page;
        this.subscribeData();
    }
}