import {EventEmitter} from "@angular/core";
import {GrouftyFiles} from "./files.file-upload";
import {GrouftyUploadStatus} from "./status.file-upload";

export class GrouftyUploader {
    
    public url : string;
    public method : string = 'POST';

    //Events
    public onUploadStart : EventEmitter<GrouftyUploadStatus> = new EventEmitter<GrouftyUploadStatus>();
    public onUploadProgress : EventEmitter<GrouftyUploadStatus> = new EventEmitter<GrouftyUploadStatus>();
    public onUploadDone : EventEmitter<GrouftyUploadStatus> = new EventEmitter<GrouftyUploadStatus>();
    public onUploadError : EventEmitter<GrouftyUploadStatus> = new EventEmitter<GrouftyUploadStatus>();
    
    //Status
    private _status : GrouftyUploadStatus;

    public static isFile(value:any) {
        return (File && value instanceof File);
    }

    public static hasFileApiSupport(): boolean {
        return !!(
            'File' in window &&
            'FileReader' in window &&
            'FileList' in window &&
            'Blob' in window);
    }

    public upload(files: Array<GrouftyFiles>,
                  onSuccess?: ((status: GrouftyUploadStatus) => void),
                  onError?: ((status: GrouftyUploadStatus) => void),
                  onProgress?: ((status: GrouftyUploadStatus) => void)) {
        if (GrouftyUploader.hasFileApiSupport()) {
            this._xhrTransport(files, onSuccess, onError, onProgress);
        } else {
            this._iframeTransport(files, onSuccess, onError, onProgress);
        }
    }

    private _iframeTransport(files: Array<GrouftyFiles>,
                             onSuccess?: ((status: GrouftyUploadStatus) => void),
                             onError?: ((status: GrouftyUploadStatus) => void),
                             onProgress?: ((status: GrouftyUploadStatus) => void)) {
        console.log("iFrame Transport not implemented (yet).")
    }

    private _xhrTransport(files: Array<GrouftyFiles>,
                          onSuccess?: ((status: GrouftyUploadStatus) => void),
                          onError?: ((status: GrouftyUploadStatus) => void),
                          onProgress?: ((status: GrouftyUploadStatus) => void)) {
        this._status = new GrouftyUploadStatus();
        
        let xhr = new XMLHttpRequest();
        let form = new FormData();

        this.onUploadStart.emit(this._status);

        for (let i = 0; i < files.length; i++) {
            if (!files[i].isValid()) {
                this._status.status = "ERROR";
                this._status.code = -1;
                this.onUploadError.emit(this._status);
                return;
            }

            for (let j = 0; j < files[i].files.length; j++) {
                form.append(files[i].alias, files[i].files[j], files[i].files[j].name);
            }
        }

        xhr.upload.onprogress = (event) => {
            this._status.status = "IN_PROGRESS";
            this._status.progress = Math.round(event.lengthComputable ? event.loaded * 99 / event.total : 0);
            this.onUploadProgress.emit(this._status);
            if (onProgress) {
                onProgress(this._status);
            }
        };

        xhr.onload = () => {
            let headers = GrouftyUploader._parseHeaders(xhr.getAllResponseHeaders());
            this._status.code = xhr.status;
            this._status.progress = 100;
            this._status.response = xhr.response;
            
            if (GrouftyUploader._isSuccessCode(xhr.status)) {
                this._status.status = "SUCCESS";
                this.onUploadDone.emit(this._status);
                if (onSuccess) {
                    onSuccess(this._status);
                }
            } else {
                this._status.status = "ERROR";
                this.onUploadError.emit(this._status);
                if (onError) {
                    onError(this._status);
                }
            }
        };

        xhr.onerror = () => {
            let headers = GrouftyUploader._parseHeaders(xhr.getAllResponseHeaders());
            this._status.status = "ERROR";
            this._status.code = xhr.status;
            this._status.progress = 100;
            this._status.response = xhr.response;
            this.onUploadError.emit(this._status);
            if (onError) {
                onError(this._status);
            }
        };

        xhr.onabort = () => {
            let headers = GrouftyUploader._parseHeaders(xhr.getAllResponseHeaders());
            this._status.status = "ABORT";
            this._status.code = 0;
            this._status.progress = 0;
            this._status.response = null;
            this.onUploadError.emit(this._status);
            if (onError) {
                onError(this._status);
            }
        };

        xhr.open(this.method, this.url, true);
        xhr.withCredentials = true;
        xhr.send(form);
    }

    private static _parseHeaders(headers:any) {
        let parsed:any = {}, key:any, val:any, i:any;

        if (!headers) {
            return parsed;
        }

        headers.split('\n').map((line:any) => {
            i = line.indexOf(':');
            key = line.slice(0, i).trim().toLowerCase();
            val = line.slice(i + 1).trim();

            if (key) {
                parsed[key] = parsed[key] ? parsed[key] + ', ' + val : val;
            }
        });

        return parsed;
    }

    private static _isSuccessCode(status:any) {
        return (status >= 200 && status < 300) || status === 304;
    }
    
}