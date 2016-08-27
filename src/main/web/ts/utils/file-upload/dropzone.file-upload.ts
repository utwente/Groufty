import {Directive, EventEmitter, ElementRef} from '@angular/core';

@Directive({
    selector: '[file-drop-zone]',
    events: ['fileOver', 'fileDrop'],
    host: {
        '(drop)': 'onDrop($event)',
        '(dragover)': 'onDragOver($event)',
        '(dragleave)': 'onDragLeave($event)'
    }
})
export class GrouftyDropZone {
    public fileOver:EventEmitter<boolean> = new EventEmitter<boolean>();
    public fileDrop:EventEmitter<File[]> = new EventEmitter<File[]>();

    public constructor(private element:ElementRef) { }

    public onDrop(event:any) {
        let transfer = GrouftyDropZone._getTransfer(event);
        if (!transfer) {
            return;
        }

        let files = transfer.files;
        let array = [];
        for (let i = 0; i < files.length; i++) {
            array.push(files.item(i))
        }

        GrouftyDropZone._preventAndStop(event);
        this.fileOver.emit(false);
        this.fileDrop.emit(array);
    }

    public onDragOver(event:any) {
        let transfer = GrouftyDropZone._getTransfer(event);
        if (!GrouftyDropZone._haveFiles(transfer.types)) {
            return;
        }
        transfer.dropEffect = 'copy';

        GrouftyDropZone._preventAndStop(event);
        this.fileOver.emit(true);
    }

    public onDragLeave(event:any):any {
        if (event.currentTarget === (<any>this).element[0]) {
            return;
        }

        GrouftyDropZone._preventAndStop(event);
        this.fileOver.emit(false);
    }

    private static _getTransfer(event:any):any {
        return event.dataTransfer ? event.dataTransfer : event.originalEvent.dataTransfer; // jQuery fix;
    }

    private static _preventAndStop(event:any):any {
        event.preventDefault();
        event.stopPropagation();
    }

    private static _haveFiles(types:any):any {
        if (!types) {
            return false;
        }

        if (types.indexOf) {
            return types.indexOf('Files') !== -1;
        } else if (types.contains) {
            return types.contains('Files');
        } else {
            return false;
        }
    }
}