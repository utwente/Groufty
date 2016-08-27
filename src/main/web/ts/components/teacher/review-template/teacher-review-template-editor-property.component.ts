import {EventEmitter, Component, Input} from "@angular/core";
import {
    ReviewTemplateProperty,
    ReviewTemplatePropertyType
} from "../../../model/review-templates/review-template-property";
import {MarkdownComponent} from "../../utils/markdown.component";
import {TeacherReviewTemplateEditorRubricComponent} from "./teacher-review-template-editor-rubric.component";
import {ControlGroup, Control, FormBuilder, Validators, AbstractControl} from "@angular/common";

@Component({
    selector: 'template-property',
    templateUrl: './templates/teacher/review-template/teacher-review-template-editor-property.component.html',
    directives: [MarkdownComponent, TeacherReviewTemplateEditorRubricComponent],
    outputs: ["remove", "moveUp", "moveDown"],
    inputs: ["property", "index", "disableUp", "disableDown", "showHints", "disableStructure"]
})
export class TeacherReviewTemplateEditorPropertyComponent {

    public index : number;
    public property : ReviewTemplateProperty;
    public showHints : boolean;

    public disableStructure: boolean;
    public disableUp : boolean;
    public disableDown : boolean;

    public remove : EventEmitter<any> = new EventEmitter<any>();
    public moveUp : EventEmitter<any> = new EventEmitter<any>();
    public moveDown : EventEmitter<any> = new EventEmitter<any>();

    public description : Control;
    public weight : Control;
    public form : ControlGroup;

    public constructor(private _builder: FormBuilder) {
        this.description = new Control('', Validators.required);
        this.weight = new Control('', TeacherReviewTemplateEditorPropertyComponent.validateValue);
        this.form = this._builder.group({
            description: this.description,
            weight: this.weight
        });
    }

    public setType(type : ReviewTemplatePropertyType) {
        this.property.type = type;
    }

    public doMoveUp(event : any) {
        this.moveUp.emit(event);
    }

    public doMoveDown(event : any) {
        this.moveDown.emit(event);
    }

    public doRemove(event : any) {
        this.remove.emit(event);
    }

    public setTextType(event: any) {
        event.stopPropagation();
        if (!this.disableStructure) {
            this.setType("TEXT");
        }
    }

    public setRubricType(event: any) {
        event.stopPropagation();
        if (!this.disableStructure) {
            this.setType("RUBRIC");
        }
    }

    public setGradeType(event: any) {
        event.stopPropagation();
        if (!this.disableStructure) {
            this.setType("GRADE");
        }
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