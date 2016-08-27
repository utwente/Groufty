/// <reference path="../../../typings/index.d.ts" />

import {Component, OnChanges} from "@angular/core";

/**
 * Markdown Component class.
 * Converts a markdown compatible text inside markdown tags to HTML readable output.
 * @author Javalon Development Group
 * @version 0.0.3
 */
@Component({
    selector: 'markdown',
    template: '<div [innerHTML]="parsedMd"></div>',
    inputs: ['md']
})
export class MarkdownComponent implements OnChanges {

    public md : string;
    public parsedMd: string;

    public ngOnChanges() {
        this.parsedMd = "";
        if (typeof this.md !== 'undefined' && this.md != null && this.md != "") {
            var md = marked.setOptions({
                renderer: new marked.Renderer(),
                gfm: true,
                tables: true,
                breaks: false,
                pedantic: false,
                sanitize: true,
                smartLists: true,
                smartypants: false
            });
            this.parsedMd = md.parse(this.md);
        }
    }

}