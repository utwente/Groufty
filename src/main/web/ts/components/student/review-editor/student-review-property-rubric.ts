import {NgClass} from "@angular/common";
import {Component} from "@angular/core";
import {StudentReviewProperty} from "./student-review-property";
import {ReviewProperty} from "../../../model/reviews/review-property";
import {ReviewPropertyRubricOption} from "../../../model/reviews/review-property-rubric-option";
import {MarkdownComponent} from "../../utils/markdown.component";

@Component({
    selector: 'review-rubric',
    templateUrl: './templates/student/review-editor/student-review-property-rubric.html',
    directives: [NgClass, MarkdownComponent],
    inputs: ['editable', 'hint', 'id', 'property', 'value', 'grade']
})
export class StudentReviewPropertyRubric extends StudentReviewProperty {

    public current : Array<number> = [];

    public chooseValue(r : number, o: number) {
        if (this.editable) {
            this.current[r] = o;
        }
    }

    public setValue(value : ReviewProperty): void {
        this.current = StudentReviewPropertyRubric.generateNullArray(
            Math.max(value.selectedOptions.length, this.property.rows.length)
        );
        for (let i = 0; i < value.selectedOptions.length && i < this.property.rows.length; i++) {
            this.current[i] = value.selectedOptions[i].option;
        }
    }

    public getValue() : ReviewProperty {
        let property = new ReviewProperty();
        property.type = "RUBRIC";
        property.selectedOptions = new Array<ReviewPropertyRubricOption>(this.property.rows.length);
        for (let i = 0; i < this.property.rows.length; i++) {
            property.selectedOptions[i] = new ReviewPropertyRubricOption();
            property.selectedOptions[i].option = this.current[i];
        }
        return property;
    }

    public isValid() : boolean {
        let result = true;
        for (let i = 0; i < this.property.rows.length; i++) {
            result = result && (this.current[i] == null ||
                (this.current[i] >= 0 && this.current[i] < this.property.header.length));
        }
        return result;
    }

    public isReady() : boolean {
        let result = true;
        for (let i = 0; i < this.property.rows.length; i++) {
            result = result && this.current[i] != null && this.current[i] >= 0 &&
                this.current[i] < this.property.header.length;
        }
        return result;
    }

    private static generateNullArray(length : number) {
        let array = new Array<number>(length);
        for (let i = 0; i < length; i++) {
            array[i] = null;
        }
        return array;
    }

    public getPoints() : number {
        return this._getLocalPoints() / this._getMaxPoints();
    }

    private _getLocalPoints() : number {
        let result = 0;
        for (let i = 0; i < this.property.rows.length; i++) {
            if (this.current[i] != null) {
                result += this.property.rows[i].weight * this.property.header[this.current[i]].value;
            }
        }
        return result;
    }

    private _getMaxPoints() : number {
        let max = 0;
        for (let i = 0; i < this.property.header.length; i++) {
            max = Math.max(max, this.property.header[i].value);
        }
        let result = 0;
        for (let i = 0; i < this.property.rows.length; i++) {
            result += this.property.rows[i].weight * max;
        }
        return result;
    }

}