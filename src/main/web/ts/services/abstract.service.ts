import {Observable} from "rxjs/Observable";
import {Injectable} from "@angular/core";
import {Page} from "../model/utils/page";
import {Http, Headers, RequestOptions} from "@angular/http";

/**
 * Abstract service class implementing service basics such as error handling and retrieving pages.
 * @author Javalon Development Team
 * @version 0.1
 */
@Injectable()
export abstract class AbstractService {
	constructor (protected http: Http) {}

	/**
	 * Get a page from the specified url with the specified properties.
	 * @param url The API url to get a page from.
	 * @param size The amount of tasks on the page.
	 * @param page The page to get.
	 * @param params Indicates whether the url already includes params
	 * @returns {Observable<R>} The observable object for the request.
	 * @private
	 */
	protected getPage<T>(url : string, size : number = null, page : number = null, params : boolean = false) : Observable<Page<T>>{
		if (size != null) {
			var sizeParam;
			if (params) {
				sizeParam = "&size=";
			} else {
				sizeParam = "?size=";
			}
			url = url + sizeParam + size;
			if (page != null) {
				url = url + "&page=" + page;
			}
		}
		return this.http.get(url)
			.map(res => <Page<T>> res.json())
			.catch(this.handleError);
	}


	/**
	 * Get resource of type T at the given url
	 * @param url
	 * @returns {Promise<T>|Observable<R>|Promise<ErrorObservable>|Promise<R>|any}
	 */
	public performGet<T>(url : string) : Observable<T> {
		return this.http.get(url)
			.map(res => <T> res.json())
			.catch(this.handleError)
	}
		
	/**
	 * Update resource of type T at the given url using specified body. Default header is application JSON.
	 * @param url
	 * @param body
	 * @param headers
	 * @returns {Promise<T>|Observable<R>|Promise<ErrorObservable>|Promise<R>|any}
	 */
	protected performUpdate<T>(url: string, body : string, headers : Headers = null) : Observable<T>{
		if (headers == null) {
			headers = new Headers({ 'Content-Type': 'application/json' });
		}
		let options = new RequestOptions({ headers: headers });
		return this.http.put(url, body, options)
			.map(res => <T> res.json())
			.catch(this.handleError)
	}

	/**
	 * Create resource of type T at the given url using specified body. Default header is application JSON.
	 * @param url
	 * @param body
	 * @param headers
	 * @returns {Promise<T>|Observable<R>|Promise<ErrorObservable>|Promise<R>|any}
	 */
	protected performCreate<T>(url: string, body : string, headers : Headers = null) : Observable<T> {
		if (headers == null) {
			headers = new Headers({ 'Content-Type': 'application/json' });
		}
		let options = new RequestOptions({ headers: headers });
		return this.http.post(url, body, options)
			.map(res => <T> res.json())
			.catch(this.handleError)
	}

	/**
	 * Create resource of type T at the given url using specified body. Default header is application JSON.
	 * @param url
	 * @param body
	 * @param headers
	 * @returns {Promise<T>|Observable<R>|Promise<ErrorObservable>|Promise<R>|any}
	 */
	protected performDelete(url: string, headers : Headers = null) {
		if (headers == null) {
			headers = new Headers({});
		}
		let options = new RequestOptions({ headers: headers });
		return this.http.delete(url, options)
			.catch(this.handleError)
	}
	
	/**
	 * Handle an error response from a request.
	 * @param error The response containing the error.
	 * @returns {Observable<T>} The error observable.
	 */
	protected handleError(error: any) {
		let errMsg = (error.message) ? error.message :
			error.status ? `${error.status} - ${error.statusText}` : 'Server error';
		console.error(errMsg); // log to console
		return Observable.throw(error); // rethrow the error for others to catch
	}
	
	protected getHttp() {
		return this.http;
	}
}