import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {NgIf, NgClass} from '@angular/common';

@Component({
    selector: 'alert',
    directives: [NgIf, NgClass],
    templateUrl: './templates/utils/alert.component.html',
})
export class AlertComponent implements OnInit {
    @Input() public type:string = 'warning';
    @Input() public dismissible:boolean;
    @Input() public dismissOnTimeout:number;

    @Output() public close:EventEmitter<AlertComponent> = new EventEmitter<AlertComponent>();

    private _closed:boolean;
    private _classes:Array<string> = [];

    public ngOnInit() : void {
        this._classes[0] = `alert-${this.type}`;
        if (this.dismissible) {
            this._classes[1] = 'alert-dismissible';
        } else {
            this._classes.length = 1;
        }

        if (this.dismissOnTimeout) {
            setTimeout(() => this.onClose(), this.dismissOnTimeout);
        }
    }

    public onClose(): void {
        this._closed = true;
        this.close.emit(this);
    }
}
