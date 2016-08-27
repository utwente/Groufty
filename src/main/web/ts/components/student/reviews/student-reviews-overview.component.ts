import {Component, OnInit} from "@angular/core";
import {StudentReviewsExpandComponent} from "./student-reviews-expand.component";
import {ReviewOverviewService} from "../../../services/review-overview.service";
import {ReviewOverview} from "../../../model/reviews/review-overview";
import {Page} from "../../../model/utils/page";
import {EnumCleanerPipe} from "../../../utils/enum-cleaner.pipe";
import {MomentPipe} from "../../../utils/moment.pipe";
import {MessagesComponent} from "../../utils/messages.component";
import {Message} from "../../../model/utils/message";

@Component({
    templateUrl: './templates/student/reviews/student-reviews-overview.component.html',
    directives: [StudentReviewsExpandComponent, MessagesComponent],
    providers: [ReviewOverviewService],
    pipes: [EnumCleanerPipe, MomentPipe]
})
export class StudentReviewsOverviewComponent implements OnInit{
    // Message array
    public messages : Message[] = [];

    // Page object
    public page : Page<ReviewOverview> = new Page<ReviewOverview>();

    // Pagination defaults
    public currentPage : number = 0;
    public defaultSize : number = 8;

    // Expanded items array
    public expanded : Array<boolean>;
    
    // Error state
    public errorState : boolean;

    public constructor (private _reviewOverviewService : ReviewOverviewService) {}

    public ngOnInit() : void {
        this.subscribeData();
    }

    private subscribeData() : void {
        this._reviewOverviewService.getStudentReviewOverviews(this.defaultSize , this.currentPage)
            .subscribe(
                data => {
                    this.page = data;
                    this.expanded = new Array<boolean>(data.size);
                }, e => {
                    if (!this.errorState) {
                        this.messages.push(new Message("Your review tasks reviews could not be loaded. Please try again later.", "danger", false));
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