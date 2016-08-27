import {Observable} from "rxjs/Observable";
import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {GrouftyHTTPService} from "./http.service";
import {AbstractService} from "./abstract.service";
import {ReleaseInfo} from "../model/release/release-info";

/**
 * Class that provide the release services.
 * @author Javalon Development Team
 * @version 0.1
 */
@Injectable()
export class ReleaseInfoService extends AbstractService {
	private _releaseUrl = GrouftyHTTPService.getAPI() + '/release';

	constructor(http:Http) {
		super(http);
	}

	/**
	 * Get this build's release information
	 * @returns {Observable<T>}
	 */
	public getReleaseInfo() : Observable<ReleaseInfo> {
		return super.performGet<ReleaseInfo>(this._releaseUrl);
	}

}