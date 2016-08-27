/// <reference path="../../../typings/pdf-viewer.d.ts" />
/// <reference path="../../../typings/index.d.ts" />

import {Input, Component, ElementRef} from "@angular/core";
import {FORM_DIRECTIVES} from "@angular/common";
import Timer = NodeJS.Timer
import {GrouftyHTTPService} from "../../services/http.service";

/**
 * PDF Component class.
 * Converts a PDF tag to an in browser PDF reader.
 * @author Javalon Development Group
 * @version 0.5.0
 */
@Component({
    selector: 'pdf',
    templateUrl: './templates/utils/pdf.component.html',
    directives: [FORM_DIRECTIVES]
})
export class PDFComponent {

    public noPreviousPage : boolean = true;
    public noNextPage : boolean = true;
    public noZoomOut : boolean = true;
    public noZoomIn : boolean = true;
    public pages : number = 0;
    public page : number = 0;

    private _wrapper : HTMLElement;
    private _file : string;
    private _page : number = 1;
    private _rotation : number = 0;
    private _scale : number = 0;

    private _pdfDocument : PDFDocumentProxy;
    private _pdfPageWidth : number = 0;
    private _pdfPageView: PDFPageView = null;
    private _pdfPage : PDFPageProxy;

    /**
     * Constructor that gets the HTML element to render the PDFPageView into.
     * @param element The element to render the PDFPageView into.
     */
    public constructor(private element: ElementRef) {
        //Setup the PDFJS worker.
        PDFJS.workerSrc = GrouftyHTTPService.getJS() + '/libs/pdf.worker.js';
        PDFJS.externalLinkTarget = PDFJS.LinkTarget.BLANK;
    }

    public ngOnInit() {
        this._wrapper = this.element.nativeElement.lastElementChild.firstElementChild;
    }

    /**
     * Get the PDF file that is shown by this PDF component.
     */
    @Input() public get file():string {
        return this._file;
    }

    /**
     * Set the PDF file that is shown by this PDF component.
     * @param value
     */
    public set file(value:string) {
        this._file = value;
        PDFJS.getDocument(this._file).then((pdf) => {
            this._pdfDocument = pdf;
            this._rotation = 0;
            this._page = 1;
            this.pages = this._pdfDocument.numPages;
            if (this._pdfPageView == null) {
                this.setup();
            } else {
                this.render();
            }
        });
    }

    /**
     * Function called when the PDF component is resized.
     */
    public resize() : void {
        this._pdfPageView.update(this._wrapper.offsetWidth / this._pdfPageWidth);
        this._pdfPageView.draw();
    }

    /**
     * Reset the PDF component and create a new PDFPageView.
     */
    public setup() : void {
        this._pdfDocument.getPage(this._page).then(pdfPage => {
            this._pdfPage = pdfPage;
            this._pdfPageView = new PDFJS.PDFPageView({
                container: this._wrapper,
                id: this._page,
                scale: 1.0,
                defaultViewport: pdfPage.getViewport(1.0, this._rotation),
                textLayerFactory: new PDFJS.DefaultTextLayerFactory(),
                annotationLayerFactory: new PDFJS.DefaultAnnotationLayerFactory()
            });
            this._pdfPageView.setPdfPage(pdfPage);
            this._pdfPageWidth = this._pdfPageView.width;
            let scale = this._scale == 0 ? this._wrapper.offsetWidth / this._pdfPageWidth : this._scale;
            this._pdfPageView.update(scale, this._rotation);
            this.page = this._page;
            this.update();
            return this._pdfPageView.draw();
        });
    }

    /**
     * Render the PDF component according to internal state.
     */
    public render() : void {
        this._pdfDocument.getPage(this._page).then(pdfPage => {
            this._pdfPage = pdfPage;
            this._pdfPageView.setPdfPage(pdfPage);
            this._pdfPageView.update(1, this._rotation);
            this._pdfPageWidth = this._pdfPageView.width;
            let scale = this._scale == 0 ? this._wrapper.offsetWidth / this._pdfPageWidth : this._scale;
            this._pdfPageView.update(scale, this._rotation);
            this.page = this._page;
            this.update();
            return this._pdfPageView.draw();
        });
    }

    /**
     * Update the buttons of the component.
     */
    public update(): void {
        this.noPreviousPage = false;
        this.noNextPage = false;
        this.noZoomIn = false;
        this.noZoomOut = false;
        if (this._page == 1) {
            this.noPreviousPage = true;
        }
        if (this._page == this._pdfDocument.numPages) {
            this.noNextPage = true;
        }
        if (this._scale == 0) {
            this.noZoomOut = true;
        }
        if (this._scale == 2.5 || (this._wrapper.offsetWidth / this._pdfPageWidth) >= 2.5) {
            this.noZoomIn = true;
        }
    }

    /**
     * Finds the next scale option.
     */
    private findNextScale(): number {
        let scale = 0;
        while (((this._wrapper.offsetWidth / this._pdfPageWidth) - scale) > 0 && scale <= 2.5) {
            scale += 0.25;
        }
        return scale;
    }

    /**
     * Go the the page with the specified number.
     * @param page The number of the page to browse to.
     */
    public toPage(page : any) : void {
        page = parseInt(page);
        if (this._page != page && page > 0 && page <= this._pdfDocument.numPages) {
            this._page = page;
            this.render();
        }
    }

    /**
     * Render the last page of the document.
     */
    public lastPage() : void {
        this._page = this._pdfDocument.numPages;
        this.render()
    }

    /**
     * Render the first page of the document.
     */
    public firstPage() : void {
        this._page = 1;
        this.render();
    }

    /**
     * Render the next page if available.
     */
    public nextPage() : void {
        if (this._page < this._pdfDocument.numPages) {
            this._page++;
            this.render();
        }
    }

    /**
     * Render the previous page if available.
     */
    public previousPage() : void {
        if (this._page > 1) {
            this._page--;
            this.render();
        }
    }

    /**
     * Zoom in to the page.
     */
    public zoomIn(): void {
        this._scale = this._scale == 0 ? this.findNextScale() : Math.min(this._scale + 0.25, 2.5);
        this.render();
    }

    /**
     * Zoom out from the page.
     */
    public zoomOut() : void {
        this._scale -= 0.25;
        if (this._scale < (this._wrapper.offsetWidth / this._pdfPageWidth)) {
            this._scale = 0;
        }
        this.render();
    }

    /**
     * Rotate the page 90 degrees to the left.
     */
    public rotateLeft() : void {
        this._rotation = (this._rotation - 90) % 360;
        this.render();
    }

    /**
     * Rotate the page 90 degrees to the right.
     */
    public rotateRight(): void {
        this._rotation = (this._rotation + 90) % 360;
        this.render();
    }

}