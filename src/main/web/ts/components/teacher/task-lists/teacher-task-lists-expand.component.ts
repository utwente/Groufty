import {Component, Input, OnInit} from "@angular/core";
import {Page} from "../../../model/utils/page";
import {Router} from '@angular/router-deprecated';
import {MomentPipe} from "../../../utils/moment.pipe";
import {TeacherTaskListExpand} from "../../../model/tasks/task-list-expand";
import {TaskListService} from "../../../services/task-list.service";

@Component({
    templateUrl: './templates/teacher/task-lists/teacher-task-lists-expand.component.html',
    selector: 'task-list-expand',
    directives: [TeacherTaskListsExpandComponent],
    providers: [TaskListService],
    pipes: [MomentPipe]
})
export class TeacherTaskListsExpandComponent implements OnInit{

    @Input("task-list-id") taskListId : number;
    @Input("editable") editable : boolean = true;

    // Pagination defaults
    public currentPage : number = 0;
    public defaultSize : number = 10;

    // Page object
    public page : Page<TeacherTaskListExpand> = new Page<TeacherTaskListExpand>();

    public constructor (private _taskListService : TaskListService, private _router : Router) {}

    public ngOnInit() : void {
        this.subscribeData();
    }

    private subscribeData() : void {
        this._taskListService.getTeacherTaskListExpand(this.taskListId, this.defaultSize, this.currentPage)
            .subscribe(
                data => this.page = data
            );
    }

    public browseToTask(taskId : number) {
	    //TODO: add proper link as soon as task edit page is done
        /*
         this._router.navigate([
            'TeacherTaskDetail', {id: taskId}
        ]);*/
    }

    public showMoreTasks() {
        this.defaultSize = this.page.totalElements;
        this.subscribeData();
    }

}