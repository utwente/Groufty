export type ContentType = "FILE_SUBMISSION" | "TEXT_SUBMISSION | PDF_SUBMISSION"

export class Task {
    public authorId : number;
    public description : string;
    public fileDetails : string;
    public name : string;
    public reviewTemplateId : number;
    public contentType : ContentType;
    public taskId : number;
    public taskListId : number;
}