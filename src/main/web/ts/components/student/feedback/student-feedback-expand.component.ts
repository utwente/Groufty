import {Component, OnInit, Input} from "@angular/core";
import {TaskListExpand} from "../../../model/tasks/task-list-expand";
import {Router} from "@angular/router-deprecated";
import {Page} from "../../../model/utils/page";
import {MomentPipe} from "../../../utils/moment.pipe";
import {TaskListService} from "../../../services/task-list.service";

@Component({
    templateUrl: './templates/student/feedback/student-feedback-expand.component.html',
    selector: 'feedback-expand',
    directives: [StudentFeedbackExpandComponent],
    providers: [TaskListService],
    pipes: [MomentPipe]
})
export class StudentFeedbackExpandComponent implements OnInit{

    @Input("tasklist-id") taskListId : number;

    // Pagination defaults
    public currentPage : number = 0;
    public defaultSize : number = 10;

    // Page object
    public page : Page<TaskListExpand> = new Page<TaskListExpand>();

    public constructor (private _taskListService : TaskListService, private _router : Router) {}

    public ngOnInit() : void {
        this.subscribeData();
    }

    private subscribeData() : void {
        this._taskListService.getStudentTaskListExpand(this.taskListId, this.defaultSize, this.currentPage)
            .subscribe(
                data => this.page = data
            );
    }

    public browseToTask(taskId : number) {
        this._router.navigate([
            'StudentFeedbackDetail', {id: taskId}
        ]);
    }

    public showMoreTasks() {
        this.defaultSize = this.page.totalElements;
        this.subscribeData();
    }

}