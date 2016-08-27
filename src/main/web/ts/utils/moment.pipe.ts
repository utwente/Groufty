/// <reference path="../../typings/index.d.ts" />

/*
 * Time helper using momentjs
 * Usage:
 *   timestamp | moment:'DD.MM.YYYY'
 * Defaults to 'L' - locale ie. '01/24/2016'
 */
import {PipeTransform, Pipe} from "@angular/core";

@Pipe({name: 'moment'})
export class MomentPipe implements PipeTransform {

    public transform(value:number, arg:string) : any {
        if (typeof value === "undefined" || value == null) {
            return "";
        }
        let date = moment(value);
        if (date.isValid()) {
            return date.format(arg || 'L');
        } else {
            return value;
        }
    }

}