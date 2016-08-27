import {Component, Input} from '@angular/core';
import {NgClass} from '@angular/common';
import {Flag} from "../../../model/reviews/flag";
import {ReviewService} from "../../../services/review.service";
import {Message} from "../../../model/utils/message";
import {MessagesComponent} from "../../utils/messages.component";

@Component({
	selector: 'flag-form',
	templateUrl: './templates/student/review-editor/student-flag-form.html',
	directives: [NgClass, MessagesComponent],
	providers: [ReviewService]
})

export class FlagForm {
	public flag : Flag;
	private messages : Message[] = [];
	public active : boolean = false;
	public flagForm : boolean = false;

	@Input("review-id") reviewId : number;
	@Input("flagged") flagged : boolean = false;
	@Input("reason") reason : string = "";
	
	public constructor(private _reviewService : ReviewService) {
		this.flag = new Flag();
		this.flag.message = "";
	}

	public flagReview() : void {
		if (this.active) {
			this.active = false;
		} else {
			this.active = true;
		}

	}
	
	onSubmit() {
		this._reviewService.flagReview(this.reviewId, this.flag).subscribe(res => {
			this.active = false;
			this.messages.push(new Message("Review has been flagged.", "success", true, 2500));
			this.flagged = true;
		}, e => {
			this.messages.push(new Message("Review could not be flagged.", "warning", true, 2500));
			this.active = false;
		});
	}
}