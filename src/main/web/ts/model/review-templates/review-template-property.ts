import {ReviewTemplateRubricHeader} from "./review-template-rubric-header";
import {ReviewTemplateRubricRow} from "./review-template-rubric-row";

export type ReviewTemplatePropertyType = "RUBRIC" | "TEXT" | "GRADE";

export class ReviewTemplateProperty {
    public description : string;
    public weight: number;
    public type: ReviewTemplatePropertyType;
    public header : Array<ReviewTemplateRubricHeader>;
    public rows: Array<ReviewTemplateRubricRow>;
}