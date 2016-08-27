export type UploadStatus = "STARTING" | "IN_PROGRESS" | "ABORT" | "ERROR" | "SUCCESS";

export class GrouftyUploadStatus {

    public status : UploadStatus = "STARTING";
    public progress : number = 0;
    public code : number = 0;
    public response : any = null;

}