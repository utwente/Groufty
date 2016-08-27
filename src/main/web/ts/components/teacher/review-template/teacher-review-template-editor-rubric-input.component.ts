import {Component} from "@angular/core";
import {Control, ControlGroup, FormBuilder, Validators, AbstractControl} from "@angular/common";
import {MarkdownComponent} from "../../utils/markdown.component";

@Component({
    selector: 'input-rubric-cell',
    templateUrl: './templates/teacher/review-template/teacher-review-template-editor-rubric-input.component.html',
    inputs: ['value', 'preview', 'number', 'showHints', 'required'],
    directives: [MarkdownComponent]
})
export class TeacherReviewTemplateEditorRubricInputComponent {

    public value : any;
    public preview : boolean;
    public showHints : boolean;
    public textField : Control;
    public numberValueField : Control;
    public numberWeightField : Control;
    public form : ControlGroup;
    public number : boolean;
    public required : boolean = true;

    public constructor(private _builder: FormBuilder) {
        this.number = false;
        this.textField = new Control('', Validators.required);
        this.numberValueField = new Control('', TeacherReviewTemplateEditorRubricInputComponent.validateValue);
        this.numberWeightField = new Control('', TeacherReviewTemplateEditorRubricInputComponent.validateValue);
        this.form = this._builder.group({
            textField: this.textField,
            numberValueField: this.numberValueField,
            numberWeightField: this.numberWeightField
        });
    }

    public getWeightDefined() : boolean {
        return typeof this.value.weight !== 'undefined';
    }

    public getValueDefined() : boolean {
        return typeof this.value.value !== 'undefined';
    }

    public static validateValue(control: AbstractControl): {[key: string]: boolean} {
        if (control.value == null) {
            return {"number": true};
        }
        let value = parseInt(control.value);
        if (value < 0) {
            return {"number": true};
        }
        return null;
    }

}