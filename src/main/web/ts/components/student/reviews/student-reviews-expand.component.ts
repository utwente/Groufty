import {Component, OnInit, Input} from "@angular/core";
import {Page} from "../../../model/utils/page"
import {Router} from "@angular/router-deprecated";
import {ReviewExpand} from "../../../model/reviews/review-expand";
import {ReviewExpandService} from "../../../services/review-expand.service";
import {MomentPipe} from "../../../utils/moment.pipe";

@Component({
    templateUrl: './templates/student/reviews/student-reviews-expand.component.html',
    selector: 'review-expand',
    directives: [StudentReviewsExpandComponent],
    providers: [ReviewExpandService],
    pipes: [MomentPipe]
})
export class StudentReviewsExpandComponent implements OnInit{

    @Input("reviewTask-id") taskId : number;
    @Input("show-grades") showGrades : boolean = false;

    // Pagination defaults
    public currentPage : number = 0;
    public defaultSize : number = 10;

    // Page object
    public page : Page<ReviewExpand> = new Page<ReviewExpand>();

    public constructor (private _taskListExpandService : ReviewExpandService, private _router : Router) {}

    public ngOnInit() : void {
        this.subscribeData();
    }

    private subscribeData() : void {
        this._taskListExpandService.getReviewExpand(this.taskId, this.defaultSize, this.currentPage)
            .subscribe(
                data => this.page = data
            );
    }
    
    public browseToTask(reviewId : number) {
        this._router.navigate([
            'StudentReviewDetail', {id: reviewId}
        ]);
    }

    public showMoreTasks() {
        this.defaultSize = this.page.totalElements;
        this.subscribeData();
    }

}