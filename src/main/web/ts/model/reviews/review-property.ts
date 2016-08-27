import {ReviewTemplatePropertyType} from "../review-templates/review-template-property";
import {ReviewPropertyRubricOption} from "./review-property-rubric-option";

export class ReviewProperty {
    public type : ReviewTemplatePropertyType;
    public text : string;
    public grade : number;
    public selectedOptions: Array<ReviewPropertyRubricOption>;
}