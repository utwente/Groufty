import {Directive, ElementRef, EventEmitter} from '@angular/core';

@Directive({
    selector: '[file-select]',
    events: ['fileSelect'],
    host: {
        '(change)': 'onChange()'
    }
})
export class GrouftyFileSelect {
    public fileSelect:EventEmitter<File[]> = new EventEmitter<File[]>();

    constructor(private element:ElementRef) {}

    public onChange() {
        let files = this.element.nativeElement.files;
        let array = [];
        for (let i = 0; i < files.length; i++) {
            array.push(files.item(i))
        }
        this.fileSelect.emit(array);
    }
}
