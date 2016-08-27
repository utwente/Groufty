import {ReviewTemplateRubricOption} from "./review-template-rubric-option";

export class ReviewTemplateRubricRow {
    public label: string;
    public weight: number;
    public options: Array<ReviewTemplateRubricOption>;
}