import {SubmissionListExpand} from "../../../model/submissions/submission-list-expand";
import {Component, Input, OnInit} from "@angular/core";
import {Page} from "../../../model/utils/page";
import {Router} from '@angular/router-deprecated';
import {MomentPipe} from "../../../utils/moment.pipe";
import {TaskListService} from "../../../services/task-list.service";

@Component({
    templateUrl: './templates/teacher/submission-lists/teacher-submission-lists-expand.component.html',
	selector: 'submission-list-expand',
	directives: [TeacherSubmissionListsExpandComponent],
	providers: [TaskListService],
	pipes: [MomentPipe]
})
export class TeacherSubmissionListsExpandComponent implements OnInit{

    @Input("task-list-id") taskListId : number;
    @Input("author-id") authorId : number;

    // Pagination defaults
    public currentPage : number = 0;
    public defaultSize : number = 10;

    // Page object
    public page : Page<SubmissionListExpand> = new Page<SubmissionListExpand>();

    public constructor (private _taskListService : TaskListService, private _router : Router) {}

    public ngOnInit() : void {
        
        this.subscribeData();
    }

    private subscribeData() : void {
        this._taskListService.getTeacherSubmissionListExpand(this.taskListId, this.authorId, this.defaultSize, this.currentPage)
            .subscribe(
                data => this.page = data
            );
    }

    public browseToSubmissionDetail(taskId : number) {
         this._router.navigate([
         '/TeacherSubmission/TeacherSubmissionDetail', {tid: taskId, aid: this.authorId}
         ]);
    }

    public showMoreSubmissions() {
        this.defaultSize = this.page.totalElements;
        this.subscribeData();
    }

}