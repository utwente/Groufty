import {Component} from "@angular/core";
import {Router} from "@angular/router-deprecated";
import {Message} from "../../../model/utils/message";
import {Page} from "../../../model/utils/page";
import {ReviewTemplateOverview} from "../../../model/review-templates/review-template-overview";
import {ReviewTemplateOverviewService} from "../../../services/review-template-overview.service";
import {MessagesComponent} from "../../utils/messages.component";
import {ReviewTemplateService} from "../../../services/review-template.service";

@Component({
    templateUrl: './templates/teacher/review-template/teacher-review-template-overview.component.html',
    directives: [MessagesComponent],
    providers: [ReviewTemplateOverviewService, ReviewTemplateService]
})
export class TeacherReviewTemplateOverviewComponent {
    // Message array
    public messages : Message[] = [];

    // Page object
    public page : Page<ReviewTemplateOverview> = new Page<ReviewTemplateOverview>();

    // Pagination defaults
    public currentPage : number = 0;
    public defaultSize : number = 8;

    // Error state
    public errorState : boolean;

    public constructor (private _reviewTemplateOverviewService : ReviewTemplateOverviewService,
                        private _reviewTemplateService : ReviewTemplateService,
                        private _router : Router) {}

    public ngOnInit() : void {
        this.subscribeData();
    }

    private subscribeData() : void {
        this._reviewTemplateOverviewService.getTeacherReviewTemplateOverviews(this.defaultSize , this.currentPage)
            .subscribe(
                data => {
                    this.page = data;
                }, e => {
                    if (!this.errorState) {
                        this.messages.push(new Message("The review templates could not be loaded. Please try again later.", "danger", false));
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

    public editReviewTemplate(event: any, index : number) : void {
        event.stopPropagation();
        this._router.navigate(['TeacherReviewTemplateEditor', {id: this.page.content[index].reviewTemplateId}])
    }

    public removeReviewTemplate(event: any, index: number): void {
        event.stopPropagation();
        if (this.page.content[index].taskCount > 0) {
            this.messages.push(new Message("The review template could not be deleted. It is still in use by a task.", "danger"));
        } else {
            this._reviewTemplateService.deleteReviewTemplate(this.page.content[index].reviewTemplateId).subscribe(s => {
                this.messages.push(new Message("The review template is successfully deleted.", "success", true, 5000));
                this.subscribeData();
            }, error => {
                this.messages.push(new Message(
                    (error.message) ? "The review template could not be deleted. " + error.message :
                        error.status ? `The review template could not be deleted. ${error.status} - ${error.statusText}` :
                            "The review template could not be deleted. Unknown server error",
                    "danger"
                ));
            });
        }
    }

    public duplicateReviewTemplate(event: any, index: number): void {
        event.stopPropagation();
        this._router.navigate(['TeacherReviewTemplateDuplicate', {id: this.page.content[index].reviewTemplateId}]);
    }

    public createTemplate() : void {
        this._router.navigate(['TeacherReviewTemplateEditor', {id: "new"}]);
    }
}