import {Validators, Control, ControlGroup, FormBuilder} from "@angular/common";
import {Component} from "@angular/core";
import {StudentReviewProperty} from "./student-review-property";
import {MarkdownComponent} from "../../utils/markdown.component";
import {ReviewProperty} from "../../../model/reviews/review-property";

@Component({
    selector: 'review-text',
    templateUrl: './templates/student/review-editor/student-review-property-text.html',
    directives: [MarkdownComponent],
    inputs: ['editable', 'hint', 'id', 'property', 'value', 'grade']
})
export class StudentReviewPropertyText extends StudentReviewProperty {

    public text : Control;
    public form : ControlGroup;
    public current : string;

    public constructor(private _builder: FormBuilder) {
        super();
        this.text = new Control('', Validators.required);
        this.form = this._builder.group({
            text: this.text
        });
    }

    public setValue(value : ReviewProperty): void {
        this.current = value.text;
    }

    public getValue() : ReviewProperty {
        let property = new ReviewProperty();
        property.type = "TEXT";
        property.text = this.current;
        return property;
    }

    public isValid() : boolean {
        return true;
    }

    public isReady() : boolean {
        return this.text.valid;
    }

    public getWeight() : number {
        return 0;
    }

    public getPoints() : number {
        return 1;
    }

}