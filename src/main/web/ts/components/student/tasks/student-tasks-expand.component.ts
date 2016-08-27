import {Component, Input, OnInit} from "@angular/core";
import {StudentTaskListExpand} from "../../../model/tasks/task-list-expand";
import {Page} from "../../../model/utils/page";
import {Router} from '@angular/router-deprecated';
import {MomentPipe} from "../../../utils/moment.pipe";
import {TaskListService} from "../../../services/task-list.service";

@Component({
    templateUrl: './templates/student/tasks/student-tasks-expand.component.html',
    selector: 'task-expand',
    directives: [StudentTasksExpandComponent],
    providers: [TaskListService],
    pipes: [MomentPipe]
})
export class StudentTasksExpandComponent implements OnInit{

    @Input("tasklist-id") taskListId : number;

    // Pagination defaults
    public currentPage : number = 0;
    public defaultSize : number = 10;

    // Page object
    public page : Page<StudentTaskListExpand> = new Page<StudentTaskListExpand>();

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
            'StudentTaskDetail', {id: taskId}
        ]);
    }

    public showMoreTasks() {
        this.defaultSize = this.page.totalElements;
        this.subscribeData();
    }
    
}