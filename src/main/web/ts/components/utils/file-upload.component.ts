import {NgClass, NgStyle, CORE_DIRECTIVES, FORM_DIRECTIVES} from "@angular/common";
import {EventEmitter, Component, Input, NgZone} from "@angular/core";
import {GrouftyFiles} from "../../utils/file-upload/files.file-upload";
import {GrouftyUploader} from "../../utils/file-upload/uploader.file-upload";
import {GrouftyDropZone} from "../../utils/file-upload/dropzone.file-upload";
import {GrouftyFileSelect} from "../../utils/file-upload/selector.file-upload";
import {GrouftyUploadStatus} from "../../utils/file-upload/status.file-upload";
import {GrouftyFileDescriptor} from "../../utils/file-upload/descriptor.file-upload";

@Component({
    selector: 'file-upload',
    templateUrl: './templates/utils/file-upload.component.html',
    directives: [GrouftyDropZone, GrouftyFileSelect, NgClass, NgStyle, CORE_DIRECTIVES, FORM_DIRECTIVES],
    outputs: ['onSelected', 'onUploadStart', 'onUploadProgress', 'onUploadDone', 'onUploadError']
})
export class FileUploadComponent {

    @Input() public autoUpload : boolean = false;
    public hasFileApiSupport : boolean = GrouftyUploader.hasFileApiSupport();

    public onSelected : EventEmitter<GrouftyFileDescriptor> = new EventEmitter<GrouftyFileDescriptor>();
    public onUploadStart : EventEmitter<GrouftyUploadStatus>;
    public onUploadProgress : EventEmitter<GrouftyUploadStatus>;
    public onUploadDone : EventEmitter<GrouftyUploadStatus>;
    public onUploadError : EventEmitter<GrouftyUploadStatus>;

    private _files : Array<GrouftyFiles>;
    private _progress : number = 0;
    private _state : number = 0;
    private _errorMessage : string = null;
    private _uploader : GrouftyUploader;
    private _hasFileDragOver : boolean = false;
    private _enableUpload : boolean = false;

    public constructor(private zone: NgZone) {
        this._uploader = new GrouftyUploader();
        this.onUploadStart = this._uploader.onUploadStart;
        this.onUploadProgress = this._uploader.onUploadProgress;
        this.onUploadDone = this._uploader.onUploadDone;
        this.onUploadError = this._uploader.onUploadError;
    }

    @Input() public get files(): Array<GrouftyFileDescriptor> {
        return this._files;
    }

    public set files(value: Array<GrouftyFileDescriptor>) {
        this._files = new Array<GrouftyFiles>(value.length);
        for (let i = 0; i < value.length; i++) {
            this._files[i] = GrouftyFiles.fromDescriptor(value[i]);
        }
    }

    @Input() public get apiUrl(): string {
        return this._uploader.url;
    }

    public set apiUrl(value:string) {
        this._uploader.url = value;
    }

    @Input() public get method(): string {
        return this._uploader.method;
    }

    public set method(value:string) {
        this._uploader.method = value;
    }

    public onFileOver(value : boolean) {
        if (this._state <= 1 && this.files.length == 1) {
            this._hasFileDragOver = value;
        }
    }

    public onFileDrop(value: File[]) {
        if (this._state <= 1 && this.files.length == 1) {
            this.onFileSelect(0, value);
        }
    }

    public onFileSelect(index: number, value : File[]) {
        this._files[index].files = value;
        this._files[index].feedback = true;
        this.onSelected.emit(this._files[index]);
        if ((value.length < this._files[index].minAmount &&
            value.length > this._files[index].maxAmount) && this.files.length == 1) {
            this._files[index].files = [];
            this._files[index].feedback = false;
            this._state = 5;
        }
        if (!this._files[index].isValid() && this.files.length == 1) {
            this._files[index].files = [];
            this._files[index].feedback = false;
            this._state = 6;
        }
        this.update();
    }

    public onBack() {
        this._errorMessage = null;
        this._state = 0;
        this.update();
    }

    public onFileCancel() {
        if (this._files.length == 1) {
            this._files[0].files = [];
            this._files[0].feedback = false;
        }
        this.onBack();
    }

    public onFileUpload() {
        this._state = 2;
        this._progress = 0;
        this._hasFileDragOver = false;
        this._uploader.upload(this._files, () => {
            this._state = 3;
            this._progress = 0;
            setTimeout(() => {
                this.onFileCancel();
                this._state = 0;
            }, 5000);
        }, status => {
            this._state = 4;
            this._progress = status.progress;
            let response = JSON.parse(status.response);
            if (typeof(response.message) !== 'undefined') {
                this._errorMessage = response.message;
            } else {
                this._errorMessage = "";
            }
        }, status => {
           this.zone.run(() => {
               this._progress = status.progress;
           });
        });
    }
    
    public update() : void {
        this._enableUpload = true;
        for (let i = 0; i < this._files.length; i++) {
            this._enableUpload = this._enableUpload && this._files[i].isValid();
        }
        if (this._enableUpload && this.autoUpload) {
            this.onFileUpload();
        } else if(this._enableUpload && this._files.length == 1) {
            this._state = 1;
        }
    }

}