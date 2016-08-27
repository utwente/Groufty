import {Component} from "@angular/core";
import {RouteParams, RouteData} from "@angular/router-deprecated";
import {Control, ControlGroup, FormBuilder, Validators} from "@angular/common";
import {TeacherReviewTemplateEditorPropertyComponent} from "./teacher-review-template-editor-property.component";
import {ReviewTemplateService} from "../../../services/review-template.service";
import {ReviewTemplateOverviewService} from "../../../services/review-template-overview.service";
import {Message} from "../../../model/utils/message";
import {ReviewTemplate} from "../../../model/review-templates/review-template";
import {MessagesComponent} from "../../utils/messages.component";
import {MarkdownComponent} from "../../utils/markdown.component";
import {ReviewTemplateProperty} from "../../../model/review-templates/review-template-property";
import {ReviewTemplateOverview} from "../../../model/review-templates/review-template-overview";

@Component({
    templateUrl: './templates/teacher/review-template/teacher-review-template-editor.component.html',
    directives: [MessagesComponent, MarkdownComponent, TeacherReviewTemplateEditorPropertyComponent],
    providers: [ReviewTemplateService, ReviewTemplateOverviewService]
})
export class TeacherReviewTemplateEditorComponent {
    // Message array
    public messages : Message[] = [];

    // The review template.
    public template : ReviewTemplate;
    public overview : ReviewTemplateOverview;

    // Error status flag.
    public errorStatus : boolean = false;

    // Form related information.
    public showHints : boolean = false;
    public disableStructure : boolean = true;
    public name : Control;
    public form : ControlGroup;

    public constructor(private _reviewTemplateService : ReviewTemplateService,
                       private _reviewTemplateOverviewService : ReviewTemplateOverviewService,
                       private _routeParams:RouteParams,
                       private _routeData:RouteData,
                       private _builder: FormBuilder) {
        this.name = new Control('', Validators.required);
        this.form = this._builder.group({
            name: this.name,
        });
    };

    public ngOnInit():void {
        let paramId = this._routeParams.get('id');
        let templateId = paramId == "new" ? -1 : parseInt(paramId);
        let isDuplicate = this._routeData.get('duplicate');
        this.prepareData(templateId, isDuplicate);
    }
    
    public prepareData(templateId : number, duplicate : boolean) {
        this.template = new ReviewTemplate();
        this.template.name = "";
        this.template.reviewTemplateProperties = [];
        if (templateId != -1) {
            this._reviewTemplateService.getReviewTemplate(templateId).subscribe(template => {
                this.template = template;
                if (duplicate) {
                    delete this.template.reviewTemplateId;
                    this.template.name += " (Copy)";
                }
                this.getOverview();
            }, error => {
                    this.errorStatus = true;
                    this.messages.push(new Message(
                        error.status == 404 ? "The review template could not be found." :
                            (error.message) ? error.message :
                                error.status ? `${error.status} - ${error.statusText}` :
                                    "Unknown erver error",
                        "danger"
                    ));
                }
            );
        } else {
            this.getOverview();
        }
    }

    public getOverview(): void {
        this.overview = new ReviewTemplateOverview();
        this.overview.reviewCount = 0;
        this.overview.taskCount = 0;
        this.disableStructure = this.overview.reviewCount > 0;
        if (typeof this.template.reviewTemplateId !== "undefined") {
            this._reviewTemplateOverviewService.getTeacherReviewTemplateOverview(this.template.reviewTemplateId).subscribe(overview => {
                this.overview = overview;
                this.disableStructure = this.overview.reviewCount > 0;
            });
        }
    }

    public getPropertiesLength() : number {
        return this.template.reviewTemplateProperties.length;
    }

    public static isField(field : any, notEmpty: boolean = false) {
        return typeof field !== 'undefined' && (!notEmpty || field != "");
    }

    public checkRubric(i : number) : boolean {
        for (let j = 0; j < this.template.reviewTemplateProperties[i].header.length; j++) {
            if (!TeacherReviewTemplateEditorComponent.isField(
                    this.template.reviewTemplateProperties[i].header[j].label, true)) {
                this.messages.push(new Message("Could not save the review template. The description of column " + (j+1) +
                    " is missing in the rubric of section " + (i+1) + ".", "danger"));
                return false;
            }
            if (!TeacherReviewTemplateEditorComponent.isField(
                    this.template.reviewTemplateProperties[i].header[j].value) &&
                    this.template.reviewTemplateProperties[i].header[j].value >= 0) {
                this.messages.push(new Message("Could not save the review template. The value of column " + (j+1) +
                    " is invalid in the rubric of section " + (i+1) + ".", "danger"));
                return false;
            }
        }
        for (let j = 0; j < this.template.reviewTemplateProperties[i].rows.length; j++) {
            if (!TeacherReviewTemplateEditorComponent.isField(
                    this.template.reviewTemplateProperties[i].rows[j].label, true)) {
                this.messages.push(new Message("Could not save the review template. The description of criterion " + (j+1) +
                    " is missing in the rubric of section " + (i+1) + ".", "danger"));
                return false;
            }
            if (!TeacherReviewTemplateEditorComponent.isField(
                    this.template.reviewTemplateProperties[i].rows[j].weight) &&
                this.template.reviewTemplateProperties[i].rows[j].weight >= 0) {
                this.messages.push(new Message("Could not save the review template. The weight of criterion " + (j+1) +
                    " is invalid in the rubric of section " + (i+1) + ".", "danger"));
                return false;
            }
            if (this.template.reviewTemplateProperties[i].rows[j].options.length !=
                this.template.reviewTemplateProperties[i].header.length) {
                this.messages.push(new Message("Could not save the review template. Unknown error in the rubric of section " + (i+1) + ".", "danger"));
                return false;
            }
        }
        return true;
    }

    public checkTemplate(): boolean {
        for (let i = 0; i < this.getPropertiesLength(); i++) {
            if (!TeacherReviewTemplateEditorComponent.isField(
                    this.template.reviewTemplateProperties[i].description, true)) {
                this.messages.push(new Message("Could not save the review template. The description of section " + (i+1) + " is not set.", "danger"));
                this.showHints = true;
                return false;
            }
            if (this.template.reviewTemplateProperties[i].type != "TEXT") {
                if (!TeacherReviewTemplateEditorComponent.isField(
                        this.template.reviewTemplateProperties[i].weight) &&
                        this.template.reviewTemplateProperties[i].weight >= 0) {
                    this.messages.push(new Message("Could not save the review template. The weight of section " + (i+1) + " is invalid.", "danger"));
                    this.showHints = true;
                    return false;
                }
            } else {
                if (TeacherReviewTemplateEditorComponent.isField(
                        this.template.reviewTemplateProperties[i].weight)) {
                    delete this.template.reviewTemplateProperties[i].weight;
                }
            }
            if (this.template.reviewTemplateProperties[i].type == "RUBRIC") {
                if (!TeacherReviewTemplateEditorComponent.isField(
                        this.template.reviewTemplateProperties[i].header)) {
                    this.messages.push(new Message("Could not save the review template. Unknown error in rubric of section " + (i+1) + ".", "danger"));
                    this.showHints = true;
                    return false;
                }
                if (!TeacherReviewTemplateEditorComponent.isField(
                        this.template.reviewTemplateProperties[i].rows)) {
                    this.messages.push(new Message("Could not save the review template. Unknown error in rubric of section " + (i+1) + ".", "danger"));
                    this.showHints = true;
                    return false;
                }
                if (!this.checkRubric(i)) {
                    this.showHints = true;
                    return false;
                }
            } else {
                if (TeacherReviewTemplateEditorComponent.isField(
                        this.template.reviewTemplateProperties[i].header)) {
                    delete this.template.reviewTemplateProperties[i].header;
                }
                if (TeacherReviewTemplateEditorComponent.isField(
                        this.template.reviewTemplateProperties[i].rows)) {
                    delete this.template.reviewTemplateProperties[i].rows;
                }
            }
        }
        return true;
    }

    public handleSaveError(error) {
        this.messages.push(new Message(
            (error.message) ? "The review template could not be saved. " + error.message :
                error.status ? `The review template could not be saved. ${error.status} - ${error.statusText}` :
                    "The review template could not be saved. Unknown server error",
            "danger"
        ));
    }

    public saveTemplate(event : any) {
        this.showHints = false;
        this.messages.splice(0);
        if (this.checkTemplate()) {
            if (typeof this.template.reviewTemplateId !== 'undefined') {
                this._reviewTemplateService.updateReviewTemplate(this.template).subscribe(t => {
                    this.template = t;
                    this.messages.push(new Message("The review template has been saved!", "success"));
                }, error => this.handleSaveError(error));
            } else {
                this._reviewTemplateService.createReviewTemplate(this.template).subscribe(t => {
                    this.template = t;
                    this.messages.push(new Message("The review template has been created!", "success"));
                }, error => this.handleSaveError(error));
            }
        }
    }

    public addProperty(event : any) {
        event.stopPropagation();
        if (!this.disableStructure) {
            let property = new ReviewTemplateProperty();
            property.type = "TEXT";
            property.description = "";
            this.template.reviewTemplateProperties.push(property);
        }
    }

    public moveUpProperty(event : any, index : number) {
        event.stopPropagation();
        if (!this.disableStructure && index > 0 && index < this.getPropertiesLength()) {
            let property = this.template.reviewTemplateProperties[index - 1];
            this.template.reviewTemplateProperties[index - 1] = this.template.reviewTemplateProperties[index];
            this.template.reviewTemplateProperties[index] = property;
        }
    }

    public moveDownProperty(event : any, index : number) {
        event.stopPropagation();
        if (!this.disableStructure && index >= 0 && index < this.getPropertiesLength() - 1) {
            let property = this.template.reviewTemplateProperties[index + 1];
            this.template.reviewTemplateProperties[index + 1] = this.template.reviewTemplateProperties[index];
            this.template.reviewTemplateProperties[index] = property;
        }
    }

    public removeProperty(event : any, index : number) {
        event.stopPropagation();
        if (!this.disableStructure && index >= 0 && index < this.getPropertiesLength()) {
            this.template.reviewTemplateProperties.splice(index, 1);
        }
    }

}