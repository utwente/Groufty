import {NgClass} from "@angular/common";
import {Component, ViewChildren, QueryList, Input} from "@angular/core";
import {ReviewTemplate} from "../../../model/review-templates/review-template";
import {Review} from "../../../model/reviews/review";
import {ReviewTemplateService} from "../../../services/review-template.service";
import {StudentReviewPropertyGrade} from "./student-review-property-grade";
import {StudentReviewPropertyRubric} from "./student-review-property-rubric";
import {StudentReviewPropertyText} from "./student-review-property-text";
import {ReviewService} from "../../../services/review.service";
import {Message} from "../../../model/utils/message";
import {MessagesComponent} from "../../utils/messages.component";
import {ReviewProperty} from "../../../model/reviews/review-property";
import {Flag} from "../../../model/reviews/flag";
import {FlagForm} from "./student-review-flag-form";
import {MarkdownComponent} from "../../utils/markdown.component";

@Component({
    selector: 'review-template',
    templateUrl: './templates/student/review-editor/student-review-editor.html',
    directives: [NgClass, MessagesComponent, MarkdownComponent, StudentReviewPropertyGrade,
        StudentReviewPropertyRubric, StudentReviewPropertyText, FlagForm],
    providers: [ReviewTemplateService, ReviewService],
    inputs: ['template', 'review', 'editable', 'grades', 'flaggable']
})
export class StudentReviewEditor {
    
    public editable : boolean;
    public grades : boolean;
    public messages : Message[] = [];
	public flaggable : boolean = true;

    private _hints : boolean = false;
    private _review : Review;
    private _template : ReviewTemplate;
    private _showGrade : boolean = false;
    
    public flagged : boolean = false;

    @ViewChildren(StudentReviewPropertyGrade) private _grades : QueryList<StudentReviewPropertyGrade>;
    @ViewChildren(StudentReviewPropertyRubric) private _rubrics : QueryList<StudentReviewPropertyRubric>;
    @ViewChildren(StudentReviewPropertyText) private _texts : QueryList<StudentReviewPropertyText>;

    public constructor(private _reviewTemplateService : ReviewTemplateService,
                       private _reviewService : ReviewService) {
        this._review = new Review();
        this._review.reviewProperties = [];
        this._review.submitted = false;
        this._review.flag = new Flag();
    }

    public set template(id : number) {
        if (typeof id !== "undefined") {
            this._reviewTemplateService.getReviewTemplate(id).subscribe(res => {
                this._template = res;
            });
        }
    }

    public get template(): number {
        return this._template.reviewTemplateId;
    }

    public set review(id : number) {
        if (typeof id !== "undefined" && id != null) {
            this._reviewService.getReview(id).subscribe(res => {
                this._review = res;
                this.flagged = res.flag != null ? true : false;
                setTimeout(() => {
                    this._showGrade = true;
                }, 1000);
            });
        }
    }

    public get review(): number {
        return this._review.reviewId;
    }
    
    

    public valid(): boolean {
        let result = true;
        this._grades.forEach(e => result = result && e.isValid());
        this._texts.forEach(e => result = result && e.isValid());
        this._rubrics.forEach(e => result = result && e.isValid());
        return result;
    }

    public ready(): boolean {
        let result = true;
        this._grades.forEach(e => result = result && e.isReady());
        this._texts.forEach(e => result = result && e.isReady());
        this._rubrics.forEach(e => result = result && e.isReady());
        return result;
    }

    public points() : number {
        let points = 0;
        this._grades.forEach(e => points += e.getPoints() * e.getWeight());
        this._texts.forEach(e => points += e.getPoints() * e.getWeight());
        this._rubrics.forEach(e => points += e.getPoints() * e.getWeight());
        return points;
    }

    public maxPoints() : number {
        let points = 0;
        this._grades.forEach(e => points += e.getWeight());
        this._texts.forEach(e => points += e.getWeight());
        this._rubrics.forEach(e => points += e.getWeight());
        return points;
    }

    public grade() : number {
        return Math.round((this.points() / this.maxPoints()) * 1000) / 100;
    }

    public unsubmit() : void {
        this._save(false);
    }

    public submit(): void {
        this._save(true);
    }

    public save() : void {
        this._save(this._review.submitted);
    }

    private _updatedReview(submit : boolean) {
        let result = new Review();
        result.flag = this._review.flag;
        result.lastEdited = this._review.lastEdited;
        result.reviewId = this._review.reviewId;
        result.authorId = this._review.authorId;
        result.submitted = submit;
        result.reviewProperties = new Array<ReviewProperty>(this._template.reviewTemplateProperties.length);
        this._grades.forEach(e => result.reviewProperties[e.id] = e.getValue());
        this._texts.forEach(e => result.reviewProperties[e.id] = e.getValue());
        this._rubrics.forEach(e => result.reviewProperties[e.id] = e.getValue());
        return result;
    }
    
    private _save(submit : boolean) : void {
        this.messages = [];
        if (this.valid()) {
            if (!this.ready() && submit) {
                this._hints = true;
                this.messages.push(new Message("The review cannot be submitted whenever some questions are " +
                    "left unanswered or contain errors. Please answer those questions.", "danger"));
            } else {
                this._reviewService.updateReview(this._updatedReview(submit)).subscribe(r => {
                    this._review = r;
                    if(submit) {
                        this._hints = false;
                        this.messages.push(new Message("The review has been saved and submitted successfully.", "success", true, 2500));
                    } else {
                        this._hints = false;
                        this.messages.push(new Message("The review has been saved. Be sure to submit your submission once ready.", "warning", true, 8000));
                    }
                }, e => {
                    this.messages.push(new Message("The review could not be saved. Please try again later.", "danger"));
                });
            }
        } else {
            this._hints = true;
            this.messages.push(new Message("The review cannot be saved whenever the answers to some " +
                "questions are in an invalid state. Please correct the answers first.", "danger"));
        }
    }
    
}