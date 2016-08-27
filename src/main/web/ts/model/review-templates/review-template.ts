import {ReviewTemplateProperty} from "./review-template-property";

export class ReviewTemplate {
    public name : string;
    public reviewTemplateId : number;
    public reviewTemplateProperties : Array<ReviewTemplateProperty>;
}