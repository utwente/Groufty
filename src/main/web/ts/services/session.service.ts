import {Injectable, EventEmitter, Injector} from "@angular/core";
import {Http, Headers, RequestOptions, Response} from "@angular/http";
import {PromiseWrapper} from "@angular/common/src/facade/promise";
import {Router} from "@angular/router-deprecated";
import {User} from "../model/users/user";
import {SessionCredentials} from "../model/utils/session-credentials";
import {grouftyInjector} from "../utils/groufty.injector";
import {GrouftyHTTPService} from "./http.service";
import {ActivationRule} from "../model/utils/activation-rule";
import {Observable} from "rxjs/Rx";

/**
 * Service that provides a service that connects to the authenication
 * endpoint in the back-end to provide the front-end session.
 * @author Javalon Development Team
 * @version 0.4
 */
@Injectable()
export class SessionService {

    private _user : User = null;
    private _authUrl : string = GrouftyHTTPService.getAPI() + '/users/session';
    private _initial : boolean = true;

    public onChange: EventEmitter<User> = new EventEmitter<User>();

    public constructor(private _http: Http, private _grouftyHttp: GrouftyHTTPService) {
        this._resolveSession().subscribe(
            user => this._updateSession(user),
            error => this._updateSession(null));
    }

    /**
     * Update the Session contained by this SessionService.
     * @param user The user to update the session with.
     * @private
     */
    private _updateSession(user : User): void {
        this._initial = false;
        this._user = user;
        this.onChange.emit(this._user);
    }

    /**
     * Get the Observable that resolves the session via the back-end.
     * @returns {Observable<User>} The observable that returns the logged in user.
     * @private
     */
    private _resolveSession() : Observable<User> {
        return this._http.get(this._grouftyHttp.noCache(this._authUrl))
            .map(res => <User> res.json())
            .catch(this.handleError);
    }

    /**
     * Create a new session by logging in using the specified credentials.
     * @param creds The credentials of the user.
     * @param success The function that is called on successful login.
     * @param error The function that is called on an error.
     * @param complete The function that is called when the request is complete.
     */
    public login(creds : SessionCredentials, success?: ((value: User) => void),
                 error?: (error: any) => void, complete?: () => void): void {
        let body = JSON.stringify(creds);
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});
        this._http.put(this._grouftyHttp.noCache(this._authUrl), body, options)
            .map(res => <User> res.json())
            .catch(this.handleError)
            .subscribe(user => {
                this._updateSession(user);
                if (success) {
                    success(user);
                }
            }, error, complete);
    }

    /**
     * Ends the session and does logout the user.
     * @param success The function that is called on successful login.
     * @param error The function that is called on an error.
     * @param complete The function that is called when the request is complete.
     */
    public logout(success?: (() => void), error?: (error: any) => void, complete?: () => void): void {
        this._http.delete(this._grouftyHttp.noCache(this._authUrl))
            .catch(this.handleError)
            .subscribe(() => {
                this._updateSession(null);
                if (success) {
                    success();
                }
            }, e => {
                if (e.status == 403) {
                    this._updateSession(null);
                    if (success) {
                        success();
                    }
                } else {
                    if (error) {
                        error(e);
                    }
                }
            }, complete);
    }

    /**
     * Resolves the user object for the current session.
     * @returns {Promise<User>} Returns a promise that will eventually return the user, or null.
     */
    public resolveUser() : Promise<User> {
        if (this._initial) {
            return this._resolveSession().toPromise().catch(() => null);
        }
        return PromiseWrapper.resolve(this._user);
    }

    /**
     * Checks if there is an active session.
     * @returns {Promise<boolean>} Returns an promise that will eventually return true if
     * there is an active session, of false if there is no active session.
     */
    public isActive() : Promise<boolean> {
        if (this._initial) {
            return this._resolveSession().map(user => user != null).toPromise().catch(() => false);
        }
        return PromiseWrapper.resolve(this._user != null)
    }

    /**
     * Static function that can be used in @CanActivate to check if a user has access.
     * @param rules The array with the rules to check against.
     * @param defaultRoute The default route if no rule matches.
     * @returns {Promise<boolean>} The promise returning the user's session.
     */
    public static canActivate(rules: ActivationRule[], defaultRoute: string = null) {
        let injector: Injector = grouftyInjector(); // get the stored reference to the injector
        let sessionService: SessionService = injector.get(SessionService);
        let router: Router = injector.get(Router);

        return new Promise((resolve) => {
            sessionService.resolveUser().then(user => {
                for (let i = 0; i < rules.length; i++) {
                    let match = false;
                    if (user != null) {
                        match = rules[i].match(true, user.authority);
                    } else {
                        match = rules[i].match(false, null);
                    }
                    if (match) {
                        if (rules[i].page != null) {
                            router.navigateByUrl(rules[i].page);
                            resolve(false);
                        } else {
                            resolve(true);
                        }
                        return;
                    }
                }
                if (defaultRoute != null) {
                    router.navigateByUrl(defaultRoute);
                }
                resolve(false);
                return;
            });
        });
    }

    /**
     * Handle an error response from a request.
     * @param error The response containing the error.
     * @returns {Observable<T>} The error observable.
     */
    private handleError(error: Response) {
        return Observable.throw(error || 'Server error');
    }
}