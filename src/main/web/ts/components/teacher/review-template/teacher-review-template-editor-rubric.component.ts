import {Component} from "@angular/core";
import {ReviewTemplateProperty} from "../../../model/review-templates/review-template-property";
import {TeacherReviewTemplateEditorRubricInputComponent} from "./teacher-review-template-editor-rubric-input.component";
import {ReviewTemplateRubricRow} from "../../../model/review-templates/review-template-rubric-row";
import {ReviewTemplateRubricOption} from "../../../model/review-templates/review-template-rubric-option";
import {ReviewTemplateRubricHeader} from "../../../model/review-templates/review-template-rubric-header";

@Component({
    selector: 'rubric-editor',
    templateUrl: './templates/teacher/review-template/teacher-review-template-editor-rubric.component.html',
    inputs: ['rubric', 'showHints', 'disableStructure'],
    directives: [TeacherReviewTemplateEditorRubricInputComponent]
})
export class TeacherReviewTemplateEditorRubricComponent {

    public property : ReviewTemplateProperty;
    public showHints : boolean;
    public disableStructure: boolean;
    public preview : boolean = false;

    public set rubric(value : ReviewTemplateProperty) {
        this.property = value;
        if (!this.property.header) {
            this.property.header = [];
        }
        if (!this.property.rows) {
            this.property.rows = [];
        }
    }

    public get rubric(): ReviewTemplateProperty {
        return this.property;
    }

    public togglePreview(event: any): void {
        event.stopPropagation();
        this.preview = !this.preview;
    }

    public getRows() : number {
        return this.property.rows.length;
    }

    public getColumns() : number {
        return this.property.header.length;
    }

    public moveRowDown(event: any, index: number): void {
        event.stopPropagation();
        if (!this.disableStructure && index >= 0 && index < this.getRows() - 1) {
            let row = this.property.rows[index + 1];
            this.property.rows[index + 1] = this.property.rows[index];
            this.property.rows[index] = row;
        }
    }

    public moveRowUp(event: any, index: number): void {
        event.stopPropagation();
        if (!this.disableStructure && index > 0 && index < this.getRows()) {
            let row = this.property.rows[index - 1];
            this.property.rows[index - 1] = this.property.rows[index];
            this.property.rows[index] = row;
        }
    }

    public deleteRow(event: any, index: number): void {
        event.stopPropagation();
        if (!this.disableStructure && index >= 0 && index < this.getRows()) {
            this.property.rows.splice(index, 1);
        }
    }

    public addRow(event: any) {
        event.stopPropagation();
        if (!this.disableStructure) {
            let row = new ReviewTemplateRubricRow();
            row.options = new Array<ReviewTemplateRubricOption>(this.getColumns());
            row.weight = null;
            for (let i = 0; i < this.getColumns(); i++) {
                row.options[i] = new ReviewTemplateRubricOption();
            }
            this.property.rows.push(row);
        }
    }

    public moveColumnRight(event: any, index: number) {
        event.stopPropagation();
        if (!this.disableStructure && index >= 0 && index < this.getColumns() - 1) {
            let column = this.property.header[index + 1];
            this.property.header[index + 1] = this.property.header[index];
            this.property.header[index] = column;
            for (let i = 0; i < this.getRows(); i++) {
                let column = this.property.rows[i].options[index + 1];
                this.property.rows[i].options[index + 1] = this.property.rows[i].options[index];
                this.property.rows[i].options[index] = column;
            }
        }
    }

    public moveColumnLeft(event: any, index: number) {
        event.stopPropagation();
        if (!this.disableStructure && index > 0 && index < this.getColumns()) {
            let column = this.property.header[index - 1];
            this.property.header[index - 1] = this.property.header[index];
            this.property.header[index] = column;
            for (let i = 0; i < this.getRows(); i++) {
                let column = this.property.rows[i].options[index - 1];
                this.property.rows[i].options[index - 1] = this.property.rows[i].options[index];
                this.property.rows[i].options[index] = column;
            }
        }
    }

    public removeColumn(event: any, index: number) {
        event.stopPropagation();
        if (!this.disableStructure && index >= 0 && index < this.getColumns()) {
            this.property.header.splice(index, 1);
            for (let i = 0; i < this.getRows(); i++) {
                this.property.rows[i].options.splice(index, 1);
            }
        }
    }

    public addColumn(event: any) {
        event.stopPropagation();
        if (!this.disableStructure) {
            let header = new ReviewTemplateRubricHeader();
            header.value = null;
            this.property.header.push(header);
            for (let i = 0; i < this.getRows(); i++) {
                this.property.rows[i].options.push(new ReviewTemplateRubricOption());
            }
        }
    }

}