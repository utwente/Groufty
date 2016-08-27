import {Component} from "@angular/core";
import {OnInit} from "@angular/core";
import {StudentTasksExpandComponent} from "./student-tasks-expand.component";
import {TaskListService} from "../../../services/task-list.service";
import {Page} from "../../../model/utils/page";
import {EnumCleanerPipe} from "../../../utils/enum-cleaner.pipe";
import {StudentTaskListOverview} from "../../../model/tasks/task-list-overview";
import {MomentPipe} from "../../../utils/moment.pipe";
import {MessagesComponent} from "../../utils/messages.component";
import {Message} from "../../../model/utils/message";

@Component({
    templateUrl: './templates/student/tasks/student-tasks-overview.component.html',
    directives: [StudentTasksExpandComponent, MessagesComponent],
    providers: [TaskListService],
    pipes: [EnumCleanerPipe, MomentPipe]
})
export class StudentTasksOverviewComponent implements OnInit {
    // Message array
    public messages : Message[] = [];

    // Page object
    public page : Page<StudentTaskListOverview> = new Page<StudentTaskListOverview>();

    // Pagination defaults
    public currentPage : number = 0;
    public defaultSize : number = 10;

    // Expanded items array - representing expanded task lists
    public expanded : Array<boolean>;

    // Error state
    public errorState : boolean;

    public constructor (private _taskListOverviewService : TaskListService) {}

    public ngOnInit() : void {
        this.subscribeData();
    }

    private subscribeData() : void {
        this._taskListOverviewService.getStudentTaskOverviews(this.defaultSize , this.currentPage)
            .subscribe(
                data => {
                    this.page = data;
                    this.expanded = new Array<boolean>(data.size);
                }, e => {
                    if (!this.errorState) {
                        this.messages.push(new Message("Your tasks could not be loaded. Please try again later.", "danger", false));
                        this.errorState = true;
                    }
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

    public taskListExpand(index : number ) {
        if (typeof this.expanded[index] === "undefined") {
            this.expanded[index] = true
        } else {
            this.expanded[index] = !this.expanded[index];
        }
    }
}