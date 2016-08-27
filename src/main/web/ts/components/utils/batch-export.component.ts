import {ExportOptions} from "../../model/utils/export-options";
import {Component} from "@angular/core";
import {Http, Response} from "@angular/http";
import {GrouftyHTTPService} from "../../services/http.service";
import {Observable} from "rxjs/Observable";

@Component({
	selector: 'batch-export',
	templateUrl: './templates/utils/batch-export.component.html',
	directives: [],
})
export class BatchExportComponent {
	public options : ExportOptions = new ExportOptions();
	private _batchExportUrl = GrouftyHTTPService.getAPI() + '/inout';

	public constructor() {}

	public onSubmit() {
		var params = "";

		for (var key in this.options) {

			if(this.options.hasOwnProperty(key)) {
				params += key + '=' + this.options[key] + '&';
			}
		}

		window.location.href = this._batchExportUrl + '/?' + params;
	}

	/**
	 * Handle an error response from a request.
	 * @param error The response containing the error.
	 * @returns {Observable<T>} The error observable.
	 */
	private handleError(error: Response) {
		console.error(error);
		return Observable.throw(error.json().error || 'Server error');
	}
}