import {Control, AbstractControl, NgClass, FORM_DIRECTIVES, ControlGroup, FormBuilder} from "@angular/common";
import {Component} from "@angular/core";
import {StudentReviewProperty} from "./student-review-property";
import {ReviewProperty} from "../../../model/reviews/review-property";
import {MarkdownComponent} from "../../utils/markdown.component";

@Component({
    selector: 'review-grade',
    templateUrl: './templates/student/review-editor/student-review-property-grade.html',
    directives: [NgClass, MarkdownComponent, FORM_DIRECTIVES],
    inputs: ['editable', 'hint', 'id', 'property', 'value', 'grade']
})
export class StudentReviewPropertyGrade extends StudentReviewProperty {

    public gradeField : Control;
    public form : ControlGroup;
    public current : number;

    public constructor(private _builder: FormBuilder) {
        super();
        this.gradeField = new Control('', StudentReviewPropertyGrade.validateGrade);
        this.form = this._builder.group({
            grade: this.gradeField
        });
    }
    
    public setValue(value : ReviewProperty): void {
        this.current = value.grade;
    }

    public getValue() : ReviewProperty {
        let property = new ReviewProperty();
        property.type = "GRADE";
        property.grade = this.current;
        return property;
    }

    public isValid() : boolean {
        return this.gradeField.valid;
    }

    public isReady() : boolean {
        return this.gradeField.valid && this.gradeField.value != null;
    }
    
    public getPoints() : number {
        if (!this.editable) {
            return this.value.grade / 10;
        } else if (this.gradeField.value != null) {
            let grade = parseFloat(this.gradeField.value);
            if (typeof grade === 'undefined') {
                return 0;
            }
            return grade >= 1.0 && grade <= 10.0 ? grade / 10 : 0;
        } else {
            return 0;
        }
    }

    public static validateGrade(control: AbstractControl): {[key: string]: boolean} {
        if (control.value == null) {
            return {"grade": true};
        }
        let grade = parseFloat(control.value);
        if (grade < 1.0 || grade > 10.0) {
            return {"grade": true};
        }
        return null;
    }

}